package com.xlk.takstarpaperlessmanage.model;

import com.mogujie.tt.protobuf.InterfaceMacro;

/**
 * @author Created by xlk on 2021/6/1.
 * @desc
 */
public class LenLimit {
    /**
     * 检查签到密码的长度
     * @param pwd 密码
     * @return
     */
    public boolean checkSignInPassword(String pwd) {
        return pwd.length() <= InterfaceMacro.Pb_String_LenLimit.Pb_SIGNIN_PSW_LEN_VALUE;
    }
}
