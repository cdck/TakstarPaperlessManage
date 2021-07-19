package com.xlk.takstarpaperlessmanage.adapter;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.blankj.utilcode.util.LogUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceRoom;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.model.bean.MemberRoleBean;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Created by xlk on 2021/5/22.
 * @desc
 */
public class ModifyMemberRoleAdapter extends BaseQuickAdapter<MemberRoleBean, BaseViewHolder> {
    private Map<Integer, Integer> memberRole = new HashMap<>();

    public ModifyMemberRoleAdapter(@Nullable List<MemberRoleBean> data) {
        super(R.layout.item_modify_member_role, data);
    }

    public List<InterfaceRoom.pbui_Item_MeetSeatDetailInfo> getNewSeat() {
        List<InterfaceRoom.pbui_Item_MeetSeatDetailInfo> temps = new ArrayList<>();
        for (int i = 0; i < getData().size(); i++) {
            MemberRoleBean bean = getData().get(i);
            InterfaceRoom.pbui_Item_MeetRoomDevSeatDetailInfo seat = bean.getSeat();
            int personid = bean.getMember().getPersonid();
            InterfaceRoom.pbui_Item_MeetSeatDetailInfo build = InterfaceRoom.pbui_Item_MeetSeatDetailInfo.newBuilder()
                    .setNameId(personid)
                    .setSeatid(seat != null ? seat.getDevid() : 0)
                    .setRole(memberRole.containsKey(personid) ? memberRole.get(personid) : 0)
                    .build();
            temps.add(build);
        }
        return temps;
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, MemberRoleBean item) {
        InterfaceRoom.pbui_Item_MeetRoomDevSeatDetailInfo seat = item.getSeat();
        boolean isBind = seat != null;
        holder.setText(R.id.item_view_1, String.valueOf(holder.getLayoutPosition() + 1))
                .setText(R.id.item_view_2, item.getMember().getName().toStringUtf8())
                .setText(R.id.item_view_3, isBind ? seat.getDevname().toStringUtf8() : "");
        Spinner item_spinner = holder.getView(R.id.item_spinner);

        ArrayAdapter<String> spFontAdapter  = new ArrayAdapter<String>(getContext(), R.layout.spinner_checked_black_text,
                getContext().getResources().getStringArray(R.array.member_role));
        item_spinner.setAdapter(spFontAdapter);

        item_spinner.setVisibility(isBind ? View.VISIBLE : View.GONE);
        if (isBind) {
            int role = seat.getRole();
            item_spinner.setSelection(role);
            item_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    int role = 0;
                    switch (position) {
                        case 1: {
                            role = InterfaceMacro.Pb_MeetMemberRole.Pb_role_member_normal_VALUE;
                            break;
                        }
                        case 2: {
                            role = InterfaceMacro.Pb_MeetMemberRole.Pb_role_member_compere_VALUE;
                            break;
                        }
                        case 3: {
                            role = InterfaceMacro.Pb_MeetMemberRole.Pb_role_member_secretary_VALUE;
                            break;
                        }
                        case 4: {
                            role = InterfaceMacro.Pb_MeetMemberRole.Pb_role_admin_VALUE;
                            break;
                        }
                    }
                    LogUtils.e(item.getMember().getName().toStringUtf8() + " 设置新的角色=" + role);
                    memberRole.put(item.getSeat().getMemberid(), role);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }
}
