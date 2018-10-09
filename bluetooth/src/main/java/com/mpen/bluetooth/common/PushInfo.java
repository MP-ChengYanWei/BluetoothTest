package com.mpen.bluetooth.common;

import java.util.ArrayList;

/**
 * 功能说明：推送消息的model
 *
 *
 * 作者：ZSC on 2017/5/16 17:47
 */
public class PushInfo {
    public final String action = "pushApp";
    public String type;
    public String battery;
    public String path;
    public ArrayList videos;
}
