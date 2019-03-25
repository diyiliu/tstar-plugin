package com.tiza.plugin.protocol.gb32960.cmd;

import com.tiza.plugin.model.Header;
import com.tiza.plugin.protocol.gb32960.Gb32960DataProcess;

/**
 * 车载终端控制命令
 * Description: Gb32960_82
 * Author: DIYILIU
 * Update: 2019-03-25 11:39
 */
public class Gb32960_82 extends Gb32960DataProcess {

    public Gb32960_82() {
        this.cmdId = 0x82;
    }

    @Override
    public void parse(byte[] content, Header header) {


    }
}
