/*
 * Copyright (C) 2013 yixia.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pgq.simplevideoplayer;


import android.app.Activity;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;


public class VideoViewPlayer extends Activity implements MediaPlayer.OnPreparedListener, MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener {

    /**
     * TODO: Set the path variable to a streaming video URL or a local media file
     * path.
     */
    private String path = "";
    private io.vov.vitamio.widget.VideoView mVideoView;
    private int mLayout = VideoView.VIDEO_LAYOUT_ZOOM;
    private GestureDetector mGestureDetector;
    private MediaController mMediaController;
    /** 是否需要自动恢复播放，用于自动暂停，恢复播放 */
    private boolean needResume;
    private android.widget.LinearLayout mLoadingView;
    private android.widget.TextView tvvideoCache;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!LibsChecker.checkVitamioLibs(this)) {
            return;
        }
        setContentView(R.layout.videoview);
        tvvideoCache = (TextView) findViewById(R.id.tv_videoCache);
        mLoadingView = (LinearLayout) findViewById(R.id.video_loading);
        mVideoView = (VideoView) findViewById(R.id.surface_view);

        mGestureDetector = new GestureDetector(this, new MyGestureListener());

        path = getIntent().getExtras().getString("path");

        if (path == "") {
            Toast.makeText(this, "没有视频地址", Toast.LENGTH_LONG).show();
            return;
        } else {
            mVideoView.setVideoURI(Uri.parse(path));
            mMediaController = new MediaController(this);
            mVideoView.setMediaController(mMediaController);
            mVideoView.requestFocus();
            mVideoView.setOnPreparedListener(this);
            mVideoView.setOnBufferingUpdateListener(this);
            mVideoView.setOnInfoListener(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mVideoView != null)
            mVideoView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mVideoView != null)
            mVideoView.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mVideoView != null)
            mVideoView.stopPlayback();
    }

    @Override
    public void onPrepared(final MediaPlayer mediaPlayer) {
        mediaPlayer.setPlaybackSpeed(1.0f);
        startPlayer();
    }

    @Override
    public boolean onInfo(MediaPlayer arg0, int arg1, int arg2) {
        switch (arg1) {
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                //开始缓存，暂停播放
                if (isPlaying()) {
                    stopPlayer();
                    needResume = true;
                }
                mLoadingView.setVisibility(View.VISIBLE);
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                //缓存完成，继续播放
                if (needResume) {
                    startPlayer();
                }
                mLoadingView.setVisibility(View.GONE);
                break;
            case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
                //显示 下载速度
//                Log.e("download rate:" + arg2);
//                mListener.onDownloadRateChanged(arg2);
                mMediaController.setDownSpeed(arg2);
                break;
        }
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (mVideoView != null)
            mVideoView.setVideoLayout(mLayout, 0);
        super.onConfigurationChanged(newConfig);
    }

    private void stopPlayer() {
        if (mVideoView != null)
            mVideoView.pause();
    }

    private void startPlayer() {
        if (mVideoView != null)
            mVideoView.start();
    }

    private boolean isPlaying() {
        return mVideoView != null && mVideoView.isPlaying();
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        tvvideoCache.setText(getResources().getString(R.string.video_layout_loading) + "\n" + percent + "%");
    }

    public class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (mVideoView.isPlaying()) {
                if (mLayout == VideoView.VIDEO_LAYOUT_ZOOM) {
                    mLayout = VideoView.VIDEO_LAYOUT_ORIGIN;
                } else {
                    mLayout++;
                }
                if (mVideoView != null) {
                    mVideoView.setVideoLayout(mLayout, 0);
                }
            }
            return true;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mGestureDetector.onTouchEvent(event)) {
            return true;
        }
        return super.onTouchEvent(event);
    }
}
