package com.tiza.plugin.model;

import java.util.Map;

/**
 * Description: IDataParse
 * Author: DIYILIU
 * Update: 2018-12-19 10:11
 */
public interface IDataParse {

    void detach(Header header, byte[] bytes);

    void sendToKafka(Header header, Map param);

    void sendToDb(String sql, Object... args);

    void dealPosition(Header header, Position position);
}
