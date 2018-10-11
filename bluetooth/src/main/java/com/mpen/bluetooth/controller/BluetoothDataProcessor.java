package com.mpen.bluetooth.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mpen.bluetooth.common.VideoInfos;
import com.mpen.bluetooth.constant.BTConstants;
import com.mpen.bluetooth.utils.Contants;
import com.mpen.bluetooth.utils.DataController;
import com.mpen.bluetooth.utils.PenUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by LYH on 2018/1/24.
 * 蓝牙数据业务层数据处理
 * （以广播的形式 发送给 相关  activity）
 */

public class BluetoothDataProcessor implements BluetoothManager.BluetoothListener {

    private static final String TAG = "BluetoothDataProcessor";

    //接收到数据层传输过来的数据，并分发，哪里需要哪里接收
    //只接受完整的 业务层数据。
    @Override
    public void onReveiveData(String message, Context content) {
        Log.d(TAG, "onReveiveData: " + message);
        Log.i("Linux", "<<<<JSON<<<<: " + message);
        DataController.getInstance().appendData("笔端消息：" + message);
        Intent receiveIntent = new Intent(BTConstants.RECEIVE_DATA);
        receiveIntent.putExtra("data", message);
        content.sendBroadcast(receiveIntent);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        int operateType = jsonObject.optInt("type");
        try {
            switch (DDBOperateType.getEnumByCode(operateType)) {
                case DDB_OPERATE_WIFI_LIST://获取wifi列表
                case DDB_OPERATE_REFRESH_WIFI://获取wifi列表
                    Intent wifiIntent = new Intent(BTConstants.GET_WIFI_LIST_ACTION);
                    wifiIntent.putExtra("wifiInfo", jsonObject.toString());
                    content.sendBroadcast(wifiIntent);
                    break;
                case DDB_OPERATE_DOWNLOAD_LIST://获取笔中的已下载列表
                    Intent downloadIntent = new Intent(BTConstants.GET_DOWNLOAD_LIST_ACTION);
                    downloadIntent.putExtra("downloadList", jsonObject.toString());
                    content.sendBroadcast(downloadIntent);
                    break;
                case DDB_OPERATE_DOWNLOAD_DELETE://删除笔中的下载内容
                    Log.d(TAG, "删除了图书！" + jsonObject.toString());
                    Intent spaceIntent = new Intent(BTConstants.UPDATE_DOWNLOAD_LIST_ACTION);
                    spaceIntent.putExtra("updateList", jsonObject.toString());
                    content.sendBroadcast(spaceIntent);
                    break;
                case DDB_OPERATE_DOWNLOAD_ADD://添加下载内容
                    break;
                case DDB_OPERATE_DOWNLOAD_UPDATE://更新笔里下载内容的进度
                    Intent updateDownloadIntent = new Intent(BTConstants.UPDATE_DOWNLOAD_LIST_ACTION);
                    updateDownloadIntent.putExtra("updateList", jsonObject.toString());
                    content.sendBroadcast(updateDownloadIntent);
                    break;
                case DDB_OPERATE_DOWNLOADING_BOOK://开始下载某本书
                    break;
                case DDB_OPERATE_PAUSE_BOOK://暂停下载某本书
                    break;
                case DDB_OPERATE_DEVICE_INFO://获取笔的所有信息
                    PenUtils.analyzePenInfo(jsonObject.toString());
                    Intent getPenInfoIntent = new Intent(BTConstants.GET_PEN_INFO_ACTION);
                    content.sendBroadcast(getPenInfoIntent);
                    break;
                case DDB_RETURN_PEN_UPDATE_STATE://检查笔里App是否有更新
                    Intent updateIntent = new Intent(BTConstants.GET_UPDATE_INFO_ACTION);
                    updateIntent.putExtra("updateInfo", jsonObject.toString());
                    content.sendBroadcast(updateIntent);
                    break;
                case DDB_RETURN_PEN_DOWN_APP_PROGRESS://获取点读笔固件更新进度
                    Intent updateProgressIntent = new Intent(BTConstants.GET_UPDATE_PROGRESS_ACTION);
                    updateProgressIntent.putExtra("updateProgress", jsonObject.toString());
                    content.sendBroadcast(updateProgressIntent);
                    break;
                case DDB_RETURN_PEN_DOWN_FINISH://笔的App更新下载完成
                    content.sendBroadcast(new Intent(BTConstants.UPDATE_FINISH_ACTION));
                    break;

                case REMOVE_SAVED_NETCONFIG:
                    break;
                default:
                    break;

                case DDB_VIDEO_BOOK_CODE://播放视频
                    try {
                        JSONObject obj = jsonObject.getJSONObject("data");
                        String path = obj.optString("url");
                        Bundle bundle = new Bundle();

                        if (obj.has("videos")) {
                            JSONArray videosObj = obj.optJSONArray("videos");
                            ArrayList<VideoInfos> infos = new Gson().fromJson(videosObj.toString(), new TypeToken<ArrayList<VideoInfos>>() {
                            }.getType());
                            bundle.putSerializable("videoinfos", infos);
                        }

                        // TODO: 2018/6/3
//                      占时缺少 DDBApplication.getContext(), VideoActivity.class
//                        Intent intent = new Intent(MpenBluetooth.getContext(), VideoActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        intent.putExtras(bundle);
//                        intent.putExtra("url", path);
//                        content.startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case DDB_RETURN_PEN_DOWN_APP_FAIL://更新下载失败
                    content.sendBroadcast(new Intent(BTConstants.UPDATE_FAIL_ACTION));
                    break;
                case DDB_OPERATE_REFRESH_RECORD://更新学习记录
                    content.sendBroadcast(new Intent(BTConstants.UPDATE_RECORD_ACTION));
                    break;
                case DDB_OPERATE_WIFI_STATE://笔的WIFI状态改变
                    Intent intent = new Intent(BTConstants.PEN_WIFI_STATE_ACTION);
                    try {
                        JSONObject dataObj = jsonObject.getJSONObject("data");
                        String wifiState = dataObj.optString("wifiState");
                        intent.putExtra("wifiState", wifiState);
                        content.sendBroadcast(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case DDB_OPERATE_BLUETOOTH_DEVICE:
                    Intent boxIntent = new Intent(BTConstants.BOX_DEVICE_FOUND_ACTION);
                    boxIntent.putExtra("boxdevice", jsonObject.toString());
                    content.sendBroadcast(boxIntent);
                    break;
                case OPERATE_BONDED_DEVICES:
                    Intent boxBondIntent = new Intent(Contants.BOX_DEVICE_BONDED_ACTION);
                    boxBondIntent.putExtra("bondDevice", jsonObject.toString());
                    content.sendBroadcast(boxBondIntent);
                    break;
            }
        } catch (NullPointerException e) {
            Log.e(TAG, "参数未同步！");
        }
    }
}
