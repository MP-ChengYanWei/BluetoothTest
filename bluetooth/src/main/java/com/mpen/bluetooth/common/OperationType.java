package com.mpen.bluetooth.common;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * Created by wutingyou on 2017/4/10.
 * 手机app蓝牙通信数据类型
 * TODO:兼容以前手机app，暂时没有修改蓝牙通信协议
 */

public enum OperationType {
    OPERATE_WIFI_LIST("获取wifi列表", 0, null),
    OPERATE_WIFI_SET("设置wifi", 1, null),
    OPERATE_DOWNLOAD_LIST("已下载列表", 2, null),
    OPERATE_DOWNLOAD_DELETE("删除下载内容", 3, new TypeToken<BookDownloadIds>() {
    }.getType()),
    OPERATE_DOWNLOAD_ADD("添加下载内容", 4, null),
    OPERATE_DOWNLOAD_UPDATE("更新下载内容进度", 5, null),
    TEMP_NO_USE_1("暂时未用到，待填充", 6, null),
    OPERATE_PLAY_TEST("做自测题", 7, null),
    OPERATE_DEVICE_INFO("设备信息", 8, null),
    OPERATE_DOWNLOADING_BOOK("开始下载某本书", 9, null),
    OPERATE_PAUSE_BOOK("暂停下载某本书", 10, null),
    RETURN_PEN_DOWN_APP_PROGRESS("点读笔固件更新进度", 11, null),
    TEMP_NO_USE_2("暂时未用到，待填充", 12, null),
    RETURN_PEN_UPDATE_STATE("点读笔是否有更新", 13, null),
    RETURN_PEN_DOWN_FINISH("下载完成", 14, null),
    UPDATE_VOICE_TYPE("更改语音类型", 15, null),
    OPERATE_REFRESH_WIFI("重新搜索WIFI", 16, null),
    VIDEO_BOOK_CODE("包含的视频信息", 17, new TypeToken<VideoInfos>() {
    }.getType()),
    OPERATE_START_APP_UPDATE("app开始更新", 18, null),
    OPERATE_START_SYSTEM_UPDATE("系统开始更新", 19, null),
    OPERATE_UNBIND_PEN("解绑笔", 20, null),
    OPERATE_FIND_BOOK("查找指定资源", 21, null),
    TEMP_NO_USE_3("ble绑定请求笔信息", 22, null),
    RETURN_PEN_DOWN_APP_FAIL("更新内容下载失败", 23, null),
    OPERATE_REFRESH_RECORD("刷新学习记录", 24, null),
    OPERATE_WIFI_STATE("笔的WIFI状态", 25, new TypeToken<WifiState>() {
    }.getType()),
    OPERATE_MODIFY_SCREENNAME("手机App更改屏显昵称", 26, null),
    DDB_OPERATE_BLUETOOTH_DEVICE("获取笔搜寻到的周围蓝牙设备", 27, null),
    DDB_OPERATE_CONNECT_BOX("连接指定的蓝牙音箱", 28, null),
    DDB_OPERATE_BIND_VOICE("提示绑定成功", 29, null),
    DDB_OPERATE_REGISTER_MSG("笔里面是否发送某类型消息", 30, null),
    REMOVE_SAVED_NETCONFIG("移除笔里已保存的WiFi信息", 31, null),
    OPERATE_BONDED_DEVICES("获取已经配对的蓝牙设备列表", 32, null),
    OPERATE_REMOVE_BONDED_DEVICES("获取已经配对的蓝牙设备列表", 33, null); // 33

    private static final String TAG = "OperationType";

    private String info;//说明

    private int type;//码值
    private Type obj;

    OperationType(String info, int type, Type obj) {
        this.info = info;
        this.type = type;
        this.obj = obj;
    }

    public int getType() {
        return type;
    }

    public Type getObj() {
        return obj;
    }

    public String getInfo() {
        return info;
    }


    private static final OperationType[] VALUES = OperationType.values();

    public static OperationType valueOf(int codeid) {
        if (codeid >= 0 && codeid <= 32) {
            return VALUES[codeid];
        } else {
            return null;
        }
    }

}
