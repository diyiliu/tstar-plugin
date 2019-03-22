package com.tiza.plugin.protocol.gb32960.cmd;

import com.tiza.plugin.model.Header;
import com.tiza.plugin.protocol.gb32960.Gb32960DataProcess;
import com.tiza.plugin.util.CommonUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 车辆登入
 *
 * Description: Gb32960_01
 * Author: DIYILIU
 * Update: 2019-03-18 16:57
 */

@Slf4j
@Service
public class Gb32960_01 extends Gb32960DataProcess {

    public Gb32960_01() {
        this.cmdId = 0x01;
    }

    @Override
    public void parse(byte[] content, Header header) {
        ByteBuf buf = Unpooled.copiedBuffer(content);
        // 数据采集时间
        Date date = CommonUtil.getBufDate(buf, 6);

        // 登入流水号
        buf.readUnsignedShort();

        byte[] iccidBytes = new byte[20];
        buf.readBytes(iccidBytes);
        // SIM 卡 ICCID号
        String iccid = new String(iccidBytes);

        int count = buf.readByte();
        int length = buf.readByte();
        if (buf.readableBytes() < count * length){

            log.warn("解析可充电储能系统字节长度不足!");
            return;
        }

        // 可充电储能子系统数
        List<String> codes = new ArrayList<>();
        for (int i = 0; i < count; i++){
            byte[] codeBytes = new byte[length];
            buf.readBytes(codeBytes);

            // 可充电储能系统编码
            String code = new String(codeBytes);
            codes.add(code);
        }
    }
}
