package com.tiza.plugin.protocol.gb32960.cmd;

import com.tiza.plugin.model.Header;
import com.tiza.plugin.protocol.gb32960.Gb32960DataProcess;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 心跳
 * Description: Gb32960_07
 * Author: DIYILIU
 * Update: 2019-03-18 16:57
 */

@Slf4j
@Service
public class Gb32960_07 extends Gb32960DataProcess {

    public Gb32960_07() {
        this.cmdId = 0x07;
    }

    @Override
    public void parse(byte[] content, Header header) {


    }
}
