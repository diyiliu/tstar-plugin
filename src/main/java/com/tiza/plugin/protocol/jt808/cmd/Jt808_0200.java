package com.tiza.plugin.protocol.jt808.cmd;

import com.tiza.plugin.model.Header;
import com.tiza.plugin.model.Jt808Header;
import com.tiza.plugin.model.Position;
import com.tiza.plugin.protocol.jt808.Jt808DataProcess;
import com.tiza.plugin.util.CommonUtil;
import com.tiza.plugin.util.DateUtil;
import com.tiza.plugin.util.GpsCorrectUtil;
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
        String terminal = jt808Header.getTerminalId();

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
        if (time == 0){
            log.info("设备[{}]GPS时间[{}]异常", terminal, CommonUtil.bytesToStr(timeBytes));
            return;
        }

        while (buf.readableBytes() > 2) {
            int id = buf.readByte();
            int length = buf.readByte();
            byte[] bytes = new byte[length];
            if (length > buf.readableBytes()) {
                break;
            }
            buf.readBytes(bytes);
        }

        log.info("收到终端[{}]位置信息[{}, {}, {}] ... ", terminal, DateUtil.dateToString(new Date(time)), lat, lng);

        double latD = CommonUtil.keepDecimal(lat, 0.000001, 6);
        double lngD = CommonUtil.keepDecimal(lng, 0.000001, 6);

        double[] enLatLng =  GpsCorrectUtil.transform(latD, lngD);

        Position position = new Position();
        position.setLat(latD);
        position.setLng(lngD);
        position.setEnLat(enLatLng[0]);
        position.setEnLng(enLatLng[1]);
        position.setTime(time);

        CommonUtil.mountPosition(position, position.getEnLng(), position.getEnLat());

        dataParse.dealPosition(terminal, position);
    }
}
