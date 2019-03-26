package com.tiza.plugin.protocol.gb32960.cmd;

import com.tiza.plugin.model.Gb32960Header;
import com.tiza.plugin.model.Header;
import com.tiza.plugin.protocol.gb32960.Gb32960DataProcess;
import org.springframework.stereotype.Service;

/**
 * 车载终端控制命令
 * Description: Gb32960_82
 * Author: DIYILIU
 * Update: 2019-03-25 11:39
 */

@Service
public class Gb32960_82 extends Gb32960DataProcess {

    public Gb32960_82() {
        this.cmdId = 0x82;
    }

    @Override
    public void parse(byte[] content, Header header) {
        Gb32960Header gb32960Header = (Gb32960Header) header;

        dataParse.detach(respData(gb32960Header));
    }
}
