package com.tiza.plugin.protocol.jt808.cmd;

import com.tiza.plugin.model.Header;
import com.tiza.plugin.model.Jt808Header;
import com.tiza.plugin.protocol.jt808.Jt808DataProcess;
import com.tiza.plugin.util.CommonUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Description: Jt808_0001
 * Author: DIYILIU
 * Update: 2017-05-25 15:24
 */

@Slf4j
@Service
public class Jt808_0001 extends Jt808DataProcess {

    public Jt808_0001() {
        this.cmdId = 0x0001;
    }

    @Override
    public void parse(byte[] content, Header header) {
        Jt808Header jt808Header = (Jt808Header) header;
        // 关键字
        int key = jt808Header.getKey();

        ByteBuf buf = Unpooled.copiedBuffer(content);

        int replySerial = buf.readUnsignedShort();
        int replyCmd = buf.readUnsignedShort();

        byte result = buf.readByte();

        log.info("终端应答[{}, {}]，应答结果[{}]", CommonUtil.toHex(replyCmd, 4), replySerial, result);
    }
}
