package com.xlk.takstarpaperlessmanage.view.admin.fragment.c_pre.agenda;

import com.blankj.utilcode.util.LogUtils;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceAgenda;
import com.mogujie.tt.protobuf.InterfaceBase;
import com.mogujie.tt.protobuf.InterfaceFile;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.xlk.takstarpaperlessmanage.base.BasePresenter;
import com.xlk.takstarpaperlessmanage.model.Constant;
import com.xlk.takstarpaperlessmanage.model.EventMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by xlk on 2021/5/18.
 * @desc
 */
public class AgendaPresenter extends BasePresenter<AgendaContract.View> implements AgendaContract.Presenter {

    public List<InterfaceAgenda.pbui_ItemAgendaTimeInfo> agendas = new ArrayList<>();
    public List<InterfaceFile.pbui_Item_MeetDirFileDetailInfo> sharedFiles = new ArrayList<>();
    public List<InterfaceFile.pbui_Item_MeetDirDetailInfo> dirInfos = new ArrayList<>();

    public AgendaPresenter(AgendaContract.View view) {
        super(view);
    }

    @Override
    protected void busEvent(EventMessage msg) throws InvalidProtocolBufferException {
        switch (msg.getType()) {
            //会议目录变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETDIRECTORY_VALUE: {
                LogUtils.i(TAG, "busEvent 会议目录变更通知");
                queryMeetDir();
                break;
            }
            //议程变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETAGENDA_VALUE: {
                queryAgenda();
                break;
            }
            //会议目录文件变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETDIRECTORYFILE_VALUE:
                byte[] bytes = (byte[]) msg.getObjects()[0];
                InterfaceBase.pbui_MeetNotifyMsgForDouble info = InterfaceBase.pbui_MeetNotifyMsgForDouble.parseFrom(bytes);
                int opermethod = info.getOpermethod();
                int id = info.getId();
                int subid = info.getSubid();
                LogUtils.i(TAG, "busEvent 会议目录文件变更通知 id=" + id + ", subid=" + subid + ", opermethod=" + opermethod);
                if (id == Constant.SHARE_DIR_ID) {
                    queryShareFile();
                }
                break;
        }
    }

    @Override
    public void queryAgenda() {
        InterfaceAgenda.pbui_meetAgenda info = jni.queryAgenda();
        if (info != null) {
            int agendatype = info.getAgendatype();
            if (agendatype == InterfaceMacro.Pb_AgendaType.Pb_MEET_AGENDA_TYPE_TEXT_VALUE) {
                mView.updateAgendaText(info.getText().toStringUtf8());
            } else if (agendatype == InterfaceMacro.Pb_AgendaType.Pb_MEET_AGENDA_TYPE_FILE_VALUE) {
                try {
                    int mediaid = info.getMediaid();
                    byte[] bytes = jni.queryFileProperty(InterfaceMacro.Pb_MeetFilePropertyID.Pb_MEETFILE_PROPERTY_NAME.getNumber(), mediaid);
                    InterfaceBase.pbui_CommonTextProperty textProperty = InterfaceBase.pbui_CommonTextProperty.parseFrom(bytes);
                    String fileName = textProperty.getPropertyval().toStringUtf8();
                    mView.updateAgendaFile(mediaid, fileName);
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            } else {
                agendas.clear();
                agendas.addAll(info.getItemList());
                mView.updateAgendaList();
            }
        }
    }

    @Override
    public void queryMeetDir() {
        InterfaceFile.pbui_Type_MeetDirDetailInfo info = jni.queryMeetDir();
        dirInfos.clear();
        if (info != null) {
            List<InterfaceFile.pbui_Item_MeetDirDetailInfo> itemList = info.getItemList();
            for (int i = 0; i < itemList.size(); i++) {
                InterfaceFile.pbui_Item_MeetDirDetailInfo item = itemList.get(i);
                if (item.getId() != Constant.ANNOTATION_DIR_ID) {
                    dirInfos.add(item);
                }
            }
        }
        mView.updateDirList();
    }

    @Override
    public void queryShareFile() {
        InterfaceFile.pbui_Type_MeetDirFileDetailInfo info = jni.queryMeetDirFile(Constant.SHARE_DIR_ID);
        sharedFiles.clear();
        if (info != null) {
            List<InterfaceFile.pbui_Item_MeetDirFileDetailInfo> itemList = info.getItemList();
            for (int i = 0; i < itemList.size(); i++) {
                InterfaceFile.pbui_Item_MeetDirFileDetailInfo item = itemList.get(i);
                String fileName = item.getName().toStringUtf8();
                if (fileName.endsWith(".pdf") || fileName.endsWith(".doc") || fileName.endsWith(".docx")) {
                    sharedFiles.add(item);
                }
            }
        }
        mView.updateAgendaFileList();
    }
}
