package com.mp.bluetooth.activity.bean;

import java.util.Date;

/**
 * Created by Mpen on 2018/10/16.
 */

public class WifiTestResult {

    /**
     * id : 47542fa32600441faf08fd7dabb7fe76
     * fkPenId : 4efb0e49--1b523807-00000000-8a751caf
     * name : diandubi
     * password : 888888
     * createTime : Oct 10, 2018 7:46:08 PM
     * verifyCode : Oct 10, 2018 7:46:08 PM
     */

    private String id;
    private String fkPenId;//笔id
    private String name;//WIFI名称
    private String password;//WIFI密码
    private String verifyCode;//测试数据
    private Date createTime;//创建时间

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFkPenId() {
        return fkPenId;
    }

    public void setFkPenId(String fkPenId) {
        this.fkPenId = fkPenId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }
}
