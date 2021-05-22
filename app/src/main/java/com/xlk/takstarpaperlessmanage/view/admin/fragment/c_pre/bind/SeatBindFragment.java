package com.xlk.takstarpaperlessmanage.view.admin.fragment.c_pre.bind;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceRoom;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.adapter.ModifyMemberRoleAdapter;
import com.xlk.takstarpaperlessmanage.adapter.SeatBindMemberAdapter;
import com.xlk.takstarpaperlessmanage.base.BaseFragment;
import com.xlk.takstarpaperlessmanage.model.bean.MemberRoleBean;
import com.xlk.takstarpaperlessmanage.model.bean.SeatBean;
import com.xlk.takstarpaperlessmanage.ui.CustomSeatView;
import com.xlk.takstarpaperlessmanage.ui.RvItemDecoration;
import com.xlk.takstarpaperlessmanage.util.JxlUtil;
import com.xlk.takstarpaperlessmanage.util.PopUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author Created by xlk on 2021/5/21.
 * @desc
 */
public class SeatBindFragment extends BaseFragment<SeatBindPresenter> implements SeatBindContract.View {
    private LinearLayout rootView;
    private RecyclerView rvMember;
    private CustomSeatView seatView;
    private SeatBindMemberAdapter bindMemberAdapter;
    private SeatBindMemberAdapter seatBindMemberAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_bind_seat;
    }

    @Override
    protected void initView(View inflate) {
        rootView = (LinearLayout) inflate.findViewById(R.id.root_view);
        rvMember = (RecyclerView) inflate.findViewById(R.id.rv_member);
        seatView = (CustomSeatView) inflate.findViewById(R.id.seat_view);
        inflate.findViewById(R.id.btn_member_role).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<MemberRoleBean> temps = new ArrayList<>();
                temps.addAll(presenter.memberRoleBeans);
                showMemberRolePop(temps);
            }
        });
        inflate.findViewById(R.id.btn_bind).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int memberId = bindMemberAdapter.getSelectedId();
                if (memberId == -1) {
                    ToastUtils.showShort(R.string.please_choose_member_first);
                    return;
                }
                List<Integer> selectedIds = seatView.getSelectedIds();
                if (selectedIds.isEmpty()) {
                    ToastUtils.showShort(R.string.please_choose_seat);
                    return;
                }
                jni.modifyMeetRanking(memberId, InterfaceMacro.Pb_MeetMemberRole.Pb_role_member_normal_VALUE, selectedIds.get(0));
            }
        });
        inflate.findViewById(R.id.btn_unbind).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Integer> selectedIds = seatView.getSelectedIds();
                if (selectedIds.isEmpty()) {
                    ToastUtils.showShort(R.string.please_choose_seat);
                    return;
                }
                jni.modifyMeetRanking(0, InterfaceMacro.Pb_MeetMemberRole.Pb_role_member_normal_VALUE, selectedIds.get(0));
            }
        });
        inflate.findViewById(R.id.btn_random_bind).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < presenter.memberRoleBeans.size(); i++) {
                    for (int j = 0; j < presenter.seatData.size(); j++) {
                        if (i == j) {
                            jni.modifyMeetRanking(presenter.memberRoleBeans.get(i).getMember().getPersonid(),
                                    InterfaceMacro.Pb_MeetMemberRole.Pb_role_member_normal_VALUE,
                                    presenter.seatData.get(j).getDevid());
                            break;
                        }
                    }
                }
            }
        });
        inflate.findViewById(R.id.btn_unbind_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<InterfaceRoom.pbui_Item_MeetSeatDetailInfo> temps = new ArrayList<>();
                for (int i = 0; i < presenter.seatData.size(); i++) {
                    InterfaceRoom.pbui_Item_MeetSeatDetailInfo build = InterfaceRoom.pbui_Item_MeetSeatDetailInfo.newBuilder()
                            .setNameId(0)
                            .setSeatid(presenter.seatData.get(i).getDevid())
                            .setRole(InterfaceMacro.Pb_MeetMemberRole.Pb_role_member_normal_VALUE)
                            .build();
                    temps.add(build);
                }
                jni.modifyMeetRanking(temps);
            }
        });
        inflate.findViewById(R.id.btn_import).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        inflate.findViewById(R.id.btn_export).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JxlUtil.exportSeatInfo(presenter.memberRoleBeans);
            }
        });
    }

    @Override
    protected SeatBindPresenter initPresenter() {
        return new SeatBindPresenter(this);
    }

    @Override
    protected void initial() {
        seatView.setChooseSingle(true);
        seatView.setCanDragSeat(false);
        seatView.post(() -> {
            seatView.setViewSize(seatView.getWidth(), seatView.getHeight());
            presenter.queryMember();
            presenter.queryRoomBgPic();
        });
    }

    private void showMemberRolePop(List<MemberRoleBean> memberRoleBeans) {
        /** 删除PopupWindow **/
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.pop_seat_member_role, null, false);
        View ll_content = getActivity().findViewById(R.id.ll_content);
        View rv_navigation = getActivity().findViewById(R.id.rv_navigation);
        int width = ll_content.getWidth();
        int height = ll_content.getHeight();
        int width1 = rv_navigation.getWidth();
        PopupWindow pop = PopUtil.createPopupWindow(inflate, width * 2 / 3, height * 2 / 3, rootView, Gravity.CENTER, width1 / 2, 0);
        RecyclerView rv_pop_member = inflate.findViewById(R.id.rv_pop_member);
        rv_pop_member.setLayoutManager(new LinearLayoutManager(getContext()));
        //rv_pop_member.addItemDecoration(new RvItemDecoration(getContext()));
        ModifyMemberRoleAdapter modifyMemberRoleAdapter = new ModifyMemberRoleAdapter(memberRoleBeans);
        rv_pop_member.setAdapter(modifyMemberRoleAdapter);
        inflate.findViewById(R.id.iv_close).setOnClickListener(v -> pop.dismiss());
        inflate.findViewById(R.id.btn_cancel).setOnClickListener(v -> pop.dismiss());
        inflate.findViewById(R.id.btn_define).setOnClickListener(v -> {
            List<InterfaceRoom.pbui_Item_MeetSeatDetailInfo> newSeat = modifyMemberRoleAdapter.getNewSeat();
            jni.modifyMeetRanking(newSeat);
            pop.dismiss();
        });
    }

    @Override
    public void updateMemberList() {
        getActivity().runOnUiThread(() -> {
            if (bindMemberAdapter == null) {
                bindMemberAdapter = new SeatBindMemberAdapter(presenter.memberRoleBeans);
                rvMember.setLayoutManager(new LinearLayoutManager(getContext()));
                rvMember.addItemDecoration(new RvItemDecoration(getContext()));
                rvMember.setAdapter(bindMemberAdapter);
                bindMemberAdapter.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                        bindMemberAdapter.setSelected(presenter.memberRoleBeans.get(position).getMember().getPersonid());
                    }
                });
            } else {
                bindMemberAdapter.notifyDataSetChanged();
            }
        });
    }

    private List<SeatBean> seatBeans = new ArrayList<>();

    @Override
    public void updateSeatDataList() {
        if (getActivity() == null) {
            return;
        }
        getActivity().runOnUiThread(() -> {
            seatBeans.clear();
            for (int i = 0; i < presenter.seatData.size(); i++) {
                InterfaceRoom.pbui_Item_MeetRoomDevSeatDetailInfo info = presenter.seatData.get(i);
                SeatBean seatBean = new SeatBean(info.getDevid(), info.getDevname().toStringUtf8(), info.getX(), info.getY(),
                        info.getDirection(), info.getMemberid(), info.getMembername().toStringUtf8(),
                        info.getIssignin(), info.getRole(), info.getFacestate());
                seatBeans.add(seatBean);
            }
            seatView.addSeat(seatBeans);
        });
    }

    @Override
    public void updateRoomBg(String filePath, int mediaId) {
        if (getActivity() == null) {
            return;
        }
        getActivity().runOnUiThread(() -> {
            Drawable drawable = Drawable.createFromPath(filePath);
            seatView.setBackground(drawable);
            Bitmap bitmap = BitmapFactory.decodeFile(filePath);
            if (bitmap != null) {
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
                seatView.setLayoutParams(params);
                seatView.setImgSize(width, height);
                LogUtils.e(TAG, "updateRoomBg 图片宽高 -->" + width + ", " + height);
                presenter.queryPlaceRanking();
                bitmap.recycle();
            }
        });
    }
}
