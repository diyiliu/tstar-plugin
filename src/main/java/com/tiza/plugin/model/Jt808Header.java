package com.tiza.plugin.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Description: Jt808Header
 * Author: DIYILIU
 * Update: 2017-05-25 14:08
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class Jt808Header extends Header {

    private int cmd;
    private int length;
    private int encrypt;
    private byte split;
    private String terminalId;
    private int serial;
    private byte[] content = null;
    private byte check;
    private int packageCount;

    // 网关时间
    private long gwTime;
}
