package com.mpen.bluetooth.common;

/**
 * Created by wutingyou on 2017/4/14.
 * 和手机端共享的蓝牙统一协议数据格式封装， data 数据格式
 * TODO：目前兼容旧的协议数据格式，后续也可以扩展
 * 笔端发送给手机 app 信息格式
 */

public class PenInfo {
    public String wifiName;
    public String penId;
    public String macAddress;
    public String level;
    public String version;
    public String trueName;
    public String screenName;
    public String id;
    public String isBind;
}
