package com.tiza.plugin.protocol.jt808.cmd;

import com.tiza.plugin.bean.VehicleInfo;
import com.tiza.plugin.model.Header;
import com.tiza.plugin.model.Jt808Header;
import com.tiza.plugin.protocol.hw.model.HwHeader;
import com.tiza.plugin.protocol.jt808.Jt808DataProcess;
import com.tiza.plugin.util.CommonUtil;
import com.tiza.plugin.util.JacksonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Description: Jt808_0900
 * Author: DIYILIU
 * Update: 2017-05-25 15:24
 */

@Slf4j
@Service
public class Jt808_0900 extends Jt808DataProcess {

    public Jt808_0900() {
        this.cmdId = 0x0900;
    }

    @Override
    public void parse(byte[] content, Header header) {
        Jt808Header jt808Header = (Jt808Header) header;
        if (content.length < 9) {
            log.error("数据长度不足: [{}]", CommonUtil.bytesToStr(content));
            return;
        }
        log.info("收到终端[{}]透传信息[{}] ... ", jt808Header.getTerminalId(), CommonUtil.bytesToStr(content));

        String terminalId = jt808Header.getTerminalId();
        HwHeader hwHeader = (HwHeader) hwDataProcess.parseHeader(content);
        if (hwHeader != null) {
            hwHeader.setTerminalId(terminalId);
            hwHeader.setTime(jt808Header.getGwTime());

            hwDataProcess.parse(hwHeader.getContent(), hwHeader);
            Map param = new HashMap();
            if (param != null) {
                // 写入 kafka 准备指令下发
                param.put("id", hwHeader.getCmd());
                param.putAll(hwHeader.getParamMap());

                sendToKafka(jt808Header, param);

                // 更新当前表
                updateVehicleInfo(hwHeader);
            }
        }
    }

    public void updateVehicleInfo(HwHeader hwHeader) {
        VehicleInfo vehicleInfo = (VehicleInfo) vehicleInfoProvider.get(hwHeader.getTerminalId());

        String sql = "SELECT t.WORK_PARAM FROM veh_current_position t WHERE t.VBI_ID=" + vehicleInfo.getId();
        String json = jdbcTemplate.queryForObject(sql, String.class);

        // 工况参数
        Map workMap = new HashMap();
        try {
            if (StringUtils.isNotEmpty(json)) {
                workMap = JacksonUtil.toObject(json, HashMap.class);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 覆盖历史工况信息
        workMap.putAll(hwHeader.getParamMap());

        json = JacksonUtil.toJson(workMap);
        Object[] args = new Object[]{json, new Date(), vehicleInfo.getId()};
        sql = "UPDATE veh_current_position " +
                "SET " +
                " WORK_PARAM = ?," +
                " MODIFY_TIME = ?" +
                "WHERE " +
                "VBI_ID = ?";

        sendToDb(sql, args);
    }
}
