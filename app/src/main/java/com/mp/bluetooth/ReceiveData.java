package com.mp.bluetooth;

import java.util.List;

/**
 * Created by Mpen on 2018/9/7.
 */

public class ReceiveData {

    /**
     * data : {"data":{"active":"WD","cfg":{"wifi_list":["diandubi","Xiaomi_A745","48.whaty.com","·@费（#￥*8_~@","DIRECT-ypM2070 Series","47.whaty.com","TP-LINK_B510","chanpin_WLAN_2_4","du的iMac","island-300F30","whaty_huiyishi","2d_test","Powercore_North","DESKTOP-EAV55R9 6682","cpb","Xiaomi_62E5","46.whaty.com"]},"msgid":"13"}}
     * type : 1
     */

    private DataBeanX data;
    private int type;

    public DataBeanX getData() {
        return data;
    }

    public void setData(DataBeanX data) {
        this.data = data;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public static class DataBeanX {
        /**
         * data : {"active":"WD","cfg":{"wifi_list":["diandubi","Xiaomi_A745","48.whaty.com","·@费（#￥*8_~@","DIRECT-ypM2070 Series","47.whaty.com","TP-LINK_B510","chanpin_WLAN_2_4","du的iMac","island-300F30","whaty_huiyishi","2d_test","Powercore_North","DESKTOP-EAV55R9 6682","cpb","Xiaomi_62E5","46.whaty.com"]},"msgid":"13"}
         */

        private DataBean data;

        public DataBean getData() {
            return data;
        }

        public void setData(DataBean data) {
            this.data = data;
        }

        public static class DataBean {
            /**
             * active : WD
             * cfg : {"wifi_list":["diandubi","Xiaomi_A745","48.whaty.com","·@费（#￥*8_~@","DIRECT-ypM2070 Series","47.whaty.com","TP-LINK_B510","chanpin_WLAN_2_4","du的iMac","island-300F30","whaty_huiyishi","2d_test","Powercore_North","DESKTOP-EAV55R9 6682","cpb","Xiaomi_62E5","46.whaty.com"]}
             * msgid : 13
             */

            private String active;
            private CfgBean cfg;
            private String msgid;

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

            public String getMsgid() {
                return msgid;
            }

            public void setMsgid(String msgid) {
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
}
