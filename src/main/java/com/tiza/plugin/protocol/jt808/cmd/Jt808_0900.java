package com.tiza.plugin.protocol.jt808.cmd;

import com.tiza.plugin.model.Header;
import com.tiza.plugin.model.Jt808Header;
import com.tiza.plugin.protocol.hw.model.HwHeader;
import com.tiza.plugin.protocol.jt808.Jt808DataProcess;
import com.tiza.plugin.util.CommonUtil;
import com.tiza.plugin.util.JacksonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

        HwHeader hwHeader = (HwHeader) hwDataProcess.parseHeader(content);
        if (hwHeader == null){

            return;
        }
        hwDataProcess.parse(hwHeader.getContent(), hwHeader);
        Map param = hwHeader.getParamMap();
        param.put("id", hwHeader.getCmd());

        // 写入 kafka 准备指令下发
        sendToKafka(jt808Header, param);
    }
}
