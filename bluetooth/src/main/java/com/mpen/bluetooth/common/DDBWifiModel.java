package com.mpen.bluetooth.common;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * 关于wifi的model
 * Created by npw on 2015/11/10.
 */
public class DDBWifiModel implements Serializable {

    /**
     * wifi的名称
     */
    private String name;
    /**
     * wifi的强度
     * 满格：大于-40;
     * 两格：小于-40，大于-85;
     * 一格：小于-85，大于-90;
     * 小于-90，等同于无信号
     */
    private String level;
    /**
     * wifi的类型：1:没有密码;2:用wep加密;3:用wap加密
     */
    private String type;

    /**
     * 是否已连接该wifi："0":未连接；"1":已连接
     */
    private String connected;

    /**
     * 是否已保存该wifi："0":未保存；"1":已保存
     */
    private String save;

    public String getConnected() {
        return connected;
    }

    public void setConnected(String connected) {
        this.connected = connected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSave() {
        return save;
    }

    public void setSave(String save) {
        this.save = save;
    }

    public DDBWifiModel modelWithData(Object data) {
        if (data != null && data.toString().length() > 0) {
            DDBWifiModel model = new DDBWifiModel();
            try {
                JSONObject wifiInfo = new JSONObject(data.toString());
                model.setName(wifiInfo.optString("name"));
                model.setLevel(wifiInfo.optString("level"));
                model.setType(wifiInfo.optString("type"));
                model.setConnected(wifiInfo.optString("connected"));
                model.setSave(wifiInfo.optString("save"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return model;
        }
        return null;
    }

    public String getId() {
        return null;
    }

    @Override
    public String toString() {
        return "DDBWifiModel{" +
                "name='" + name + '\'' +
                ", level='" + level + '\'' +
                ", type='" + type + '\'' +
                ", connected='" + connected + '\'' +
                '}';
    }
}
