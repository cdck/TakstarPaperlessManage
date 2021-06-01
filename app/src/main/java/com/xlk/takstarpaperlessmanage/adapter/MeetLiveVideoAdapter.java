package com.xlk.takstarpaperlessmanage.adapter;

import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mogujie.tt.protobuf.InterfaceVideo;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.model.bean.VideoDevice;

import java.util.List;

import androidx.annotation.Nullable;

/**
 * @author xlk
 * @date 2020/3/18
 * @desc
 */
public class MeetLiveVideoAdapter extends BaseQuickAdapter<VideoDevice, BaseViewHolder> {
    private final String TAG = "MeetLiveVideoAdapter-->";
    private int selectDevId;
    private int selectId;
    private VideoDevice selectedItem;

    public MeetLiveVideoAdapter( @Nullable List<VideoDevice> data) {
        super(R.layout.item_meet_video, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, VideoDevice item) {
        helper.setText(R.id.i_m_v_tv, item.getName());
        int deviceid = item.getVideoDetailInfo().getDeviceid();
        int id = item.getVideoDetailInfo().getId();
        boolean selected = (selectDevId == deviceid) && (selectId == id);
        if (selected) selectedItem = item;
        boolean isOnline = item.getDeviceDetailInfo().getNetstate() == 1;
        TextView tv = helper.getView(R.id.i_m_v_tv);
        ImageView iv = helper.getView(R.id.i_m_v_iv);
        if (isOnline) {
            iv.setBackgroundResource(R.drawable.video_s);
        } else {
            iv.setBackgroundResource(R.drawable.icon_video_n);
        }
        iv.setSelected(selected);
        tv.setTextColor(selected ? getContext().getResources().getColor(R.color.white) : getContext().getResources().getColor(R.color.blue));
        helper.getView(R.id.i_m_v_ll).setSelected(selected);
    }

    public void setSelected(int devid, int id) {
        this.selectDevId = devid;
        this.selectId = id;
        LogUtils.d(TAG, "setSelected --> selectDevId= " + selectDevId + ", selectId= " + selectId);
        notifyDataSetChanged();
    }

    public VideoDevice getSelected() {
        return selectedItem;
    }

    public void notifySelect() {
        boolean has = false;
        for (int i = 0; i < getData().size(); i++) {
            InterfaceVideo.pbui_Item_MeetVideoDetailInfo videoDetailInfo = getData().get(i).getVideoDetailInfo();
            if (videoDetailInfo.getDeviceid() == selectDevId && videoDetailInfo.getId() == selectId) {
                has = true;
            }
        }
        if (!has) {
            selectDevId = -1;
            selectId = -1;
            selectedItem = null;
        }
        notifyDataSetChanged();
    }
}
