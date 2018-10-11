package com.mp.bluetooth.activity.bean;

/**
 * 指令测试结果
 * Created by cyw on 2018/10/10.
 */

public class TestResult {

    /**
     * data : {"active":"S","cfg":{"count":1,"cycle":0,"parallels":1,"size":50,"type":2},"msgid":3}
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
         * active : S
         * cfg : {"count":1,"cycle":0,"parallels":1,"size":50,"type":2}
         * msgid : 3
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
             * count : 1
             * cycle : 0
             * parallels : 1
             * size : 50
             * type : 2
             */

            private int count;
            private int cycle;
            private int parallels;
            private int size;
            private int type;

            public int getCount() {
                return count;
            }

            public void setCount(int count) {
                this.count = count;
            }

            public int getCycle() {
                return cycle;
            }

            public void setCycle(int cycle) {
                this.cycle = cycle;
            }

            public int getParallels() {
                return parallels;
            }

            public void setParallels(int parallels) {
                this.parallels = parallels;
            }

            public int getSize() {
                return size;
            }

            public void setSize(int size) {
                this.size = size;
            }

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
            }
        }
    }
}
