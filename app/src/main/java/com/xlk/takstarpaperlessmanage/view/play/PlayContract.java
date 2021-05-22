package com.xlk.takstarpaperlessmanage.view.play;

import android.view.Surface;

import com.xlk.takstarpaperlessmanage.base.BaseContract;

import java.util.List;

/**
 * @author Created by xlk on 2021/5/20.
 * @desc
 */
interface PlayContract {
    interface View extends BaseContract.View{

        void updateProgressUi(int per, String currentTime, String totalTime);

        void updateYuv(int w, int h, byte[] y, byte[] u, byte[] v);

        void setCodecType(int type);

        void setCanNotExit();

        void close();

        void notifyOnlineAdapter();

        void updateAnimator(int status);

        void updateTopTitle(String title);
    }
    interface Presenter extends BaseContract.Presenter{

        void queryMember();

        void setSurface(Surface surface);

        void releasePlay();

        String queryDevName(int deivceid);

        void playOrPause();

        void stopPlay();

        void cutVideoImg();

        void setPlayPlace(int progress);

        void mediaPlayOperate(List<Integer> ids, int value);

        void releaseMediaRes();
    }
}
