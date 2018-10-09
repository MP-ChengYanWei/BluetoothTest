package com.mpen.bluetooth.common;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * 缓存空间页面“书”(和笔交互使用)
 * Created by npw on 2015/11/20.
 */
public class DDBCacheBookModel implements Serializable {
    public DDBCacheBookModel() {
    }

    /**
     * 已下载的进度
     */
    private int progress;

    /**
     * 书的id
     */
    private String id;
    /**
     * 书的下载状态
     * 0:完成下载,1:正在下载,2:暂停,3:等待
     */
    private String status;
    /**
     * 已下载的大小
     */
    private int downloadSize;
    /**
     * 总大小
     */
    private int totalSize;
    /**
     * 书的封面
     */
    private String photo;
    /**
     * 书的名称
     */
    private String name;
    /**
     * 是否下载完成:0  未完成；1  已完成
     */
    private String finish;

    /**
     * 在编辑界面该书是否被选中
     */
    public boolean isCheck = false;

    public String getFinish() {
        return finish;
    }

    public void setFinish(String finish) {
        this.finish = finish;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getDownloadSize() {
        return downloadSize;
    }

    public void setDownloadSize(int downloadSize) {
        this.downloadSize = downloadSize;
    }

    public int getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(int totalSize) {
        this.totalSize = totalSize;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DDBCacheBookModel modelWithData(Object data) {
        DDBCacheBookModel model = new DDBCacheBookModel();
        try {
            JSONObject courseInfo = new JSONObject(data.toString());
            model.setId(courseInfo.optString("id"));
            model.setProgress(courseInfo.optInt("progress"));
            model.setStatus(courseInfo.optString("status"));
            model.setDownloadSize(courseInfo.optInt("downloadSize"));
            model.setTotalSize(courseInfo.optInt("totalSize"));
            model.setPhoto(courseInfo.optString("photo"));
            model.setName(courseInfo.optString("name"));
            model.setFinish(courseInfo.optString("finish"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return model;
    }

    @Override
    public String toString() {
        return "DDBCacheBookModel{" +
                "progress=" + progress +
                ", id='" + id + '\'' +
                ", status='" + status + '\'' +
                ", downloadSize=" + downloadSize +
                ", totalSize=" + totalSize +
                ", photo='" + photo + '\'' +
                ", name='" + name + '\'' +
                ", finish='" + finish + '\'' +
                ", isCheck=" + isCheck +
                '}';
    }

    //测试添加数据使用
    public DDBCacheBookModel(int str1, String str2, int str3, int str4, String str5, String str6, String str7) {
        this.progress = str1;
        this.status = str2;
        this.downloadSize = str3;
        this.totalSize = str4;
        this.photo = str5;
        this.name = str6;
        this.id = str7;
    }

}
