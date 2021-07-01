package com.xlk.takstarpaperlessmanage.view.admin.fragment.a_setup.other;

import android.graphics.Color;
import android.text.TextUtils;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceAdmin;
import com.mogujie.tt.protobuf.InterfaceBase;
import com.mogujie.tt.protobuf.InterfaceFaceconfig;
import com.mogujie.tt.protobuf.InterfaceFile;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.xlk.takstarpaperlessmanage.base.BasePresenter;
import com.xlk.takstarpaperlessmanage.model.Constant;
import com.xlk.takstarpaperlessmanage.model.EventMessage;
import com.xlk.takstarpaperlessmanage.model.EventType;
import com.xlk.takstarpaperlessmanage.model.bean.MainInterfaceBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.xlk.takstarpaperlessmanage.model.Constant.s2b;

/**
 * @author Created by xlk on 2021/5/14.
 * @desc 系统设置-其它设置
 */
public class OtherPresenter extends BasePresenter<OtherContract.View> implements OtherContract.Presenter {
    public List<InterfaceBase.pbui_Item_UrlDetailInfo> allUrls = new ArrayList<>();
    public List<InterfaceFile.pbui_Item_MeetDirFileDetailInfo> upgradeFiles = new ArrayList<>();
    public List<InterfaceFile.pbui_Item_MeetDirFileDetailInfo> releaseFileData = new ArrayList<>();
    public List<InterfaceFile.pbui_Item_MeetDirFileDetailInfo> bgFiles = new ArrayList<>();
    /**
     * 主界面信息
     */
    public List<MainInterfaceBean> mainInterfaceBeans = new ArrayList<>();
    /**
     * 投影界面信息
     */
    public List<MainInterfaceBean> projectiveInterfaceBeans = new ArrayList<>();
    /**
     * 公告界面信息
     */
    public List<MainInterfaceBean> noticeInterfaceBeans = new ArrayList<>();
    private Timer timer;
    private TimerTask task;
    private InterfaceAdmin.pbui_Item_AdminDetailInfo localAdmin;

    public OtherPresenter(OtherContract.View view) {
        super(view);
    }

    @Override
    protected void busEvent(EventMessage msg) throws InvalidProtocolBufferException {
        switch (msg.getType()) {
            case EventType.BUS_MAIN_BG: {
                String filePath = (String) msg.getObjects()[0];
                mView.updateMainBgImg(filePath);
                break;
            }
            case EventType.BUS_MAIN_LOGO: {
                String filePath = (String) msg.getObjects()[0];
                mView.updateMainLogoImg(filePath);
                break;
            }
            case EventType.BUS_PROJECTIVE_BG: {
                String filePath = (String) msg.getObjects()[0];
                mView.updateProjectiveBgImg(filePath);
                break;
            }
            case EventType.BUS_PROJECTIVE_LOGO: {
                String filePath = (String) msg.getObjects()[0];
                mView.updateProjectiveLogoImg(filePath);
                break;
            }
            case EventType.BUS_BULLETIN_BG: {
                String filePath = (String) msg.getObjects()[0];
                mView.updateNoticeBgImg(filePath);
                break;
            }
            case EventType.BUS_BULLETIN_LOGO: {
                String filePath = (String) msg.getObjects()[0];
                mView.updateNoticeLogoImg(filePath);
                break;
            }
            //网页变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEFAULTURL_VALUE: {
                queryWebUrl();
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
//                queryUpdateFile();
                queryBigFile();
                queryReleaseFile();
                break;
            }
            //界面配置变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETFACECONFIG_VALUE: {
                byte[] bytes = (byte[]) msg.getObjects()[0];
                InterfaceBase.pbui_MeetNotifyMsg info = InterfaceBase.pbui_MeetNotifyMsg.parseFrom(bytes);
                int id = info.getId();
                int opermethod = info.getOpermethod();
                LogUtils.i(TAG, "BusEvent -->" + "界面配置变更通知 id=" + id + ", opermethod=" + opermethod);
                executeLater();
                break;
            }
            //管理员变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ADMIN_VALUE: {
                if (msg.getMethod() == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_NOTIFY_VALUE) {
                    byte[] bytes = (byte[]) msg.getObjects()[0];
                    InterfaceBase.pbui_MeetNotifyMsg pbui_meetNotifyMsg = InterfaceBase.pbui_MeetNotifyMsg.parseFrom(bytes);
                    int opermethod = pbui_meetNotifyMsg.getOpermethod();
                    int id = pbui_meetNotifyMsg.getId();
                    LogUtils.i("BusEvent 管理员变更通知 id=" + id + ", opermethod=" + opermethod);
                    queryLocalAdmin();
                }
                break;
            }
        }
    }

    private void executeLater() {
        //解决短时间内收到很多通知，查询很多次的问题
        if (timer == null) {
            timer = new Timer();
            LogUtils.i(TAG, "executeLater 创建timer");
            task = new TimerTask() {
                @Override
                public void run() {
                    queryInterFaceConfiguration();
                    task.cancel();
                    timer.cancel();
                    task = null;
                    timer = null;
                }
            };
            LogUtils.i(TAG, "executeLater 500毫秒之后查询");
            timer.schedule(task, 500);
        }
    }

    @Override
    public void queryLocalAdmin() {
        InterfaceAdmin.pbui_TypeAdminDetailInfo info = jni.queryAdmin();
        int localAdminId = queryCurrentAdminId();
        LogUtils.d("当前登录管理员ID=" + localAdminId);
        localAdmin = null;
        if (info != null) {
            for (int i = 0; i < info.getItemList().size(); i++) {
                InterfaceAdmin.pbui_Item_AdminDetailInfo item = info.getItemList().get(i);
                LogUtils.i("管理员账户=" + item.getAdminname().toStringUtf8() + ",密码=" + item.getPw().toStringUtf8());
                if (item.getAdminid() == localAdminId && localAdminId != 0) {
                    localAdmin = item;
                }
            }
        }
    }

    @Override
    public void modifyLocalAdminPassword(String oldpwd, String newpwd) {
        if (localAdmin != null) {
            jni.modifyAdminPwd(localAdmin.getAdminname().toStringUtf8(), newpwd, oldpwd);
        } else {
            LogUtils.e("本机登录的管理员为null");
        }
    }

    @Override
    public void saveMainLogo(int mediaId) {
        InterfaceFaceconfig.pbui_Item_FacePictureItemInfo build = InterfaceFaceconfig.pbui_Item_FacePictureItemInfo.newBuilder()
                .setFaceid(InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACEID_LOGO_VALUE)
                .setMediaid(mediaId)
                .build();
        byte[] bytes = InterfaceFaceconfig.pbui_Type_FaceConfigInfo.newBuilder()
                .addPicture(build)
                .build().toByteArray();
        jni.modifyInterfaceConfig(bytes);
    }

    @Override
    public void saveProjectiveLogo(int mediaId) {
        InterfaceFaceconfig.pbui_Item_FacePictureItemInfo build = InterfaceFaceconfig.pbui_Item_FacePictureItemInfo.newBuilder()
                .setFaceid(InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACEID_PROJECTIVE_LOGO_VALUE)
                .setMediaid(mediaId)
                .build();
        byte[] bytes = InterfaceFaceconfig.pbui_Type_FaceConfigInfo.newBuilder()
                .addPicture(build)
                .build().toByteArray();
        jni.modifyInterfaceConfig(bytes);
    }

    @Override
    public void saveNoticeLogo(int mediaId) {
        InterfaceFaceconfig.pbui_Item_FacePictureItemInfo build = InterfaceFaceconfig.pbui_Item_FacePictureItemInfo.newBuilder()
                .setFaceid(InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACE_BulletinLogo_VALUE)
                .setMediaid(mediaId)
                .build();
        byte[] bytes = InterfaceFaceconfig.pbui_Type_FaceConfigInfo.newBuilder()
                .addPicture(build)
                .build().toByteArray();
        jni.modifyInterfaceConfig(bytes);
    }

    @Override
    public void saveInterfaceConfig(List<MainInterfaceBean> data) {
        List<InterfaceFaceconfig.pbui_Item_FaceTextItemInfo> allText = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            MainInterfaceBean info = data.get(i);
            LogUtils.i(TAG, "saveInterfaceConfig 保存界面配置 item=" + info.toString());
            int faceId = info.getFaceId();
            if (faceId == InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACEID_PROJECTIVE_LOGO_VALUE
                    || faceId == InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACE_BulletinLogo_VALUE) {
                continue;
            }
            InterfaceFaceconfig.pbui_Item_FaceTextItemInfo build = InterfaceFaceconfig.pbui_Item_FaceTextItemInfo.newBuilder()
                    .setFaceid(faceId)
                    .setFlag(info.getFlag())
                    .setFontsize(info.getFontSize())
                    .setColor(info.getColor())
                    .setAlign(info.getAlign())
                    .setFontflag(info.getFontFlag())
                    .setFontname(s2b(info.getFontName()))
                    .setLx(info.getLx())
                    .setLy(info.getLy())
                    .setBx(info.getBx())
                    .setBy(info.getBy())
                    .build();
            allText.add(build);
        }
        byte[] bytes = InterfaceFaceconfig.pbui_Type_FaceConfigInfo.newBuilder()
                .addAllText(allText)
                .build().toByteArray();
        jni.modifyInterfaceConfig(bytes);
    }

    @Override
    public void saveMainBg(int mediaid) {
        if (mediaid != 0) {
            InterfaceFaceconfig.pbui_Item_FacePictureItemInfo build = InterfaceFaceconfig.pbui_Item_FacePictureItemInfo.newBuilder()
                    .setFaceid(InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACEID_MAINBG_VALUE)
                    .setMediaid(mediaid)
                    .build();
            byte[] bytes = InterfaceFaceconfig.pbui_Type_FaceConfigInfo.newBuilder()
                    .addPicture(build)
                    .build().toByteArray();
            jni.modifyInterfaceConfig(bytes);
        }
    }

    @Override
    public void saveProjectiveBg(int mediaid) {
        if (mediaid != 0) {
            InterfaceFaceconfig.pbui_Item_FacePictureItemInfo build = InterfaceFaceconfig.pbui_Item_FacePictureItemInfo.newBuilder()
                    .setFaceid(InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACEID_PROJECTIVE_MIANBG_VALUE)
                    .setMediaid(mediaid)
                    .build();
            byte[] bytes = InterfaceFaceconfig.pbui_Type_FaceConfigInfo.newBuilder()
                    .addPicture(build)
                    .build().toByteArray();
            jni.modifyInterfaceConfig(bytes);
        }
    }

    @Override
    public void saveNoticeBg(int mediaid) {
        if (mediaid != 0) {
            InterfaceFaceconfig.pbui_Item_FacePictureItemInfo build = InterfaceFaceconfig.pbui_Item_FacePictureItemInfo.newBuilder()
                    .setFaceid(InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACE_BulletinBK_VALUE)
                    .setMediaid(mediaid)
                    .build();
            byte[] bytes = InterfaceFaceconfig.pbui_Type_FaceConfigInfo.newBuilder()
                    .addPicture(build)
                    .build().toByteArray();
            jni.modifyInterfaceConfig(bytes);
        }
    }

    @Override
    public void queryWebUrl() {
        InterfaceBase.pbui_meetUrl info = jni.queryUrl();
        allUrls.clear();
        if (info != null) {
            int isetdefault = info.getIsetdefault();
            LogUtils.i("查询网址：1表示系统全局=" + isetdefault);
            allUrls.addAll(info.getItemList());
        }
        mView.updateWebUrlList();
    }

    @Override
    public void queryReleaseFile() {
        InterfaceFile.pbui_TypePageResQueryrFileInfo info = jni.queryFile(
                0, InterfaceMacro.Pb_MeetFileQueryFlag.Pb_MEET_FILETYPE_QUERYFLAG_ATTRIB_VALUE, 0, 0, 0,
                InterfaceMacro.Pb_MeetFileAttrib.Pb_MEETFILE_ATTRIB_PUBLISH_VALUE, 1, 0);
        //过滤掉媒体id小于0的文件，测试查看结果是：文档类和其它类的文件媒体id都<0
        releaseFileData.clear();
        if (info != null) {
            List<InterfaceFile.pbui_Item_MeetDirFileDetailInfo> itemList = info.getItemList();
            for (int i = 0; i < itemList.size(); i++) {
                InterfaceFile.pbui_Item_MeetDirFileDetailInfo item = itemList.get(i);
                String fileName = item.getName().toStringUtf8();
                boolean releaseFile = Constant.isReleaseFile(item.getAttrib());
                LogUtils.i("当前文件 releaseFile=" + releaseFile + ",fileName=" + fileName);
                if (releaseFile) {
                    releaseFileData.add(item);
                }
            }
        }
        LogUtils.i(TAG, "queryReleaseFile releaseFileData.size=" + releaseFileData.size());
        mView.updateReleaseFileList();
    }

    @Override
    public void queryUpgradeFile() {
        InterfaceFile.pbui_TypePageResQueryrFileInfo object = jni.queryFile(
                0, InterfaceMacro.Pb_MeetFileQueryFlag.Pb_MEET_FILETYPE_QUERYFLAG_ATTRIB_VALUE, 0, 0, 0,
                InterfaceMacro.Pb_MeetFileAttrib.Pb_MEETFILE_ATTRIB_DEVICEUPDATE_VALUE, 1, 0);
        upgradeFiles.clear();
        if (object != null) {
            upgradeFiles.addAll(object.getItemList());
        }
        LogUtils.i(TAG, "queryUpdateFile updateFileData.size=" + upgradeFiles.size());
        mView.updateUpgradeFileList();
    }

    @Override
    public void saveReleaseFile(int mediaId) {
        InterfaceFaceconfig.pbui_Item_FacePictureItemInfo build = InterfaceFaceconfig.pbui_Item_FacePictureItemInfo.newBuilder()
                .setFaceid(InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACEID_SHOWFILE_VALUE)
                .setFlag(InterfaceMacro.Pb_MeetFaceFlag.Pb_MEET_FACEFLAG_SHOW_VALUE)
                .setMediaid(mediaId)
                .build();
        byte[] bytes = InterfaceFaceconfig.pbui_Type_FaceConfigInfo.newBuilder()
                .addPicture(build)
                .build().toByteArray();
        jni.modifyInterfaceConfig(bytes);
    }

    @Override
    public void queryBigFile() {
        InterfaceFile.pbui_TypePageResQueryrFileInfo info = jni.queryFile(0, InterfaceMacro.Pb_MeetFileQueryFlag.Pb_MEET_FILETYPE_QUERYFLAG_ATTRIB_VALUE
                , 0, 0, 0, InterfaceMacro.Pb_MeetFileAttrib.Pb_MEETFILE_ATTRIB_BACKGROUND_VALUE, 1, 0);
        bgFiles.clear();
        if (info != null) {
            bgFiles.addAll(info.getItemList());
        }
        mView.updateBgFileList();
    }

    @Override
    public void modifyCompanyName(String companyName) {
        InterfaceFaceconfig.pbui_Item_FaceOnlyTextItemInfo build = InterfaceFaceconfig.pbui_Item_FaceOnlyTextItemInfo.newBuilder()
                .setFaceid(InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACEID_COLTDTEXT_VALUE)
                .setFlag(InterfaceMacro.Pb_MeetFaceFlag.Pb_MEET_FACEFLAG_ONLYTEXT_VALUE)
                .setText(s2b(companyName)).build();
        byte[] bytes = InterfaceFaceconfig.pbui_Type_FaceConfigInfo.newBuilder()
                .addOnlytext(build)
                .build().toByteArray();
        jni.modifyInterfaceConfig(bytes);
    }

    @Override
    public void queryInterFaceConfiguration() {
        InterfaceFaceconfig.pbui_Type_FaceConfigInfo info = jni.queryInterFaceConfiguration();
        mainInterfaceBeans.clear();
        projectiveInterfaceBeans.clear();
        noticeInterfaceBeans.clear();
        if (info != null) {
            List<InterfaceFaceconfig.pbui_Item_FacePictureItemInfo> pictureList = info.getPictureList();
            List<InterfaceFaceconfig.pbui_Item_FaceTextItemInfo> textList = info.getTextList();
            List<InterfaceFaceconfig.pbui_Item_FaceOnlyTextItemInfo> onlytextList = info.getOnlytextList();
            for (int i = 0; i < pictureList.size(); i++) {
                InterfaceFaceconfig.pbui_Item_FacePictureItemInfo item = pictureList.get(i);
                int faceid = item.getFaceid();
                int flag = item.getFlag();
                int mediaid = item.getMediaid();
                String userStr = "";
                if (faceid == InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACEID_MAINBG_VALUE) {
                    //主界面背景
                    userStr = Constant.MAIN_BG_PNG_TAG;
                } else if (faceid == InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACEID_LOGO_VALUE) {
                    //logo图标
                    userStr = Constant.MAIN_LOGO_PNG_TAG;
                } else if (faceid == InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACEID_PROJECTIVE_MIANBG_VALUE) {
                    //投影界面背景图
                    userStr = Constant.PROJECTIVE_BG_PNG_TAG;
                } else if (faceid == InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACEID_PROJECTIVE_LOGO_VALUE) {
                    //投影界面logo图标
                    userStr = Constant.PROJECTIVE_LOGO_PNG_TAG;
                } else if (faceid == InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACE_BulletinBK_VALUE) {
                    //公告背景图
                    userStr = Constant.NOTICE_BG_PNG_TAG;
                } else if (faceid == InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACE_BulletinLogo_VALUE) {
                    //公告logo图标
                    userStr = Constant.NOTICE_LOGO_PNG_TAG;
                }
                LogUtils.i(TAG, "queryInterFaceConfiguration pictureList faceid=" + faceid + ",mediaid=" + mediaid + ",userStr=" + userStr);
                if (!TextUtils.isEmpty(userStr)) {
                    String fileName = jni.queryFileNameByMediaId(mediaid);
                    FileUtils.createOrExistsDir(Constant.config_dir);
                    String filepath = Constant.config_dir + /*userStr + ".png"*/fileName;
                    if (!FileUtils.isFileExists(filepath)) {
                        jni.downloadFile(filepath, mediaid, 1, 0, userStr);
                    } else {
                        if (faceid == InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACEID_MAINBG_VALUE) {
                            //主界面背景
                            mView.updateMainBgImg(filepath);
                        } else if (faceid == InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACEID_LOGO_VALUE) {
                            //logo图标
                            mView.updateMainLogoImg(filepath);
                        } else if (faceid == InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACEID_PROJECTIVE_MIANBG_VALUE) {
                            //投影界面背景图
                            mView.updateProjectiveBgImg(filepath);
                        } else if (faceid == InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACEID_PROJECTIVE_LOGO_VALUE) {
                            //投影界面logo图标
                            mView.updateProjectiveLogoImg(filepath);
                        } else if (faceid == InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACE_BulletinBK_VALUE) {
                            //公告背景图
                            mView.updateNoticeBgImg(filepath);
                        } else if (faceid == InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACE_BulletinLogo_VALUE) {
                            //公告logo图标
                            mView.updateNoticeLogoImg(filepath);
                        }
                    }
                }
                if (faceid == InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACEID_SHOWFILE_VALUE) {
                    String fileName = jni.queryFileNameByMediaId(mediaid);
                    LogUtils.i(TAG, "queryInterFaceConfiguration 会议发布文件名=" + fileName);
                    mView.updateReleaseFileName(fileName, mediaid);
                }
            }

            for (int i = 0; i < textList.size(); i++) {
                InterfaceFaceconfig.pbui_Item_FaceTextItemInfo item = textList.get(i);
                int faceid = item.getFaceid();
                LogUtils.d(TAG, "queryInterFaceConfiguration textList faceid=" + faceid);
                int flag = item.getFlag();
                MainInterfaceBean bean = new MainInterfaceBean(faceid, flag);
                bean.setAlign(item.getAlign());
                bean.setColor(item.getColor());
                bean.setFontFlag(item.getFontflag());
                bean.setFontName(item.getFontname().toStringUtf8());
                bean.setFontSize(item.getFontsize());
                bean.setLx(item.getLx());
                bean.setLy(item.getLy());
                bean.setBx(item.getBx());
                bean.setBy(item.getBy());
                if (isMainInterface(faceid)) {
                    mainInterfaceBeans.add(bean);
                } else if (isProjectiveInterface(faceid)) {
                    projectiveInterfaceBeans.add(bean);
                } else if (isNoticeInterface(faceid)) {
                    noticeInterfaceBeans.add(bean);
                }
            }

            for (int i = 0; i < onlytextList.size(); i++) {
                if (onlytextList.get(i).getFaceid() == InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACEID_COLTDTEXT_VALUE) {
                    String company = onlytextList.get(i).getText().toStringUtf8();
                    mView.updateCompany(company);
                }
            }
        }
        MainInterfaceBean proLogo = new MainInterfaceBean(InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACEID_PROJECTIVE_LOGO_VALUE, 1);
        proLogo.setAlign(4);
        proLogo.setColor(Color.rgb(250, 0, 0));
        proLogo.setFontFlag(0);
        proLogo.setFontName("");
        proLogo.setFontSize(20);
        proLogo.setLx(0);
        proLogo.setLy(0);
        proLogo.setBx(20);
        proLogo.setBy(15);
        projectiveInterfaceBeans.add(proLogo);
        MainInterfaceBean noticeLogo = new MainInterfaceBean(InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACE_BulletinLogo_VALUE, 1);
        noticeLogo.setAlign(4);
        noticeLogo.setColor(Color.rgb(250, 0, 0));
        noticeLogo.setFontFlag(0);
        noticeLogo.setFontName("");
        noticeLogo.setFontSize(20);
        noticeLogo.setLx(0);
        noticeLogo.setLy(0);
        noticeLogo.setBx(20);
        noticeLogo.setBy(15);
        noticeInterfaceBeans.add(noticeLogo);
        mView.updateInterface();
    }

    private boolean isMainInterface(int faceid) {
        return
                //logo图标
                faceid == InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACE_LOGO_GEO_VALUE
                        //会议名称
                        || faceid == InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACEID_MEETNAME_VALUE
                        //参会人名称
                        || faceid == InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACEID_MEMBERNAME_VALUE
                        //参会人单位
                        || faceid == InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACEID_MEMBERCOMPANY_VALUE
                        //参会人职业
                        || faceid == InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACEID_MEMBERJOB_VALUE
                        //座席名称
                        || faceid == InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACEID_SEATNAME_VALUE
                        //日期时间
                        || faceid == InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACEID_TIMER_VALUE
                        //单位名称
                        || faceid == InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACEID_COMPANY_VALUE
                        //公司名称 onlyText
//                        || faceid == InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACEID_COLTDTEXT_VALUE
                        //会议状态
                        || faceid == InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACE_topstatus_GEO_VALUE
                        //进入会议按钮
                        || faceid == InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACE_checkin_GEO_VALUE
                        //进入后台
                        || faceid == InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACE_manage_GEO_VALUE
                        //备注
                        || faceid == InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACE_remark_GEO_VALUE
                        //角色
                        || faceid == InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACE_role_GEO_VALUE
                        //版本
                        || faceid == InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACE_ver_GEO_VALUE
                ;
    }

    private boolean isProjectiveInterface(int faceid) {
        return
                //背景
//                faceid == InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACEID_PROJECTIVE_MIANBG_VALUE
                //logo
//                || faceid == InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACEID_PROJECTIVE_LOGO_VALUE
                //会议名称
                faceid == InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACEID_PROJECTIVE_MEETNAME_VALUE
                        //坐席名称
                        || faceid == InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACEID_PROJECTIVE_SEATNAME_VALUE
                        //日期时间
                        || faceid == InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACEID_PROJECTIVE_TIMER_VALUE
                        //单位名称
                        || faceid == InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACEID_PROJECTIVE_COMPANY_VALUE
                        //公司名称
                        || faceid == InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACEID_PROJECTIVE_COMPANYNAME_VALUE
                        //会议状态
                        || faceid == InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACEID_PROJECTIVE_STATUS_VALUE
                        //应到
                        || faceid == InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACEID_PROJECTIVE_SIGN_ALL_VALUE
                        //已到
                        || faceid == InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACEID_PROJECTIVE_SIGN_IN_VALUE
                        //未到
                        || faceid == InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACEID_PROJECTIVE_SIGN_OUT_VALUE
                ;
    }

    private boolean isNoticeInterface(int faceid) {

        return
                //公告背景
//                faceid == InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACE_BulletinBK_VALUE
                //公告logo
//                || faceid == InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACE_BulletinLogo_VALUE
                //公告标题
                faceid == InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACE_BulletinTitle_VALUE
                        //公告内容
                        || faceid == InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACE_BulletinContent_VALUE
                        //公告按钮
                        || faceid == InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACE_BulletinBtn_VALUE
                ;
    }

}
