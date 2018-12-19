package com.tiza.plugin.model;

import lombok.Data;

/**
 * Description: Position
 * Author: DIYILIU
 * Update: 2018-12-19 10:45
 */

@Data
public class Position {

    private long time;

    private double lng;
    private double lat;

    private String province;
    private String proCode;

    private String city;
    private String cityCode;

    private String area;
    private String areaCode;
}
