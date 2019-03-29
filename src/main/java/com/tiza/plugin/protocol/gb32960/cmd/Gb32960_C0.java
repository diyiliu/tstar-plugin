package com.tiza.plugin.protocol.gb32960.cmd;

import com.tiza.plugin.model.DeviceData;
import com.tiza.plugin.model.Gb32960Header;
import com.tiza.plugin.model.Header;
import com.tiza.plugin.protocol.gb32960.Gb32960DataProcess;
import com.tiza.plugin.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Description: Gb32960_C0
 * Author: DIYILIU
 * Update: 2019-03-29 14:55
 */

@Slf4j
@Service
public class Gb32960_C0 extends Gb32960DataProcess {

    public Gb32960_C0() {
        this.cmdId = 0xC0;
    }

    @Override
    public void parse(byte[] content, Header header) {
        Gb32960Header gb32960Header = (Gb32960Header) header;
        log.info("收到终端[{}]透传信息[{}] ... ", gb32960Header.getVin(), CommonUtil.bytesToStr(content));

        DeviceData deviceData = new DeviceData();
        deviceData.setDeviceId(gb32960Header.getVin());
        deviceData.setCmdId(cmdId);
        deviceData.setTime(gb32960Header.getGwTime());
        deviceData.setBytes(content);

        // 子协议解析
        dataParse.detach(deviceData);
    }
}
