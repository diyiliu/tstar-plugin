package com.tiza.plugin.protocol.gb32960;

import com.tiza.plugin.cache.ICache;
import com.tiza.plugin.model.Gb32960Header;
import com.tiza.plugin.model.Header;
import com.tiza.plugin.model.facade.IDataParse;
import com.tiza.plugin.model.facade.IDataProcess;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Description: Gb32960DataProcess
 * Author: DIYILIU
 * Update: 2018-12-06 10:25
 */

@Slf4j
@Service
public class Gb32960DataProcess implements IDataProcess {
    protected int cmdId = 0xFF;

    @Resource
    protected ICache cmdCacheProvider;

    @Resource
    protected IDataParse dataParse;

    @Override
    public Header parseHeader(byte[] bytes) {
        ByteBuf buf = Unpooled.copiedBuffer(bytes);
        // 读取头标志[0x23,0x23]
        buf.readBytes(new byte[2]);

        // 命令标识
        int cmd = buf.readUnsignedByte();
        // 应答标识
        int resp = buf.readUnsignedByte();

        byte[] vinBytes = new byte[17];
        buf.readBytes(vinBytes);
        String vin = new String(vinBytes);

        // 加密方式
        buf.readByte();
        int length = buf.readUnsignedShort();
        byte[] content = new byte[length];
        buf.readBytes(content);

        Gb32960Header header = new Gb32960Header();
        header.setCmd(cmd);
        header.setResp(resp);
        header.setVin(vin);
        header.setContent(content);

        return header;
    }

    @Override
    public void parse(byte[] content, Header header) {

    }

    @Override
    public byte[] pack(Header header, Object... argus) {
        return new byte[0];
    }

    @Override
    public void init() {
        cmdCacheProvider.put(cmdId, this);
    }
}
