package com.tiza.plugin.protocol.jt808;

import com.tiza.plugin.cache.ICache;
import com.tiza.plugin.model.Header;
import com.tiza.plugin.model.IDataProcess;
import com.tiza.plugin.model.Jt808Header;
import com.tiza.plugin.protocol.hw.HwDataProcess;
import com.tiza.plugin.util.CommonUtil;
import com.tiza.plugin.util.JacksonUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * Description: Jt808DataProcess
 * Author: DIYILIU
 * Update: 2018-12-06 10:25
 */

@Slf4j
@Service
public class Jt808DataProcess implements IDataProcess {

    protected int cmdId = 0xFFFF;

    @Resource
    private ICache cmdCacheProvider;

    @Resource
    protected HwDataProcess hwDataProcess;

    @Resource
    protected ICache vehicleInfoProvider;

    @Resource
    protected JdbcTemplate jdbcTemplate;

    @Resource
    private Producer kafkaProducer;

    @Value("${kafka.send-topic}")
    private String sendTopic;

    @Override
    public Header parseHeader(byte[] bytes) {
        ByteBuf buf = Unpooled.copiedBuffer(bytes);
        // 读取消息头标识位
        buf.readByte();

        buf.markReaderIndex();
        byte[] checkArray = new byte[bytes.length - 3];
        buf.readBytes(checkArray);
        // 计算校验位
        byte checkReady = CommonUtil.getCheck(checkArray);
        buf.resetReaderIndex();

        short cmd = buf.readShort();
        int bodyProperty = buf.readUnsignedShort();
        Jt808Header jt808Header = CommonUtil.parseJt808Body(bodyProperty);

        byte[] array = new byte[6];
        buf.readBytes(array);
        String terminalId = CommonUtil.bytesToStr(array);

        int serial = buf.readUnsignedShort();

        int length = jt808Header.getLength();
        byte[] bodyContent = new byte[length];
        buf.readBytes(bodyContent);

        byte check = buf.readByte();
        // 读取消息尾标识位
        buf.readByte();

        if (check != checkReady) {

            log.error("校验位验证失败，指令[{}]", CommonUtil.bytesToString(bytes));
            return null;
        }

        if (!vehicleInfoProvider.containsKey(terminalId)) {
            log.warn("设备[{}]不存在!", terminalId);

            return null;
        }

        jt808Header.setCmd(cmd);
        jt808Header.setTerminalId(terminalId);
        jt808Header.setSerial(serial);
        jt808Header.setContent(bodyContent);
        jt808Header.setCheck(check);

        return jt808Header;
    }

    @Override
    public void parse(byte[] content, Header header) {

    }

    @Override
    public byte[] pack(Header header, Object... argus) {
        return new byte[0];
    }

    @Override
    public void init() {

        cmdCacheProvider.put(cmdId, this);
    }

    /**
     * 写入 kafka
     *
     * @param header
     * @param param
     */
    public void sendToKafka(Jt808Header header, Map param) {
        String terminal = header.getTerminalId();

        Map map = new HashMap();
        map.put("terminal", terminal);
        map.put("cmd", header.getCmd());
        map.put("content", CommonUtil.bytesToStr(header.getContent()));
        map.put("data", param);
        map.put("timestamp", System.currentTimeMillis());

        String json = JacksonUtil.toJson(map);
        kafkaProducer.send(new KeyedMessage(sendTopic, terminal, json));
    }

    /**
     * 写入数据库
     *
     * @param sql
     */
    public void sendToDb(String sql, Object... args) {

        jdbcTemplate.update(sql, args);
    }
}
