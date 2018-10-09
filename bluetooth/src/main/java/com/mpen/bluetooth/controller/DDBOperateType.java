package com.mpen.bluetooth.controller;

/**
 * Created by LYH on 2018/1/24.
 */

public enum DDBOperateType {
    DDB_OPERATE_WIFI_LIST("获取wifi列表"), // 0
    DDB_OPERATE_WIFI_SET("设置wifi"), // 1
    DDB_OPERATE_DOWNLOAD_LIST("已下载列表"), // 2
    DDB_OPERATE_DOWNLOAD_DELETE("删除下载内容"), // 3
    DDB_OPERATE_DOWNLOAD_ADD("添加下载内容"), // 4
    DDB_OPERATE_DOWNLOAD_UPDATE("更新下载内容进度"), // 5
    OPERATE_VIDEO_BASE_INFO("视频基本信息发送"), // 6
    DDB_OPERATE_PLAY_TEST("做自测题"), // 7
    DDB_OPERATE_DEVICE_INFO("设备信息"), // 8
    DDB_OPERATE_DOWNLOADING_BOOK("开始下载某本书"), // 9
    DDB_OPERATE_PAUSE_BOOK("暂停下载某本书"), // 10
    DDB_RETURN_PEN_DOWN_APP_PROGRESS("点读笔固件更新进度"), // 11
    DDB_NO_USE("占位"), // 12
    DDB_RETURN_PEN_UPDATE_STATE("点读笔是否有更新"), // 13
    DDB_RETURN_PEN_DOWN_FINISH("下载完成"), // 14
    DDB_UPDATE_VOICE_TYPE("更改语音类型"), // 15
    DDB_OPERATE_REFRESH_WIFI("重新搜索WIFI"), // 16
    DDB_VIDEO_BOOK_CODE("包含的视频信息"), // 17
    DDB_OPERATE_START_APP_UPDATE("app开始更新"), // 18
    DDB_OPERATE_START_SYSTEM_UPDATE("系统开始更新"), // 19
    DDB_OPERATE_UNBIND_PEN("解绑笔"), // 20
    DDB_OPERATE_FIND_BOOK("查找指定资源"), // 21
    DDB_OPERATE_BLE_BIND("ble绑定请求笔信息"), // 22
    DDB_RETURN_PEN_DOWN_APP_FAIL("更新内容下载失败"), // 23
    DDB_OPERATE_REFRESH_RECORD("刷新学习记录"), // 24
    DDB_OPERATE_WIFI_STATE("笔的WIFI状态"), // 25
    DDB_OPERATE_MODIFY_SCREENNAME("手机App更改屏显昵称"), // 26
    DDB_OPERATE_BLUETOOTH_DEVICE("获取笔搜寻到的周围蓝牙设备"), // 27
    DDB_OPERATE_BOX_CONNECT("连接指定的蓝牙音箱"), // 28
    DDB_OPERATE_BIND_VOICE("提示绑定成功"), // 29
    DDB_OPERATE_REGISTER_MSG("笔里面是否发送某类型消息"), // 30
    REMOVE_SAVED_NETCONFIG("移除笔里已保存的WiFi信息"), // 31
    OPERATE_BONDED_DEVICES("获取已经配对的蓝牙设备列表"), // 32
    OPERATE_REMOVE_BONDED_DEVICES("获取已经配对的蓝牙设备列表"); // 33

    private String info;
    public final static String NO_UPDATE_VERSION = "no_update_version"; //没有更新内容返回的
    public final static String HAVE_UPDATE_VERSION = "have_update_version"; //有更新内容返回的
    public final static String DOWN_FINISH = "down_finish";  //固件下载完成
    private final static DDBOperateType[] ALL_VALUES = DDBOperateType.values();

    DDBOperateType(String info) {
        this.info = info;
    }

    public String getOperateEes() {
        return info;
    }

    public static DDBOperateType getEnumByCode(int code) {
        if (code < 0 || code >= ALL_VALUES.length || code == 12) {
            return null;
        }
        return ALL_VALUES[code];
    }

}
