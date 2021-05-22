package com.xlk.takstarpaperlessmanage.view.admin.fragment.c_pre.table;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceBase;
import com.mogujie.tt.protobuf.InterfaceFile;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceTablecard;
import com.xlk.takstarpaperlessmanage.base.BasePresenter;
import com.xlk.takstarpaperlessmanage.model.Constant;
import com.xlk.takstarpaperlessmanage.model.EventMessage;
import com.xlk.takstarpaperlessmanage.model.EventType;
import com.xlk.takstarpaperlessmanage.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by xlk on 2021/5/22.
 * @desc
 */
class TablePresenter extends BasePresenter<TableContract.View> implements TableContract.Presenter {
    public List<InterfaceTablecard.pbui_Item_MeetTableCardDetailInfo> tables = new ArrayList<>();
    private int bgMediaId;
    public List<InterfaceFile.pbui_Item_MeetDirFileDetailInfo> backgroundImages = new ArrayList<>();

    public TablePresenter(TableContract.View view) {
        super(view);
    }

    @Override
    protected void busEvent(EventMessage msg) throws InvalidProtocolBufferException {
        switch (msg.getType()) {
            case EventType.BUS_TABLE_CARD_BG: {
                String filePath = (String) msg.getObjects()[0];
                mView.updateTableCardBg(filePath);
                break;
            }
            //会议双屏显示信息变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETTABLECARD_VALUE: {
                LogUtil.i(TAG, "busEvent 会议双屏显示信息变更通知");
                queryTableCard();
                break;
            }
            //会议目录文件变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETDIRECTORYFILE_VALUE: {
                byte[] bytes = (byte[]) msg.getObjects()[0];
                InterfaceBase.pbui_MeetNotifyMsgForDouble info = InterfaceBase.pbui_MeetNotifyMsgForDouble.parseFrom(bytes);
                int opermethod = info.getOpermethod();
                int id = info.getId();
                int subid = info.getSubid();
                LogUtils.i(TAG, "BusEvent 会议目录文件变更通知 id=" + id + ",subId=" + subid + ",opermethod=" + opermethod);
                queryBackgroundImage();
                break;
            }
        }
    }

    @Override
    public void queryBackgroundImage() {
        InterfaceFile.pbui_TypePageResQueryrFileInfo info = jni.queryFile(0,
                InterfaceMacro.Pb_MeetFileQueryFlag.Pb_MEET_FILETYPE_QUERYFLAG_ATTRIB_VALUE
                , 0, 0, 0, InterfaceMacro.Pb_MeetFileAttrib.Pb_MEETFILE_ATTRIB_TABLECARD_VALUE, 1, 0);
        backgroundImages.clear();
        if (info != null) {
            backgroundImages.addAll(info.getItemList());
        }
        mView.updateBackgroundImageList();
    }

    @Override
    public void queryTableCard() {
        InterfaceTablecard.pbui_Type_MeetTableCardDetailInfo info = jni.queryTableCard();
        if (info != null) {
            bgMediaId = info.getBgphotoid();
            String fileName = jni.queryFileNameByMediaId(bgMediaId);
            String filePath = Constant.config_dir + fileName;
            boolean fileExists = FileUtils.isFileExists(filePath);
            if (fileExists && !fileName.isEmpty()) {
                mView.updateTableCardBg(filePath);
            } else if (bgMediaId != 0) {
                jni.downloadFile(filePath, bgMediaId, 1, 1, Constant.DOWNLOAD_TABLE_CARD_BG);
            }
            tables.clear();
            tables.addAll(info.getItemList());
            mView.updateTableCard();
        }
    }

    @Override
    public void clearBackgroundImage() {
        bgMediaId = 0;
    }

    @Override
    public void save(int modflag, List<InterfaceTablecard.pbui_Item_MeetTableCardDetailInfo> tableCardData) {
        jni.modifyTableCard(bgMediaId, modflag, tableCardData);
    }
}
