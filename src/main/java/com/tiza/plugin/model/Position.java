package com.tiza.plugin.model;

import lombok.Data;

/**
 * Description: Position
 * Author: DIYILIU
 * Update: 2018-12-19 10:45
 */

@Data
public class Position {
    private Long time;

    private Double lng;
    private Double lat;

    private Double enLng;
    private Double enLat;

    private String address;
    private String province;
    private String city;
    private String area;

    private String proCode;
    private String cityCode;
    private String areaCode;

    private Integer status;

    private Double speed;
}
