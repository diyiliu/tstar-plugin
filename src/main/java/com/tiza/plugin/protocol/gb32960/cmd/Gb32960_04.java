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
 * 车辆登出
 *
 * Description: Gb32960_04
 * Author: DIYILIU
 * Update: 2019-03-18 16:57
 */

@Slf4j
@Service
public class Gb32960_04 extends Gb32960DataProcess {

    public Gb32960_04() {
        this.cmdId = 0x04;
    }

    @Override
    public void parse(byte[] content, Header header) {
        Gb32960Header gb32960Header = (Gb32960Header) header;
        ByteBuf buf = Unpooled.copiedBuffer(content);

        // 数据采集时间
        Date date = CommonUtil.getBufDate(buf, 6);
        // 登出流水号
        buf.readUnsignedShort();

        Map realMode = new HashMap();
        realMode.put("inOut", 0);

        // 处理实时上报数据
        DeviceData deviceData = buildData(gb32960Header);
        deviceData.setTime(date.getTime());
        deviceData.setDataBody(realMode);
        dataParse.detach(deviceData);
    }
}
