package com.mpen.bluetooth.common;

import com.mpen.bluetooth.bluetooth.Packet;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/11/24.
 */

public class DataRecord {

    private static DataRecord INSTANCE;

    public static DataRecord getInstance() {
        if (INSTANCE == null) {
            synchronized (DataRecord.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DataRecord();
                }
            }
        }
        return INSTANCE;
    }

    private final Map<String, Packet[]> requestPackets = new HashMap<String, Packet[]>();// 接收到的数据
    private final Map<String, Packet[]> responsePackets = new HashMap<String, Packet[]>();// 发送数据的hasmap

    public Map<String, Packet[]> getRequestPackets() {
        return requestPackets;
    }

    public Map<String, Packet[]> getResponsePackets() {
        return responsePackets;
    }
}

