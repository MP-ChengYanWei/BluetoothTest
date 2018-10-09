package com.mpen.bluetooth.bluetooth;


import com.mpen.bluetooth.common.DataRecord;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/**
 * Created by LYH on 2017/11/24.
 */

public class PacketReceiver {

    public enum PacketStatus {
        COMPLETE, NEED_MORE, NEW, ERROR
    }

    public static Packet getPacketMobile(String packData) {// 返回接收到的 数据的序列号 用于维护 dataRecord
        try {
            return getPacketMobile(packData.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    protected static Packet analyzePacketHead(String headData, byte version) {// 分析数据包头部内容
        Packet packet = null;

        final String[] headTemp = headData.split("_");
        if (headTemp == null || headTemp.length > 5 || headTemp.length < 4) {
            return null;
        }
        final int serialNumber = Integer.parseInt(headTemp[1], 16);// 消息索引
        final int packNo = Integer.parseInt(headTemp[2], 16); // 包序号
        final int totalPack = Integer.parseInt(headTemp[3], 16);// 总包数
        int dataLength = 0;
        if (headTemp.length == 5) {
            dataLength = Integer.parseInt(headTemp[4], 16);// json 数据的总长度
            packet = new Packet(version, serialNumber, packNo, totalPack, dataLength);
        } else {
            packet = new Packet(version, serialNumber, packNo, totalPack, dataLength);
        }

        return packet;


    }

    public static Packet getPacketMobile(byte[] packData) {

        final byte[] tmpByteCRC = new byte[2];
        System.arraycopy(packData, packData.length - 2, tmpByteCRC, 0, 2);

        final byte crcByte = (byte) (Integer.parseInt(new String(tmpByteCRC), 16) & 0xFF);
        final byte crcBytePack = Packet.computeCrc(packData, 0, packData.length - 2);

        if (crcByte != crcBytePack) {
            return null;
        }

        int offset = 0;// 数据偏移量
        for (; offset < packData.length; offset++) {
            if (packData[offset] == 0x40) {// ('@' ascii值)
                break;
            }
        }

        Packet tmpPacket = null;
        try {
            tmpPacket = analyzePacketHead(new String(packData, 0, offset, "UTF-8"), packData[0]);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        offset++;//后移一位 越过 @

        final int tmpJsonDataLen = packData.length - (offset + tmpByteCRC.length);
        final byte[] dataBytes = new byte[tmpJsonDataLen];
        System.arraycopy(packData, offset, dataBytes, 0, dataBytes.length);
        tmpPacket.setDataBytes(dataBytes);
        tmpPacket.setCrcByte(crcByte);

        return tmpPacket;
    }

    public static PacketStatus receivePacket(Packet packet) { // 接收到 传输层 数据包

        if (packet == null) {
            return PacketStatus.ERROR;
        }
        if (!packet.isValid() && packet.packNo <= packet.getTotalPack()) {
            return PacketStatus.ERROR;
        }
        Packet[] packetList = DataRecord.getInstance().getResponsePackets().get(String.valueOf(packet.serialNumber));
        if (packetList == null) {
            packetList = new Packet[packet.getTotalPack()];
        }
        packetList[packet.packNo - 1] = packet;
        DataRecord.getInstance().getResponsePackets().put(String.valueOf(packet.serialNumber), packetList);

        int tmpPackNoTotal = 0; //DataRecord packetList 有已经存储了多少个
        for (Packet list : packetList) {
            if (list != null) {
                tmpPackNoTotal++;
                continue;
            }
        }
        if (tmpPackNoTotal == packet.getTotalPack()) {
            return PacketStatus.COMPLETE;
        } else if (tmpPackNoTotal == 1) {
            return PacketStatus.NEW;
        } else if (tmpPackNoTotal < packet.getTotalPack()) {
            return PacketStatus.NEED_MORE;
        }
        return PacketStatus.ERROR;
    }

    public static String getMessage(int serialNumber) { // 根据序列号 返回业务成 json
        int tmpJsonLength = 0;
        String tmpData = null;

        final Packet[] packets = DataRecord.getInstance().getResponsePackets().get(String.valueOf(serialNumber));

        if (packets == null || packets.length == 0) {
            return null;
        }

        ByteBuffer tmpDataJson = ByteBuffer.allocate(packets[0].dataLength);

        for (Packet packet : packets) {
            if (packet == null) {
                return null;
            }
            tmpDataJson.put(packet.getDataBytes());
            if (packet.packNo == 1) {
                tmpJsonLength = packet.dataLength;
            }
        }
        try {
            final byte[] jsonData = tmpDataJson.array();
            tmpData = new String(jsonData, "UTF-8");

            if (tmpJsonLength == jsonData.length) {
                return tmpData;
            }

        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        return null;
    }
}
