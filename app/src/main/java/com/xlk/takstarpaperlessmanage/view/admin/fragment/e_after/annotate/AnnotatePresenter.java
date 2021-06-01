package com.xlk.takstarpaperlessmanage.view.admin.fragment.e_after.annotate;

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
 * @author Created by xlk on 2021/5/27.
 * @desc
 */
class AnnotatePresenter extends BasePresenter<AnnotateContract.View> implements AnnotateContract.Presenter {

    public List<InterfaceFile.pbui_Item_MeetDirFileDetailInfo> files = new ArrayList<>();

    public AnnotatePresenter(AnnotateContract.View view) {
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
                if (id == Constant.ANNOTATION_DIR_ID) {
                    queryFile();
                }
                break;
            }
        }
    }

    @Override
    public void queryFile() {
        InterfaceFile.pbui_Type_MeetDirFileDetailInfo info = jni.queryMeetDirFile(Constant.ANNOTATION_DIR_ID);
        files.clear();
        if (info != null) {
            files.addAll(info.getItemList());
        }
        mView.updateFileList();
    }
}
