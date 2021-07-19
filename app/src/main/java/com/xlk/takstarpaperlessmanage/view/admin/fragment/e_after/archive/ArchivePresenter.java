package com.xlk.takstarpaperlessmanage.view.admin.fragment.e_after.archive;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceAdmin;
import com.mogujie.tt.protobuf.InterfaceAgenda;
import com.mogujie.tt.protobuf.InterfaceBase;
import com.mogujie.tt.protobuf.InterfaceBullet;
import com.mogujie.tt.protobuf.InterfaceFile;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceMeet;
import com.mogujie.tt.protobuf.InterfaceMember;
import com.mogujie.tt.protobuf.InterfaceRoom;
import com.mogujie.tt.protobuf.InterfaceSignin;
import com.mogujie.tt.protobuf.InterfaceVote;
import com.xlk.takstarpaperlessmanage.base.BasePresenter;
import com.xlk.takstarpaperlessmanage.helper.task.BasicInformationTask;
import com.xlk.takstarpaperlessmanage.helper.archive.LineUpTaskHelp;
import com.xlk.takstarpaperlessmanage.helper.task.DownloadFileTask;
import com.xlk.takstarpaperlessmanage.helper.task.MemberTask;
import com.xlk.takstarpaperlessmanage.helper.task.SignInTask;
import com.xlk.takstarpaperlessmanage.helper.task.VoteTask;
import com.xlk.takstarpaperlessmanage.helper.task.ZipFileTask;
import com.xlk.takstarpaperlessmanage.model.Constant;
import com.xlk.takstarpaperlessmanage.model.EventMessage;
import com.xlk.takstarpaperlessmanage.model.EventType;
import com.xlk.takstarpaperlessmanage.model.bean.MemberRoleBean;
import com.xlk.takstarpaperlessmanage.model.bean.SignInBean;
import com.xlk.takstarpaperlessmanage.util.DateUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Created by xlk on 2021/5/28.
 * @desc
 */
class ArchivePresenter extends BasePresenter<ArchiveContract.View> implements ArchiveContract.Presenter {
    /**
     * 会议公告
     */
    private List<InterfaceBullet.pbui_Item_BulletDetailInfo> noticeData = new ArrayList<>();
    /**
     * 议程的文本内容
     */
    private String agendaContent;
    /**
     * 议程的文件id
     */
    private int agendaMediaId;
    /**
     * 当前的议程类型
     */
    private int agendaType;
    /**
     * 当前的会议信息
     */
    private InterfaceMeet.pbui_Item_MeetMeetInfo currentMeetInfo;
    /**
     * 当前的会场（会议室）信息
     */
    private InterfaceRoom.pbui_Item_MeetRoomDetailInfo currentRoomInfo;
    /**
     * 当前登录的管理员信息
     */
    private InterfaceAdmin.pbui_Item_AdminDetailInfo currentAdminInfo;
    /**
     * 参会人员信息（包含参会人角色）
     */
    private List<MemberRoleBean> devSeatInfos = new ArrayList<>();
    /**
     * 签到信息
     */
    private List<SignInBean> signInData = new ArrayList<>();
    /**
     * 投票信息
     */
    private List<InterfaceVote.pbui_Item_MeetVoteDetailInfo> voteData = new ArrayList<>();
    /**
     * 选举信息
     */
    private List<InterfaceVote.pbui_Item_MeetVoteDetailInfo> electionData = new ArrayList<>();
    /**
     * 共享文件信息
     */
    private List<InterfaceFile.pbui_Item_MeetDirFileDetailInfo> shareFileData = new ArrayList<>();
    /**
     * 批注文件信息
     */
    private List<InterfaceFile.pbui_Item_MeetDirFileDetailInfo> annotationFileData = new ArrayList<>();
    /**
     * 会议资料信息（其它目录下的文件）
     */
    private Map<Integer, DirFileInfo> otherFileData = new HashMap<>();
    /**
     * =true表示压缩时进行加密处理
     */
    private boolean isEncryption = false;
    private String dirPath = Constant.archive_zip_dir;
    private List<InterfaceFile.pbui_Item_MeetDirDetailInfo> dirInfos = new ArrayList<>();

    public ArchivePresenter(ArchiveContract.View view) {
        super(view);
    }

    @Override
    public void queryAllData() {
        queryBulletin();
        queryAgenda();
        queryMeetById();
        queryRoom();
        queryAdmin();
        queryMember();
        querySignin();
        queryVote();
        queryDir();
    }

    private void queryDir() {
        InterfaceFile.pbui_Type_MeetDirDetailInfo object = jni.queryMeetDir();
        dirInfos.clear();
        if (object != null) {
            dirInfos.addAll(object.getItemList());
        }
        for (int i = 0; i < dirInfos.size(); i++) {
            InterfaceFile.pbui_Item_MeetDirDetailInfo item = dirInfos.get(i);
            queryDirFile(item.getId());
        }
    }

    public String getDirNameById(int dirId) {
        for (int i = 0; i < dirInfos.size(); i++) {
            if (dirInfos.get(i).getId() == dirId) {
                return dirInfos.get(i).getName().toStringUtf8();
            }
        }
        return "";
    }

    private void queryDirFile(int dirId) {
        InterfaceFile.pbui_Type_MeetDirFileDetailInfo info = jni.queryMeetDirFile(dirId);
        if (dirId == Constant.ANNOTATION_DIR_ID) {
            annotationFileData.clear();
            if (info != null) {
                List<InterfaceFile.pbui_Item_MeetDirFileDetailInfo> itemList = info.getItemList();
                annotationFileData.addAll(itemList);
            }
        } else if (dirId == Constant.SHARE_DIR_ID) {
            shareFileData.clear();
            if (info != null) {
                shareFileData.addAll(info.getItemList());
            }
        } else {
//            if (otherFileData.containsKey(dirId)) {
            if (info != null) {
                otherFileData.put(dirId, new DirFileInfo(dirId, getDirNameById(dirId), info.getItemList()));
            } else {
                if (otherFileData.containsKey(dirId)) {
                    otherFileData.remove(dirId);
                }
//                otherFileData.put(dirId, new ArrayList<>());
            }
//            } else {
//                if (info != null) {
//                    otherFileData.put(dirId, info.getItemList());
//                } else {
//                    otherFileData.put(dirId, new ArrayList<>());
//                }
        }

    }

    private void queryVote() {
        InterfaceVote.pbui_Type_MeetVoteDetailInfo pbui_type_meetVoteDetailInfo = jni.queryVote();
        if (pbui_type_meetVoteDetailInfo != null) {
            List<InterfaceVote.pbui_Item_MeetVoteDetailInfo> itemList = pbui_type_meetVoteDetailInfo.getItemList();
            for (int i = 0; i < itemList.size(); i++) {
                InterfaceVote.pbui_Item_MeetVoteDetailInfo item = pbui_type_meetVoteDetailInfo.getItem(i);
                if (item.getMaintype() == InterfaceMacro.Pb_MeetVoteType.Pb_VOTE_MAINTYPE_vote_VALUE) {
                    voteData.add(item);
                } else if (item.getMaintype() == InterfaceMacro.Pb_MeetVoteType.Pb_VOTE_MAINTYPE_election_VALUE) {
                    electionData.add(item);
                }
            }
        }
    }

    private void queryMember() {
        InterfaceMember.pbui_Type_MemberDetailInfo pbui_type_memberDetailInfo = jni.queryMember();
        devSeatInfos.clear();
        signInData.clear();
        if (pbui_type_memberDetailInfo != null) {
            List<InterfaceMember.pbui_Item_MemberDetailInfo> itemList = pbui_type_memberDetailInfo.getItemList();
            for (int i = 0; i < itemList.size(); i++) {
                devSeatInfos.add(new MemberRoleBean(itemList.get(i)));
                signInData.add(new SignInBean(itemList.get(i)));
            }
        }
        querySignin();
        queryPlaceRanking();
    }

    private void queryPlaceRanking() {
        InterfaceRoom.pbui_Type_MeetRoomDevSeatDetailInfo info = jni.placeDeviceRankingInfo(queryCurrentRoomId());
        if (info != null) {
            for (int i = 0; i < devSeatInfos.size(); i++) {
                MemberRoleBean bean = devSeatInfos.get(i);
                for (int j = 0; j < info.getItemList().size(); j++) {
                    InterfaceRoom.pbui_Item_MeetRoomDevSeatDetailInfo item = info.getItemList().get(j);
                    if (item.getMemberid() == bean.getMember().getPersonid()) {
                        bean.setSeat(item);
                        break;
                    }
                }
            }
        }
    }

    private void querySignin() {
        InterfaceSignin.pbui_Type_MeetSignInDetailInfo pbui_type_meetSignInDetailInfo = jni.querySignin();
        if (pbui_type_meetSignInDetailInfo != null) {
            List<InterfaceSignin.pbui_Item_MeetSignInDetailInfo> itemList = pbui_type_meetSignInDetailInfo.getItemList();
            for (int i = 0; i < signInData.size(); i++) {
                SignInBean signInBean = signInData.get(i);
                for (int j = 0; j < itemList.size(); j++) {
                    InterfaceSignin.pbui_Item_MeetSignInDetailInfo item = itemList.get(j);
                    if (item.getNameId() == signInBean.getMember().getPersonid()) {
                        signInBean.setSign(item);
                        break;
                    }
                }
            }
        }
    }

    private void queryAdmin() {
        int currentAdminId = queryCurrentAdminId();
        InterfaceAdmin.pbui_TypeAdminDetailInfo pbui_typeAdminDetailInfo = jni.queryAdmin();
        if (pbui_typeAdminDetailInfo != null) {
            List<InterfaceAdmin.pbui_Item_AdminDetailInfo> itemList = pbui_typeAdminDetailInfo.getItemList();
            for (int i = 0; i < itemList.size(); i++) {
                InterfaceAdmin.pbui_Item_AdminDetailInfo pbui_item_adminDetailInfo = itemList.get(i);
                if (pbui_item_adminDetailInfo.getAdminid() == currentAdminId) {
                    currentAdminInfo = pbui_item_adminDetailInfo;
                    break;
                }
            }
        }
    }

    private void queryAgenda() {
        InterfaceAgenda.pbui_meetAgenda meetAgenda = jni.queryAgenda();
        if (meetAgenda != null) {
            agendaType = meetAgenda.getAgendatype();
            agendaContent = meetAgenda.getText().toStringUtf8();
            agendaMediaId = meetAgenda.getMediaid();
            LogUtils.i(TAG, "queryAgenda agendaMediaId=" + agendaMediaId + ",agendaContent=" + agendaContent.length());
        }
    }

    private void queryBulletin() {
        InterfaceBullet.pbui_BulletDetailInfo pbui_bulletDetailInfo = jni.queryBulletin();
        noticeData.clear();
        if (pbui_bulletDetailInfo != null) {
            noticeData.addAll(pbui_bulletDetailInfo.getItemList());
        }
    }

    private void queryMeetById() {
        InterfaceMeet.pbui_Item_MeetMeetInfo info = jni.queryMeetingById(getCurrentMeetingId());
        currentMeetInfo = info;
        queryRoom();
    }

    private void queryRoom() {
        InterfaceRoom.pbui_Item_MeetRoomDetailInfo room = jni.queryRoomById(queryCurrentRoomId());
        currentRoomInfo = room;
    }

    @Override
    protected void busEvent(EventMessage msg) throws InvalidProtocolBufferException {
        switch (msg.getType()) {
            //公告变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETBULLET_VALUE: {
                queryBulletin();
                break;
            }
            //议程变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETAGENDA_VALUE: {
                queryAgenda();
                break;
            }
            //会议信息变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETINFO_VALUE: {
                queryMeetById();
                break;
            }
            //会场信息变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ROOM_VALUE: {
                LogUtils.i(TAG, "BusEvent 会场信息变更通知");
                queryRoom();
                break;
            }
            //管理员变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ADMIN_VALUE: {
                LogUtils.i(TAG, "busEvent 管理员变更通知");
                queryAdmin();
                break;
            }
            //会议排位变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETSEAT_VALUE: {
                LogUtils.i(TAG, "busEvent " + "会议排位变更通知");
                queryPlaceRanking();
                break;
            }
            //参会人员变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEMBER_VALUE: {
                LogUtils.i(TAG, "BusEvent -->" + "参会人员变更通知");
                queryMember();
                break;
            }
            //签到变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETSIGN_VALUE: {
                LogUtils.i(TAG, "busEvent 签到变更通知");
                querySignin();
                break;
            }
            //投票变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETVOTEINFO_VALUE: {
                LogUtils.d(TAG, "BusEvent -->" + "投票变更通知");
                queryVote();
                break;
            }
            //会议目录文件变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETDIRECTORYFILE_VALUE: {
                byte[] bytes = (byte[]) msg.getObjects()[0];
                InterfaceBase.pbui_MeetNotifyMsgForDouble info = InterfaceBase.pbui_MeetNotifyMsgForDouble.parseFrom(bytes);
                int opermethod = info.getOpermethod();
                int id = info.getId();
                int subid = info.getSubid();
                LogUtils.i(TAG, "busEvent 会议目录文件变更通知 id=" + id + ",subid=" + subid + ",opermethod=" + opermethod);
                queryDirFile(id);
                break;
            }
            //会议目录变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETDIRECTORY_VALUE: {
                LogUtils.i(TAG, "busEvent 会议目录变更通知");
                queryDir();
                break;
            }
            //更新输出位置
            case EventType.RESULT_DIR_PATH: {
                int dirType = (int) msg.getObjects()[0];
                String dirPath = (String) msg.getObjects()[1];
                if (dirType == Constant.CHOOSE_DIR_TYPE_ARCHIVE) {
                    this.dirPath = dirPath;
                    mView.updateArchiveDirPath(dirPath);
                }
                break;
            }

            /*
            //归档议程文件下载进度通知
            case EventType.BUS_ARCHIVE_AGENDA_FILE: {
                Object[] objects = msg.getObjects();
                int mediaId = (int) objects[0];
                String fileName = (String) objects[1];
                int progress = (int) objects[2];
                for (int i = 0; i < archiveInforms.size(); i++) {
                    ArchiveInform archiveInform = archiveInforms.get(i);
                    if (archiveInform.getMediaId() == mediaId) {
                        archiveInform.setContent("下载文件：" + fileName);
                        archiveInform.setResult("下载进度：" + progress + "%");
                    }
                }
                mView.updateArchiveInform(archiveInforms);
                // TODO: 2021/7/9 议程文件
                break;
            }
             */
            //归档共享文件下载进度通知
            case EventType.BUS_ARCHIVE_SHARE_FILE: {
                Object[] objects = msg.getObjects();
                int mediaId = (int) objects[0];
                String filepath = (String) objects[1];
                int progress = (int) objects[2];
                boolean disrupted = (boolean) objects[3];
                String fileName = FileUtils.getFileName(filepath);
                if (disrupted) {
                    mView.updateArchiveInform(0, fileName, mediaId, "", -1);
                } else {
                    mView.updateArchiveInform(0, fileName, mediaId, "", progress);
                }
                if (progress == 100) {
                    addNextDownloadShareFileTask(mediaId);
                }
                break;
            }
            //归档批注文件下载进度通知
            case EventType.BUS_ARCHIVE_ANNOTATION_FILE: {
                Object[] objects = msg.getObjects();
                int mediaId = (int) objects[0];
                String filepath = (String) objects[1];
                int progress = (int) objects[2];
                boolean disrupted = (boolean) objects[3];
                String fileName = FileUtils.getFileName(filepath);
                if (disrupted) {
                    mView.updateArchiveInform(1, fileName, mediaId, "", -1);
                } else {
                    mView.updateArchiveInform(1, fileName, mediaId, "", progress);
                }
                if (progress == 100) {
                    addNextDownloadAnnotationFileTask(mediaId);
                }
                break;
            }
            //归档会议资料文件下载进度通知
            case EventType.BUS_ARCHIVE_MEET_DATA_FILE: {
                Object[] objects = msg.getObjects();
                int mediaId = (int) objects[0];
                String filepath = (String) objects[1];
                int progress = (int) objects[2];
                boolean disrupted = (boolean) objects[3];
                String fileName = FileUtils.getFileName(filepath);
                File file = FileUtils.getFileByPath(filepath);
                File parentFile = file.getParentFile();
                if (disrupted) {
                    mView.updateArchiveInform(0, fileName, mediaId, parentFile.getName(), -1);
                } else {
                    mView.updateArchiveInform(2, fileName, mediaId, parentFile.getName(), progress);
                }
                if (progress == 100) {
                    String name = parentFile.getName();
                    LogUtils.e("父目录名称=" + name + ",当前文件=" + filepath);
                    addNextDownloadOtherFileTask(name, mediaId);
                }
                break;
            }
        }
    }

    /**
     * 设置是否加密压缩
     *
     * @param isEncryption =true 需要加密
     */
    public void setEncryption(boolean isEncryption) {
        this.isEncryption = isEncryption;
        LogUtils.i(TAG, "setEncryption 是否加密=" + isEncryption);
    }

    @Override
    public BasicInformationTask.Info getBasicInformationTaskInfo() {
        String conferenceBasicInformation = "";
        if (currentMeetInfo != null) {
            conferenceBasicInformation += "会议名称：" + currentMeetInfo.getName().toStringUtf8()
                    + "\n使用会场：" + currentRoomInfo.getName().toStringUtf8()
                    + "\n会场地址：" + currentRoomInfo.getAddr().toStringUtf8()
                    + "\n会议保密：" + (currentMeetInfo.getSecrecy() == 1 ? "是" : "否")
                    + "\n会议开始时间：" + DateUtil.secondFormatDateTime(currentMeetInfo.getStartTime())
                    + "\n会议结束时间：" + DateUtil.secondFormatDateTime(currentMeetInfo.getEndTime())
                    + "\n签到方式：" + Constant.getMeetSignInTypeName(currentMeetInfo.getSigninType())
                    + "\n会议管理员：" + (currentAdminInfo != null ? currentAdminInfo.getAdminname().toStringUtf8() : queryCurrentAdminName())
                    + "\n管理员描述：" + (currentAdminInfo != null ? currentAdminInfo.getComment().toStringUtf8() : "")
            ;
        }
        String bulletinText = "";
        for (int i = 0; i < noticeData.size(); i++) {
            InterfaceBullet.pbui_Item_BulletDetailInfo item = noticeData.get(i);
            bulletinText += "标题：" + item.getTitle().toStringUtf8() + "\n" + "内容：" + item.getContent().toStringUtf8() + "\n\n";
        }
        return new BasicInformationTask.Info(conferenceBasicInformation, agendaContent, agendaMediaId, bulletinText);
    }

    @Override
    public MemberTask.Info getMemberTaskInfo() {
        return new MemberTask.Info(devSeatInfos);
    }

    @Override
    public SignInTask.Info getSignInTaskInfo() {
        return new SignInTask.Info(signInData);
    }

    @Override
    public VoteTask.Info getVoteTaskInfo() {
        return new VoteTask.Info(voteData, electionData, devSeatInfos.size());
    }

    @Override
    public void clearShouldDownloadFiles() {
        jni.clearDownload();
        shouldDownloadShareFiles.clear();
        shouldDownloadAnnotationFiles.clear();
        shouldDownloadOtherFiles.clear();
    }

    LinkedList<InterfaceFile.pbui_Item_MeetDirFileDetailInfo> shouldDownloadShareFiles = new LinkedList<>();

    /**
     * 添加下一个需要下载的共享文件
     */
    public void addNextDownloadShareFileTask(int mediaId) {
        //删除上一个已完成的任务
        LineUpTaskHelp.getInstance().deleteTaskNoAll("共享文件-" + mediaId);
        if (shouldDownloadShareFiles.isEmpty()) {
            addNextDownloadAnnotationFileTask(0);
            return;
        }
        //取出并删除最先添加的
        InterfaceFile.pbui_Item_MeetDirFileDetailInfo item = shouldDownloadShareFiles.poll();
        DownloadFileTask task = new DownloadFileTask(new DownloadFileTask.Info("共享文件",
                Constant.ARCHIVE_SHARE_FILE, item));
        task.taskNo = "共享文件-" + item.getMediaid();
        LineUpTaskHelp.getInstance().addTask(task);
    }

    LinkedList<InterfaceFile.pbui_Item_MeetDirFileDetailInfo> shouldDownloadAnnotationFiles = new LinkedList<>();

    /**
     * 添加下一个需要下载的批注文件
     */
    public void addNextDownloadAnnotationFileTask(int mediaId) {
        //删除上一个已完成的任务
        LineUpTaskHelp.getInstance().deleteTaskNoAll("批注文件-" + mediaId);
        if (shouldDownloadAnnotationFiles.isEmpty()) {
            addNextDownloadOtherFileTask("", 0);
            return;
        }
        //取出并删除最先添加的
        InterfaceFile.pbui_Item_MeetDirFileDetailInfo item = shouldDownloadAnnotationFiles.poll();
        DownloadFileTask task = new DownloadFileTask(new DownloadFileTask.Info("批注文件",
                Constant.ARCHIVE_ANNOTATION_FILE, item));
        task.taskNo = "批注文件-" + item.getMediaid();
        LineUpTaskHelp.getInstance().addTask(task);
    }

    LinkedList<DownloadOtherFileInfo> shouldDownloadOtherFiles = new LinkedList<>();

    /**
     * 添加下一个需要下载的会议资料文件
     */
    public void addNextDownloadOtherFileTask(String dir, int mediaId) {
        //删除上一个已完成的任务
        LineUpTaskHelp.getInstance().deleteTaskNoAll("会议资料-" + dir + "-" + mediaId);
        if (shouldDownloadOtherFiles.isEmpty()) {
            LogUtils.d("所有的文件都下载完毕");
            addZipTask();
            return;
        }
        //取出并删除最先添加的
        DownloadOtherFileInfo poll = shouldDownloadOtherFiles.poll();
        String dirName = poll.getDirName();
        InterfaceFile.pbui_Item_MeetDirFileDetailInfo item = poll.getItem();
        DownloadFileTask task = new DownloadFileTask(new DownloadFileTask.Info("会议资料/" + dirName,
                Constant.ARCHIVE_MEET_DATA_FILE, item));
        task.taskNo = "会议资料-" + dirName + "-" + item.getMediaid();
        LineUpTaskHelp.getInstance().addTask(task);
    }

    private void addZipTask() {
        ZipFileTask task = new ZipFileTask(new ZipFileTask.Info(dirPath, isEncryption));
        task.taskNo = "压缩文件";
        LineUpTaskHelp.getInstance().addTask(task);
    }

    @Override
    public void addShouldDownloadShareFiles(LineUpTaskHelp lineUpTaskHelp) {
        if (shareFileData.isEmpty()) return;
        FileUtils.createOrExistsDir(Constant.DIR_ARCHIVE_TEMP + "共享文件/");
        shouldDownloadShareFiles.addAll(shareFileData);
    }

    @Override
    public void addShouldDownloadAnnotationFiles(LineUpTaskHelp lineUpTaskHelp) {
        if (annotationFileData.isEmpty()) return;
        FileUtils.createOrExistsDir(Constant.DIR_ARCHIVE_TEMP + "批注文件/");
        shouldDownloadAnnotationFiles.addAll(annotationFileData);
    }

    @Override
    public void addShouldDownloadMeetDataFiles(LineUpTaskHelp lineUpTaskHelp) {
        if (otherFileData.isEmpty()) return;
        FileUtils.createOrExistsDir(Constant.DIR_ARCHIVE_TEMP + "会议资料/");
        Collection<DirFileInfo> values = otherFileData.values();
        Iterator<DirFileInfo> iterator = values.iterator();
        while (iterator.hasNext()) {
            DirFileInfo next = iterator.next();
            FileUtils.createOrExistsDir(Constant.DIR_ARCHIVE_TEMP + "会议资料/" + next.dirName);
            for (int i = 0; i < next.files.size(); i++) {
                InterfaceFile.pbui_Item_MeetDirFileDetailInfo item = next.files.get(i);
                shouldDownloadOtherFiles.add(new DownloadOtherFileInfo(next.dirId, next.dirName, item));
            }
        }
    }
}
