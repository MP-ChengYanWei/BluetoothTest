package com.mpen.bluetooth.bluetooth;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;


/**
 * Created by LYH on 2017/11/24.
 */

public class PacketBuilder {

    /**
     * 平台下发消息流水号
     */
    private static int serialNumber = 0;

    public static boolean isphone = true;// true 手机 ；false笔

    public static void initPlatform(boolean isPhone) {
        isphone=isPhone;
        serialNumber = isPhone ? 0 : 1;
        // 手机的序列号用偶数，笔的用奇数
    }

    /**
     * 获得新的消息流水号
     *
     * @return
     */
    private synchronized static int nextSerialNum() {
        // 确保流水号为正值
        serialNumber += 2;
        if (serialNumber > 65534) {
            initPlatform(isphone);
        }
        return serialNumber;
    }

    private static int numHexDigits(int num) {
        if (num < 16) {
            return 1;
        } else if (num < 256) {
            return 2;
        } else if (num < 4096) { //16*16*16
            return 3;
        } else if (num < 65536) {//16 * 16 * 16 * 16
            return 4;
        } else return String.format("%X", num).length();
    }

    /***
     *
     *
     * @param bluetoothPackLimit
     *            单包可用字节数
     * @param serialNum
     *            序列号
     * @param packetNum
     *            第几个包
     * @param jsonDataLength
     *            json最大数据
     * @return
     */
    private static int getEstimatedDataLength(int bluetoothPackLimit, int serialNum, int packetNum,
                                              int jsonDataLength) {

        bluetoothPackLimit -= 7;// 去掉 必须的 4个_一个@ 两位校验位

        bluetoothPackLimit -= numHexDigits(serialNum);// 去掉 序列号
        bluetoothPackLimit -= numHexDigits(packetNum);// 去掉 包序号

        if (packetNum == 1) {
            bluetoothPackLimit--;// 去掉 _
            bluetoothPackLimit -= numHexDigits(jsonDataLength);// 去掉 包序号
        }

        final int total =
                (jsonDataLength + bluetoothPackLimit - 1) / bluetoothPackLimit;

        bluetoothPackLimit -= numHexDigits(total);
        return --bluetoothPackLimit;
    }

    public static Packet[] build(String data, int packetSize, byte version) {
        return build(data, nextSerialNum(), packetSize, version);
    }

    /***
     *
     * @param data  原始json 数据
     * @param serialNum 序列号
     * @param packetSize    packet大小
     * @param version       版本号
     * @return
     */
    public static Packet[] build(String data, int serialNum, int packetSize, byte version) {
        ArrayList<Packet> responsePacket = new ArrayList<Packet>();

        try {
            final byte[] tmpJsonByte = data.getBytes("UTF-8");

            int tmpPacketNum = 0;
            int packJsonDataLen = getEstimatedDataLength(packetSize, serialNum, ++tmpPacketNum,
                    tmpJsonByte.length);
            ByteBuffer buffer = ByteBuffer.allocate(packetSize);

            for (int charIndex = 0, nChars = data.length(), codepoint; charIndex < nChars; charIndex += Character
                    .charCount(codepoint)) {
                codepoint = data.codePointAt(charIndex);
                // Do something with codepoint.
                final byte[] charBytes = new String(Character.toChars(codepoint)).getBytes("utf8");
                if (charBytes.length + buffer.position() <= packJsonDataLen) {
                    buffer.put(charBytes);
                    if (charIndex == nChars - 1) {// 扫尾工作
                        responsePacket.add(getPacket(buffer, version, serialNum, tmpPacketNum, packetSize, tmpJsonByte.length));
                    }
                } else {
                    responsePacket.add(
                            getPacket(buffer, version, serialNum, tmpPacketNum, packetSize, tmpJsonByte.length));
                    packJsonDataLen = getEstimatedDataLength(packetSize, serialNum, ++tmpPacketNum,
                            tmpJsonByte.length);
                    buffer.put(charBytes);
                    continue;
                }

            }

            for (Packet packet : responsePacket) {
                packet.setTotalPack(tmpPacketNum);
                packet.updateCRC();
            }

            return (Packet[]) responsePacket.toArray(new Packet[responsePacket.size()]);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

    }

    private static Packet getPacket(ByteBuffer buffer, byte version, int serialNum, int PacketNum, int packetSize,
                                    int jsonDataLength) {
        // TODO Auto-generated method stub

        final byte[] tmpJsonDataByte = new byte[buffer.position()];
        buffer.flip();
        buffer.get(tmpJsonDataByte, 0, tmpJsonDataByte.length);
        buffer.clear();
        if (PacketNum != 1) {
            jsonDataLength = 0;
        }

        return new Packet(version, serialNum, PacketNum, packetSize, jsonDataLength, tmpJsonDataByte);
    }
}