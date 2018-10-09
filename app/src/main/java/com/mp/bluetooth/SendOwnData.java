package com.mp.bluetooth;

/**
 * Created by Mpen on 2018/9/6.
 */

public class SendOwnData {

    private String msgid;
    private String active;
    private String text;
    private SendData cfg;

    public String getMsgid() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public SendData getConfig() {
        return cfg;
    }

    public void setConfig(SendData config) {
        this.cfg = config;
    }
}
