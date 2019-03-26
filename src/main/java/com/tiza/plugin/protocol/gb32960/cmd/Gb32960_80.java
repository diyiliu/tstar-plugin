package com.tiza.plugin.protocol.gb32960.cmd;

import com.tiza.plugin.model.DeviceData;
import com.tiza.plugin.model.Gb32960Header;
import com.tiza.plugin.model.Header;
import com.tiza.plugin.protocol.gb32960.Gb32960DataProcess;
import com.tiza.plugin.util.CommonUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 查询指令
 * Description: Gb32960_80
 * Author: DIYILIU
 * Update: 2019-03-25 11:39
 */

@Slf4j
@Service
public class Gb32960_80 extends Gb32960DataProcess {

    public Gb32960_80() {
        this.cmdId = 0x80;
    }

    @Override
    public void parse(byte[] content, Header header) {
        Gb32960Header gb32960Header = (Gb32960Header) header;
        int resp = gb32960Header.getResp();

        ByteBuf buf = Unpooled.copiedBuffer(content);
        // 数据采集时间
        Date date = CommonUtil.getBufDate(buf, 6);
        gb32960Header.setDataTime(date.getTime());

        int count = buf.readUnsignedByte();
        if (count > 252) {

            log.warn("参数总数异常: [{}]", count);
        }

        Map<Integer, Object> respMap = new HashMap();
        Object value = "";
        for (int i = 0; i < count; i++) {
            int id = buf.readByte();

            if (0x01 == id || 0x02 == id || 0x03 == id ||
                    0x06 == id || 0x0A == id || 0x0B == id || 0x0F == id) {

                value = buf.readUnsignedShort();
            } else if (0x04 == id || 0x09 == id || 0x0C == id ||
                    0x0D == id || 0x10 == id) {

                value = buf.readUnsignedByte();
            } else if (0x07 == id || 0x08 == id) {
                byte[] bytes = new byte[5];
                buf.readBytes(bytes);

                value = new String(bytes);
            } else if (0x05 == id || 0x0E == id) {
                // 数据长度
                int length = Integer.valueOf(String.valueOf(respMap.get(id - 1)));

                byte[] bytes = new byte[length];
                buf.readBytes(bytes);

                value = new String(bytes);
            }
            respMap.put(id, value);
        }

        // 处理指令应答
        DeviceData deviceData = buildData(gb32960Header, "cmdResp", respMap);
        deviceData.setDataStatus(resp);
        dataParse.detach(deviceData);
    }
}
