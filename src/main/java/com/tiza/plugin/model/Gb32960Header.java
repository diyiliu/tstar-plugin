package com.tiza.plugin.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Description: Gb32960Header
 * Author: DIYILIU
 * Update: 2019-03-18 16:48
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class Gb32960Header extends Header{

    private int cmd;
    private int resp;
    private int length;
    private String vin;
    private String terminalId;
    private int serial;
    private byte[] content = null;
    private int check;

    private long gwTime;
}
