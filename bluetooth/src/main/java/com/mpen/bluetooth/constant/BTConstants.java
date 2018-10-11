package com.mpen.bluetooth.constant;

/**
 * Created by LYH on 2018/1/24.
 * 存放一些 Bluetooth 常量
 */

public class BTConstants {

    //开始蓝牙搜索的广播
    public static final String START_DISCOVERY_ACTION = "com.whaty.ddb.start_discovery_action";
    //停止蓝牙搜索的广播
    public static final String STOP_DISCOVERY_ACTION = "com.whaty.ddb.start_discovery_action";

    //和笔连接成功的广播
    public static final String APP_CONNECT_SUCCESS_ACTION = "com.whaty.app_connect_success_action";
    //和笔连接失败的广播
    public static final String APP_CONNECT_ERROR_ACTION = "com.whaty.app_connect_error_action";
    //和笔蓝牙连接状态改变的广播
    public static final String APP_CONNECT_STATE_CHANGE_ACTION = "com.whaty.app.connect_state_change_action";

    /************业务相关的数据 start***************/
    //收到的消息的广播
    public static final String RECEIVE_DATA = "com.whaty.receive_data_action";
    //发送消息的广播
    public static final String SEND_DATA = "com.whaty.send_data_action";
    //连接过程中的连接失败
    public static final String CONNECT_ING_ERROR_ACTION = "com.whaty.connect_ing_error_action";
    //获取wifi列表的广播
    public static final String GET_WIFI_LIST_ACTION = "com.whaty.get_wifi_list_action";
    //获取下载列表的广播
    public static final String GET_DOWNLOAD_LIST_ACTION = "com.whaty.get_download_list_action";
    //更新下载列表的广播
    public static final String UPDATE_DOWNLOAD_LIST_ACTION = "com.whaty.update_download_list_action";
    //获取笔的信息的广播
    public static final String GET_PEN_INFO_ACTION = "com.whaty.get_pen_info_action";
    //检查笔里的App是否有更新的广播
    public static final String GET_UPDATE_INFO_ACTION = "com.whaty.get_update_info_action";
    //获取笔里App的更新进度
    public static final String GET_UPDATE_PROGRESS_ACTION = "com.whaty.get_update_progress_action";
    //笔里App更新完成的广播
    public static final String UPDATE_FINISH_ACTION = "com.whaty.update_finish_action";
    //更新下载失败的广播
    public static final String UPDATE_FAIL_ACTION = "com.whaty.update_fail_action";
    //更新学习记录（我的笔空界面刷新）的广播
    public static final String UPDATE_RECORD_ACTION = "com.whaty.update_record_action";
    //WIFI设置成功的广播
    public static final String SET_WIFI_SUCCESS_ACTION = "com.whaty.set_wifi_success_action";
    //蓝牙报空指针异常，重启蓝牙
    public static final String BLUETOOTH_ISNULL_ACTION = "com.whaty.bluetooth_isnull_action";
    //笔的wifi状态改变的广播
    public static final String PEN_WIFI_STATE_ACTION = "com.whaty.pen_wifi_state_action";
    //更改屏显昵称
    public static final String UPDATE_SCREENNAME_ACTION = "com.whaty.ddb.update_screenname_action";
    //搜寻到了蓝牙音箱设备
    public static final String BOX_DEVICE_FOUND_ACTION = "com.whaty.ddb.box_found_action";
    //搜寻到了蓝牙已配对信息
    public static final String BOX_DEVICE_BONDED_ACTION = "com.whaty.ddb.box_bonded_action";
    /************业务相关的数据 end***************/
}
