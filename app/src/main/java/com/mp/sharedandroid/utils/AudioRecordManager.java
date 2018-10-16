package com.mp.sharedandroid.utils;

import android.media.MediaRecorder;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 录制音频的控制器
 */
public class AudioRecordManager {

    private static final String TAG = "AudioRecordManager";
    private volatile static AudioRecordManager INSTANCE;
    private MediaRecorder mediaRecorder;
    private String audioFileName;
    private int progress = 0;
    private final Timer timer = new Timer(true);
    private RecordStatus recordStatus = RecordStatus.STOP;
    private TimerTask task;

    public enum RecordStatus {
        READY,
        START,
        STOP
    }

    private AudioRecordManager() {

    }

    public static AudioRecordManager getInstance() {
        if (INSTANCE == null) {
            synchronized (AudioRecordManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AudioRecordManager();
                }
            }
        }
        return INSTANCE;
    }

    public void init(String audioFileName) {
        this.audioFileName = audioFileName;
        recordStatus = RecordStatus.READY;
    }

    public void startRecord(final OnMaxRecordListener listener) {
        if (recordStatus == RecordStatus.READY) {
            progress = 0;
            Log.d(TAG, "startRecord()");
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setOutputFile(audioFileName);

            try {
                mediaRecorder.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaRecorder.start();
            recordStatus = RecordStatus.START;
            task = new TimerTask() {

                @Override
                public void run() {
                    ++progress;
                    //最大录制进度180秒,重复执行179次
                    if (progress > 179) {
                        stopRecord();
                        listener.onFinishRecordListener();
                    }
                }
            };
            //开始定时任务延时1秒,每1秒执行一次
            timer.schedule(task, 1000, 1000);
        } else {
            Log.e(TAG, "startRecord() invoke init first.");
        }
    }

    public void stopRecord() {
        if (recordStatus == RecordStatus.START) {
            Log.d(TAG, "startRecord()");
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            recordStatus = RecordStatus.STOP;
            audioFileName = null;
            task.cancel();
        } else {
            Log.e(TAG, "startRecord() invoke start first.");
        }
    }

    public void cancelRecord() {
        if (recordStatus == RecordStatus.START) {
            Log.d(TAG, "cancelRecord()");
            String file = audioFileName;
            stopRecord();
            File file1 = new File(file);
            file1.delete();
        } else {
            Log.e(TAG, "startRecord() invoke start first.");
        }
    }

    /**
     * 获得录音的音量，范围 0-32767, 归一化到0 ~ 1
     *
     * @return
     */
    public float getMaxAmplitude() {
        if (recordStatus == RecordStatus.START) {
            return mediaRecorder.getMaxAmplitude() * 1.0f / 32768;
        }
        return 0;
    }

}
