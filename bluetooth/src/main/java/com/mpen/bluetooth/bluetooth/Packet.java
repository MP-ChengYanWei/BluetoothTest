package com.mpen.bluetooth.bluetooth;

import java.io.UnsupportedEncodingException;

public final class Packet {
    public final byte version;// 传输层版本号
    public final int serialNumber;// 消息索引
    public final int packNo;// 包序号
    private int totalPack;// 总包数

    public final int dataLength;// json 数据的总长度
    private byte[] dataBytes;// json按照 bytes 方式存储
    private byte crcByte;// 校验位
    private boolean isBuild = false;//构建消息还是解析消息

    public Packet(byte version, int serialNumber, int packNo, int totalPack, int dataLength, boolean isBuild) {
        this.isBuild = isBuild;
        // TODO Auto-generated constructor stub
        this.version = version;
        this.serialNumber = serialNumber;
        this.packNo = packNo;
        this.totalPack = totalPack;
        this.dataLength = dataLength;
    }

    public Packet(byte version, int serialNumber, int packNo, int totalPack, int dataLength, byte[] dataBytes, boolean isBuild) {
        this(version, serialNumber, packNo, totalPack, dataLength, isBuild);
        this.dataBytes = dataBytes;
    }

    public static final int BLE_PACKET_SIZE = 60;
    public static final int BLUETOOTH_PACKET_SIZE = 125;

    public static final char VER_A = 'A';// 固定版本
    public static final char VER_OLD = '{';// 固定版本
    public static final byte VER_A_BYTE = 65;
    public static final byte VER_OLD_BYTE = 123;


    public enum Version {OLD, A, UNKNOWN}

    public static final Version getVer(String data) {
        switch (data.charAt(0)) {
            case VER_OLD:
                return Version.OLD;
            case VER_A:
                return Version.A;
            default:
                return Version.UNKNOWN;
        }
    }

    public static final Version getVer(byte[] data) {
        switch (data[0]) {
            case VER_OLD_BYTE:
                return Version.OLD;
            case VER_A_BYTE:
                return Version.A;
            default:
                return Version.UNKNOWN;
        }
    }

    void setCrcByte(byte crcByte) {
        this.crcByte = crcByte;
    }

    int getTotalPack() {
        return totalPack;
    }

    void setTotalPack(int totalPack) {
        this.totalPack = totalPack;
    }

    void setDataBytes(byte[] dataBytes) {
        this.dataBytes = dataBytes;
    }

    void updateCRC() {
        final byte[] TMPPACK = getHeadAndJson2Str2Bytes();
        this.crcByte = computeCrc(TMPPACK, 0, TMPPACK.length);
    }

    public byte[] getDataBytes() {
        return this.dataBytes;
    }

    protected boolean isValid() {// 验证 数据包头 和 校验位 等信息
        return totalPack > 0 && this.packNo > 0 && this.dataBytes != null;
    }

    private byte[] getHeadAndJson2Str2Bytes() {// 组装数据的头部 数据 bytes
        try {
            return getHeadAndJson2Str().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    private String getHeadAndJson2Str() {
        try {
            return String.format("%s@%s", getPacketHead2Str(), new String(this.dataBytes, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }


    private String getPacketHead2Str() {
        if (isBuild || this.packNo == 1) {
            return String.format("%c_%X_%X_%X_%X",
                    this.version,
                    this.serialNumber,
                    this.packNo,
                    this.totalPack,
                    this.dataLength);
        } else {
            return String.format("%c_%X_%X_%X",
                    this.version,
                    this.serialNumber,
                    this.packNo,
                    this.totalPack);
        }
    }

    /**
     * 获得CRC校验结果，除校验位字节外，其他所有字节的异或值
     *
     * @param crcing
     * @param pos
     * @param len
     * @return
     * @throws IllegalArgumentException
     */
    public static byte computeCrc(byte[] crcing, int pos, int len) throws IllegalArgumentException {
        if (crcing == null) {
            throw new IllegalArgumentException("待校验字节数组不能为空");
        }
        if (pos < 0 || (pos + len) > crcing.length) {
            throw new IllegalArgumentException("pos(" + pos + ")必须大于0，且pos(" + pos + ") + len(" + len
                    + ")必须小于等于crcing.length(" + crcing.length + ")");
        }
        byte crced = 0;
        for (int i = pos; i < (pos + len); i++) {
            crced ^= crcing[i];
        }
        return crced;
    }

    /***
     * 返回 string
     *
     * @return
     */
    public String objToString() {
        return String.format("%s%02X", getHeadAndJson2Str(), crcByte & 0xFF);
    }

    /***
     * 返回 Bytes
     *
     * @return
     */
    public byte[] objToBytes() {
        try {
            return objToString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

    }

}
