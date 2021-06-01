package com.xlk.takstarpaperlessmanage.view.admin.fragment.e_after.sign;

import com.blankj.utilcode.util.LogUtils;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceMeet;
import com.mogujie.tt.protobuf.InterfaceMember;
import com.mogujie.tt.protobuf.InterfaceRoom;
import com.mogujie.tt.protobuf.InterfaceSignin;
import com.xlk.takstarpaperlessmanage.base.BasePresenter;
import com.xlk.takstarpaperlessmanage.model.EventMessage;
import com.xlk.takstarpaperlessmanage.model.bean.PdfSignBean;
import com.xlk.takstarpaperlessmanage.model.bean.SignInBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by xlk on 2021/5/27.
 * @desc
 */
class SignPresenter extends BasePresenter<SignContract.View> implements SignContract.Presenter {
    public List<SignInBean> signInBeans = new ArrayList<>();
    private int signInCount;
    private InterfaceMeet.pbui_Item_MeetMeetInfo currentMeetInfo;
    private InterfaceRoom.pbui_Item_MeetRoomDetailInfo currentRoomInfo;

    public SignPresenter(SignContract.View view) {
        super(view);
    }

    @Override
    protected void busEvent(EventMessage msg) throws InvalidProtocolBufferException {
        switch (msg.getType()) {
            //参会人员变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEMBER_VALUE:
                LogUtils.i(TAG, "busEvent 参会人员变更通知");
                queryMember();
                break;
            //签到变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETSIGN_VALUE:
                LogUtils.i(TAG, "busEvent 签到变更通知");
                querySignInfo();
                break;
            //会议信息变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETINFO_VALUE: {
                LogUtils.i(TAG, "BusEvent 会议信息变更通知");
                queryMeetingInfo();
                break;
            }
            //会场信息变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ROOM_VALUE: {
                LogUtils.i(TAG, "BusEvent 会场信息变更通知");
                queryMeetingRoomInfo();
                break;
            }
            default:
                break;
        }
    }

    @Override
    public void queryMember() {
        InterfaceMember.pbui_Type_MemberDetailInfo info = jni.queryMember();
        signInBeans.clear();
        if (info != null) {
            List<InterfaceMember.pbui_Item_MemberDetailInfo> itemList = info.getItemList();
            for (int i = 0; i < itemList.size(); i++) {
                signInBeans.add(new SignInBean(itemList.get(i)));
            }
        }
        querySignInfo();
        queryMeetingInfo();
        queryMeetingRoomInfo();
    }

    private void queryMeetingInfo() {
        currentMeetInfo = jni.queryMeetingById(getCurrentMeetingId());
    }

    private void queryMeetingRoomInfo() {
        currentRoomInfo = jni.queryRoomById(queryCurrentRoomId());
    }

    public PdfSignBean getPdfData() {
        return new PdfSignBean(currentMeetInfo, currentRoomInfo, signInBeans, signInCount);
    }

    private void querySignInfo() {
        InterfaceSignin.pbui_Type_MeetSignInDetailInfo info = jni.querySignin();
        //已经签到的人数
        signInCount = 0;
        if (info != null) {
            List<InterfaceSignin.pbui_Item_MeetSignInDetailInfo> itemList = info.getItemList();
            for (int i = 0; i < signInBeans.size(); i++) {
                SignInBean bean = signInBeans.get(i);
                bean.setSign(null);
                for (int j = 0; j < itemList.size(); j++) {
                    InterfaceSignin.pbui_Item_MeetSignInDetailInfo sign = itemList.get(j);
                    if (bean.getMember().getPersonid() == sign.getNameId()) {
                        bean.setSign(sign);
                        if (sign.getUtcseconds() > 0) {
                            signInCount++;
                        }
                    }
                }
            }
        } else {
            //变更通知处查询，没有任何签到信息时需要置空
            for (int i = 0; i < signInBeans.size(); i++) {
                SignInBean bean = signInBeans.get(i);
                bean.setSign(null);
            }
        }
        mView.updateSignList(signInCount);
    }

    @Override
    public void deleteSignIn(List<Integer> memberIds) {
        if (getCurrentMeetingId() == 0) {
            LogUtils.e("查询不到当前会议");
            return;
        }
        jni.deleteSignIn(getCurrentMeetingId(), memberIds);
    }

}
