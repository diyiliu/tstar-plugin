package com.tiza.plugin.model.facade;

import com.tiza.plugin.model.DeviceData;
import com.tiza.plugin.model.Position;

import java.util.Map;

/**
 * Description: IDataParse
 * Author: DIYILIU
 * Update: 2018-12-19 10:11
 */
public interface IDataParse {

    void detach(DeviceData deviceData);

    void dealData(DeviceData deviceData, Map param, String type);

    void dealPosition(String deviceId, Position position);

    void sendToDb(String sql, Object... args);
}
