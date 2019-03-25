package com.tiza.plugin.bean;

import lombok.Data;

/**
 * Description: VehicleInfo
 * Author: DIYILIU
 * Update: 2018-12-13 10:45
 */

@Data
public class VehicleInfo {

    private Long id;

    private String terminalId;

    private Integer vehType;

    private String workParam;

    private String iccid;

    private String license;

    /** 最新时间 **/
    private Long datetime;

    private Integer status;
}
