package com.mp.bluetooth.activity.bean;

/**
 * Created by Mpen on 2018/10/10.
 */

public class TestData {

    /**
     * data : {"active":"P","msgid":8,"text":"[{\"id\":\"004737a884ec45a6ac42d7ce4afa615c\",\"coverCo"}
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
         * active : P
         * msgid : 8
         * text : [{"id":"004737a884ec45a6ac42d7ce4afa615c","coverCo
         */

        private String active;
        private int msgid;
        private String text;

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

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
