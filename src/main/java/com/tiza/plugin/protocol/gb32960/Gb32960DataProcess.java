package com.tiza.plugin.protocol.gb32960;

import com.tiza.plugin.model.Header;
import com.tiza.plugin.model.IDataProcess;

/**
 * Description: Gb32960DataProcess
 * Author: DIYILIU
 * Update: 2018-12-06 10:25
 */

public class Gb32960DataProcess implements IDataProcess {

    @Override
    public Header parseHeader(byte[] bytes) {
        return null;
    }

    @Override
    public void parse(byte[] content, Header header) {

    }

    @Override
    public byte[] pack(Header header, Object... argus) {
        return new byte[0];
    }

    @Override
    public void init() {

    }

}
