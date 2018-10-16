package com.mp.sharedandroid.utils;

/**
 * 处理和笔相关的工具类
 * Created by npw on 2016/7/6.
 */

public class PenUtils {

    /**
     * 解析获取的笔的信息并存入SP
     *
     * @param penInfo 笔的信息
     */
    public static void analyzePenInfo(String penInfo) {
//        try {
//            JSONObject penInfoObj = new JSONObject(penInfo).optJSONObject("data");
//            SPUtils.put(SPUtils.PENID, penInfoObj.optString("penId"));
//            SPUtils.put(SPUtils.ALLSPACE, penInfoObj.optString("allSpace"));
//            SPUtils.put(SPUtils.SURPLUSSPACE, penInfoObj.optString("surplusSpace"));
//            SPUtils.put(SPUtils.LEVEL, penInfoObj.optString("level"));
//            SPUtils.put(SPUtils.MACADDRESS, penInfoObj.optString("macAddress"));
//            SPUtils.put(SPUtils.VERSION, penInfoObj.optString("version"));
//            SPUtils.put(SPUtils.VOICE_TYPE, penInfoObj.optString("voice"));
//            SPUtils.put(SPUtils.WIFINAME, penInfoObj.optString("wifiName"));
//            SPUtils.put(SPUtils.PENNICKNAME, penInfoObj.optString("screenName"));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

    /**
     * 从SP中清除绑定笔的信息
     */
    public static void removePenInfo() {
        SPUtils.remove(SPUtils.BINDDEVICE);
        SPUtils.remove(SPUtils.MACADDRESS);
        SPUtils.remove(SPUtils.PENID);
        SPUtils.remove(SPUtils.ALLSPACE);
        SPUtils.remove(SPUtils.SURPLUSSPACE);
        SPUtils.remove(SPUtils.LEVEL);
        SPUtils.remove(SPUtils.MACADDRESS);
        SPUtils.remove(SPUtils.VERSION);
        SPUtils.remove(SPUtils.VOICE_TYPE);
        SPUtils.remove(SPUtils.WIFINAME);
        SPUtils.put(SPUtils.AUTOLOGIN, false);
        SPUtils.put(SPUtils.MACADDRESS, "");
    }

    /**
     * 根据位置选择对应的语音类型
     *
     * @param position 语音类型所在的位置
     */
    public static String choiceVoice(int position) {
        String voiceType = "";
        switch (position) {
            case 0:
                voiceType = "xiaoyu";//男音
                break;
            case 1:
                voiceType = "xiaoyan";//女音
                break;
            case 2:
                voiceType = "xiaoqian";//东北话
                break;
            case 3:
                voiceType = "nannan";//童音
                break;
            case 4:
                voiceType = "xiaomei";//粤语
                break;
            case 5:
                voiceType = "xiaolin";//台湾话
                break;
            case 6:
                voiceType = "xiaorong";//四川话
                break;
            case 7:
                voiceType = "xiaokun";//河南话
                break;
            case 8:
                voiceType = "xiaoqiang";//湖南话
                break;
            case 9:
                voiceType = "vixying";//陕西话
                break;
        }
        return voiceType;
    }

    /**
     * 根据语音类型返回语音所在的位置
     *
     * @param voiceType 语音类型
     * @return 所在的位置
     */
    public static int getCurrentPosition(String voiceType) {
        int currentPosition = -1;
        switch (voiceType) {
            case "xiaoyu":
                currentPosition = 0;//男音
                break;
            case "xiaoyan":
                currentPosition = 1; //女音
                break;
            case "xiaoqian":
                currentPosition = 2;//东北话
                break;
            case "nannan":
                currentPosition = 3;//童音
                break;
            case "xiaomei":
                currentPosition = 4;//粤语
                break;
            case "xiaolin":
                currentPosition = 5;//台湾话
                break;
            case "xiaorong":
                currentPosition = 6;//四川话
                break;
            case "xiaokun":
                currentPosition = 7;//河南话
                break;
            case "xiaoqiang":
                currentPosition = 8;//湖南话
                break;
            case "vixying":
                currentPosition = 9;//陕西话
                break;
        }
        return currentPosition;
    }

    /**
     * 根据位置返回对应的语音名字
     *
     * @param position 语音类型所在的位置
     * @return 语音名字
     */
    public static String getVoiceNameByPosition(int position) {
        String voiceName = "";
        switch (position) {
            case 0:
                voiceName = "男音";//男音
                break;
            case 1:
                voiceName = "女音";//女音
                break;
            case 2:
                voiceName = "东北话";//东北话
                break;
            case 3:
                voiceName = "童音";//童音
                break;
            case 4:
                voiceName = "粤语";//粤语
                break;
            case 5:
                voiceName = "台湾话";//台湾话
                break;
            case 6:
                voiceName = "四川话";//四川话
                break;
            case 7:
                voiceName = "河南话";//河南话
                break;
            case 8:
                voiceName = "湖南话";//湖南话
                break;
            case 9:
                voiceName = "陕西话";//陕西话
                break;
        }
        return voiceName;
    }

    /**
     * 根据语音类型返回语音名称
     *
     * @param voiceType 语音类型
     * @return 语音名称
     */
    public static String getVoiceNameByType(String voiceType) {
        String voiceName = "";
        switch (voiceType) {
            case "xiaoyu":
                voiceName = "男音";//男音
                break;
            case "xiaoyan":
                voiceName = "女音"; //女音
                break;
            case "xiaoqian":
                voiceName = "东北话";//东北话
                break;
            case "nannan":
                voiceName = "童音";//童音
                break;
            case "xiaomei":
                voiceName = "粤语";//粤语
                break;
            case "xiaolin":
                voiceName = "台湾话";//台湾话
                break;
            case "xiaorong":
                voiceName = "四川话";//四川话
                break;
            case "xiaokun":
                voiceName = "河南话";//河南话
                break;
            case "xiaoqiang":
                voiceName = "湖南话";//湖南话
                break;
            case "vixying":
                voiceName = "陕西话";//陕西话
                break;
        }
        return voiceName;
    }
}
