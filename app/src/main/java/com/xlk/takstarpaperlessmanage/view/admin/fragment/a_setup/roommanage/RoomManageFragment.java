package com.xlk.takstarpaperlessmanage.view.admin.fragment.a_setup.roommanage;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.LogUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.mogujie.tt.protobuf.InterfaceRoom;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.adapter.ClientAdapter;
import com.xlk.takstarpaperlessmanage.adapter.RoomAdapter;
import com.xlk.takstarpaperlessmanage.base.BaseFragment;
import com.xlk.takstarpaperlessmanage.ui.RvItemDecoration;
import com.xlk.takstarpaperlessmanage.util.PopUtil;
import com.xlk.takstarpaperlessmanage.util.ToastUtil;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author Created by xlk on 2021/5/12.
 * @desc
 */
public class RoomManageFragment extends BaseFragment<RoomManagePresenter> implements RoomManageContract.View {
    private RecyclerView rv_room;
    private RoomAdapter roomAdapter;
    private PopupWindow modifyPop, deletePop, deviceManagePop;
    private ClientAdapter roomDevAdapter, otherDevAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_room_manage;
    }

    @Override
    protected void initView(View inflate) {
        rv_room = inflate.findViewById(R.id.rv_room);
        inflate.findViewById(R.id.btn_add).setOnClickListener(v -> {
            showAddorModifyPop(null);
        });
    }

    @Override
    protected RoomManagePresenter initPresenter() {
        return new RoomManagePresenter(getContext(), this);
    }

    @Override
    protected void initial() {
        presenter.queryRoom();
        updateRoomDeviceRv();
        updateOtherDeviceRv();
    }

    @Override
    public void updateMeetRoomList() {
        if (roomAdapter == null) {
            roomAdapter = new RoomAdapter(presenter.meetRooms);
            roomAdapter.addChildClickViewIds(R.id.tv_device_manage, R.id.tv_modify, R.id.tv_delete);
            rv_room.setLayoutManager(new LinearLayoutManager(getContext()));
            rv_room.addItemDecoration(new RvItemDecoration(getContext()));
            rv_room.setAdapter(roomAdapter);
            roomAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
                @Override
                public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                    InterfaceRoom.pbui_Item_MeetRoomDetailInfo item = presenter.meetRooms.get(position);
                    switch (view.getId()) {
                        case R.id.tv_device_manage: {
                            presenter.setCurrentRoomId(item.getRoomid());
                            presenter.queryDevice(item.getRoomid());
                            showDeviceManagePop();
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
                }
            });
        } else {
            roomAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void updateRoomDeviceRv() {
        if (roomDevAdapter == null) {
            roomDevAdapter = new ClientAdapter(presenter.roomDevices, true);
            roomDevAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                    int devcieid = presenter.roomDevices.get(position).getDevcieid();
                    roomDevAdapter.setSelected(devcieid);
                }
            });
        } else {
            roomDevAdapter.notifySelected();
        }
    }

    @Override
    public void updateOtherDeviceRv() {
        if (otherDevAdapter == null) {
            otherDevAdapter = new ClientAdapter(presenter.otherDevices, true);
            otherDevAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                    int devcieid = presenter.otherDevices.get(position).getDevcieid();
                    otherDevAdapter.setSelected(devcieid);
                }
            });
        } else {
            otherDevAdapter.notifySelected();
        }
    }

    private void showDeviceManagePop() {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.pop_room_device_manage, null, false);
        int dp_10 = ConvertUtils.dp2px(10);
        int dp_20 = ConvertUtils.dp2px(20);
        View top_view = getActivity().findViewById(R.id.top_view);
        View rv_navigation = getActivity().findViewById(R.id.rv_navigation);
        View ll_navigation = getActivity().findViewById(R.id.ll_navigation);
        int top_viewH = top_view.getHeight();
        int rv_navigationW = rv_navigation.getWidth();
        int ll_navigationH = ll_navigation.getHeight();
        int x = rv_navigationW + dp_10 + dp_10 + dp_10 + dp_10;
        int y = top_viewH + ll_navigationH + dp_10 + dp_10 + dp_20 + dp_10;
        View fl_admin = getActivity().findViewById(R.id.fl_admin);
        int width = fl_admin.getWidth();
        int height = fl_admin.getHeight();
        LogUtils.i("showParameterConfigurationPop width=" + width + ",height=" + height + ",dp10=" + dp_10);
        deviceManagePop = PopUtil.createPopupWindowAt(inflate, width - dp_20 - dp_20 - dp_10, height - dp_20 - dp_20,
                false, rv_room, Gravity.TOP | Gravity.START, x, y);
        RecyclerView rv_room_device = inflate.findViewById(R.id.rv_room_device);
        rv_room_device.setAdapter(roomDevAdapter);
        rv_room_device.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_room_device.addItemDecoration(new RvItemDecoration(getContext()));
        RecyclerView rv_other_device = inflate.findViewById(R.id.rv_other_device);
        rv_other_device.setAdapter(otherDevAdapter);
        rv_other_device.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_other_device.addItemDecoration(new RvItemDecoration(getContext()));
        //添加
        inflate.findViewById(R.id.btn_add).setOnClickListener(v -> {
            List<Integer> deviceIds = otherDevAdapter.getSelectedIds();
            if (deviceIds.isEmpty()) {
                ToastUtil.showShort(R.string.please_select_device_first);
                return;
            }
            presenter.addDevice2Room(deviceIds);
        });
        //移除
        inflate.findViewById(R.id.btn_remove).setOnClickListener(v -> {
            List<Integer> deviceIds = roomDevAdapter.getSelectedIds();
            if (deviceIds.isEmpty()) {
                ToastUtil.showShort(R.string.please_select_device_first);
                return;
            }
            presenter.removeDeviceFromRoom(deviceIds);
        });
        inflate.findViewById(R.id.iv_close).setOnClickListener(v -> deviceManagePop.dismiss());
        inflate.findViewById(R.id.btn_cancel).setOnClickListener(v -> deviceManagePop.dismiss());
        inflate.findViewById(R.id.btn_define).setOnClickListener(v -> {
            presenter.defineModify();
        });
        deviceManagePop.setOnDismissListener(() -> {
            presenter.setCurrentRoomId(0);
        });
    }

    private void showDeletePop(InterfaceRoom.pbui_Item_MeetRoomDetailInfo item) {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.pop_delete, null, false);
        View ll_content = getActivity().findViewById(R.id.ll_content);
        View rv_navigation = getActivity().findViewById(R.id.rv_navigation);
        int width = ll_content.getWidth();
        int height = ll_content.getHeight();
        int width1 = rv_navigation.getWidth();
        int height1 = rv_navigation.getHeight();
        deletePop = PopUtil.createPopupWindow(inflate, width / 2, height / 2, rv_room, Gravity.CENTER, width1 / 2, 0);
        TextView tv_hint = inflate.findViewById(R.id.tv_hint);
        tv_hint.setText(getString(R.string.delete_meeting_room_hint));
        inflate.findViewById(R.id.iv_close).setOnClickListener(v -> deletePop.dismiss());
        inflate.findViewById(R.id.btn_cancel).setOnClickListener(v -> deletePop.dismiss());
        inflate.findViewById(R.id.btn_define).setOnClickListener(v -> {
            jni.delMeetingRoom(item.getRoomid());
            deletePop.dismiss();
        });
    }

    /**
     * 展示添加或修改会议室弹框
     *
     * @param item =null 为添加
     */
    private void showAddorModifyPop(InterfaceRoom.pbui_Item_MeetRoomDetailInfo item) {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.pop_modify_room, null, false);
        View ll_content = getActivity().findViewById(R.id.ll_content);
        View rv_navigation = getActivity().findViewById(R.id.rv_navigation);
        int width = ll_content.getWidth();
        int height = ll_content.getHeight();
        int width1 = rv_navigation.getWidth();
        int height1 = rv_navigation.getHeight();
        modifyPop = PopUtil.createPopupWindow(inflate, width / 2, height / 2, rv_room, Gravity.CENTER, width1 / 2, 0);
        TextView tv_title = inflate.findViewById(R.id.tv_title);
        boolean isAdd = item == null;
        tv_title.setText(isAdd ? getString(R.string.add) : getString(R.string.modify));
        EditText edt_name = inflate.findViewById(R.id.edt_name);
        EditText edt_location = inflate.findViewById(R.id.edt_location);
        EditText edt_remark = inflate.findViewById(R.id.edt_remark);
        if (!isAdd) {
            edt_name.setText(item.getName().toStringUtf8());
            edt_location.setText(item.getAddr().toStringUtf8());
            edt_remark.setText(item.getComment().toStringUtf8());
        }
        inflate.findViewById(R.id.iv_close).setOnClickListener(v -> modifyPop.dismiss());
        inflate.findViewById(R.id.btn_cancel).setOnClickListener(v -> modifyPop.dismiss());
        inflate.findViewById(R.id.btn_define).setOnClickListener(v -> {
            String name = edt_name.getText().toString().trim();
            String location = edt_location.getText().toString().trim();
            String remark = edt_remark.getText().toString().trim();
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(location)) {
                ToastUtil.showShort(R.string.please_enter_the_content_to_be_modified);
                return;
            }
            if (isAdd) {
                jni.addMeetingRoom(name, location, remark);
            } else {
                jni.modifyMeetingRoom(item.getRoomid(), name, location, remark);
            }
            modifyPop.dismiss();
        });
    }
}
