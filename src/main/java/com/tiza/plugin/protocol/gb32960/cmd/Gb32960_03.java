package com.tiza.plugin.protocol.gb32960.cmd;

import com.tiza.plugin.model.Header;
import com.tiza.plugin.protocol.gb32960.Gb32960DataProcess;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 补发信息上报
 *
 * Description: Gb32960_03
 * Author: DIYILIU
 * Update: 2019-03-18 16:57
 */

@Slf4j
@Service
public class Gb32960_03 extends Gb32960DataProcess {

    public Gb32960_03() {
        this.cmdId = 0x03;
    }

    @Override
    public void parse(byte[] content, Header header) {
        Gb32960DataProcess dataProcess = (Gb32960DataProcess) cmdCacheProvider.get(0x02);
        dataProcess.parse(content, header);
    }
}
