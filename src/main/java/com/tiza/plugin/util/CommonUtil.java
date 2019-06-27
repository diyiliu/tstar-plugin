package com.tiza.plugin.util;

import cn.com.tiza.earth4j.LocationParser;
import cn.com.tiza.earth4j.entry.Location;
import com.tiza.plugin.cache.ICache;
import com.tiza.plugin.model.Gb32960Header;
import com.tiza.plugin.model.Jt808Header;
import com.tiza.plugin.model.Position;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.commons.collections.CollectionUtils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description: CommonUtil
 * Author: DIYILIU
 * Update: 2015-09-17 9:15
 */
public class CommonUtil {


    /**
     * 解析省市区(TStar 工具)
     *
     * @param position
     * @param lng
     * @param lat
     */
    public static void mountPosition(Position position, double lng, double lat) {
        Location location = LocationParser.getInstance().parse(lng, lat);

        if (location != null) {
            position.setProvince(location.getProv());
            position.setCity(location.getCity());
            position.setArea(location.getDistrict());
            position.setProCode(location.getProvCode());
            position.setCityCode(location.getCityCode());
            position.setAreaCode(location.getDistrictCode());
        }
    }

    public static boolean isEmpty(String str) {

        if (str == null || str.trim().length() < 1) {
            return true;
        }

        return false;
    }

    public static byte[] str2Bytes(String str, int length) {
        byte[] bytes = new byte[length];
        byte[] vinArr = str.getBytes();
        int len = vinArr.length > length ? length : vinArr.length;
        System.arraycopy(vinArr, 0, bytes, 0, len);

        return bytes;
    }


    public static byte[] ipToBytes(String host) {

        String[] array = host.split("\\.");

        byte[] bytes = new byte[array.length];

        for (int i = 0; i < array.length; i++) {

            bytes[i] = (byte) Integer.parseInt(array[i]);
        }

        return bytes;
    }

    public static String bytesToIp(byte[] bytes) {

        if (bytes.length == 4) {

            StringBuilder builder = new StringBuilder();

            for (byte b : bytes) {

                builder.append((int) b & 0xff).append(".");
            }

            return builder.substring(0, builder.length() - 1);
        }

        return null;
    }

    public static byte[] dateToBytes(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int year = calendar.get(Calendar.YEAR) - 2000;
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        return new byte[]{(byte) year, (byte) month, (byte) day, (byte) hour, (byte) minute, (byte) second};
    }

    /**
     * 创建时间，修改对应时间
     *
     * @param bytes
     * @return
     */
    public static Date bytesToDate(byte[] bytes) {

        if (bytes.length == 3 || bytes.length == 6) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date(0));
            toDate(calendar, bytes);

            return calendar.getTime();
        }

        return null;
    }

    /**
     * 读取时间
     * 年月日 时分秒
     *
     * @param buf
     * @param length (3或6)
     * @return
     */
    public static Date getBufDate(ByteBuf buf, int length) {
        byte[] dateBytes = new byte[length];
        buf.readBytes(dateBytes);

        return CommonUtil.bytesToDate(dateBytes);
    }

    /**
     * @param date
     * @param bytes
     * @return
     */
    public static Date bytesToDate(Date date, byte[] bytes) {

        if (bytes.length == 3 || bytes.length == 6) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            toDate(calendar, bytes);

            return calendar.getTime();
        }

        return null;
    }


    public static void toDate(Calendar calendar, byte[] bytes) {

        calendar.set(Calendar.YEAR, 2000 + bytes[0]);
        calendar.set(Calendar.MONTH, bytes[1] - 1);
        calendar.set(Calendar.DAY_OF_MONTH, bytes[2]);
        if (bytes.length == 6) {

            calendar.set(Calendar.HOUR_OF_DAY, bytes[3]);
            calendar.set(Calendar.MINUTE, bytes[4]);
            calendar.set(Calendar.SECOND, bytes[5]);

        } else if (bytes.length == 3) {

            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
        }
    }


    public static long bytesToLong(byte[] bytes) {

        long l = 0;
        for (int i = 0; i < bytes.length; i++) {
            l += (long) ((bytes[i] & 0xff) * Math.pow(256, bytes.length - i - 1));
        }
        return l;
    }


    public static byte[] longToBytes(long number, int length) {
        long temp = number;

        byte[] bytes = new byte[length];
        for (int i = bytes.length - 1; i > -1; i--) {

            bytes[i] = new Long(temp & 0xff).byteValue();
            temp = temp >> 8;
        }

        return bytes;
    }

    public static String bytesToStr(byte[] bytes) {
        StringBuffer buf = new StringBuffer();
        for (byte a : bytes) {
            buf.append(String.format("%02X", getNoSin(a)));
        }

        return buf.toString();
    }

    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
        byte[] byte_3 = new byte[byte_1.length + byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }

    public static String bytesToString(byte[] bytes) {
        StringBuffer buf = new StringBuffer();
        for (byte a : bytes) {
            buf.append(String.format("%02X", getNoSin(a))).append(" ");
        }

        return buf.substring(0, buf.length() - 1);
    }

    public static byte[] hexStringToBytes(String hex) {
        hex = hex.replace(" ", "");
        char[] charArray = hex.toCharArray();

        if (charArray.length % 2 != 0) {
            // 无法转义
            return null;
        }

        int length = charArray.length / 2;
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++) {

            String b = new String(new char[]{charArray[i * 2], charArray[i * 2 + 1]});
            bytes[i] = (byte) Integer.parseInt(b, 16);
        }

        return bytes;
    }


    public static String toHex(int i) {

        return String.format("%02X", i);
    }

    public static String toHex(int i, int size) {

        return String.format("%0" + size + "X", i);
    }

    public static int getNoSin(byte b) {
        if (b >= 0) {
            return b;
        } else {
            return 256 + b;
        }
    }

    public static double keepDecimal(double d, int digit) {
        BigDecimal decimal = new BigDecimal(d);
        decimal = decimal.setScale(digit, RoundingMode.HALF_UP);

        return decimal.doubleValue();
    }

    /**
     * 保留小数
     *
     * @param num
     * @param precision
     * @param digit
     * @return
     */
    public static double keepDecimal(Number num, double precision, int digit) {
        BigDecimal decimal = new BigDecimal(String.valueOf(num));
        decimal = decimal.multiply(new BigDecimal(precision)).setScale(digit, BigDecimal.ROUND_HALF_UP);

        return decimal.doubleValue();
    }

    public static String parseBytes(byte[] array, int offset, int length) {
        ByteBuf buf = Unpooled.copiedBuffer(array, offset, length);

        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);

        return new String(bytes);
    }

    public boolean isInnerIp(String address) {
        String ip = address.substring(0, address.indexOf(":"));
        String reg = "(127[.]0[.]0[.]1)|(localhost)|(10[.]\\d{1,3}[.]\\d{1,3}[.]\\d{1,3})|(172[.]((1[6-9])|(2\\d)|(3[01]))[.]\\d{1,3}[.]\\d{1,3})|(192[.]168[.]\\d{1,3}[.]\\d{1,3})";
        Pattern pat = Pattern.compile(reg);
        Matcher mat = pat.matcher(ip);

        return mat.find();
    }

    public static String parseVIN(byte[] array, int offset) {

        ByteBuf buf = Unpooled.copiedBuffer(array);
        buf.readBytes(new byte[offset]);

        int len = buf.readByte();
        byte[] bytes = new byte[len];
        buf.readBytes(bytes);

        return new String(bytes);
    }

    public static byte[] restoreBinary(String content) {

        String[] array = content.split(" ");

        byte[] bytes = new byte[array.length];

        for (int i = 0; i < array.length; i++) {

            bytes[i] = Integer.valueOf(array[i], 16).byteValue();
        }

        return bytes;
    }

    public static String parseSIM(byte[] bytes) {

        Long sim = 0l;
        int len = bytes.length;
        for (int i = 0; i < len; i++) {
            sim += (long) (bytes[i] & 0xff) << ((len - i - 1) * 8);
        }

        return sim.toString();
    }

    public static byte[] packSIM(String sim) {

        byte[] array = new byte[5];
        Long simL = Long.parseLong(sim);

        for (int i = 0; i < array.length; i++) {
            Long l = (simL >> (i * 8)) & 0xff;
            array[array.length - 1 - i] = l.byteValue();
        }
        return array;
    }

    public static String parseIMEI(byte[] bytes) {

        String imei = bytesToStr(bytes);

        return imei.substring(0, 15);
    }

    public static byte[] packIMEI(String imei) {

        if (imei.length() == 15) {
            imei += 0;
        }

        return hexStringToBytes(imei);
    }

    public static byte[] packBCD(String str, int length) {
        if (str.length() < length) {
            str = String.format("%0" + (length - str.length()) + "d", 0) + str;
        }

        byte[] bytes = hexStringToBytes(str);

        return bytes;
    }

    public static long parseBCDTime(byte[] bytes) {
        if (bytes.length != 6) {
            return 0;
        }

        String str = CommonUtil.bytesToStr(bytes);
        if (!pureNumber(str)) {

            return 0;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(0);

        calendar.set(Calendar.YEAR, 2000 + Integer.parseInt(str.substring(0, 2)));
        calendar.set(Calendar.MONTH, Integer.parseInt(str.substring(2, 4)) - 1);
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(str.substring(4, 6)));
        calendar.set(Calendar.HOUR, Integer.parseInt(str.substring(6, 8)));
        calendar.set(Calendar.MINUTE, Integer.parseInt(str.substring(8, 10)));
        calendar.set(Calendar.SECOND, Integer.parseInt(str.substring(10, 12)));

        return calendar.getTimeInMillis();
    }

    public static boolean pureNumber(String str) {
        String regex = ".*[a-zA-Z]+.*";
        Matcher m = Pattern.compile(regex).matcher(str);

        return !m.matches();
    }

    /**
     * 异或校验
     *
     * @param bytes
     * @return
     */
    public static byte getCheck(byte[] bytes) {
        byte b = bytes[0];
        for (int i = 1; i < bytes.length; i++) {
            b ^= bytes[i];
        }

        return b;
    }

    public static byte sumCheck(byte[] bytes) {
        int sum = bytes[0];
        for (int i = 1; i < bytes.length; i++) {
            sum += bytes[i];
        }

        return (byte) (sum & 0xFF);
    }

    public static int renderHeight(byte[] bytes) {

        int plus = bytes[0] & 0x80;

        bytes[0] &= 0x7F;

        if (plus == 0) {
            return (int) bytesToLong(bytes);
        }

        return 0 - (int) bytesToLong(bytes);
    }

    public static int getBits(int val, int start, int len) {
        int left = 31 - start;
        int right = 31 - len + 1;
        return (val << left) >>> right;
    }

    public static byte[] byteToByte(byte[] workParamBytes, int start, int len, String endian) {
        byte[] tempBytes = new byte[len];
        int totalLen = start + len - 1;

        if (endian.equalsIgnoreCase("little")) {
            int tempI = 0;
            for (int j = totalLen; j >= start; j--) {// 小端模式
                tempBytes[tempI] = workParamBytes[j];
                tempI++;
            }
        } else {
            int tempI = 0;
            for (int j = start; j <= totalLen; j++) {// 大端模式
                tempBytes[tempI] = workParamBytes[j];
                tempI++;
            }
        }
        return tempBytes;
    }

    public static int getNosin2int(byte[] array) {
        int res = 0;
        if (array.length == 1) {
            res = getNonSign(array[0]);
        }
        if (array.length == 2) {
            res = getNonSign(array[0]) * 256 + getNonSign(array[1]);
        }

        return res;
    }

    public static int byte2int(byte[] array) {

        if (array.length < 4) {
            return byte2short(array);
        }

        int r = 0;
        for (int i = 0; i < array.length; i++) {
            r <<= 8;
            r |= array[i] & 0xFF;
        }

        return r;
    }

    public static short byte2short(byte[] array) {

        short r = 0;
        for (int i = 0; i < array.length; i++) {
            r <<= 8;
            r |= array[i] & 0xFF;
        }

        return r;
    }

    /**
     * 解析算数表达式
     *
     * @param exp
     * @return
     */
    public static String parseExp(int val, String exp, String type) throws ScriptException {

        ScriptEngineManager factory = new ScriptEngineManager();
        ScriptEngine engine = factory.getEngineByName("JavaScript");

        String retVal;
        if (type.equalsIgnoreCase("hex")) {
            retVal = String.format("%02X", val);
        } else if (type.equalsIgnoreCase("decimal")) {
            retVal = engine.eval(val + exp).toString();
        } else {
            //表达式解析会出现类型问题
            retVal = engine.eval(val + exp).toString();
        }

        return retVal;
    }

    /**
     * 对象转数组
     *
     * @param obj
     * @return
     */
    public static byte[] toByteArray(Object obj) {
        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            bytes = bos.toByteArray();
            oos.close();
            bos.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return bytes;
    }

    /**
     * 数组转对象
     *
     * @param bytes
     * @return
     */
    public static Object toObject(byte[] bytes) {
        Object obj = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bis);
            obj = ois.readObject();
            ois.close();
            bis.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return obj;
    }

    /**
     * 取无符号的byte
     *
     * @param Sign
     * @return
     */
    public static int getNonSign(byte Sign) {
        if (Sign < 0) {
            return (Sign + 256);
        } else {
            return Sign;
        }
    }

    public static String canSystemtime(byte[] bytes) {
        String canSystemtime = null;
        try {
            canSystemtime = String.format("20" + "%02d-%02d-%02d %02d:%02d:%02d", bytes[0], bytes[1], bytes[2], bytes[3], bytes[4], (byte) 0);
        } catch (Exception e) {
        }
        return canSystemtime;
    }

    /**
     * 任意2进制字符串 转int
     *
     * @param BinaryString
     * @return
     */
    public static int BinaryString2int(String BinaryString) {
        int x = 0;
        for (char c : BinaryString.toCharArray())
            x = x * 2 + (c == '1' ? 1 : 0);
        return x;
    }

    /**
     * 0x7d0x02 ————> 0x7e
     * 0x7d0x01 ————> 0x7d
     *
     * @param bytes
     * @return
     */
    public static byte[] decoderJt808Format(byte[] bytes) {
        String hex = CommonUtil.bytesToString(bytes).toUpperCase();
        hex = hex.replaceAll("7D 01", "7D").replaceAll("7D 02", "7E");

        byte[] array = CommonUtil.hexStringToBytes(hex);

        if (array == null) {
            System.err.println("解封装0x7D01,0x7D02异常");
        }

        return array;
    }

    /**
     * 0x7e ————> 0x7d 后紧跟一个0x02
     * 0x7d ————> 0x7d 后紧跟一个0x01
     *
     * @param bytes
     * @return
     */
    public static byte[] encoderJt808Format(byte[] bytes) {
        String hex = CommonUtil.bytesToString(bytes).toUpperCase();
        hex = hex.replaceAll("7D", "7D 01").replaceAll("7E", "7D 02");

        byte[] array = CommonUtil.hexStringToBytes(hex);

        if (array == null) {
            System.err.println("封装0x7D01,0x7D02异常");
        }

        return array;
    }

    public static byte[] packJt808Body(Jt808Header header) {
        byte[] bytes = new byte[2];

        byte temp = (byte) (header.getSplit() & 0x01);
        temp = (byte) ((temp << 3) | (header.getEncrypt() & 0x01));
        bytes[0] = (byte) ((temp << 1) | ((header.getLength() >> 8) & 0x01));
        bytes[1] = (byte) (header.getLength() & 0xFF);

        return bytes;
    }

    public static Jt808Header parseJt808Body(int bodyProperty) {
        int split = (bodyProperty >> 13) & 0x01;
        int encrypt = (bodyProperty >> 10) & 0x07;
        int length = bodyProperty & 0x3FF;

        Jt808Header jt808Header = new Jt808Header();
        jt808Header.setSplit((byte) split);
        jt808Header.setEncrypt(encrypt);
        jt808Header.setLength(length);

        return jt808Header;
    }

    /**
     * 消息头（消息头 + 消息体）
     * （无分包）
     *
     * @param header
     * @return
     */
    public static byte[] jt808HeaderToContent(Jt808Header header) {
        ByteBuf buf = Unpooled.buffer(header.getLength() + 12);
        buf.writeShort(header.getCmd());
        buf.writeBytes(packJt808Body(header));
        buf.writeBytes(CommonUtil.packBCD(header.getTerminalId(), 12));
        buf.writeShort(header.getSerial());
        buf.writeBytes(header.getContent());

        return buf.array();
    }

    /**
     * jt808Header to bytes 下发指令
     *
     * @param header
     * @return
     */
    public static byte[] jt808HeaderToBytes(Jt808Header header) {
        byte[] bytes = jt808HeaderToContent(header);
        byte check = CommonUtil.getCheck(bytes);
        // 添加校验位
        byte[] array = Unpooled.copiedBuffer(bytes, new byte[]{check}).array();
        // 转义
        array = CommonUtil.encoderJt808Format(array);

        // 添加标识位
        return Unpooled.copiedBuffer(new byte[]{0x7E}, array, new byte[]{0x7E}).array();
    }


    /**
     * 生成 jt808 回复指令内容 (不分包)
     *
     * @param terminal 设备ID
     * @param content  需要下行的指令内容
     * @param cmd      需要下行的命令ID
     * @param serial   下行的序列号
     * @return
     */
    public static byte[] jt808Response(String terminal, byte[] content, int cmd, int serial) {
        // 消息体长度
        int length = content.length;

        Jt808Header header = new Jt808Header();
        // 不分包
        header.setSplit((byte) 0);
        // 不加密
        header.setEncrypt(0);
        header.setLength(length);
        header.setTerminalId(terminal);
        header.setContent(content);
        header.setCmd(cmd);
        header.setSerial(serial);

        // 添加标识位
        return CommonUtil.jt808HeaderToBytes(header);
    }

    /**
     * 消息头（消息头 + 消息体）
     *
     * @param header
     * @return
     */
    public static byte[] gb32960HeaderToContent(Gb32960Header header) {
        int length = header.getLength();

        ByteBuf buf = Unpooled.buffer(22 + length);
        buf.writeByte(header.getCmd());
        buf.writeByte(header.getResp());
        buf.writeBytes(header.getVin().getBytes());
        buf.writeByte(0x01);
        buf.writeShort(length);
        buf.writeBytes(header.getContent());

        return buf.array();
    }


    /**
     * gb32960Header to bytes 下发指令
     *
     * @param header
     * @return
     */
    public static byte[] gb32960HeaderToBytes(Gb32960Header header) {
        byte[] bytes = gb32960HeaderToContent(header);
        byte check = CommonUtil.getCheck(bytes);

        return Unpooled.copiedBuffer(new byte[]{0x23, 0x23}, bytes, new byte[]{check}).array();
    }


    /**
     * 生成 gb32960 指令内容
     *
     * @param vin     设备 vin
     * @param content 需要下行的指令内容
     * @param cmd     需要下行的命令ID
     * @return
     */
    public static byte[] gb32960Response(String vin, byte[] content, int cmd, boolean resp) {
        // 消息体长度
        int length = content.length;

        Gb32960Header header = new Gb32960Header();
        header.setResp(resp ? 0x01 : 0xFE);
        header.setLength(length);
        header.setVin(vin);
        header.setContent(content);
        header.setCmd(cmd);

        // 添加标识位
        return CommonUtil.gb32960HeaderToBytes(header);
    }

    /**
     * 命令序号
     **/
    private static AtomicLong msgSerial = new AtomicLong(0);

    public static int getMsgSerial() {
        Long serial = msgSerial.incrementAndGet();
        if (serial > 65535) {
            msgSerial.set(0);
            serial = msgSerial.incrementAndGet();
        }

        return serial.intValue();
    }

    /**
     * 更新缓存
     *
     * @param oldKeys
     * @param tempKeys
     * @param itemCache
     */
    public static void refreshCache(Set oldKeys, Set tempKeys, ICache itemCache) {
        Collection subKeys = CollectionUtils.subtract(oldKeys, tempKeys);
        for (Iterator iterator = subKeys.iterator(); iterator.hasNext(); ) {
            itemCache.remove(iterator.next());
        }
    }


    /**
     * gb32960 参数设置长度
     *
     * @param id
     * @return
     */
    public static byte[] gb32960SetParam(int id, String value) {
        if (0x01 == id || 0x02 == id || 0x03 == id ||
                0x06 == id || 0x0A == id || 0x0B == id || 0x0F == id) {

            return Unpooled.copiedBuffer(new byte[]{(byte) id}, CommonUtil.longToBytes(Long.valueOf(value), 2)).array();
        }

        if (0x09 == id || 0x0C == id || 0x10 == id) {

            return new byte[]{(byte) id, Byte.valueOf(value)};
        }

        if (0x07 == id || 0x08 == id) {
            byte[] bytes = new byte[6];
            bytes[0] = (byte) id;

            byte[] src = value.getBytes();
            System.arraycopy(src, 0, bytes, 1, src.length);

            return bytes;
        }

        if (0x05 == id || 0x0E == id) {
            byte[] bytes = value.getBytes();
            int length = bytes.length;

            return Unpooled.copiedBuffer(new byte[]{(byte) (id - 1), (byte) length, (byte) id}, bytes).array();
        }

        return new byte[0];
    }

    /**
     * 添加前导0
     *
     * @param str
     * @param length
     * @return
     */
    public static String addPrefixZero(String str, int length) {
        int strLen = str.length();
        if (strLen < length) {
            String zero = String.format("%0" + (length - str.length()) + "d", 0);

            return zero + str;
        }

        return str;
    }

    public static <T> int convertInt(T t) {
        if (t == null) {
            return 0;
        }

        return Integer.parseInt(String.valueOf(t));
    }

    /**
     * 字节转二进制字符串
     *
     * @param b
     * @return
     */
    public static String byte2BinaryStr(byte b) {
        StringBuffer strBuf = new StringBuffer();
        for (int i = 0; i < 8; i++) {
            int value = (b >> i) & 0x01;
            strBuf.append(value);
        }

        return strBuf.toString();
    }

    public static String bytes2BinaryStr(byte[] bytes) {
        StringBuffer strBuf = new StringBuffer();
        for (byte b : bytes) {
            strBuf.append(byte2BinaryStr(b));
        }
        return strBuf.toString();
    }

    public static byte[] tstarKafkaArray(String terminal, int cmd, String str, long time, int serial) {
        byte[] terminalArr = terminal.getBytes();
        int tLen = terminalArr.length;

        byte[] msgArr = str.getBytes(Charset.forName("UTF-8"));
        int mLen = msgArr.length;

        ByteBuf buf = Unpooled.buffer(21 + tLen + mLen);
        buf.writeByte(tLen);
        buf.writeBytes(terminalArr);
        buf.writeLong(time);
        buf.writeInt(cmd);
        buf.writeInt(serial);
        buf.writeInt(mLen);
        buf.writeBytes(msgArr);

        return buf.array();
    }
}
