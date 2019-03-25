package com.tiza.plugin.protocol.gb32960.cmd;

import com.tiza.plugin.model.Header;
import com.tiza.plugin.protocol.gb32960.Gb32960DataProcess;

/**
 * 设置指令
 *
 * Description: Gb32960_81
 * Author: DIYILIU
 * Update: 2019-03-25 11:39
 */
public class Gb32960_81 extends Gb32960DataProcess {

    public Gb32960_81() {
        this.cmdId = 0x81;
    }

    @Override
    public void parse(byte[] content, Header header) {



    }
}
