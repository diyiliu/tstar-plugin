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

    /** 设备类型 2: 垃圾桶; 3: 垃圾袋; **/
    private Integer vehType;

    /** 协议类型 **/
    private String protocol;

    private String workParam;

    private String iccid;

    private String license;

    /** 最新时间 **/
    private Long datetime;

    private Integer status;

    private String owner;

    private String ownerName;
}
