package com.tiza.plugin.protocol.gb32960.cmd;

import com.tiza.plugin.model.DeviceData;
import com.tiza.plugin.model.Gb32960Header;
import com.tiza.plugin.model.Header;
import com.tiza.plugin.model.Position;
import com.tiza.plugin.protocol.gb32960.Gb32960DataProcess;
import com.tiza.plugin.util.CommonUtil;
import com.tiza.plugin.util.GpsCorrectUtil;
import com.tiza.plugin.util.JacksonUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 实时信息上报
 * Description: Gb32960_02
 * Author: DIYILIU
 * Update: 2019-03-18 16:57
 */

@Slf4j
@Service
public class Gb32960_02 extends Gb32960DataProcess {

    public Gb32960_02() {
        this.cmdId = 0x02;
    }

    @Override
    public void parse(byte[] content, Header header) {
        Gb32960Header gb32960Header = (Gb32960Header) header;
        ByteBuf buf = Unpooled.copiedBuffer(content);

        // 数据采集时间
        Date date = CommonUtil.getBufDate(buf, 6);
        gb32960Header.setDataTime(date.getTime());

        List paramValues = new ArrayList();
        Map map = new HashMap();
        map.put("SystemTime", new Date());
        map.put("GpsTime", date);

        paramValues.add(map);

        Map realMode = new HashMap();
        // 中断标识
        boolean interrupt = false;
        try {
            while (buf.readableBytes() > 0) {
                int flag = buf.readUnsignedByte();
                switch (flag) {

                    case 0x01:

                        interrupt = parseVehicle(buf, paramValues, realMode);
                        break;
                    case 0x02:

                        interrupt = parseMotor(buf, paramValues);
                        break;
                    case 0x03:

                        interrupt = parseBattery(buf, paramValues);
                        break;
                    case 0x04:

                        interrupt = parseEngine(buf, paramValues);
                        break;
                    case 0x05:

                        interrupt = parsePosition(buf, paramValues);
                        break;
                    case 0x06:

                        interrupt = parseExtreme(buf, paramValues);
                        break;
                    case 0x07:

                        interrupt = parseAlarm(buf, paramValues, realMode, date);
                        break;

                    case 0x08:

                        interrupt = parseStorageVoltage(buf, paramValues);
                        break;
                    case 0x09:

                        interrupt = parseStorageTemp(buf, paramValues);
                        break;
                    case 0xAA:

                        interrupt = passThrough(buf, paramValues);
                        break;
                    default:
                        if (buf.readableBytes() > 2) {
                            int length = buf.readUnsignedShort();
                            if (buf.readableBytes() < length) {
                                interrupt = true;
                                break;
                            }
                            buf.readBytes(new byte[length]);
                        }
                        break;

                }
                if (interrupt) {
                    log.info("终端[{}]指令cmd[{}], 解析中断错误[{}]!", gb32960Header.getVin(), CommonUtil.toHex(flag), CommonUtil.bytesToStr(content));
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 舍弃空包
        if (paramValues.size() < 1 && !paramValues.contains("position")) {
            log.info("终端[{}]不处理空包数据[{}]!", gb32960Header.getVin(), JacksonUtil.toJson(paramValues));
            return;
        }


        // 车辆实时状态 (忽略补发数据 0x03)
        if (MapUtils.isNotEmpty(realMode) && 0x02 == gb32960Header.getCmd()) {
            dataParse.detach(buildData(gb32960Header, "realMode", realMode));
        }

        // 车辆工况数据处理
        DeviceData deviceData = buildData(gb32960Header);
        deviceData.setDataBody(paramValues);
        dataParse.dealWithTStar(deviceData, getTstarHandle());
    }

    /**
     * 整车数据
     *
     * @param byteBuf
     * @return
     */
    private boolean parseVehicle(ByteBuf byteBuf, List paramValues, Map realMode) {
        if (byteBuf.readableBytes() < 20) {

            return true;
        }

        int vehStatus = byteBuf.readUnsignedByte();
        if (0x01 == vehStatus || 0x02 == vehStatus) {
            realMode.put("onOff", vehStatus);
        }
        int charge = byteBuf.readUnsignedByte();
        if (0x01 == charge || 0x04 == charge) {
            realMode.put("topOff", charge);
        }
        int runMode = byteBuf.readUnsignedByte();

        int speed = byteBuf.readUnsignedShort();
        long mile = byteBuf.readUnsignedInt();

        int voltage = byteBuf.readUnsignedShort();
        int electricity = byteBuf.readUnsignedShort();

        int soc = byteBuf.readUnsignedByte();
        int dcStatus = byteBuf.readUnsignedByte();

        int gears = byteBuf.readUnsignedByte() & 0x0F;
        int ohm = byteBuf.readUnsignedShort();
        byteBuf.readShort();


        Map map = new HashMap();
        map.put("VehicleStatus", vehStatus);
        map.put("ChargeStatus", charge);
        map.put("DrivingMode", runMode);

        // 速度
        if (0xFFFF == speed) {

            map.put("SpeedStatus", 255);
        } else if (0xFFFE == speed) {

            map.put("SpeedStatus", 254);
        } else {

            map.put("SpeedStatus", 1);
            map.put("Speed", CommonUtil.keepDecimal(speed, 0.1, 1));
        }

        // 里程
        if (0xFFFFFFFFl == mile) {

            map.put("ODOStatus", 255);
        } else if (0xFFFFFFFEl == mile) {

            map.put("ODOStatus", 254);
        } else {

            map.put("ODOStatus", 1);
            map.put("ODO", CommonUtil.keepDecimal(mile, 0.1, 1));
        }

        // 电压
        if (0xFFFF == voltage) {

            map.put("VoltageStatus", 255);
        } else if (0xFFFE == voltage) {

            map.put("VoltageStatus", 254);
        } else {

            map.put("VoltageStatus", 1);
            map.put("Voltage", CommonUtil.keepDecimal(voltage, 0.1, 1));
        }

        // 电流
        if (0xFFFF == electricity) {

            map.put("AmpStatus", 255);
        } else if (0xFFFE == electricity) {

            map.put("AmpStatus", 254);
        } else {

            map.put("AmpStatus", 1);
            map.put("Amp", CommonUtil.keepDecimal(electricity, 0.1, 1) - 1000);
        }

        // SOC
        if (0xFF == soc || 0xFE == soc) {

            map.put("SOCStatus", soc);
        } else {

            map.put("SOCStatus", 1);
            map.put("SOC", soc);
        }

        // DC-DC
        map.put("DCDC", dcStatus);

        map.put("Gears", gears);

        map.put("Resistance", ohm);

        paramValues.add(map);

        return false;
    }

    /**
     * 驱动电机数据
     *
     * @param byteBuf
     * @return
     */
    private boolean parseMotor(ByteBuf byteBuf, List paramValues) {

        int count = byteBuf.readUnsignedByte();
        if (count > 253 || byteBuf.readableBytes() < count * 12) {

            return true;
        }

        List list = new ArrayList();
        for (int i = 0; i < count; i++) {

            int serial = byteBuf.readUnsignedByte();
            int status = byteBuf.readUnsignedByte();

            int controlTemp = byteBuf.readUnsignedByte();

            int rpm = byteBuf.readUnsignedShort();
            int torque = byteBuf.readUnsignedShort();

            int temp = byteBuf.readUnsignedByte();
            int voltage = byteBuf.readUnsignedShort();
            int electricity = byteBuf.readUnsignedShort();

            Map m = new HashMap();
            m.put("sn", serial);
            m.put("status", status);

            if (0xFE == controlTemp || 0xFF == controlTemp) {
                m.put("cuTemp", controlTemp);
            } else {
                m.put("cuTemp", controlTemp - 40);
            }

            if (0xFFFE == rpm || 0xFFFF == rpm) {
                m.put("rpm", rpm);
            } else {
                m.put("rpm", rpm - 20000);
            }

            if (0xFFFE == torque || 0xFFFF == torque) {
                m.put("torque", torque);
            } else {
                m.put("torque", CommonUtil.keepDecimal(torque - 20000, 0.1, 1));
            }

            if (0xFE == temp || 0xFF == temp) {
                m.put("temp", temp);
            } else {
                m.put("temp", temp - 40);
            }

            if (0xFFFE == voltage || 0xFFFF == voltage) {
                m.put("cuVoltage", voltage);
            } else {
                m.put("cuVoltage", CommonUtil.keepDecimal(voltage, 0.1, 1));
            }

            if (0xFFFE == electricity || 0xFFFF == electricity) {
                m.put("dcBusCurrent", electricity);
            } else {
                m.put("dcBusCurrent", CommonUtil.keepDecimal(electricity, 0.1, 1) - 1000);
            }

            list.add(m);
        }
        Map map = new HashMap();
        map.put("MOTORNUMBER", count);
        map.put("MOTORSINFO", list);
        paramValues.add(map);

        return false;
    }

    /**
     * 燃料电池数据
     *
     * @param byteBuf
     * @return
     */
    private boolean parseBattery(ByteBuf byteBuf, List paramValues) {

        if (byteBuf.readableBytes() < 8) {

            return true;
        }
        Map map = new HashMap();

        int voltage = byteBuf.readUnsignedShort();
        if (0xFFFE == voltage) {

            map.put("BATTERYVOLTAGESTATUS", 254);
        } else if (0xFFFF == voltage) {

            map.put("BATTERYVOLTAGESTATUS", 255);
        } else {

            map.put("BATTERYVOLTAGESTATUS", 1);
            map.put("BATTERYVOLTAGE", CommonUtil.keepDecimal(voltage, 0.1, 1));
        }

        int electricity = byteBuf.readUnsignedShort();
        if (0xFFFE == electricity) {

            map.put("BATTERYAMPSTATUS", 254);
        } else if (0xFFFF == electricity) {

            map.put("BATTERYAMPSTATUS", 255);
        } else {

            map.put("BATTERYAMPSTATUS", 1);
            map.put("BATTERYAMP", CommonUtil.keepDecimal(electricity, 0.1, 1));
        }

        int drain = byteBuf.readUnsignedShort();
        if (0xFFFE == drain) {

            map.put("BATTERYFUELCONSUMESTATUS", 254);
        } else if (0xFFFF == drain) {

            map.put("BATTERYFUELCONSUMESTATUS", 255);
        } else {

            map.put("BATTERYFUELCONSUMESTATUS", 1);
            map.put("BATTERYFUELCONSUME", CommonUtil.keepDecimal(drain, 0.01, 2));
        }

        int count = byteBuf.readUnsignedShort();
        // 数量无效
        if (0xFFFE == count || 0xFFFF == count) {

            return false;
        }

        if (byteBuf.readableBytes() < count * 1 + 10) {

            return true;
        }

        int sum = 0;
        for (int i = 0; i < count; i++) {

            sum += byteBuf.readUnsignedByte() - 40;
        }

        // 平均温度
        int avgTem = count > 0 ? sum / count : 0;
        map.put("BATTERYTEMP", avgTem);

        int maxTemp = byteBuf.readUnsignedShort();
        if (0xFFFE == maxTemp) {

            map.put("H2MAXTEMPSTATUS", 254);
        } else if (0xFFFF == maxTemp) {

            map.put("H2MAXTEMPSTATUS", 255);
        } else {

            map.put("H2MAXTEMPSTATUS", 1);
            map.put("H2MAXTEMP", CommonUtil.keepDecimal(maxTemp, 0.1, 1) - 40);
        }

        int tempNumber = byteBuf.readUnsignedByte();
        if (0xFE == tempNumber || 0xFF == tempNumber) {
            map.put("H2MAXTEMPSENSORSTATUS", tempNumber);
        } else {
            map.put("H2MAXTEMPSENSORSTATUS", 1);
            map.put("H2MAXTEMPSENSOR", tempNumber);
        }

        int maxPPM = byteBuf.readUnsignedShort();
        if (0xFFFE == maxPPM) {

            map.put("H2MAXDENSITYSTATUS", 254);
        } else if (0xFFFF == maxPPM) {

            map.put("H2MAXDENSITYSTATUS", 255);
        } else {

            map.put("H2MAXDENSITYSTATUS", 1);
            map.put("H2MAXDENSITY", maxPPM);
        }

        int ppmNumber = byteBuf.readUnsignedByte();
        if (0xFE == ppmNumber || 0xFF == ppmNumber) {
            map.put("H2MAXDENSITYSENSORSTATUS", ppmNumber);
        } else {
            map.put("H2MAXDENSITYSENSORSTATUS", 1);
            map.put("H2MAXDENSITYSENSOR", ppmNumber);
        }

        int maxPressure = byteBuf.readShort();
        if (0xFFFE == maxPressure) {

            map.put("H2MAXPRESSURESTATUS", 254);
        } else if (0xFFFF == maxPressure) {

            map.put("H2MAXPRESSURESTATUS", 255);
        } else {

            map.put("H2MAXPRESSURESTATUS", 1);
            map.put("H2MAXPRESSURE", CommonUtil.keepDecimal(maxPressure, 0.1, 1));
        }

        int pressureNumber = byteBuf.readUnsignedByte();
        if (0xFE == pressureNumber || 0xFF == pressureNumber) {
            map.put("H2MAXPRESSURESENSORSTATUS", pressureNumber);
        } else {
            map.put("H2MAXPRESSURESENSORSTATUS", 1);
            map.put("H2MAXPRESSURESENSOR", pressureNumber);
        }

        int dcStatus = byteBuf.readUnsignedByte();
        map.put("HIGHPRESSUREDCDC", dcStatus);

        paramValues.add(map);

        return false;
    }

    /**
     * 发动机数据
     *
     * @param byteBuf
     * @return
     */
    private boolean parseEngine(ByteBuf byteBuf, List paramValues) {

        if (byteBuf.readableBytes() < 5) {

            return true;
        }

        int status = byteBuf.readUnsignedByte();
        int speed = byteBuf.readUnsignedShort();
        int drain = byteBuf.readUnsignedShort();

        Map map = new HashMap();
        map.put("ENGINESTATUS", status);

        if (0xFFFE == speed) {
            map.put("ENGINERPMSTATUS", 254);
        } else if (0xFFFF == speed) {
            map.put("ENGINERPMSTATUS", 255);
        } else {
            map.put("ENGINERPMSTATUS", 1);
            map.put("ENGINERPM", speed);
        }

        if (0xFFFE == drain) {
            map.put("ENGINEFUELCONSUMESTATUS", 254);
        } else if (0xFFFF == drain) {
            map.put("ENGINEFUELCONSUMESTATUS", 255);
        } else {
            map.put("ENGINEFUELCONSUMESTATUS", 1);
            map.put("ENGINEFUELCONSUME", CommonUtil.keepDecimal(drain, 0.01, 2));
        }

        paramValues.add(map);
        return false;
    }

    /**
     * 车辆位置数据
     *
     * @param byteBuf
     * @return
     */
    private boolean parsePosition(ByteBuf byteBuf, List paramValues) {
        if (byteBuf.readableBytes() < 9) {

            return true;
        }
        int status = byteBuf.readByte();

        //0:有效;1:无效
        int effective = status & 0x01;
        int latDir = status & 0x02;
        int lngDir = status & 0x04;

        long lng = byteBuf.readUnsignedInt();
        long lat = byteBuf.readUnsignedInt();

        Position position = new Position();
        position.setStatus(effective);
        position.setLng(CommonUtil.keepDecimal(lng * (lngDir == 0 ? 1 : -1), 0.000001, 6));
        position.setLat(CommonUtil.keepDecimal(lat * (latDir == 0 ? 1 : -1), 0.000001, 6));

        double[] enLatLng = GpsCorrectUtil.transform(position.getLat(), position.getLng());
        position.setEnLat(enLatLng[0]);
        position.setEnLng(enLatLng[1]);

        // 逆地址解析
        CommonUtil.mountPosition(position, position.getEnLng(), position.getEnLat());

        Map map = new HashMap();
        map.put("position", position);
        paramValues.add(map);

        return false;
    }

    /**
     * 极值数据
     *
     * @param byteBuf
     * @return
     */
    private boolean parseExtreme(ByteBuf byteBuf, List paramValues) {
        if (byteBuf.readableBytes() < 14) {

            return true;
        }

        Map map = new HashMap();
        // 最高电压
        int maxVoltageSysNo = byteBuf.readUnsignedByte();
        if (0xFF == maxVoltageSysNo || 0xFE == maxVoltageSysNo) {
            map.put("MaxVoltageBatterySubSysStatus", maxVoltageSysNo);
        } else {
            map.put("MaxVoltageBatterySubSysStatus", 1);
            map.put("MaxVoltageBatterySubSys", maxVoltageSysNo);
        }
        int maxVoltageCellNo = byteBuf.readUnsignedByte();
        if (0xFF == maxVoltageCellNo || 0xFE == maxVoltageCellNo) {
            map.put("MaxVoltageBatteryUnitStatus", maxVoltageCellNo);
        } else {
            map.put("MaxVoltageBatteryUnitStatus", 1);
            map.put("MaxVoltageBatteryUnit", maxVoltageCellNo);
        }
        int maxVoltageValue = byteBuf.readUnsignedShort();
        if (0xFF == maxVoltageValue || 0xFE == maxVoltageValue) {
            map.put("BatteryUnitMaxVoltageStatus", maxVoltageValue);
        } else {
            map.put("BatteryUnitMaxVoltageStatus", 1);
            map.put("BatteryUnitMaxVoltage", CommonUtil.keepDecimal(maxVoltageValue, 0.001, 3));
        }

        // 最低电压
        int minVoltageSysNo = byteBuf.readUnsignedByte();
        if (0xFF == minVoltageSysNo || 0xFE == minVoltageSysNo) {
            map.put("MinVoltageBatterySubSysStatus", minVoltageSysNo);
        } else {
            map.put("MinVoltageBatterySubSysStatus", 1);
            map.put("MinVoltageBatterySubSys", minVoltageSysNo);
        }
        int minVoltageCellNo = byteBuf.readUnsignedByte();
        if (0xFF == minVoltageCellNo || 0xFE == minVoltageCellNo) {
            map.put("MinVoltageBatteryUnitStatus", minVoltageCellNo);
        } else {
            map.put("MinVoltageBatteryUnitStatus", 1);
            map.put("MinVoltageBatteryUnit", minVoltageCellNo);
        }
        int minVoltageValue = byteBuf.readUnsignedShort();
        if (0xFF == minVoltageValue || 0xFE == minVoltageValue) {
            map.put("BatteryUnitMinVoltageStatus", minVoltageValue);
        } else {
            map.put("BatteryUnitMinVoltageStatus", 1);
            map.put("BatteryUnitMinVoltage", CommonUtil.keepDecimal(minVoltageValue, 0.001, 3));
        }

        // 最高温度
        int maxTempSysNo = byteBuf.readUnsignedByte();
        if (0xFF == maxTempSysNo || 0xFE == maxTempSysNo) {
            map.put("MaxTempBatterySubSysStatus", maxTempSysNo);
        } else {
            map.put("MaxTempBatterySubSysStatus", 1);
            map.put("MaxTempBatterySubSys", maxTempSysNo);
        }
        int maxTempCellNo = byteBuf.readUnsignedByte();
        if (0xFF == maxTempCellNo || 0xFE == maxTempCellNo) {
            map.put("MaxTempBatterySensorStatus", maxTempCellNo);
        } else {
            map.put("MaxTempBatterySensorStatus", 1);
            map.put("MaxTempBatterySensor", maxTempCellNo);
        }
        int maxTempValue = byteBuf.readUnsignedByte();
        if (0xFF == maxTempValue || 0xFE == maxTempValue) {
            map.put("BatteryMaxTempStatus", maxTempValue);
        } else {
            map.put("BatteryMaxTempStatus", 1);
            map.put("BatteryMaxTemp", maxTempValue - 40);
        }

        // 最低温度
        int minTempSysNo = byteBuf.readUnsignedByte();
        if (0xFF == minTempSysNo || 0xFE == minTempSysNo) {
            map.put("MinTempBatterySubSysStatus", minTempSysNo);
        } else {
            map.put("MinTempBatterySubSysStatus", 1);
            map.put("MinTempBatterySubSys", minTempSysNo);
        }
        int minTempCellNo = byteBuf.readUnsignedByte();
        if (0xFF == minTempCellNo || 0xFE == minTempCellNo) {
            map.put("MinTempBatterySensorStatus", minTempCellNo);
        } else {
            map.put("MinTempBatterySensorStatus", 1);
            map.put("MinTempBatterySensor", minTempCellNo);
        }
        int minTempValue = byteBuf.readUnsignedByte();
        if (0xFF == minTempValue || 0xFE == minTempValue) {
            map.put("BatteryMinTempStatus", minTempValue);
        } else {
            map.put("BatteryMinTempStatus", 1);
            map.put("BatteryMinTemp", minTempValue - 40);
        }

        paramValues.add(map);
        return false;
    }

    /**
     * 报警数据
     *
     * @param byteBuf
     * @return
     */
    private boolean parseAlarm(ByteBuf byteBuf, List paramValues, Map realMode, Date date) {
        int level = byteBuf.readUnsignedByte();
        long flag = byteBuf.readUnsignedInt();

        Map alarm = new HashMap();
        alarm.put("AlarmLevel", level);
        // 有效报警值[0, 3]
        if (level > -1 && level < 4) {
            alarm.put("AlarmTime", date);
            if (level > 0) {
                realMode.put("alarmLevel", level);
            }
        }
        paramValues.add(alarm);

        Map commonAlarm = toCommonAlarm(flag);
        paramValues.add(commonAlarm);

        Map<String, List<Long>> faultMap = new HashMap();
        int chargeFault = byteBuf.readUnsignedByte();
        if (0xFE != chargeFault && 0xFF != chargeFault && chargeFault > 0) {
            if (byteBuf.readableBytes() < chargeFault * 4 + 3) {
                return true;
            }
            List list = new ArrayList();
            for (int i = 0; i < chargeFault; i++) {
                long l = byteBuf.readUnsignedInt();
                list.add(l);
            }
            faultMap.put("1", list);
        }

        int motorFault = byteBuf.readUnsignedByte();
        if (0xFE != motorFault && 0xFF != motorFault && motorFault > 0) {
            if (byteBuf.readableBytes() < chargeFault * 4 + 2) {
                return true;
            }
            List list = new ArrayList();
            for (int i = 0; i < motorFault; i++) {
                long l = byteBuf.readUnsignedInt();
                list.add(l);
            }
            faultMap.put("2", list);
        }

        int engineFault = byteBuf.readUnsignedByte();
        if (0xFE != engineFault && 0xFF != engineFault && engineFault > 0) {
            if (byteBuf.readableBytes() < chargeFault * 4 + 1) {
                return true;
            }
            List list = new ArrayList();
            for (int i = 0; i < engineFault; i++) {

                long l = byteBuf.readUnsignedInt();
                list.add(l);
            }
            faultMap.put("3", list);
        }

        int otherFault = byteBuf.readUnsignedByte();
        if (0xFE != otherFault && 0xFF != otherFault && otherFault > 0) {
            if (byteBuf.readableBytes() < chargeFault * 4) {
                return true;
            }
            List list = new ArrayList();
            for (int i = 0; i < otherFault; i++) {

                long l = byteBuf.readUnsignedInt();
                list.add(l);
            }
            faultMap.put("4", list);
        }

        return false;
    }


    /**
     * 可充电储能电压数据
     *
     * @param byteBuf
     * @return
     */
    private boolean parseStorageVoltage(ByteBuf byteBuf, List paramValues) {
        Map map = new HashMap();
        paramValues.add(map);

        int count = byteBuf.readUnsignedByte();
        if (0xFE == count || 0xFF == count) {
            map.put("BATTERYVOLTAGENUMSTATUS", count);

            return false;
        }
        map.put("BATTERYVOLTAGENUMSTATUS", 1);
        map.put("BATTERYVOLTAGENUM", count);

        if (byteBuf.readableBytes() < count * 10) {

            return true;
        }

        List list = new ArrayList();
        for (int i = 0; i < count; i++) {
            Map m = new HashMap();
            list.add(m);

            int sumSysNo = byteBuf.readUnsignedByte();
            m.put("subNo", sumSysNo);

            int voltage = byteBuf.readUnsignedShort();
            if (0xFFFE == voltage) {
                m.put("voltageStatus", 254);
            } else if (0xFFFF == voltage) {
                m.put("voltageStatus", 255);
            } else {
                m.put("voltageStatus", 1);
                m.put("voltage", voltage * 0.1);
            }

            int electricity = byteBuf.readUnsignedShort();
            if (0xFFFE == electricity) {
                m.put("currentStatus", 254);
            } else if (0xFFFF == electricity) {
                m.put("currentStatus", 255);
            } else {
                m.put("currentStatus", 1);
                m.put("current", CommonUtil.keepDecimal(electricity, 0.1, 1) - 1000);
            }

            int battery = byteBuf.readUnsignedShort();
            if (0xFFFE == battery) {
                m.put("batteryNumStatus", 254);
            } else if (0xFFFF == battery) {
                m.put("batteryNumStatus", 255);
            } else {
                m.put("batteryNumStatus", 1);
                m.put("batteryNum", battery);
            }

            int serial = byteBuf.readUnsignedShort();
            m.put("frameBatteryIndex", serial);

            int n = byteBuf.readUnsignedByte();
            m.put("framBatteryNum", n);
            if (byteBuf.readableBytes() < n * 2) {

                return true;
            }

            List vList = new ArrayList();
            for (int j = 0; j < n; j++) {

                int kv = byteBuf.readUnsignedShort();

                Map vm = new HashMap();
                if (0xFFFE == kv) {
                    vm.put("status", 254);
                } else if (0xFFFF == kv) {
                    vm.put("status", 255);
                } else {
                    vm.put("status", 1);
                    vm.put("value", CommonUtil.keepDecimal(kv, 0.001, 3));
                }
                vList.add(vm);
            }
            m.put("batteryVoltages", vList);
        }
        map.put("BATTERYVOLTAGEINFO", list);

        return false;
    }


    /**
     * 可充电储能温度数据
     *
     * @param byteBuf
     * @return
     */
    private boolean parseStorageTemp(ByteBuf byteBuf, List paramValues) {
        Map map = new HashMap();
        paramValues.add(map);

        int count = byteBuf.readUnsignedByte();
        if (0xFE == count || 0xFF == count) {
            map.put("BATTERYTEMPNUMSTATUS", count);

            return false;
        }
        map.put("BATTERYTEMPNUMSTATUS", 1);
        map.put("BATTERYTEMPNUM", count);

        if (byteBuf.readableBytes() < count * 3) {

            return true;
        }

        List list = new ArrayList();
        for (int i = 0; i < count; i++) {
            Map m = new HashMap();
            list.add(m);

            int sumSysNo = byteBuf.readUnsignedByte();
            m.put("subNo", sumSysNo);

            int n = byteBuf.readUnsignedShort();
            m.put("sensorNum", n);
            if (0xFE == n || 0xFF == n) {
                continue;
            }

            if (byteBuf.readableBytes() < n) {

                return true;
            }

            List l = new ArrayList();
            for (int j = 0; j < n; j++) {
                Map t = new HashMap();
                l.add(t);

                int temp = byteBuf.readUnsignedByte();
                if (0xFE == temp || 0xFF == temp) {
                    t.put("status", temp);
                    continue;
                }

                t.put("status", 1);
                t.put("value", temp);
            }
            m.put("temps", l);
        }
        map.put("BATTERYTEMPINFO", list);

        return false;
    }


    /**
     * 透传数据处理
     *
     * @param byteBuf
     * @param paramValues
     * @return
     */
    private boolean passThrough(ByteBuf byteBuf, List paramValues){
        Map map = new HashMap();
        paramValues.add(map);

        int length = byteBuf.readUnsignedShort();
        if (byteBuf.readableBytes() < length){

            return true;
        }

        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes);
        map.put("AA", bytes);

        return false;
    }


    /**
     * 解析通用报警标志位
     *
     * @param flag
     * @return
     */
    private Map toCommonAlarm(long flag) {
        String[] alarmArray = new String[]{"TempDiffAlarm", "BatteryHighTempAlarm",
                "HighPressureAlarm", "LowPressureAlarm",
                "SocLowAlarm", "BatteryUnitHighVoltageAlarm",
                "BatteryUnitLowVoltageAlarm", "SocHighAlarm",
                "SocJumpAlarm", "BatteryMismatchAlarm",
                "BatteryUnitUniformityAlarm", "InsulationAlarm",
                "DCDCTempAlarm", "BrakeAlarm",
                "DCDCStatusAlarm", "MotorCUTempAlarm",
                "HighPressureLockAlarm", "MotorTempAlarm", "BatteryOverChargeAlarm"};

        Map alarmMap = new HashMap();

        for (int i = 0; i < alarmArray.length; i++) {
            String column = alarmArray[i];
            long value = (flag >> i) & 0x01;

            alarmMap.put(column, value);
        }

        return alarmMap;
    }
}
