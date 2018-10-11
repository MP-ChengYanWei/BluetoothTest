package com.mpen.bluetooth.controller;

import android.util.Log;

import com.mpen.bluetooth.bean.CacheBookModel;
import com.mpen.bluetooth.common.BtDataResult;
import com.mpen.bluetooth.common.OperationType;
import com.mpen.bluetooth.common.RegisterInfo;
import com.mpen.bluetooth.common.VoiceTypUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * 向笔发送指令
 * Created by npw on 2016/1/8.
 */

public class SendRequestToPen {

    public static int msgid = 1;

    private static DDBConnectNormal ddbConnectNormal = new DDBConnectNormal();

    /***
     * 返回处理结果
     * @param type 消息类型
     * @param Ret  处理结果 0:失败；1：成功
     * @param time 发送端发出的时间 （序列号 需要原样返回）
     */
    public static void ReturnResult(OperationType type, int Ret, long time) {
        BtDataResult DataResult = new BtDataResult();
        DataResult.type = type.getType();
        ddbConnectNormal.sendRequest(type, DataResult);
    }

    /**
     * 获取笔的wifi列表
     */
    public static void getWIFIList() {
        ddbConnectNormal.sendRequest(OperationType.OPERATE_WIFI_LIST, null);
    }

    /**
     * 给笔设置wifi
     *
     * @param setWifiMap 包含wifi信息的map：包含type，password，name
     */
    public static void setWIFI(HashMap<String, Object> setWifiMap) {
        ddbConnectNormal.sendRequest(OperationType.OPERATE_WIFI_SET, setWifiMap);
    }

    /**
     * 刷新笔里的WIFI列表
     */
    public static void refreshWIFI() {
        ddbConnectNormal.sendRequest(OperationType.OPERATE_REFRESH_WIFI, null);
    }

    /**
     * 获取笔的信息
     */
    public static void getPenInfo(String screenName, String loginId) {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("token", "default"); //将token传给笔
        map.put("screenName", screenName);//将用户昵称传给笔
        map.put("loginId", loginId);//将loginId传给笔
        ddbConnectNormal.sendRequest(OperationType.OPERATE_DEVICE_INFO, map);
    }

    /**
     * 获取笔里所有的下载内容
     */
    public static void getAllDownLoads() {
        ddbConnectNormal.sendRequest(OperationType.OPERATE_DOWNLOAD_LIST, null);
    }

    /**
     * 给笔添加下载内容
     *
     * @param id 书的id
     */
    public static void addDownLoad(String id) {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("id", id);
        ddbConnectNormal.sendRequest(OperationType.OPERATE_DOWNLOAD_ADD, map);
    }

    /**
     * 删除部分下载内容
     *
     * @param deleteList 要删除的list
     */
    public static void delete(ArrayList<CacheBookModel> deleteList) {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("ids", listToIds(deleteList));
        ddbConnectNormal.sendRequest(OperationType.OPERATE_DOWNLOAD_DELETE, map);
    }

    /**
     * 删除全部下载内容
     */
    public static void deleteAll() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("ids", "all");
        ddbConnectNormal.sendRequest(OperationType.OPERATE_DOWNLOAD_DELETE, map);
    }

    /**
     * 开始下载某本书
     *
     * @param id 要下载的id
     */
    public static void beginDownload(String id) {
        HashMap<String, String> map = new LinkedHashMap<>();
        map.put("id", id);
        ddbConnectNormal.sendRequest(OperationType.OPERATE_DOWNLOADING_BOOK, map);
    }

    /**
     * 暂停下载某本书
     *
     * @param id 要暂停的id
     */
    public static void pauseDownload(String id) {
        HashMap<String, String> map = new LinkedHashMap<>();
        map.put("id", id);
        ddbConnectNormal.sendRequest(OperationType.OPERATE_PAUSE_BOOK, map);
    }

    /**
     * 检查笔里的App是否有更新
     */
    public static void checkUpdate() {
        ddbConnectNormal.sendRequest(OperationType.RETURN_PEN_UPDATE_STATE, null);
    }

    /**
     * 笔里App有更新，开始更新
     */
    public static void beginUpdateApp() {
        ddbConnectNormal.sendRequest(OperationType.OPERATE_START_APP_UPDATE, null);
    }

    /**
     * 笔里系统有更新，开始更新
     */
    public static void beginUpdateSystem() {
        ddbConnectNormal.sendRequest(OperationType.OPERATE_START_SYSTEM_UPDATE, null);
    }

    /**
     * 让笔更改语音类型
     *
     * @param position 要更换的语音类型
     */
    public static void changeVoice(int position) {
        HashMap<String, String> map = new LinkedHashMap<>();
        map.put("voice", VoiceTypUtils.choiceVoice(position));
        ddbConnectNormal.sendRequest(OperationType.UPDATE_VOICE_TYPE, map);
    }

    /**
     * 更换昵称
     *
     * @param nickname 要更换的昵称
     */
    public static void changeNickName(String nickname) {
        HashMap<String, String> map = new LinkedHashMap<>();
        map.put("screenName", nickname);
        ddbConnectNormal.sendRequest(OperationType.OPERATE_MODIFY_SCREENNAME, map);
    }

    public static void startDiscoverBox() {
        ddbConnectNormal.sendRequest(OperationType.DDB_OPERATE_BLUETOOTH_DEVICE, null);
    }

    public static void sendBoxName(String name) {
        HashMap<String, String> map = new LinkedHashMap<>();
        map.put("boxName", name);
        ddbConnectNormal.sendRequest(OperationType.DDB_OPERATE_CONNECT_BOX, map);
    }

    /**
     * 解绑笔
     */
    public static void UnbindPen() {
        ddbConnectNormal.sendRequest(OperationType.OPERATE_UNBIND_PEN, null);
    }

    /**
     * 播放“绑定成功”
     */
    public static void playBindVoice() {
        Log.i("playBindVoice", "playBindVoice!");
        BluetoothManager.getInstance().isSendPaly = true;
        ddbConnectNormal.sendRequest(OperationType.DDB_OPERATE_BIND_VOICE, null);
    }

    /**
     * 获取已经配对的蓝牙设备列表
     */
    public static void getBondedList() {
        ddbConnectNormal.sendRequest(OperationType.OPERATE_BONDED_DEVICES, null);
    }

    public static void startDiscovery() {
        ddbConnectNormal.sendRequest(OperationType.DDB_OPERATE_BLUETOOTH_DEVICE, null);
    }

    /**
     * 授权消息发送
     *
     * @param registerInfo
     */
    public static void sendMsgPermit(RegisterInfo registerInfo) {
        HashMap<String, String> map = new LinkedHashMap<>();
        map.put("type", registerInfo.type);
        map.put("sendPermit", registerInfo.sendPermit);
        ddbConnectNormal.sendRequest(OperationType.DDB_OPERATE_REGISTER_MSG, map);
    }

    //将要删除的列表转换成存放id的String传给笔
    private static String listToIds(ArrayList<CacheBookModel> deleteList) {
        StringBuilder idString = new StringBuilder();
        for (int i = 0; i < deleteList.size(); i++) {
            if (i == deleteList.size() - 1) {
                idString.append(deleteList.get(i).getId());
            } else {
                idString.append(deleteList.get(i).getId()).append(",");
            }
        }
        return idString.toString();
    }

    public static void removeSavedNet(String ssid) {
        HashMap<String, String> map = new LinkedHashMap<>();
        map.put("name", ssid);
        ddbConnectNormal.sendRequest(OperationType.REMOVE_SAVED_NETCONFIG, map);
    }

    public static void removeBondDevice(String deviceName) {
        HashMap<String, String> map = new LinkedHashMap<>();
        map.put("boxName", deviceName);
        ddbConnectNormal.sendRequest(OperationType.OPERATE_REMOVE_BONDED_DEVICES, map);
    }

    public static void sendMsg(String msg) {
        ddbConnectNormal.sendMsg(msg);
    }

    public static void sendTextMsg(int msgId, String text) {
        ddbConnectNormal.sendMsg("{\"data\":{\"active\":\"P\",\"msgid\":\"" + msgId + "\",\"text\":\"" + text + "\"},\"type\":1}");
    }

    public static void sendMsg(int type, String active, String text) {
        ddbConnectNormal.sendMsg("{\"type\":" + type + ",\"data\":{\"msgid\":" + msgid++ + ",\"text\":\"" + text + "\",\"active\":\"" + active + "\"}}");
    }

    public static void sendCfgMsg(String active) {
        ddbConnectNormal.sendMsg("{\"type\":1,\"data\":{\"msgid\":" + msgid++ + ",\"cfg\":{\"msgid\":0},\"active\":\"" + active + "\"}}");
    }

    public static void sendCfgMsg(int count, int cycle) {
        ddbConnectNormal.sendMsg("{\"type\":1,\"data\":{\"msgid\":" + msgid++ + ",\"cfg\":{\"msgid\":0},\"active\":\"S\"}}");
    }

    /**
     * 单条数据收发
     *
     * @param size 数据长度
     */
    public static void sendSRT(int type, int size) {
        ddbConnectNormal.sendMsg("{\"type\":" + type + ",\"data\":{\"msgid\":" + msgid++ + ",\"cfg\":{\"size\":" + size + ",\"parallels\":1,\"cycle\":0,\"count\":1,\"type\":2},\"active\":\"S\"}}");
    }

    /**
     * 单条数据收发
     *
     * @param size 数据长度
     */
    public static void sendSRT(int size, int count, int cycle) {
        ddbConnectNormal.sendMsg("{\"type\":1,\"data\":{\"msgid\":" + msgid++ + ",\"cfg\":{\"size\":" + size + ",\"parallels\":1,\"cycle\":" + cycle + ",\"count\":" + count + ",\"type\":2},\"active\":\"S\"}}");
    }

    public static void getTestWifiList(int num) {
        ddbConnectNormal.sendMsg("{\"type\":1,\"data\":{\"msgid\":" + msgid++ + ",\"active\":\"WD\",\"cfg\":{\"detect_num\":" + num + "}}}");
    }

    public static void connWifi(String wifiInfo) {
        ddbConnectNormal.sendMsg("{\"type\":1,\"data\":{\"msgid\":" + msgid++ + ",\"cfg\":{\"wifi_data\":[" + wifiInfo + "]},\"active\":\"WC\"}}");
    }

    public static void sendActive(String active) {
        ddbConnectNormal.sendMsg("{\"type\":1,\"data\":{\"msgid\":" + msgid++ + ",\"active\":\"" + active + "\"}}");
    }

    /**
     * 切换笔程序
     *
     * @param model T 测试程序  B  主程序
     */
    public static void switchModel(String model) {
        ddbConnectNormal.sendMsg("{\"type\":-1,\"data\":{\"msgid\":" + msgid++ + ",\"active\":\"" + model + "\"}}");
    }
}
