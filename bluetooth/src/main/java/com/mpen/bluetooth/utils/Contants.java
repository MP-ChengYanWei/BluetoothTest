package com.mpen.bluetooth.utils;

/**
 * Created by wutingyou on 2018/3/22.
 * 公用基本常量，统一了以前多处常量类的情况，并规范了代码，都放此处处理
 */

public class Contants {
    public static final String LOGINTYPE = "logintype";

    public static final String SPACENOP = "     ";
    public static final String SFP_SUFFIX = "SFP";

    public static final String ISPUSH = "push";
    public static final String ISUPDATED = "isupdated";
    public static final String LASTPLAYTIME = "lastplaytime";
    public static final String LOADMESSAGE = "loadingmessage";
    public static final String NEWFRIEND = "newfriend";
    public static final String NEWFRIENDS = "newfriends";
    public static final String STATTIME = "stattime";
    public static final String VERSIONNAME = "versionname";
    public static final String VERSION_FILE = "version";
    public static final String VERSION_NAME = "versionname";
    public static final String LEARLOGINTYPE = "learloginType";
    public static final String COURSE_TYPE_FILE_NAME = "course_type.json";
    public static final String HISTORY = "history";
    public static final String USERINFO_FILE = "userinfo";
    public static final String UID = "uid";
    public static final String USERID = "userid";
    public static final String NICKNAME = "nickname";
    public static final String PIC = "pic";
    public static final String TYPE = "type";
    public static final String EMAIL = "email";
    public static final String LAST_LOGIN_ID = "last_login_id";
    public static final String SEX = "sex";
    public static final String JOBS = "job";
    public static final String MARK = "mark";
    public static final String ABOUT_ME = "about_me";
    public static final String LOGIN_CHANNEL = "loginchannel";
    public static final String EXIST = "exist";
    public static final String ADDRESS = "address";
    public static final String COMPANYID = "companyid";
    public static final String INFO_LAST_UPDATE_TIME = "i_lastime";
    public static final String PWD_LAST_UPDATE_TIME = "p_lastime";
    public static final String TEACHER_FLAG = "is_v";
    public static final String MYTALK_TYPE_FILE = "mytalk_type";
    public static final String MYTALK_STATE = "mytalkstate";
    public static final String MYNOTE_TYPE_FILE = "mynote_type";
    public static final String MYNOTE_STATE = "mynotestate";
    public static final String INAPPISFIRST_FILE = "isfirst_inapp";
    public static final String ISFIRST = "isfirstin";
    public static final String COURSE_TYPE_FILE = "course_type";
    public static final String DOWNLOAD_PAUSED_STATUS = "paused_status";
    public static final int USER_PIC_WIDTH_SLIDING = 250;
    public static final int USER_PIC_HEIGHT_SLIDING = 250;
    public static final int USER_PIC_WIDTH_COURSEINFO = 150;
    public static final int USER_PIC_HEIGHT_COURSEINFO = 150;
    public static final int USER_PIC_WIDTH_PERSONAL = 370;
    public static final int USER_PIC_HEIGHT_PERSONAL = 370;
    public static final int COURSE_IMAGE_WIDTH = 533;
    public static final int COURSE_IMAGE_HEIGHT = 300;
    public static final int PERSONAL_COURSE_IMAGE_WIDTH = 358;
    public static final int PERSONAL_COURSE_IMAGE_HEIGHT = 201;
    public static final int CHAT_DETAIL_IMAGE_MAX = 375;
    public static final int CHAT_SENDED_IMAGE_MAX = 960;
    public static final String DEFAULT_UID = "";
    public static final int SHARE_HEIGHT = 700;
    public static final int SHARE_EDIT_HEIGHT = 710;
    public static final int NOTE_HEADIMAGE_WIDTH = 150;
    public static final int NOTE_HEADIMAGE_HEIGHT = 150;
    public static final int COURSE_TYPE_IMAGE_WIDTH = 98;
    public static final int CROP_VIEW_WIDTH = 840;
    public static final int CHAT_EXPRESSION_WIDTH = 100;
    public static final int CHAT_LISTVIEW_EXPRESSION_WIDTH = 70;
    public static final String NETWORK_CHANGED_ACTION = "cn.com.whatyplugin.mooc.NETWORK_CHANGED";
    public static final String RELOAD_CHANGED_ACTION = "cn.com.whatyplugin.mooc.RELOAD_CHANGED";
    public static final String USER_LOGIN_ACTION = "cn.com.whatyplugin.mooc.USER_LOGIN";
    public static final String USER_LOGOUT_ACTION = "cn.com.whatyplugin.mooc.USER_LOGOUT";
    public static final String USER_UPDATE_HANDIMG_ACTION = "cn.com.whatyplugin.mooc.USER_UPDATE_HANDIMG";
    public static final String SDCARD_STATUS_CHANGED = "cn.com.whatyplugin.mooc.SDCARD_STATUS_CHANGED";
    public static final String UPDATE_UNREAD_NUM_ACTION = "cn.com.whatyplugin.mooc.UPDATE_UNREAD_NUM";
    public static final String UPDATE_NEWFRIEND_UNREAD_ACTION = "cn.com.whatyplugin.mooc.UPDATE_NEWFRIEND_UNREAD";
    public static final String QUERY_MESSAGE_FINISH_ACTION = "cn.com.whatyplugin.mooc.QUERY_MESSAGE_FINISH";
    public static final String SINGLE_URL = "single_url";
    public static final String MULTI_URL = "multi_url";
    public static final int DEFAULT_UPDATE = -1;
    public static final int MAX_TEST_TIMES = 2;/*阅读测试每日最多测试次数*/
    public static final int NO_UPDATE = 0;
    public static final int BASE_INFO_UPDATE = 1;
    public static final int PWD_UPDATE = 2;
    public static final int INFO_PWD_UPDATE = 3;
    public static final int USE_MANUAL_PAGE_COUNT = 14;//使用手册PDF文件页数
    public static final String UNREAD_MSG = "unread_msg";
    public static final String CHAT_CONTENT = "chat_content";
    public static final String FIRST_ENTER = "first_enter";
    public static final String CHAT_SEND_FAILED_MSG = "chat_send_failed_msg";
    public static final String NETWORK = "network";
    public static final String NETWORK_SETTING = "network_setting";

    public static final String USER_HEADTEACHERNAME = "headTeacherName";
    public static final String USER_ORGANID = "organId";
    public static final String USER_BANJINAME = "banjiName";
    public static final String USER_BANJIID = "banjiId";
    public static final String USER_PROJECTID = "projectId";
    public static final String USER_HEADTEACHERPHONE = "headTeacherPhone";

    /****分级阅读秘钥****/
    public static final String READ_APPKEY = "fltrpWYT";
    public static final String READ_APPSECRET = "GN3NoDZ6kaE3yAV4lKqjMw";


    /**
     * 一些常量控制器
     */
    //笔和App是否连接
    // public static boolean ISCONNECT = false;
    //是否登录
    public static boolean ISLOGIN = false;

    /**
     * 自定义广播
     */
    //更改头像的广播
    public static final String UPDATE_HEADIMG_ACTION = "com.whaty.ddb.update_headimg_action";
    //更改年级的广播
    public static final String UPDATE_GRADE_ACTION = "com.whaty.ddb.update_grade_action";
    //支付失败的广播
    public static final String PAY_FAIL_ACTION = "com.whaty.ddb.pay_fail_action";
    //支付成功的广播
    public static final String PAY_SUCCESS_ACTION = "com.whaty.ddb.pay_success_action";
    //自动播放完成的广播
    public static final String AUTOPLAY_COMPLETE_ACTION = "com.whaty.ddb.autoplay_complete_action";

    //用于获取向笔发送消息的标识
//    public static final String BLUETOOTH_DATA = "bluetooth_data";
    public static final String DATA = "DATA";
    //开始蓝牙搜索的广播
    public static final String START_DISCOVERY_ACTION = "com.whaty.ddb.start_discovery_action";
    //停止蓝牙搜索的广播
    public static final String STOP_DISCOVERY_ACTION = "com.whaty.ddb.start_discovery_action";
    //开始蓝牙连接的广播
    public static final String START_CONNECT_ACTION = "com.whaty.ddb.start_connect_action";
    //向笔发送消息的广播
    public static final String DATA_TO_PEN_ACTION = "com.whaty.ddb.data_to_pen_action";
    //关闭蓝牙服务的广播
    public static final String STOP_BLUETOOTH_SERVICE_ACTION = "com.whaty.ddb.stop_bluetooth_service_action";

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

    public static final String APK_NAME = "update.apk";

    public static final String CONNECT_TAG = "CONNECT_TAG";
    public static final String SPUTILS_TAG = "SPUTILS_TAG";

    //修改头像的广播
    public static final String ALTER_USER_HEADPHOTO_ACTION = "com.whaty.uc.ALTER_USER_HEADPHOTO";
    //修改昵称的广播
    public static final String ALTER_USER_NICKNAME_ACTION = "com.whaty.uc.ALTER_USER_NICKNAME";
    //修改电话的广播
    public static final String ALTER_USER_PHONE_ACTION = "com.whaty.uc.ALTER_USER_PHONE";
    //修改邮箱的广播
    public static final String ALTER_USER_EMAIL_ACTION = "com.whaty.uc.ALTER_USER_EMAIL";
    //修改年龄的广播
    public static final String ALTER_USER_AGE_ACTION = "com.whaty.uc.ALTER_USER_AGE";
    //修改性别的广播
    public static final String ALTER_USER_SEX_ACTION = "com.whaty.uc.ALTER_USER_SEX";
    //更新阅读等级广播
    public static final String UPDATE_READ_LEVEL = "com.whaty.mp.UPDATE_READ_LEVEL";

    //客服电话
    public static final String SERVICE_PHONE = "400-898-9008";

}
