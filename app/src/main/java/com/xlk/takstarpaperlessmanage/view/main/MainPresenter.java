package com.xlk.takstarpaperlessmanage.view.main;

import android.media.MediaCodecInfo;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceAdmin;
import com.mogujie.tt.protobuf.InterfaceBase;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.base.BasePresenter;
import com.xlk.takstarpaperlessmanage.model.Call;
import com.xlk.takstarpaperlessmanage.model.EventMessage;
import com.xlk.takstarpaperlessmanage.model.GlobalValue;
import com.xlk.takstarpaperlessmanage.util.CodecUtil;

import java.util.List;
import java.util.Objects;

/**
 * @author Created by xlk on 2021/4/21.
 * @desc
 */
public class MainPresenter extends BasePresenter<MainContract.View> implements MainContract.Presenter {
    public MainPresenter(MainContract.View view) {
        super(view);
    }

    @Override
    protected void busEvent(EventMessage msg) throws InvalidProtocolBufferException {
        switch (msg.getType()) {
            //平台初始化结果
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_READY_VALUE: {
                int method = msg.getMethod();
                byte[] bytes = (byte[]) msg.getObjects()[0];
                if (method == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_NOTIFY_VALUE) {
                    InterfaceBase.pbui_Ready error = InterfaceBase.pbui_Ready.parseFrom(bytes);
                    int areaid = error.getAreaid();
                    LogUtils.i(TAG, "平台初始化结果 连接上的区域服务器ID=" + areaid);
                    GlobalValue.initializationIsOver = true;
                    initial();
                } else if (method == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_LOGON_VALUE) {
                    InterfaceBase.pbui_Type_LogonError error = InterfaceBase.pbui_Type_LogonError.parseFrom(bytes);
                    //Pb_WalletSystem_ErrorCode
                    int errcode = error.getErrcode();
                    LogUtils.i(TAG, "平台初始化结果 errcode=" + errcode);
                }
                break;
            }
            //平台登陆验证返回
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEVALIDATE_VALUE: {
                byte[] s = (byte[]) msg.getObjects()[0];
                InterfaceBase.pbui_Type_DeviceValidate deviceValidate = InterfaceBase.pbui_Type_DeviceValidate.parseFrom(s);
                int valflag = deviceValidate.getValflag();
                List<Integer> valList = deviceValidate.getValList();
                List<Long> user64BitdefList = deviceValidate.getUser64BitdefList();
                String binaryString = Integer.toBinaryString(valflag);
                LogUtils.i("initFailed valflag=" + valflag + "，二进制：" + binaryString + ", valList=" + valList.toString() + ", user64List=" + user64BitdefList.toString());
                int count = 0, index;
                //  1 1101 1111
                char[] chars = binaryString.toCharArray();
                for (int i = 0; i < chars.length; i++) {
                    if ((chars[chars.length - 1 - i]) == '1') {
                        //有效位个数+1
                        count++;
                        //有效位当前位于valList的索引（跟i是无关的）
                        index = count - 1;
                        int code = valList.get(index);
                        LogUtils.d("initFailed 有效位：" + i + ",当前有效位的个数：" + count);
                        switch (i) {
                            case 0:
                                LogUtils.e("initFailed 区域服务器ID：" + code);
                                break;
                            case 1:
                                LogUtils.e("initFailed 设备ID：" + code);
                                GlobalValue.localDeviceId = code;
                                break;
                            case 2:
                                LogUtils.e("initFailed 状态码：" + code);
                                initializationResult(code);
                                break;
                            case 3:
                                LogUtils.e("initFailed 到期时间：" + code);
                                break;
                            case 4:
                                LogUtils.e("initFailed 企业ID：" + code);
                                break;
                            case 5:
                                LogUtils.e("initFailed 协议版本：" + code);
                                break;
                            case 6:
                                LogUtils.e("initFailed 注册时自定义的32位整数值：" + code);
                                break;
                            case 7:
                                LogUtils.e("initFailed 当前在线设备数：" + code);
                                break;
                            case 8:
                                LogUtils.e("initFailed 最大在线设备数：" + code);
                                break;
                            default:
                                break;
                        }
                    }
                }
                break;
            }
            //数据后台回复的错误信息
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DBSERVERERROR_VALUE: {
                byte[] bytes = (byte[]) msg.getObjects()[0];
                InterfaceBase.pbui_Type_MeetDBServerOperError info = InterfaceBase.pbui_Type_MeetDBServerOperError.parseFrom(bytes);
                if (info != null) {
                    int type = info.getType();
                    int method = info.getMethod();
                    int status = info.getStatus();
                    LogUtils.e(TAG, "数据后台回复的错误信息 type=" + type + ",method=" + method + ",status=" + status);
//                    if (type == 8 && method == 10) {
//                        //管理员登录
//                        mView.updateLoginStatus(status);
//                    }
                }
                break;
            }
            //管理员登录返回
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ADMIN_VALUE: {
                if (msg.getMethod() == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_LOGON_VALUE) {
                    LogUtils.i(TAG, "管理员登录返回");
                    byte[] bytes = (byte[]) msg.getObjects()[0];
                    InterfaceAdmin.pbui_Type_AdminLogonStatus info = InterfaceAdmin.pbui_Type_AdminLogonStatus.parseFrom(bytes);
                    if (info != null) {
                        mView.loginBack(info);
                    }
                }
                break;
            }
        }
    }

    private void initial() {
        jni.modifyContextProperties(InterfaceMacro.Pb_ContextPropertyID.Pb_MEETCONTEXT_PROPERTY_ROLE_VALUE,
                InterfaceMacro.Pb_MeetFaceStatus.Pb_MemState_MainFace_VALUE);
        int format = CodecUtil.selectColorFormat(Objects.requireNonNull(CodecUtil.selectCodec("video/avc")), "video/avc");
        switch (format) {
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar:
                Call.COLOR_FORMAT = 0;
                break;
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar:
                Call.COLOR_FORMAT = 1;
                break;
            default:
                break;
        }
        jni.InitAndCapture(0, 2);
        jni.InitAndCapture(0, 3);
    }

    private void initializationResult(int code) {
        int resId;
        switch (code) {
            case InterfaceMacro.Pb_ValidateErrorCode.Pb_PARSER_ERROR_NONE_VALUE:
                resId = R.string.error_0;
                break;
            case InterfaceMacro.Pb_ValidateErrorCode.Pb_PARSER_ERROR_EXPIRATION_VALUE:
                resId = R.string.error_1;
                break;
            case InterfaceMacro.Pb_ValidateErrorCode.Pb_PARSER_ERROR_OPER_VALUE:
                resId = R.string.error_2;
                break;
            case InterfaceMacro.Pb_ValidateErrorCode.Pb_PARSER_ERROR_ENTERPRISE_VALUE:
                resId = R.string.error_3;
                break;
            case InterfaceMacro.Pb_ValidateErrorCode.Pb_PARSER_ERROR_NODEVICEID_VALUE:
                resId = R.string.error_4;
                break;
            case InterfaceMacro.Pb_ValidateErrorCode.Pb_PARSER_ERROR_NOALLOWIN_VALUE:
                resId = R.string.error_5;
                break;
            case InterfaceMacro.Pb_ValidateErrorCode.Pb_PARSER_ERROR_FILEERROR_VALUE:
                resId = R.string.error_6;
                break;
            case InterfaceMacro.Pb_ValidateErrorCode.Pb_PARSER_ERROR_INVALID_VALUE:
                resId = R.string.error_7;
                break;
            case InterfaceMacro.Pb_ValidateErrorCode.Pb_PARSER_ERROR_IDOCCUPY_VALUE:
                resId = R.string.error_8;
                break;
            case InterfaceMacro.Pb_ValidateErrorCode.Pb_PARSER_ERROR_NOTBEING_VALUE:
                resId = R.string.error_9;
                break;
            case InterfaceMacro.Pb_ValidateErrorCode.Pb_PARSER_ERROR_ONLYDEVICEID_VALUE:
                resId = R.string.error_10;
                break;
            case InterfaceMacro.Pb_ValidateErrorCode.Pb_PARSER_ERROR_DEVICETYPENOMATCH_VALUE:
                resId = R.string.error_11;
                break;
            default:
                resId = 0;
                break;
        }
        if (resId != 0) {
            ToastUtils.showShort(resId);
        }
        /*
        //平台初始化成功
        if (code == InterfaceMacro.Pb_ValidateErrorCode.Pb_PARSER_ERROR_NONE_VALUE) {
            GlobalValue.initializationIsOver = true;
            initial();
        }
        */
    }
}
