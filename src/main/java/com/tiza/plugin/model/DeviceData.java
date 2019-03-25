package com.tiza.plugin.model;

import lombok.Data;

/**
 * Description: DeviceData
 * Author: DIYILIU
 * Update: 2019-03-22 10:18
 */

@Data
public class DeviceData {

    private String deviceId;

    private Integer cmdId;

    private Long time;

    private byte[] bytes = new byte[0];

    private String dataType;

    private Object dataBody;
}
