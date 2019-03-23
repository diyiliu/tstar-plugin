package com.tiza.plugin.model.adapter;

import cn.com.tiza.tstar.common.process.BaseHandle;
import com.tiza.plugin.model.DeviceData;
import com.tiza.plugin.model.Position;
import com.tiza.plugin.model.facade.IDataParse;

import java.util.Map;

/**
 * Description: DataParseAdapter
 * Author: DIYILIU
 * Update: 2019-03-21 14:50
 */
public class DataParseAdapter implements IDataParse {


    @Override
    public void detach(DeviceData deviceData) {

    }

    @Override
    public void dealData(DeviceData deviceData, Map param, String type) {

    }

    @Override
    public void dealPosition(String deviceId, Position position) {

    }

    @Override
    public void dealWithTStar(DeviceData deviceData, BaseHandle handle) {


    }

    @Override
    public void sendToDb(String sql, Object... args) {

    }


}
