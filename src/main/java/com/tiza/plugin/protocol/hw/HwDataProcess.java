package com.tiza.plugin.protocol.hw;

import com.tiza.plugin.cache.ICache;
import com.tiza.plugin.model.Header;
import com.tiza.plugin.model.IDataProcess;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Resource;

/**
 * Description: HwDataProcess
 * Author: DIYILIU
 * Update: 2018-12-10 14:21
 */
public class HwDataProcess implements IDataProcess {

    /** 1001: 垃圾箱; 1002: 垃圾袋 **/
    protected int dataType;

    @Resource
    protected ICache vehicleInfoProvider;

    @Resource
    protected JdbcTemplate jdbcTemplate;

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

    public int getDataType() {
        return dataType;
    }
}
