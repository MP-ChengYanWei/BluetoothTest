package com.mp.sharedandroid.network;

public class RequestUrl {

    private static RequestUrl requestUrl;

    /***************生产环境***************/
//    private static final String webturnURL = "https://api.mpen.com.cn/v1/";
//    private static final String ucBaseUrl = "https://www.mpen.com.cn/uc/";

    /***************测试环境***************/
    private static final String ucBaseUrl = "http://47.92.159.20:9011/uc/";
    private static final String webturnURL = "http://inside1.mpen.com.cn/v1/";

    /***************Other***************/
    private static final String mpenBaseUrl = "https://www.mpen.com.cn";


    // TODO: 2018/9/13 关于业务的常量需要分类
    /********************************用户中心--登录注册相关*******************************/
    // 获取短信验证码
    public static final String UC_SMS = ucBaseUrl + "sms/";
    // 找回密码
    public static final String UC_SMS_RECOVER = ucBaseUrl + "sms/recover/";
    // 注册
    public static final String UC_V2_USER = ucBaseUrl + "v2/user";
    // 登录
    public static final String LOGIN = webturnURL + "user/login";
    //修改用户信息
    public static final String POST_UPDATEUSERINFO = webturnURL + "user/";


    /***************************************************分级阅读接口*****************************************************/
    private static final String READ_LEVEL_URL = "https://reading.iceshi.org/api/";
    // 获取测试成绩
    public static final String GET_REPORTS = READ_LEVEL_URL + "users/reports";
    // 获取模拟题
    public static final String GET_TEST_SUBJECT = READ_LEVEL_URL + "mocks/questions";
    // 初始化第一页试题或恢复当前页试题
    public static final String INIT_SUBJECT = READ_LEVEL_URL + "tests/questions/initialization";
    // 抽取下一页试题
    public static final String NEXT_QUESTION = READ_LEVEL_URL + "tests/questions/extraction";
    // 提交答案
    public static final String SUBMIT_ANSWERS = READ_LEVEL_URL + "tests/questions/answers";
    // 更新持续作答时间
    public static final String REFRESH_DURATION = READ_LEVEL_URL + "tests/duration";

    // 保存阅读测试信息
    public static final String SAVE_READ_TEST_HISTORY = webturnURL + "user/exam";
    // 获取阅读测试历史记录
    public static final String GET_READ_TEST_HISTORY = webturnURL + "user/book?action=getExamDetail";
    // 获取推荐图书
    public static final String GET_READING_RESOURCE = webturnURL + "user/book?action=getReadingResource&level=";


    /*************************************1.0接口******************************************/
    //保存绑定关系
    public static final String POST_SAVE_BIND_RELATIONSHIP = webturnURL + "user/pen";
    //商城接口
    public static final String GET_GOODS = webturnURL + "shops/goods?action=getGoods";
    //获取商城广告图片
    public static final String GET_POSTERS = webturnURL + "shops/goods?action=getTopGoods";
    //获取书书籍详情
    public static final String GET_GOODSBYID = webturnURL + "shops/goods?action=getGoodsByGoodId&id=";
    //扫码绑定
    public static final String GET_CODEBINDPEN = webturnURL + "pens/serialNum?action=PenIdAndMac&serialNumber=";
    // 意见反馈，已改
    public static final String IDEA_FEED_URL = "https://question.webtrn.cn/entity/firstPage/feed_back.jsp?color=28a1df";
    // 使用帮助界面
    public static final String NORMAL_QA_URL = "https://api.mpen.com.cn/question/item.html";


    /*************************************2.0接口******************************************/
    // 获取短信验证码
    public static final String GET_SMS_VERIFY_CODE = ucBaseUrl + "sms/";
    // 注册
    public static final String REGISTER = ucBaseUrl + "v2/user";
    // 验证短信验证码
    public static final String VERIFY_SMS_VERIFY_CODE = GET_SMS_VERIFY_CODE;
    // 重置密码
    public static final String RESET_PASSWORD = ucBaseUrl + "sms/recover/";
    // 第三方登录是否绑定手机号验证
    public static final String IS_BIND_PHONE = ucBaseUrl + "sms/third/";
    // 第三方登录绑定手机号
    public static final String BIND_PHONE = ucBaseUrl + "sms/bind/";
    // 用户班级信息相关
    public static final String CLASS_INFO = webturnURL + "class/classInfo";
    // 班级学生相关
    public static final String STUDENT_INFO = webturnURL + "class/student";
    // 课前预习
    public static final String CLASS_TASK = webturnURL + "class/classTask";
    // 我的动态
    public static final String MY_DYNAMIC = webturnURL + "dynamic/dynamicList";
    //上传背景
    public static final String CHANGECOVER = webturnURL + "dynamic/editCover";
    //我的勋章
    public static final String MY_MEDAL = webturnURL + "medal/userRecord?action=integralList";
    //佩戴或拆下勋章
    public static final String ADORN = webturnURL + "medal/userRecord";
    // 获取积分总榜列表
    public static final String TOTAL_INTEGRAL = webturnURL + "integral/userRecord?action=getRankingList&pageNo=";
    // 获取好友积分榜列表
    public static final String FRIENDS_INTEGRAL = webturnURL + "integral/userRecord?action=getFriendsList&pageNo=";
    // 点赞
    public static final String PRAISE = webturnURL + "integral/userRecord";
    // 积分总榜获取个人积分情况
    public static final String GET_TOTAL_RANKING = webturnURL + "integral/userRecord?action=getPersonalRanking&type=0";
    // 好友
    public static final String GET_FRIENDS_RANKING = webturnURL + "integral/userRecord?action=getPersonalRanking&type=1";
    // 查询版本是否升级
    public static final String CHECK_UPGRADE = "https://api.mpen.com.cn/v1/" + "mobiles/app/WYT?action=upgradeApp";
    //好友列表
    public static final String FRIENDS_MANAGER_LIST = webturnURL + "friends/relationship?action=friendList";
    //新的朋友
    public static final String NEW_FRIEND_LIST = webturnURL + "friends/relationship?action=getNewFriendList";
    //好友通过
    public static final String PASS_REQUEST = webturnURL + "friends/relationship";
    //搜索用户
    public static final String SEARCH_USER = webturnURL + "friends/relationship?action=search";
    //我的消息
    public static final String USER_MANAGER = webturnURL + "userMessage/listUserMessage";
    //学情
    public static final String STUDY_STATDSTICAL = webturnURL + "learning/statistics?action=getLearningDetail&date=";
    // 我的学情
    public static final String GET_MY_LEARNING = webturnURL + "learning/statistics?action=getMyLearning";
    //如何获取积分
    public static final String GET_RECORD = "http://inside1.mpen.com.cn/views/record.html";
    //雷达图
    public static final String RADAR_MAP = "http://inside1.mpen.com.cn/views/home.html";

    /*******************************************教师端*********************************************/
    //上传音频视频
    public static final String UPLOAD_TEACHER_FILES = webturnURL + "homework/upload?action=uploadTeacherFiles";
    //布置课前导学作业
    public static final String ASSIGN_HOMEWORK = webturnURL + "homework/preview";
    //获取课本学习全部书籍
    public static final String TEXTBOOK_ALLBOOKS = webturnURL + "homework/textbook?action=getAllBooks";
    //根据书本id获取内容
    public static final String GET_CONTENT_FROM_ID = webturnURL + "homework/textbook?action=getBookContent&id=";
    //布置课后作业
    public static final String AFTER_CLASS_ASSIGN_HOMEWORK = webturnURL + "homework/homework?action=saveHomework";
    //口语考试
    public static final String ALL_ORAL_TEST = webturnURL + "homework/textbook?action=getAllOraltest";
    //某本书的具体信息
    public static final String ORAL_TEST_CONTENT = webturnURL + "homework/textbook?action=getOraltestcontent&id=";
    //作业列表
    public static final String HOMEWORK_LIST = webturnURL + "homework/assignments?action=getHomeWorkList&";
    //查询未提交
    public static final String CHECK_ASSIGNMENTS = webturnURL + "homework/checkAssignments?action=getMember&id=";
    //催作业
    public static final String RUSH_JOB = webturnURL + "homework/checkAssignments";
    //学生提交作业的具体信息
    public static final String SUBMIT_HOME_WORK_INFO = webturnURL + "homework/details?action=getDetails&homeworkId=";
    //学生提交作业
    public static final String READ_OVER_HOME_WORK = webturnURL + "homework/details";
    //获取评价字段
    public static final String GET_ENCOURAGE_DATA = webturnURL + "homework/comments?action=getAllComments";
    //获取班级作业情况
    public static final String GET_CLASS_HOMEWORK_LIST = webturnURL + "/homework/assignments?action=getClassHomeWorkList&homeWorkId=";
    //分享
    public static final String SHARE = "http://inside1.mpen.com.cn/views/share.html";
    //获取笔连接记录  用户关系
    public static final String USER_RELATIONSHIP = webturnURL + "user/relationship";
    //获取未绑定的笔
    public static final String GET_UNBINDPEN = webturnURL + "pens/?action=unBindPen&macAddress=";
    //解绑笔
    public static final String GET_STU_UNBINDRELATIONSHIP = webturnURL + "user/pen";

    /******************************************暂未使用********************************************/
    //获取商城列表
    public static String GET_SHOP_GOODS = webturnURL + "shops/goods?action=getGoods";
    //学情—获取用户信息
    public static String GET_STU_COMPELETE_USERSTUDYINFO = webturnURL + "user/book?action=completeUserStudyInfo";
    //学情—获取用户某本书学习信息
    public static String GET_STU_BOOK_STUDY_INFO = webturnURL + "user/book?action=bookStudyInfo&bookId=";
    //学情—获取用户某本书学习详情
    public static String GET_STU_BOOK_CONTENT_STUDY_INFO = webturnURL + "user/book?action=bookContentSpokenDetail&bookId=";
    //获取书籍图片
    public static final String GET_BOOKS_PHOTO = webturnURL + "shops/goods?action=getBooksPhoto&bookId=";

    public static final String getBooksPhotoUrl(String bookId) {
        return GET_BOOKS_PHOTO + bookId;
    }

    //学情跨月接口
    public static final String GET_USERDATE_STUDYTIME = webturnURL + "user/book?action=userDateStudyTime&date=";
    //获取年纪列表
    public static final String GET_GRADE_LIST = webturnURL + "user/?action=getUserLabels";
    //检查更新的接口
    public static final String CHECK_UPDATE = webturnURL + "mobiles/app/WYT?action=upgradeApp";
    //上传地理位置信息
    public static final String POST_SAVE_ADDRESS = webturnURL + "user/";
    //连接点读笔帮助页面
    public static final String CONNECT_HELP_URL = mpenBaseUrl + "/entity/ddbcode/h5/connect_error.html";
    //笔的介绍界面
    public static final String PEN_INTRODUCTION_URL = mpenBaseUrl + "/entity/ddbcode/app/toCode.html";
    //修改头像
    public static final String ALTER_PHOTO_URL = mpenBaseUrl + "/uc/user/uploadPhoto.do?";
    //学情周报月份接口
    public static final String GET_WEEK_LIST = webturnURL + "/user/weekly?action=getWeeklyList";

    //学情周报详细内容接口
    public static String getWeekDetailsURL(String start, String end) {
        return webturnURL + "/user/weekly?action=getWeekly&startDate=" + start + "&endDate=" + end;
    }

    private static final String SITE_URL = "https://www.mpen.com.cn/uc/";
    //enter next step
    public static String ENTER_NEXT_STEP = SITE_URL + "mobile/checkPhone.do?";
    //检查验证码
    public static String CHECK_VERIFYCODE_URL = SITE_URL + "mobile/verifyMobileCode.do?";
    //正常注册，获取验证码
    public static String SEND_VERIFYCODE_URL = SITE_URL + "mobile/sendVerificatinCode.do?";
    //修改用户信息
    public static String ALTER_USERINFO_URL = SITE_URL + "user/modifyEPN.do?";
    //修改密码
    public static String ALTER_PASSWORD_URL = SITE_URL + "user/modifyPassword.do?";
    //获取用户信息
    public static String GET_USER_INFO = SITE_URL + "user/getUserInfo.do?";

    // 口语考试试卷列表接口
    public static String getOralTestList(String bookId) {
        return webturnURL + "books/oralTest?action=getAllOralTestInfo&bookId=" + bookId;
    }

    // 口语考试试卷详情接口
    public static String getOralTestDetail(String bookId, String pageNum) {
        return webturnURL + "books/oralTest?action=getOralTestInfo&bookId=" + bookId + "&pageNum=" + pageNum;
    }

}
