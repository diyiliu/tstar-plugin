package com.tiza.plugin.protocol.gb32960.cmd;

import com.tiza.plugin.model.Header;
import com.tiza.plugin.protocol.gb32960.Gb32960DataProcess;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Description: Gb32960_05
 * Author: DIYILIU
 * Update: 2019-03-18 16:57
 */

@Slf4j
@Service
public class Gb32960_05 extends Gb32960DataProcess {

    public Gb32960_05() {
        this.cmdId = 0x05;
    }

    @Override
    public void parse(byte[] content, Header header) {


    }
}
