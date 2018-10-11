package com.mp.bluetooth.activity.bean;

/**
 * WIFI连接结果解析
 * Created by cyw on 2018/10/11.
 */

public class WifiConnResult {

    /**
     * data : {"active":"WC","msgid":5}
     * ret : 0
     * type : 1
     */

    private DataBean data;
    private int ret;
    private int type;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public static class DataBean {
        /**
         * active : WC
         * msgid : 5
         */

        private String active;
        private int msgid;

        public String getActive() {
            return active;
        }

        public void setActive(String active) {
            this.active = active;
        }

        public int getMsgid() {
            return msgid;
        }

        public void setMsgid(int msgid) {
            this.msgid = msgid;
        }
    }
}
