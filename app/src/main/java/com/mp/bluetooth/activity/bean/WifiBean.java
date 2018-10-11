package com.mp.bluetooth.activity.bean;

import java.util.List;

/**
 * Created by Mpen on 2018/10/10.
 */

public class WifiBean {

    /**
     * data : {"active":"WD","cfg":{"wifi_list":["diandubi","48.whaty.com","新年快乐，我要放假","47.whaty.com","diandubi123","·@费（#￥*8_~@俺","DIRECT-ypM2070 Series","Powercore_North","cj_whaty","360免费WiFi-A5","zhaoshifeng","78910","43.whaty.com","46.whaty.com","！@#￥%\u2026\u2026&*（玥）"]},"msgid":3}
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
         * active : WD
         * cfg : {"wifi_list":["diandubi","48.whaty.com","新年快乐，我要放假","47.whaty.com","diandubi123","·@费（#￥*8_~@俺","DIRECT-ypM2070 Series","Powercore_North","cj_whaty","360免费WiFi-A5","zhaoshifeng","78910","43.whaty.com","46.whaty.com","！@#￥%\u2026\u2026&*（玥）"]}
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
            private List<String> wifi_list;

            public List<String> getWifi_list() {
                return wifi_list;
            }

            public void setWifi_list(List<String> wifi_list) {
                this.wifi_list = wifi_list;
            }
        }
    }
}
