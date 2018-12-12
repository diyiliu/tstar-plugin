package com.tiza.plugin.protocol.jt808.cmd;

import com.tiza.plugin.model.Header;
import com.tiza.plugin.model.Jt808Header;
import com.tiza.plugin.protocol.jt808.Jt808DataProcess;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Description: Jt808_8900
 * Author: DIYILIU
 * Update: 2017-05-25 15:24
 */

@Slf4j
@Service
public class Jt808_8900 extends Jt808DataProcess {

    public Jt808_8900() {
        this.cmdId = 0x8900;
    }

    @Override
    public byte[] pack(Header header, Object... argus) {



        return null;
    }

}
