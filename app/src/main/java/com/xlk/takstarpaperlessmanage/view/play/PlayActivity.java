package com.xlk.takstarpaperlessmanage.view.play;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.base.BaseActivity;
import com.xlk.takstarpaperlessmanage.model.Constant;
import com.xlk.takstarpaperlessmanage.model.GlobalValue;
import com.xlk.takstarpaperlessmanage.ui.gl.MyGLSurfaceView;
import com.xlk.takstarpaperlessmanage.ui.gl.WlOnGlSurfaceViewOncreateListener;

import java.util.Timer;
import java.util.TimerTask;

import androidx.constraintlayout.widget.ConstraintLayout;

import static com.xlk.takstarpaperlessmanage.model.GlobalValue.isMandatoryPlaying;

public class PlayActivity extends BaseActivity<PlayPresenter> implements PlayContract.View, WlOnGlSurfaceViewOncreateListener {

    private ConstraintLayout video_root_layout;
    private RelativeLayout play_mp3_view;
    private ImageView opticalDisk;
    private ImageView plectrum;
    private TextView video_top_title;
    private MyGLSurfaceView video_view;
    private PopupWindow popView;
    private int playAction;
    private TextView pop_video_current_time, pop_video_time;
    private SeekBar seekBar;
    private LinearLayout pop_video_schedule;
    private int lastPer;
    private String lastSec;
    private String lastTotal;
    private int mStatus = -1;
    private ObjectAnimator opticalDiskAnimator;
    private ObjectAnimator plectrumAnimator;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_play;
    }

    @Override
    public void initView() {
        video_root_layout = (ConstraintLayout) findViewById(R.id.video_root_layout);
        play_mp3_view = (RelativeLayout) findViewById(R.id.play_mp3_view);
        opticalDisk = (ImageView) findViewById(R.id.opticalDisk);
        plectrum = (ImageView) findViewById(R.id.plectrum);
        video_top_title = (TextView) findViewById(R.id.video_top_title);
        video_view = (MyGLSurfaceView) findViewById(R.id.play_view);
        video_root_layout.setOnClickListener(v -> {
            if (popView != null && popView.isShowing()) {
                video_top_title.setVisibility(View.GONE);
                popView.dismiss();
                return;
            }
            video_top_title.setVisibility(View.VISIBLE);
            createPop();
        });
    }

    @Override
    protected PlayPresenter initPresenter() {
        return new PlayPresenter(this);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        showVideoOrMusicUI(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        showVideoOrMusicUI(intent);
    }

    private void createPop() {
        View inflate = LayoutInflater.from(this).inflate(R.layout.pop_video_bottom, null);
        popView = new PopupWindow(inflate, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        popView.setBackgroundDrawable(new BitmapDrawable());
        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
        popView.setTouchable(true);
        // true:设置触摸外面时消失
        popView.setOutsideTouchable(true);
        popView.setFocusable(true);
//        popView.setAnimationStyle(R.style.pop_Animation);
        popView.showAtLocation(video_root_layout, Gravity.BOTTOM, 0, 0);
        pop_video_current_time = inflate.findViewById(R.id.pop_video_current_time);
        pop_video_time = inflate.findViewById(R.id.pop_video_time);
        seekBar = inflate.findViewById(R.id.pop_video_seekBar);
        pop_video_schedule = inflate.findViewById(R.id.pop_video_schedule);
        pop_video_schedule.setVisibility(playAction == InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_STREAMPLAY_VALUE ? View.GONE : View.VISIBLE);

        /**  手动设置隐藏PopupWindow时保存的信息  */
        seekBar.setProgress(lastPer);
        pop_video_current_time.setText(lastSec);
        pop_video_time.setText(lastTotal);

        inflate.findViewById(R.id.pop_video_play).setOnClickListener(v -> {
            presenter.playOrPause();
        });
        inflate.findViewById(R.id.pop_video_stop).setOnClickListener(v -> {
            presenter.stopPlay();
            popView.dismiss();
            finish();
        });
        inflate.findViewById(R.id.pop_video_screen_shot).setOnClickListener(v -> {
            presenter.cutVideoImg();
            video_view.cutVideoImg();
        });
        inflate.findViewById(R.id.pop_video_launch_screen).setOnClickListener(v -> {
//            showScreenPop(1);
        });
        inflate.findViewById(R.id.pop_video_stop_screen).setOnClickListener(v -> {
//            showScreenPop(2);
            presenter.stopPlay();
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                presenter.setPlayPlace(seekBar.getProgress());
            }
        });
        popView.setOnDismissListener(() -> video_top_title.setVisibility(View.GONE));
    }

    private void showVideoOrMusicUI(Intent intent) {
        GlobalValue.isVideoPlaying = true;
        if (isMandatoryPlaying) {
            setCanNotExit();
        }
        playAction = intent.getIntExtra(Constant.EXTRA_VIDEO_ACTION, -1);
        int subtype = intent.getIntExtra(Constant.EXTRA_VIDEO_SUBTYPE, -1);
        int deivceid = intent.getIntExtra(Constant.EXTRA_VIDEO_DEVICE_ID, -1);
        if (subtype == Constant.MEDIA_FILE_TYPE_MP3) {
            //如果当前播放的是mp3文件，则只显示MP3控件
            play_mp3_view.setVisibility(View.VISIBLE);
            video_view.setVisibility(View.GONE);
            presenter.releasePlay();
        } else {
            play_mp3_view.setVisibility(View.GONE);
            video_view.setVisibility(View.VISIBLE);
            video_view.setOnGlSurfaceViewOncreateListener(this);
            if (playAction == InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_STREAMPLAY_VALUE) {
                String devName = presenter.queryDevName(deivceid);
                LogUtils.i(TAG, "showVideoOrMusicUI devName=" + devName);
                video_top_title.setText(devName);
            }
        }
        if (popView != null && popView.isShowing()) {
            pop_video_schedule.setVisibility(playAction == InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_STREAMPLAY_VALUE ? View.GONE : View.VISIBLE);
        }
    }

    public void close() {
        //500毫秒之后再判断是否退出
        GlobalValue.haveNewPlayInform = false;
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                LogUtils.i(TAG, "close :   --->>> haveNewPlayInform= " + GlobalValue.haveNewPlayInform);
                if (!GlobalValue.haveNewPlayInform) {
                    finish();
                } else {
                    timer.cancel();
                    timer.purge();
                }
            }
        }, 500);
    }

    @Override
    public void notifyOnlineAdapter() {

    }

    @Override
    public void updateAnimator(int status) {
        if (mStatus == status) return;
        mStatus = status;
        LogUtils.i(TAG, "updateAnimator 新的状态：" + mStatus);
        //0=播放中，1=暂停，2=停止,3=恢复
        switch (mStatus) {
            case 0:
                startAnimator();
                break;
            case 1:
                stopAnimator();
                break;
        }
    }

    private void plectrum(float from, float to, long duration) {
        plectrumAnimator = ObjectAnimator.ofFloat(plectrum, "rotation", from, to);
        plectrum.setPivotX(1);
        plectrum.setPivotY(1);
        plectrumAnimator.setDuration(duration);
        plectrumAnimator.start();
    }

    private void startAnimator() {
        LogUtils.i(TAG, "startAnimator ");
        plectrum(0f, 30f, 500);
        opticalDiskAnimator = ObjectAnimator.ofFloat(opticalDisk, "rotation", 0f, 360f);
        opticalDiskAnimator.setDuration(3000);
        opticalDiskAnimator.setRepeatCount(ValueAnimator.INFINITE);
        opticalDiskAnimator.setRepeatMode(ValueAnimator.RESTART);
        opticalDiskAnimator.setInterpolator(new LinearInterpolator());
        opticalDiskAnimator.start();
    }

    private void stopAnimator() {
        LogUtils.i(TAG, "stopAnimator ");
        if (opticalDiskAnimator != null) {
            opticalDiskAnimator.cancel();
            opticalDiskAnimator = null;
        }
        plectrum(30f, 0f, 500L);
    }

    @Override
    public void updateTopTitle(String title) {
        video_top_title.setText(title);
    }

    @Override
    public void updateProgressUi(int per, String currentTime, String totalTime) {
        if (seekBar != null && pop_video_time != null && pop_video_current_time != null) {
            lastPer = per;
            lastSec = currentTime;
            lastTotal = totalTime;
            seekBar.setProgress(per);
            pop_video_current_time.setText(currentTime);
            pop_video_time.setText(totalTime);
        }
    }

    @Override
    public void updateYuv(int w, int h, byte[] y, byte[] u, byte[] v) {
        setCodecType(0);
        video_view.setFrameData(w, h, y, u, v);
    }

    @Override
    public void setCodecType(int type) {
        video_view.setCodecType(type);
    }

    public void setCanNotExit() {
        if (popView != null && popView.isShowing()) {
            popView.dismiss();
        }
        video_root_layout.setClickable(false);
    }

    @Override
    public void onGlSurfaceViewOncreate(Surface surface) {
        presenter.setSurface(surface);
    }

    @Override
    public void onCutVideoImg(Bitmap bitmap) {

    }

    @Override
    public void onBackPressed() {
        if (!GlobalValue.isMandatoryPlaying) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        if (popView != null && popView.isShowing()) {
            popView.dismiss();
        }
        GlobalValue.isVideoPlaying = false;
        isMandatoryPlaying = false;
        presenter.stopPlay();
        presenter.releaseMediaRes();
        presenter.releasePlay();
        super.onDestroy();
    }
}