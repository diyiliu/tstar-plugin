package com.tiza.plugin.protocol.jt808.cmd;

import com.tiza.plugin.model.Header;
import com.tiza.plugin.model.Jt808Header;
import com.tiza.plugin.protocol.jt808.Jt808DataProcess;
import com.tiza.plugin.util.CommonUtil;
import com.tiza.plugin.util.DateUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Description: Jt808_0200
 * Author: DIYILIU
 * Update: 2017-05-25 15:24
 */

@Slf4j
@Service
public class Jt808_0200 extends Jt808DataProcess {

    public Jt808_0200() {
        this.cmdId = 0x0200;
    }

    @Override
    public void parse(byte[] content, Header header) {
        Jt808Header jt808Header = (Jt808Header) header;

        ByteBuf buf = Unpooled.copiedBuffer(content);
        long alarmFlag = buf.readUnsignedInt();
        long status = buf.readUnsignedInt();

        long lat = buf.readUnsignedInt();
        long lng = buf.readUnsignedInt();

        int height = buf.readShort();
        int speed = buf.readUnsignedShort();
        int direction = buf.readUnsignedShort();

        byte[] timeBytes = new byte[6];
        buf.readBytes(timeBytes);
        long time = CommonUtil.parseBCDTime(timeBytes);

        while (buf.readableBytes() > 2){
            int id = buf.readByte();
            int length = buf.readByte();
            byte[] bytes = new byte[length];
            if (length > buf.readableBytes()){
                break;
            }
            buf.readBytes(bytes);
        }

        log.info("收到终端[{}]位置信息[{}, {}, {}] ... ", jt808Header.getTerminalId(),
                DateUtil.dateToString(new Date(time)), lat, lng);
    }
}
