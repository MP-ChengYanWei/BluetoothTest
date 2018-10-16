package com.mp.sharedandroid.utils;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.text.TextUtils;

import java.io.IOException;

/**
 * 音频播放控制器
 */
public class AudioPlaybackManager implements OnCompletionListener {

    private static AudioPlaybackManager instance = new AudioPlaybackManager();
    private MediaPlayer mPlayer;
    private String mCurrentMediaPath;
    private OnPlayingListener mListener;

    public static AudioPlaybackManager getInstance() {
        return instance;
    }

    /**
     * 获取音频时长
     *
     * @param path
     * @return
     */
    public static int getDuration(String path) {
        if (TextUtils.isEmpty(path)) {
            return -1;
        }

        MediaPlayer player = new MediaPlayer();
        try {
            player.setDataSource(path);
            player.prepare();
            int dur = player.getDuration();
            player.release();
            return dur;
        } catch (IllegalArgumentException | SecurityException | IllegalStateException | IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private void startPlaying(String path) {
        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(this);
        mPlayer.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mPlayer.start();
            }
        });
        try {
            mPlayer.reset();
            mPlayer.setDataSource(path);
            mPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void releasePlayer() {
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    private void stopPlayer() {
        this.releasePlayer();
        if (mListener != null) {
            mListener.onStop();
            mListener = null;
        }
    }

    public void playAudio(String path, OnPlayingListener listener) {
        this.stopPlayer();
        mListener = listener;
        if (TextUtils.equals(mCurrentMediaPath, path)) {
            mCurrentMediaPath = null;
        } else {
            mCurrentMediaPath = path;
            this.startPlaying(mCurrentMediaPath);
            if (listener != null) {
                listener.onStart();
            }
        }
    }

    public void stopAudio() {
        this.stopPlayer();
        this.mCurrentMediaPath = null;
    }

    @Override
    public void onCompletion(MediaPlayer player) {
        this.releasePlayer();
        mCurrentMediaPath = null;
        if (mListener != null) {
            mListener.onComplete();
        }
    }

    public interface OnPlayingListener {
        void onStart();

        void onStop();

        void onComplete();
    }
}
