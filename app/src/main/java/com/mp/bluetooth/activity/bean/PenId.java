package com.mp.bluetooth.activity.bean;

/**
 * 获取笔ID
 * Created by cyw on 2018/10/16.
 */

public class PenId {

    /**
     * data : {"active":"IP","cfg":{"penid":"4efb0e49--1b523807-00000000-8a751caf"},"msgid":9}
     * type : 2
     */

    private DataBean data;
    private int type;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public static class DataBean {
        /**
         * active : IP
         * cfg : {"penid":"4efb0e49--1b523807-00000000-8a751caf"}
         * msgid : 9
         */

        private String active;
        private CfgBean cfg;
        private int msgid;

        public String getActive() {
            return active;
        }

        public void setActive(String active) {
            this.active = active;
        }

        public CfgBean getCfg() {
            return cfg;
        }

        public void setCfg(CfgBean cfg) {
            this.cfg = cfg;
        }

        public int getMsgid() {
            return msgid;
        }

        public void setMsgid(int msgid) {
            this.msgid = msgid;
        }

        public static class CfgBean {
            /**
             * penid : 4efb0e49--1b523807-00000000-8a751caf
             */

            private String penid;

            public String getPenid() {
                return penid;
            }

            public void setPenid(String penid) {
                this.penid = penid;
            }
        }
    }
}
