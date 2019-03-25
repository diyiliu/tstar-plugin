package com.tiza.plugin.model.facade;

import cn.com.tiza.tstar.common.process.BaseHandle;
import com.tiza.plugin.model.DeviceData;
import com.tiza.plugin.model.Position;

/**
 * Description: IDataParse
 * Author: DIYILIU
 * Update: 2018-12-19 10:11
 */
public interface IDataParse {

    void detach(DeviceData deviceData);

    void dealPosition(String deviceId, Position position);

    void dealWithTStar(DeviceData deviceData, BaseHandle handle);

    void sendToDb(String sql, Object... args);
}
