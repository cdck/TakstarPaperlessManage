package com.xlk.takstarpaperlessmanage.view.admin.fragment.a_setup.secretary;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.EncryptUtils;
import com.blankj.utilcode.util.LogUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.mogujie.tt.protobuf.InterfaceAdmin;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.adapter.AdminAdapter;
import com.xlk.takstarpaperlessmanage.adapter.SecretaryVenueAdapter;
import com.xlk.takstarpaperlessmanage.base.BaseFragment;
import com.xlk.takstarpaperlessmanage.model.Constant;
import com.xlk.takstarpaperlessmanage.ui.RvItemDecoration;
import com.xlk.takstarpaperlessmanage.util.PopUtil;
import com.xlk.takstarpaperlessmanage.util.ToastUtil;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.xlk.takstarpaperlessmanage.model.Constant.s2b;

/**
 * @author Created by xlk on 2021/5/13.
 * @desc 秘书管理
 */
public class SecretaryManageFragment extends BaseFragment<SecretaryManagePresenter> implements SecretaryManageContract.View {

    private RecyclerView rv_user;
    private AdminAdapter adminAdapter;
    private PopupWindow modifyPop, deletePop, roomManagePop;
    private SecretaryVenueAdapter controlledAdapter, optionalAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_secretary_manage;
    }

    @Override
    protected void initView(View inflate) {
        rv_user = inflate.findViewById(R.id.rv_user);
        inflate.findViewById(R.id.btn_add).setOnClickListener(v -> {
            showAddorModifyPop(null);
        });
    }

    @Override
    protected SecretaryManagePresenter initPresenter() {
        return new SecretaryManagePresenter(this);
    }

    @Override
    protected void initial() {
        presenter.queryAdmin();
    }

    @Override
    public void updateControlledRoomList() {
        if (controlledAdapter == null) {
            controlledAdapter = new SecretaryVenueAdapter(presenter.controlledRooms);
            controlledAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                    controlledAdapter.setSelected(presenter.controlledRooms.get(position).getRoomid());
                }
            });
        } else {
            controlledAdapter.notifySelected();
        }
    }

    @Override
    public void updateOptionalRoomList() {
        if (optionalAdapter == null) {
            optionalAdapter = new SecretaryVenueAdapter(presenter.optionalRooms);
            optionalAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                    optionalAdapter.setSelected(presenter.optionalRooms.get(position).getRoomid());
                }
            });
        } else {
            optionalAdapter.notifySelected();
        }
    }

    @Override
    public void updateAdminList() {
        if (adminAdapter == null) {
            adminAdapter = new AdminAdapter(presenter.adminInfos);
            adminAdapter.addChildClickViewIds(R.id.tv_permission_management, R.id.tv_modify, R.id.tv_delete);
            rv_user.setLayoutManager(new LinearLayoutManager(getContext()));
            rv_user.addItemDecoration(new RvItemDecoration(getContext()));
            rv_user.setAdapter(adminAdapter);
            adminAdapter.setOnItemChildClickListener((adapter, view, position) -> {
                InterfaceAdmin.pbui_Item_AdminDetailInfo item = presenter.adminInfos.get(position);
                switch (view.getId()) {
                    case R.id.tv_permission_management: {
                        presenter.setCurrentAdminId(item.getAdminid());
                        presenter.queryRoomsByAdminId(item.getAdminid());
                        showRoomManagePop();
                        break;
                    }
                    case R.id.tv_modify: {
                        showAddorModifyPop(item);
                        break;
                    }
                    case R.id.tv_delete: {
                        showDeletePop(item);
                        break;
                    }
                }
            });
        } else {
            adminAdapter.notifyDataSetChanged();
        }
    }

    private void showRoomManagePop() {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.pop_secretary_room_manage, null, false);
        roomManagePop = PopUtil.createCoverPopupWindow(inflate, rv_user, popWidth, popHeight, popX, popY);
        //可控会场
        RecyclerView rv_controlled = inflate.findViewById(R.id.rv_controlled);
        rv_controlled.setAdapter(controlledAdapter);
        rv_controlled.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_controlled.addItemDecoration(new RvItemDecoration(getContext()));
        //可选会场
        RecyclerView rv_optional = inflate.findViewById(R.id.rv_optional);
        rv_optional.setAdapter(optionalAdapter);
        rv_optional.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_optional.addItemDecoration(new RvItemDecoration(getContext()));
        //添加
        inflate.findViewById(R.id.btn_add).setOnClickListener(v -> {
            List<Integer> roomIds = optionalAdapter.getSelectedIds();
            if (roomIds.isEmpty()) {
                ToastUtil.showShort(R.string.please_select_venue_first);
                return;
            }
            presenter.addRoom2Admin(roomIds);
        });
        //移除
        inflate.findViewById(R.id.btn_remove).setOnClickListener(v -> {
            List<Integer> roomIds = controlledAdapter.getSelectedIds();
            if (roomIds.isEmpty()) {
                ToastUtil.showShort(R.string.please_select_venue_first);
                return;
            }
            presenter.removeRoomFromAdmin(roomIds);
        });
        inflate.findViewById(R.id.iv_close).setOnClickListener(v -> roomManagePop.dismiss());
        inflate.findViewById(R.id.btn_cancel).setOnClickListener(v -> roomManagePop.dismiss());
        inflate.findViewById(R.id.btn_define).setOnClickListener(v -> {
            presenter.defineModify();
        });
        roomManagePop.setOnDismissListener(() -> {
            presenter.setCurrentAdminId(0);
        });
    }

    private void showDeletePop(InterfaceAdmin.pbui_Item_AdminDetailInfo item) {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.pop_delete, null, false);
        View ll_content = getActivity().findViewById(R.id.ll_content);
        View rv_navigation = getActivity().findViewById(R.id.rv_navigation);
        int width = ll_content.getWidth();
        int height = ll_content.getHeight();
        int width1 = rv_navigation.getWidth();
        deletePop = PopUtil.createPopupWindow(inflate, width / 2, height / 2, rv_user, Gravity.CENTER, width1 / 2, 0);
        TextView tv_hint = inflate.findViewById(R.id.tv_hint);
        tv_hint.setText(getString(R.string.delete_user_hint));
        inflate.findViewById(R.id.iv_close).setOnClickListener(v -> deletePop.dismiss());
        inflate.findViewById(R.id.btn_cancel).setOnClickListener(v -> deletePop.dismiss());
        inflate.findViewById(R.id.btn_define).setOnClickListener(v -> {
            jni.delAdmin(item);
            deletePop.dismiss();
        });
    }

    private void showAddorModifyPop(InterfaceAdmin.pbui_Item_AdminDetailInfo item) {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.pop_modify_user, null, false);
        View ll_content = getActivity().findViewById(R.id.ll_content);
        View rv_navigation = getActivity().findViewById(R.id.rv_navigation);
        int width = ll_content.getWidth();
        int height = ll_content.getHeight();
        int width1 = rv_navigation.getWidth();
        modifyPop = PopUtil.createPopupWindow(inflate, width / 2, height / 2, rv_user, Gravity.CENTER, width1 / 2, 0);
        TextView tv_title = inflate.findViewById(R.id.tv_title);
        boolean isAdd = item == null;
        tv_title.setText(isAdd ? getString(R.string.add) : getString(R.string.modify));
        EditText edt_name = inflate.findViewById(R.id.edt_name);
        EditText edt_password = inflate.findViewById(R.id.edt_password);
        EditText edt_phone = inflate.findViewById(R.id.edt_phone);
        EditText edt_email = inflate.findViewById(R.id.edt_email);
        EditText edt_remark = inflate.findViewById(R.id.edt_remark);
        if (!isAdd) {
            edt_name.setText(item.getAdminname().toStringUtf8());
            edt_password.setText(item.getPw().toStringUtf8());
            edt_phone.setText(item.getPhone().toStringUtf8());
            edt_email.setText(item.getEmail().toStringUtf8());
            edt_remark.setText(item.getComment().toStringUtf8());
        }
        inflate.findViewById(R.id.iv_close).setOnClickListener(v -> modifyPop.dismiss());
        inflate.findViewById(R.id.btn_cancel).setOnClickListener(v -> modifyPop.dismiss());
        inflate.findViewById(R.id.btn_define).setOnClickListener(v -> {
            String name = edt_name.getText().toString().trim();
            String password = edt_password.getText().toString().trim();
            String phone = edt_phone.getText().toString().trim();
            String email = edt_email.getText().toString().trim();
            String remark = edt_remark.getText().toString().trim();
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(password)) {
                ToastUtil.showShort(R.string.user_name_and_pwd_can_not_empty);
                return;
            }
//            password = EncryptUtils.encryptMD5ToString(password);
//            password = Constant.parseAscii(Constant.s2md5(password));
            password = Constant.s2md5(password);
            InterfaceAdmin.pbui_Item_AdminDetailInfo.Builder builder = InterfaceAdmin.pbui_Item_AdminDetailInfo.newBuilder();
            builder.setAdminname(s2b(name));
            builder.setPw(s2b(password));
            builder.setPhone(s2b(phone));
            builder.setEmail(s2b(email));
            builder.setComment(s2b(remark));
            if (isAdd) {
                jni.addAdmin(builder.build());
            } else {
                builder.setAdminid(item.getAdminid());
                jni.modifyAdmin(builder.build());
            }
            modifyPop.dismiss();
        });
    }
}
