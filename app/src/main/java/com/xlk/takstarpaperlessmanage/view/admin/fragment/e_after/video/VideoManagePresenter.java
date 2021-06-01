package com.xlk.takstarpaperlessmanage.view.admin.fragment.e_after.video;

import com.blankj.utilcode.util.LogUtils;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceBase;
import com.mogujie.tt.protobuf.InterfaceFile;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.xlk.takstarpaperlessmanage.base.BasePresenter;
import com.xlk.takstarpaperlessmanage.model.Constant;
import com.xlk.takstarpaperlessmanage.model.EventMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by xlk on 2021/5/29.
 * @desc
 */
class VideoManagePresenter extends BasePresenter<VideoManageContract.View> implements VideoManageContract.Presenter {

    public List<InterfaceFile.pbui_Item_MeetDirFileDetailInfo> videoFiles = new ArrayList<>();

    public VideoManagePresenter(VideoManageContract.View view) {
        super(view);
    }

    @Override
    protected void busEvent(EventMessage msg) throws InvalidProtocolBufferException {
        switch (msg.getType()) {
            //会议目录文件变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETDIRECTORYFILE_VALUE: {
                byte[] bytes = (byte[]) msg.getObjects()[0];
                InterfaceBase.pbui_MeetNotifyMsgForDouble info = InterfaceBase.pbui_MeetNotifyMsgForDouble.parseFrom(bytes);
                int opermethod = info.getOpermethod();
                int id = info.getId();
                int subid = info.getSubid();
                LogUtils.i(TAG, "busEvent 会议目录文件变更通知 id=" + id + ",subid=" + subid + ",opermethod=" + opermethod);
                queryVideoFile();
                break;
            }
        }
    }

    @Override
    public void queryVideoFile() {
        int queryFlag = InterfaceMacro.Pb_MeetFileQueryFlag.Pb_MEET_FILETYPE_QUERYFLAG_FILETYPE_VALUE;
        int fileType = 0x00000020;//InterfaceMacro.Pb_MeetFileType.Pb_MEET_FILETYPE_RECORD_VALUE;
        InterfaceFile.pbui_TypePageResQueryrFileInfo info = jni.queryFile(0, queryFlag, 0, 0, fileType, 0, 0, 0);
        videoFiles.clear();
        if (info != null) {
            List<InterfaceFile.pbui_Item_MeetDirFileDetailInfo> itemList = info.getItemList();
            for (int i = 0; i < itemList.size(); i++) {
                InterfaceFile.pbui_Item_MeetDirFileDetailInfo item = itemList.get(i);
                int mediaid = item.getMediaid();
                if (Constant.isRecord(mediaid)) {
                    videoFiles.add(item);
                }
            }
        }
        mView.updateVideoFileList();
    }
}
