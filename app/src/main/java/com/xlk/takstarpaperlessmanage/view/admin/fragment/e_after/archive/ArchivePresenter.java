package com.xlk.takstarpaperlessmanage.view.admin.fragment.e_after.archive;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ZipUtils;
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
import com.xlk.takstarpaperlessmanage.model.Constant;
import com.xlk.takstarpaperlessmanage.model.EventMessage;
import com.xlk.takstarpaperlessmanage.model.EventType;
import com.xlk.takstarpaperlessmanage.model.bean.MemberRoleBean;
import com.xlk.takstarpaperlessmanage.model.bean.SignInBean;
import com.xlk.takstarpaperlessmanage.util.DateUtil;
import com.xlk.takstarpaperlessmanage.util.JxlUtil;
import com.xlk.takstarpaperlessmanage.util.ZipUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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
//    private Map<Integer, List<InterfaceFile.pbui_Item_MeetDirFileDetailInfo>> otherFileData = new HashMap<>();
    private Map<Integer, DirFileInfo> otherFileData = new HashMap<>();
    /**
     * 操作通知
     */
    private List<ArchiveInform> archiveInforms = new ArrayList<>();
    /**
     * 存档操作时的任务tag,不为空则说明正在下载文件，还未开始压缩
     */
    private List<String> archiveTasks = new ArrayList<>();
    /**
     * =true表示正在进行压缩阶段
     */
    private boolean isCompressing;
    /**
     * =true表示压缩时进行加密处理
     */
    private boolean isEncryption = false;
    /**
     * 加密时的密码
     */
    private String passWord = "";

//    List<Integer> shareFileMediaIds = new ArrayList<>();
//    List<Integer> annotateFileMediaIds = new ArrayList<>();
//    List<Integer> materialFileMediaIds = new ArrayList<>();

    private String dirPath = Constant.DIR_ARCHIVE_ZIP;
    private ZipThread zipThread;
    /**
     * 是否点击取消按钮
     */
    private boolean isCancel = false;
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
            //归档参会人信息完成通知
            case EventType.BUS_ARCHIVE_MEMBER: {
                archiveInforms.add(new ArchiveInform("参会人员信息导出完成", "100%"));
                mView.updateArchiveInform(archiveInforms);
                removeTask("归档参会人信息");
//                mView.updateAttendeeInformation("完成");
                break;
            }
            /*
            //需要归档的文件下载进度通知
            case EventType.ARCHIVE_BUS_DOWNLOAD_FILE: {
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
                if (progress == 100) {
                    removeTask(String.valueOf(mediaId));
                } else {
                    addTask(String.valueOf(mediaId));
                }
                break;
            }*/
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
                if (progress == 100) {
                    removeTask("议程文件" + mediaId);
                } else {
                    addTask("议程文件" + mediaId);
                }
                break;
            }
            //归档共享文件下载进度通知
            case EventType.BUS_ARCHIVE_SHARE_FILE: {
                Object[] objects = msg.getObjects();
                int mediaId = (int) objects[0];
                String fileName = (String) objects[1];
                int progress = (int) objects[2];
                if (progress == 100) {
                    addNextDownloadShareFileTask();
                } else {

                }
                /*
                for (int i = 0; i < archiveInforms.size(); i++) {
                    ArchiveInform archiveInform = archiveInforms.get(i);
                    if (archiveInform.getType() == 0 && archiveInform.getMediaId() == mediaId) {
                        archiveInform.setContent("下载文件：" + fileName);
                        archiveInform.setResult("下载进度：" + progress + "%");
                    }
                }
                mView.updateArchiveInform(archiveInforms);
                if (progress == 100) {
                    removeTask("共享文件" + mediaId);
                } else {
                    addTask("共享文件" + mediaId);
                }
                */
                break;
            }
            //归档批注文件下载进度通知
            case EventType.BUS_ARCHIVE_ANNOTATION_FILE: {
                Object[] objects = msg.getObjects();
                int mediaId = (int) objects[0];
                String fileName = (String) objects[1];
                int progress = (int) objects[2];
                if (progress == 100) {
                    addNextDownloadAnnotationFileTask();
                } else {

                }
                /*
                for (int i = 0; i < archiveInforms.size(); i++) {
                    ArchiveInform archiveInform = archiveInforms.get(i);
                    if (archiveInform.getType() == 1 && archiveInform.getMediaId() == mediaId) {
                        archiveInform.setContent("下载文件：" + fileName);
                        archiveInform.setResult("下载进度：" + progress + "%");
                    }
                }
                mView.updateArchiveInform(archiveInforms);
                if (progress == 100) {
                    removeTask("批注文件" + mediaId);
                } else {
                    addTask("批注文件" + mediaId);
                }
                */
                break;
            }
            //归档会议资料文件下载进度通知
            case EventType.BUS_ARCHIVE_MEET_DATA_FILE: {
                Object[] objects = msg.getObjects();
                int mediaId = (int) objects[0];
                String fileName = (String) objects[1];
                int progress = (int) objects[2];
                if (progress == 100) {
                    addNextDownloadOtherFileTask();
                } else {

                }
                /*
                for (int i = 0; i < archiveInforms.size(); i++) {
                    ArchiveInform archiveInform = archiveInforms.get(i);
                    if (archiveInform.getType() == 2 && archiveInform.getMediaId() == mediaId) {
                        archiveInform.setContent("下载文件：" + fileName);
                        archiveInform.setResult("下载进度：" + progress + "%");
                    }
                }
                mView.updateArchiveInform(archiveInforms);
                if (progress == 100) {
                    removeTask("会议资料" + mediaId);
                } else {
                    addTask("会议资料" + mediaId);
                }
                 */
                break;
            }
        }
    }

    @Override
    public boolean hasStarted() {
        return isCompressing || !archiveTasks.isEmpty();
    }

    /**
     * 设置是否加密压缩
     *
     * @param isEncryption =true 需要加密
     */
    public void setEncryption(boolean isEncryption) {
        if (hasStarted()) {
            LogUtils.e("已经开始归档，不能中间进行加密");
            return;
        }
        this.isEncryption = isEncryption;
        LogUtils.i(TAG, "setEncryption 是否加密=" + isEncryption);
    }

    @Override
    public void setPassword(String pwd) {
        this.passWord = pwd;
    }

    /**
     * 添加任务
     *
     * @param tag 任务tag
     */
    private void addTask(String tag) {
        if (!archiveTasks.contains(tag)) {
            LogUtils.i(TAG, "addTask 添加任务=" + tag);
            archiveTasks.add(tag);
        }
    }

    /**
     * 移除任务
     *
     * @param tag 任务tag
     */
    private void removeTask(String tag) {
        if (archiveTasks.contains(tag)) {
            archiveTasks.remove(tag);
            LogUtils.d(TAG, "removeTask 移除任务=" + tag + ",size=" + archiveTasks.size());
            if (archiveTasks.isEmpty()) {
                zipArchiveDir();
            }
        } else {
            LogUtils.e("不存在该任务：" + tag);
        }
    }

    class ZipThread extends Thread {
        public ZipThread() {
            super("ZipThread");
        }

        @Override
        public void run() {
            try {
                Thread.sleep(1000);
                if (!archiveTasks.isEmpty()) {
                    LogUtils.i(TAG, "run 还有正在下载的文件");
                    return;
                }
                if (isCompressing) {
                    LogUtils.i(TAG, "zipThread 当前正在压缩...");
                    return;
                }
                File srcFile = new File(Constant.DIR_ARCHIVE_TEMP);
                if (!srcFile.exists()) {
                    LogUtils.e(TAG, "zipThread 没有找到这个目录=" + Constant.DIR_ARCHIVE_TEMP);
                    return;
                }
                isCompressing = true;
                LogUtils.i(TAG, "zipThread 开始压缩 当前线程id=" + Thread.currentThread().getId() + "-" + Thread.currentThread().getName());
                archiveInforms.add(new ArchiveInform("开始压缩", "进行中..."));
                mView.updateArchiveInform(archiveInforms);
                FileUtils.createOrExistsDir(dirPath);
                String zipFilePath = dirPath + "/会议归档.zip";
                File zipFile = new File(zipFilePath);
                if (zipFile.exists()) {
                    zipFilePath = dirPath + "/会议归档-" + DateUtil.nowDate() + ".zip";
                }
//                System.out.println("当前文件名编码格式：" + getEncoding(zipFilePath));
                Properties properties = new Properties(System.getProperties());
                Charset charset = Charset.defaultCharset();
                LogUtils.d("charset.name=" + charset.name()
                        + ",当前系统编码:" + properties.getProperty("file.encoding")
                        + ",当前系统语言:" + properties.getProperty("user.language")
                );
                LogUtils.e("是否加密=" + isEncryption + ", 密码=" + passWord);
                long l = System.currentTimeMillis();
                if (isEncryption) {
//                    CompressUtil.zip(Constant.DIR_ARCHIVE_TEMP, zipFilePath, true, passWord);
                    File file = new File(Constant.DIR_ARCHIVE_TEMP);
                    ZipUtil.doZipFilesWithPassword(file, zipFilePath, passWord);
                } else {
                    ZipUtils.zipFile(Constant.DIR_ARCHIVE_TEMP, zipFilePath);
                }
                LogUtils.e("压缩结束----用时=" + (System.currentTimeMillis() - l));
                for (int i = 0; i < archiveInforms.size(); i++) {
                    ArchiveInform archiveInform = archiveInforms.get(i);
                    if (archiveInform.getContent().equals("开始压缩")) {
                        archiveInform.setContent("压缩完毕");
                        archiveInform.setResult("100%");
                        break;
                    }
                }
                mView.updateArchiveInform(archiveInforms);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                LogUtils.i(TAG, "删除临时目录 zipThread.getState()=" + zipThread.getState());
                FileUtils.delete(Constant.DIR_ARCHIVE_TEMP);
                zipThread = null;
                isCompressing = false;
            }
        }
    }

    public void zipArchiveDir() {
        LogUtils.i("zipArchiveDir---");
        if (zipThread == null) {
            zipThread = new ZipThread();
        }
        zipThread.start();

//        App.threadPool.execute(zipThread);
//        App.threadPool.execute(() -> {
//            try {
//                Thread.sleep(1000);
//                if (!archiveTasks.isEmpty()) {
//                    LogUtils.i(TAG, "run 还有正在下载的文件");
//                    return;
//                }
//                if (isCompressing) {
//                    LogUtils.i(TAG, "zipArchiveDir 当前正在压缩...");
//                    return;
//                }
//                File srcFile = new File(Constant.DIR_ARCHIVE_TEMP);
//                if (!srcFile.exists()) {
//                    LogUtils.e(TAG, "zipArchiveDir 没有找到这个目录=" + Constant.DIR_ARCHIVE_TEMP);
//                    return;
//                }
//                isCompressing = true;
//                long l = System.currentTimeMillis();
//                LogUtils.i(TAG, "run 开始压缩 当前线程id=" + Thread.currentThread().getId() + "-" + Thread.currentThread().getName());
//                archiveInforms.add(new ArchiveInform("开始压缩", "进行中..."));
//                mView.updateArchiveInform(archiveInforms);
//                FileUtils.createOrExistsDir(dirPath);
//                String zipFilePath = dirPath + "/会议归档.zip";
//                File zipFile = new File(zipFilePath);
//                if (zipFile.exists()) {
//                    zipFilePath = dirPath + "/会议归档-" + DateUtil.nowDate() + ".zip";
//                }
////                System.out.println("当前文件名编码格式：" + getEncoding(zipFilePath));
////                Properties initProp = new Properties(System.getProperties());
////                Charset charset = Charset.defaultCharset();
////                System.out.println("charset:" + charset.name() + ",toString=" + charset.toString());
////                System.out.println("当前系统编码:" + initProp.getProperty("file.encoding"));
////                System.out.println("当前系统语言:" + initProp.getProperty("user.language"));
//
////                if (isEncryption) {
////                    File file = new File(Constant.DIR_ARCHIVE_TEMP);
////                    ZipUtil.doZipFilesWithPassword(file, zipFilePath, "123456");
////                } else {
//                ZipUtils.zipFile(Constant.DIR_ARCHIVE_TEMP, zipFilePath);
//                LogUtils.e("压缩结束----用时=" + (System.currentTimeMillis() - l));
////                }
//                for (int i = 0; i < archiveInforms.size(); i++) {
//                    ArchiveInform archiveInform = archiveInforms.get(i);
//                    if (archiveInform.getContent().equals("开始压缩")) {
//                        archiveInform.setContent("压缩完毕");
//                        archiveInform.setResult("100%");
//                        break;
//                    }
//                }
//                mView.updateArchiveInform(archiveInforms);
//                LogUtils.i(TAG, "删除临时目录");
//                FileUtils.delete(Constant.DIR_ARCHIVE_TEMP);
////                FileUtils.deleteAllInDir(Constant.DIR_ARCHIVE_TEMP);
//                isCompressing = false;
//            } catch (InterruptedException | IOException e) {
//                e.printStackTrace();
//            }
//        });
    }

    @Override
    public void cancelArchive(boolean cancel) {
        isCancel = cancel;
        LogUtils.i("cancelArchive isCancel=" + isCancel);
    }

    @Override
    public void cancelArchive() {
//        jni.clearDownload();
        if (zipThread != null && zipThread.getState() == Thread.State.RUNNABLE) {
            try {
                zipThread.interrupt();
                LogUtils.e("zipThread interrupt");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                zipThread = null;
                LogUtils.e("zipThread设置为null");
            }
        }
        FileUtils.delete(dirPath);
        FileUtils.delete(Constant.DIR_ARCHIVE_TEMP);
        archiveInforms.clear();
        archiveTasks.clear();
        mView.updateArchiveInform(archiveInforms);
        LogUtils.i("cancelArchive 取消归档");
    }

    @Override
    public void archiveAll() {
        archiveInforms.clear();
        archiveTasks.clear();
        long l = System.currentTimeMillis();
        archiveMeetInfo();
        archiveMemberInfo();
        archiveSignInfo();
        archiveVoteInfo();
        archiveShareInfo();
        archiveAnnotationInfo();
        archiveMeetData();
        LogUtils.i(TAG, "archiveAll 归档总用时：" + (System.currentTimeMillis() - l));
    }

    @Override
    public void archiveSelected(boolean checked, boolean checked1, boolean checked2, boolean checked3, boolean checked4, boolean checked5, boolean checked6) {
        archiveInforms.clear();
        archiveTasks.clear();
        if (checked) {
            archiveMeetInfo();
        }
        if (checked1) {
            archiveMemberInfo();
        }
        if (checked2) {
            archiveSignInfo();
        }
        if (checked3) {
            archiveVoteInfo();
        }
        if (checked4) {
            archiveShareInfo();
        }
        if (checked5) {
            archiveAnnotationInfo();
        }
        if (checked6) {
            archiveMeetData();
        }
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

    LinkedList<InterfaceFile.pbui_Item_MeetDirFileDetailInfo> shouldDownloadShareFiles = new LinkedList<>();

    /**
     * 添加下一个需要下载的共享文件
     */
    public void addNextDownloadShareFileTask() {
        if (shouldDownloadShareFiles.isEmpty()) {
            addNextDownloadAnnotationFileTask();
            return;
        }
        //取出并删除最先添加的
        InterfaceFile.pbui_Item_MeetDirFileDetailInfo item = shouldDownloadShareFiles.poll();
        String fileName = item.getName().toStringUtf8();
        DownloadFileTask task = new DownloadFileTask(new DownloadFileTask.Info("共享文件",
                Constant.ARCHIVE_SHARE_FILE, item));
        task.taskNo = "共享文件-" + item.getMediaid();
        LineUpTaskHelp.getInstance().addTask(task);
    }

    LinkedList<InterfaceFile.pbui_Item_MeetDirFileDetailInfo> shouldDownloadAnnotationFiles = new LinkedList<>();

    /**
     * 添加下一个需要下载的批注文件
     */
    public void addNextDownloadAnnotationFileTask() {
        if (shouldDownloadAnnotationFiles.isEmpty()) {
            addNextDownloadOtherFileTask();
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
    public void addNextDownloadOtherFileTask() {
        if (shouldDownloadOtherFiles.isEmpty()) {
            LogUtils.d("所有的文件都下载完毕");
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

    @Override
    public void addDownloadShareFileTask(LineUpTaskHelp lineUpTaskHelp) {
        if (shareFileData.isEmpty()) return;
        FileUtils.createOrExistsDir(Constant.DIR_ARCHIVE_TEMP + "共享文件/");
        shouldDownloadShareFiles.addAll(shareFileData);
//        for (int i = 0; i < shareFileData.size(); i++) {
//            InterfaceFile.pbui_Item_MeetDirFileDetailInfo item = shareFileData.get(i);
//            new DirFileInfo(Constant.SHARE_DIR_ID, "共享文件", shareFileData);
//
//            DownloadFileTask task = new DownloadFileTask(new DownloadFileTask.Info("共享文件",
//                    Constant.ARCHIVE_SHARE_FILE, item));
//            task.taskNo = "共享文件-" + item.getMediaid();
////            task.planNo = "共享文件";
//            lineUpTaskHelp.addTask(task);
//            downloadFileTasks.add(task);
//        }
    }

    @Override
    public void addDownloadAnnotationFileTask(LineUpTaskHelp lineUpTaskHelp) {
        if (annotationFileData.isEmpty()) return;
        FileUtils.createOrExistsDir(Constant.DIR_ARCHIVE_TEMP + "批注文件/");
        shouldDownloadAnnotationFiles.addAll(annotationFileData);
//        for (int i = 0; i < annotationFileData.size(); i++) {
//            InterfaceFile.pbui_Item_MeetDirFileDetailInfo item = annotationFileData.get(i);
//            DownloadFileTask task = new DownloadFileTask(new DownloadFileTask.Info("批注文件",
//                    Constant.ARCHIVE_SHARE_FILE, item));
//            task.taskNo = "批注文件-" + item.getMediaid();
////            task.planNo = "批注文件";
//            lineUpTaskHelp.addTask(task);
//            downloadFileTasks.add(task);
//        }
    }

    @Override
    public void addDownloadMeetDataFileTask(LineUpTaskHelp lineUpTaskHelp) {
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
//                DownloadFileTask task = new DownloadFileTask(new DownloadFileTask.Info("会议资料-" + next.dirName,
//                        Constant.ARCHIVE_MEET_DATA_FILE, item));
//                task.taskNo = "会议资料-" + next.dirName + "-" + item.getMediaid();
////                task.planNo = "会议资料";
//                lineUpTaskHelp.addTask(task);
            }
        }
    }

    /**
     * 归档会议基本信息
     */
    private void archiveMeetInfo() {
        if (isCancel) return;
        LogUtils.i("archiveMeetInfo---");
//        mView.updateMeetingBasicInformation("正在导出");
        addTask("归档会议基本信息");
        long l = System.currentTimeMillis();
        //会议基本信息
        meetInfo2file();
        // 会议议程信息
        if (agendaType == InterfaceMacro.Pb_AgendaType.Pb_MEET_AGENDA_TYPE_TEXT_VALUE) {
            if (agendaContent != null && !agendaContent.isEmpty()) {
                read2file("会议议程信息.txt", agendaContent);
//                mView.updateMeetingBasicInformation("完成");
            }
        } else if (agendaType == InterfaceMacro.Pb_AgendaType.Pb_MEET_AGENDA_TYPE_FILE_VALUE) {
            downloadAgendaFile();
        }
        // 会议公告信息
        notice2file();
        LogUtils.i(TAG, "归档会议基本信息 用时：" + (System.currentTimeMillis() - l));
        removeTask("归档会议基本信息");
    }

    /**
     * 归档参会人信息
     */
    private void archiveMemberInfo() {
        if (isCancel) return;
        LogUtils.i("archiveMemberInfo---");
        if (devSeatInfos.isEmpty()) {
            return;
        }
        addTask("归档参会人信息");
        JxlUtil.exportMemberInfo("归档参会人信息", Constant.DIR_ARCHIVE_TEMP, devSeatInfos);
    }

    /**
     * 归档签到信息
     */
    private void archiveSignInfo() {
        if (isCancel) return;
        LogUtils.i("archiveSignInfo---");
        if (signInData.isEmpty()) {
            return;
        }
//        mView.updateConferenceSignInInformation("正在导出");
        addTask("归档签到信息");
        long l = System.currentTimeMillis();
        JxlUtil.exportArchiveSignIn(Constant.DIR_ARCHIVE_TEMP, signInData);
        LogUtils.i(TAG, "归档签到信息 用时=" + (System.currentTimeMillis() - l));
        archiveInforms.add(new ArchiveInform("签到信息导出完成", "100%"));
        mView.updateArchiveInform(archiveInforms);
        removeTask("归档签到信息");
//        mView.updateConferenceSignInInformation("完成");
    }

    /**
     * 归档投票结果
     */
    private void archiveVoteInfo() {
        if (isCancel) return;
        LogUtils.i("archiveVoteInfo---");
        addTask("归档投票结果");
        long l = System.currentTimeMillis();
        if (!voteData.isEmpty()) {
            JxlUtil.exportArchiveVote(Constant.DIR_ARCHIVE_TEMP, voteData, devSeatInfos.size(), true);
        }
        if (!electionData.isEmpty()) {
            JxlUtil.exportArchiveVote(Constant.DIR_ARCHIVE_TEMP, electionData, devSeatInfos.size(), false);
        }
        LogUtils.i(TAG, "归档投票结果 用时：" + (System.currentTimeMillis() - l));
        archiveInforms.add(new ArchiveInform("投票信息导出完成", "100%"));
        mView.updateArchiveInform(archiveInforms);
        removeTask("归档投票结果");
    }

    /**
     * 归档共享文件
     */
    private void archiveShareInfo() {
        if (isCancel) return;
        if (shareFileData.isEmpty()) {
            return;
        }
        FileUtils.createOrExistsDir(Constant.DIR_ARCHIVE_TEMP + "共享文件/");
        for (int i = 0; i < shareFileData.size(); i++) {
            if (isCancel) {
                LogUtils.e("中断循环下载共享文件");
                return;
            }
            InterfaceFile.pbui_Item_MeetDirFileDetailInfo item = shareFileData.get(i);
            String fileName = item.getName().toStringUtf8();
            String taskTag = "共享文件" + item.getMediaid();
            if (!archiveTasks.contains(taskTag)) {
                archiveInforms.add(new ArchiveInform(0, item.getMediaid(), "下载文件：" + fileName, "0%"));
                mView.updateArchiveInform(archiveInforms);
                addTask(taskTag);
                jni.downloadFile(Constant.DIR_ARCHIVE_TEMP + "共享文件/" + fileName, item.getMediaid(),
                        1, 0, Constant.ARCHIVE_SHARE_FILE);
            }
        }
    }

    /**
     * 归档批注文件
     */
    private void archiveAnnotationInfo() {
        if (isCancel) return;
        if (annotationFileData.isEmpty()) {
            return;
        }
        FileUtils.createOrExistsDir(Constant.DIR_ARCHIVE_TEMP + "批注文件/");
        for (int i = 0; i < annotationFileData.size(); i++) {
            if (isCancel) {
                LogUtils.e("中断循环下载批注文件");
                return;
            }
            InterfaceFile.pbui_Item_MeetDirFileDetailInfo item = annotationFileData.get(i);
            String fileName = item.getName().toStringUtf8();
            String taskTag = "批注文件" + item.getMediaid();
            if (!archiveTasks.contains(taskTag)) {
                archiveInforms.add(new ArchiveInform(1, item.getMediaid(), "下载文件：" + fileName, "0%"));
                mView.updateArchiveInform(archiveInforms);
                addTask(taskTag);
                jni.downloadFile(Constant.DIR_ARCHIVE_TEMP + "批注文件/" + fileName, item.getMediaid(),
                        1, 0, Constant.ARCHIVE_ANNOTATION_FILE);
            }
        }
    }

    /**
     * 归档会议资料
     */
    private void archiveMeetData() {
        if (isCancel) return;
        LogUtils.e("归档会议资料文件 " + (otherFileData.isEmpty()));
        if (otherFileData.isEmpty()) {
            return;
        }
        FileUtils.createOrExistsDir(Constant.DIR_ARCHIVE_TEMP + "会议资料/");
        Collection<DirFileInfo> values = otherFileData.values();
        Iterator<DirFileInfo> iterator = values.iterator();
        while (iterator.hasNext()) {
            DirFileInfo next = iterator.next();
            List<InterfaceFile.pbui_Item_MeetDirFileDetailInfo> items = next.getFiles();
            for (int i = 0; i < items.size(); i++) {
                if (isCancel) {
                    LogUtils.e("中断循环下载会议资料");
                    return;
                }
                InterfaceFile.pbui_Item_MeetDirFileDetailInfo item = items.get(i);
                String fileName = item.getName().toStringUtf8();
                int mediaid = item.getMediaid();
                String taskTag = "会议资料" + item.getMediaid();
                if (!archiveTasks.contains(taskTag)) {
                    archiveInforms.add(new ArchiveInform(2, mediaid, "下载文件：" + fileName, "0%"));
                    mView.updateArchiveInform(archiveInforms);
                    addTask(taskTag);
                    jni.downloadFile(Constant.DIR_ARCHIVE_TEMP + "会议资料/" + fileName, mediaid,
                            1, 0, Constant.ARCHIVE_MEET_DATA_FILE);
                }
            }
        }
    }

    /**
     * 会议信息写入到文件中
     */
    private void meetInfo2file() {
        if (isCancel) return;
        if (currentMeetInfo != null) {
            String content = "";
            content += "会议名称：" + currentMeetInfo.getName().toStringUtf8()
                    + "\n使用会场：" + currentRoomInfo.getName().toStringUtf8()
                    + "\n会场地址：" + currentRoomInfo.getAddr().toStringUtf8()
                    + "\n会议保密：" + (currentMeetInfo.getSecrecy() == 1 ? "是" : "否")
                    + "\n会议开始时间：" + DateUtil.secondFormatDateTime(currentMeetInfo.getStartTime())
                    + "\n会议结束时间：" + DateUtil.secondFormatDateTime(currentMeetInfo.getEndTime())
                    + "\n签到方式：" + Constant.getMeetSignInTypeName(currentMeetInfo.getSigninType())
                    + "\n会议管理员：" + (currentAdminInfo != null ? currentAdminInfo.getAdminname().toStringUtf8() : queryCurrentAdminName())
                    + "\n管理员描述：" + (currentAdminInfo != null ? currentAdminInfo.getComment().toStringUtf8() : "")
            ;
            read2file("会议基本信息.txt", content);
        }
    }

    /**
     * 下载议程文件
     */
    private void downloadAgendaFile() {
        if (isCancel) return;
        byte[] bytes = jni.queryFileProperty(InterfaceMacro.Pb_MeetFilePropertyID.Pb_MEETFILE_PROPERTY_NAME.getNumber(), agendaMediaId);
        InterfaceBase.pbui_CommonTextProperty textProperty = null;
        try {
            textProperty = InterfaceBase.pbui_CommonTextProperty.parseFrom(bytes);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        String fileName = textProperty.getPropertyval().toStringUtf8();
        LogUtils.i(TAG, "downloadAgendaFile 获取到文件议程 -->媒体id=" + agendaMediaId + ", 文件名=" + fileName);
        FileUtils.createOrExistsDir(Constant.DIR_ARCHIVE_TEMP);
        File file = new File(Constant.DIR_ARCHIVE_TEMP + fileName);
        if (file.exists()) {
//            if (GlobalValue.downloadingFiles.contains(agendaMediaId)) {
//                view.showToast(R.string.currently_downloading);
//            }
        } else {
            addTask("议程文件" + agendaMediaId);
            jni.downloadFile(Constant.DIR_ARCHIVE_TEMP + fileName, agendaMediaId, 1, 0,
                    Constant.ARCHIVE_AGENDA_FILE);
        }
    }

    /**
     * 将公告写入文件中
     */
    private void notice2file() {
        if (isCancel) return;
        if (noticeData.isEmpty()) {
            return;
        }
        String content = "";
        for (int i = 0; i < noticeData.size(); i++) {
            InterfaceBullet.pbui_Item_BulletDetailInfo item = noticeData.get(i);
            content += "标题：" + item.getTitle().toStringUtf8() + "\n" + "内容：" + item.getContent().toStringUtf8() + "\n\n";
        }
        read2file("会议公告信息.txt", content);
    }

    /**
     * 将文本内容写入到文件中
     *
     * @param fileName 自定义的带后缀文件名
     * @param content  文本内容
     */
    private void read2file(String fileName, String content) {
        if (isCancel) return;
//        FileIOUtils.writeFileFromString()
        try {
            File file = new File(Constant.DIR_ARCHIVE_TEMP + fileName);
            FileUtils.createOrExistsDir(Constant.DIR_ARCHIVE_TEMP);
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fos));
            bufferedWriter.write(content);
            bufferedWriter.close();
            if ("会议基本信息.txt".equals(fileName)) {
                archiveInforms.add(new ArchiveInform("会议基本信息导出完成", "100%"));
            } else if ("会议议程信息.txt".equals(fileName)) {
                archiveInforms.add(new ArchiveInform("会议议程信息导出完成", "100%"));
            } else if ("会议公告信息.txt".equals(fileName)) {
                archiveInforms.add(new ArchiveInform("会议公告信息导出完成", "100%"));
            }
            mView.updateArchiveInform(archiveInforms);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
