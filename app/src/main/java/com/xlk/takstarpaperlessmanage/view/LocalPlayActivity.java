package com.xlk.takstarpaperlessmanage.view;

import androidx.appcompat.app.AppCompatActivity;
import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;

import android.os.Bundle;
import android.view.View;

import com.xlk.takstarpaperlessmanage.R;

public class LocalPlayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_play);
        JzvdStd jz_video = findViewById(R.id.jz_video);
        Bundle extras = getIntent().getExtras();
        String filePath = extras.getString("filePath");
        String fileName = extras.getString("fileName");
        jz_video = findViewById(R.id.jz_video);
        jz_video.setUp(filePath, fileName);
        jz_video.startVideo();
    }

    @Override
    public void onBackPressed() {
        if (Jzvd.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Jzvd.releaseAllVideos();
    }
}