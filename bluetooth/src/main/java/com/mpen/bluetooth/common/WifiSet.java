package com.mpen.bluetooth.common;

/**
 * Created by wutingyou on 2017/4/11.
 * 和手机端共享的蓝牙统一协议数据格式封装， data 数据格式
 * TODO：目前兼容旧的协议数据格式，后续也可以扩展
 */

public class WifiSet {
    public String name;
    public String password;
    public int type;
}
