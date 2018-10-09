package com.mp.bluetooth;

/**
 * Created by Mpen on 2018/9/5.
 */

public class SendDataModel {

    private int type;
    private SendOwnData data;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public SendOwnData getData() {
        return data;
    }

    public void setData(SendOwnData data) {
        this.data = data;
    }
}
