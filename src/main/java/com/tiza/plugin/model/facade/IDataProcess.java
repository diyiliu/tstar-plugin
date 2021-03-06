package com.tiza.plugin.model.facade;

import com.tiza.plugin.model.Header;

/**
 * Description: IDataProcess
 * Author: DIYILIU
 * Update: 2016-03-21 9:55
 */
public interface IDataProcess {

    Header parseHeader(byte[] bytes);

    void parse(byte[] content, Header header);

    byte[] pack(Header header, Object... argus);

    void init();
}
