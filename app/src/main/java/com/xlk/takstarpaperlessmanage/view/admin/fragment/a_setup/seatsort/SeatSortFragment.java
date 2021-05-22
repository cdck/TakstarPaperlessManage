package com.xlk.takstarpaperlessmanage.view.admin.fragment.a_setup.seatsort;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.UriUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.mogujie.tt.protobuf.InterfaceFile;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceRoom;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.adapter.PictureFileAdapter;
import com.xlk.takstarpaperlessmanage.adapter.RoomHorizontalAdapter;
import com.xlk.takstarpaperlessmanage.base.BaseFragment;
import com.xlk.takstarpaperlessmanage.model.Constant;
import com.xlk.takstarpaperlessmanage.model.bean.SeatBean;
import com.xlk.takstarpaperlessmanage.ui.CustomSeatView;
import com.xlk.takstarpaperlessmanage.ui.RvItemDecoration;
import com.xlk.takstarpaperlessmanage.util.PopUtil;
import com.xlk.takstarpaperlessmanage.util.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.xlk.takstarpaperlessmanage.model.Constant.s2b;

/**
 * @author Created by xlk on 2021/5/12.
 * @desc
 */
public class SeatSortFragment extends BaseFragment<SeatSortPresenter> implements SeatSortContract.View, View.OnClickListener {

    private RoomHorizontalAdapter roomAdapter;
    private RecyclerView rvRoom;
    private TextView tvSeatCount;
    private CustomSeatView seatView;
    private CheckBox cbDisplayIcon;
    private Button btnUp;
    private Button btnBottom;
    private Button btnLeft;
    private Button btnRight;
    private Button btnLeftSide;
    private Button btnBottomSide;
    private Button btnPreview;
    private Button btnSave;
    private Button btnRestore;
    private Button btnDefine;
    private int currentMediaId;
    private int currentRoomId;
    private PopupWindow bgPicturePop;
    private PictureFileAdapter fileAdapter;
    private final int PICTURE_REQUEST_CODE = 1;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_seat_sort;
    }

    @Override
    protected void initView(View inflate) {
        rvRoom = (RecyclerView) inflate.findViewById(R.id.rv_room);
        tvSeatCount = (TextView) inflate.findViewById(R.id.tv_seat_count);
        seatView = (CustomSeatView) inflate.findViewById(R.id.seat_view);
        cbDisplayIcon = (CheckBox) inflate.findViewById(R.id.cb_display_icon);
        btnUp = (Button) inflate.findViewById(R.id.btn_up);
        btnBottom = (Button) inflate.findViewById(R.id.btn_bottom);
        btnLeft = (Button) inflate.findViewById(R.id.btn_left);
        btnRight = (Button) inflate.findViewById(R.id.btn_right);
        btnLeftSide = (Button) inflate.findViewById(R.id.btn_left_side);
        btnBottomSide = (Button) inflate.findViewById(R.id.btn_bottom_side);
        btnPreview = (Button) inflate.findViewById(R.id.btn_preview);
        btnSave = (Button) inflate.findViewById(R.id.btn_save);
        btnRestore = (Button) inflate.findViewById(R.id.btn_restore);
        btnDefine = (Button) inflate.findViewById(R.id.btn_define);
        cbDisplayIcon.setOnClickListener(this);
        btnUp.setOnClickListener(this);
        btnBottom.setOnClickListener(this);
        btnLeft.setOnClickListener(this);
        btnRight.setOnClickListener(this);
        btnLeftSide.setOnClickListener(this);
        btnBottomSide.setOnClickListener(this);
        btnPreview.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnRestore.setOnClickListener(this);
        btnDefine.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (currentRoomId == 0) {
            ToastUtil.showShort(R.string.please_choose_room_first);
            return;
        }
        switch (v.getId()) {
            case R.id.cb_display_icon: {
                boolean checked = cbDisplayIcon.isChecked();
                cbDisplayIcon.setChecked(checked);
                presenter.setHideIcon(!checked);
                break;
            }
            case R.id.btn_up: {
                seatView.updateDirection(CustomSeatView.TOP_DIRECTION);
                break;
            }
            case R.id.btn_bottom: {
                seatView.updateDirection(CustomSeatView.DOWN_DIRECTION);
                break;
            }
            case R.id.btn_left: {
                seatView.updateDirection(CustomSeatView.LEFT_DIRECTION);
                break;
            }
            case R.id.btn_right: {
                seatView.updateDirection(CustomSeatView.RIGHT_DIRECTION);
                break;
            }
            case R.id.btn_left_side: {
                seatView.alignLeft();
                break;
            }
            case R.id.btn_bottom_side: {
                seatView.alignBottom();
                break;
            }
            case R.id.btn_preview: {
                presenter.queryBgPicture();
                showRoomBgPicturePop();
                break;
            }
            case R.id.btn_save: {
                jni.setRoomPicture(currentRoomId, currentMediaId, s2b(""), 0);
                break;
            }
            case R.id.btn_restore: {
                showRoom(currentRoomId);
                break;
            }
            case R.id.btn_define: {
                List<SeatBean> seatBean = seatView.getSeatBean();
                List<InterfaceRoom.pbui_Item_MeetRoomDevPosInfo> devs = new ArrayList<>();
                for (int i = 0; i < seatBean.size(); i++) {
                    SeatBean bean = seatBean.get(i);
                    InterfaceRoom.pbui_Item_MeetRoomDevPosInfo build = InterfaceRoom.pbui_Item_MeetRoomDevPosInfo.newBuilder()
                            .setDevid(bean.getDevId())
                            .setX(bean.getX())
                            .setY(bean.getY())
                            .setDirection(bean.getDirection())
                            .build();
                    devs.add(build);
                }
                jni.setPlaceDeviceRankInfo(currentRoomId, devs);
                break;
            }
        }
    }

    private void showRoomBgPicturePop() {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.pop_bg_picture, null, false);
        View ll_content = getActivity().findViewById(R.id.ll_content);
        View rv_navigation = getActivity().findViewById(R.id.rv_navigation);
        int width = ll_content.getWidth();
        int height = ll_content.getHeight();
        int width1 = rv_navigation.getWidth();
        bgPicturePop = PopUtil.createPopupWindow(inflate, width * 2 / 3, height * 2 / 3, rvRoom, Gravity.CENTER, width1 / 2, 0);
        RecyclerView rv_file = inflate.findViewById(R.id.rv_file);
        fileAdapter = new PictureFileAdapter(presenter.pictureData);
        rv_file.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_file.addItemDecoration(new RvItemDecoration(getContext()));
        rv_file.setAdapter(fileAdapter);
        CheckBox cbAll = inflate.findViewById(R.id.cb_all);
        cbAll.setOnClickListener(v -> {
            boolean checked = cbAll.isChecked();
            cbAll.setChecked(checked);
            fileAdapter.setSelectedAll(checked);
        });
        fileAdapter.addChildClickViewIds(R.id.item_view_5);
        fileAdapter.setOnItemClickListener((adapter, view, position) -> {
            fileAdapter.setSelected(presenter.pictureData.get(position).getMediaid());
            cbAll.setChecked(fileAdapter.isSelectedAll());
        });
        fileAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            InterfaceFile.pbui_Item_MeetDirFileDetailInfo info = presenter.pictureData.get(position);
            jni.deleteMeetDirFile(0, info);
        });
        inflate.findViewById(R.id.btn_upload).setOnClickListener(v -> {
            chooseLocalFile(PICTURE_REQUEST_CODE);
        });
        inflate.findViewById(R.id.btn_delete).setOnClickListener(v -> {
            List<InterfaceFile.pbui_Item_MeetDirFileDetailInfo> selectedFiles = fileAdapter.getSelectedFiles();
            if (selectedFiles.isEmpty()) {
                ToastUtil.showShort(R.string.please_choose_file_first);
                return;
            }
            jni.deleteMeetDirFile(0, selectedFiles);
        });
        inflate.findViewById(R.id.iv_close).setOnClickListener(v -> bgPicturePop.dismiss());
        inflate.findViewById(R.id.btn_cancel).setOnClickListener(v -> bgPicturePop.dismiss());
        inflate.findViewById(R.id.btn_define).setOnClickListener(v -> {
            List<InterfaceFile.pbui_Item_MeetDirFileDetailInfo> selectedFiles = fileAdapter.getSelectedFiles();
            if (selectedFiles.isEmpty()) {
                ToastUtil.showShort(R.string.please_choose_file_first);
                return;
            }
            if (selectedFiles.size() > 1) {
                ToastUtil.showShort(R.string.can_only_choose_one);
                return;
            }
            InterfaceFile.pbui_Item_MeetDirFileDetailInfo info = selectedFiles.get(0);
            FileUtils.createOrExistsDir(Constant.config_dir);
            jni.downloadFile(Constant.config_dir + Constant.ROOM_BG_PNG_TAG + ".png",
                    info.getMediaid(), 1, 0, Constant.ROOM_BG_PNG_TAG);
            bgPicturePop.dismiss();
        });
    }

    @Override
    protected SeatSortPresenter initPresenter() {
        return new SeatSortPresenter(this);
    }

    @Override
    protected void initial() {
        seatView.post(() -> {
            seatView.setViewSize(seatView.getWidth(), seatView.getHeight());
            presenter.queryRoom();
        });
    }

    @Override
    public void updatePictureList() {
        if (fileAdapter != null) {
            fileAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void updateMeetRoomList() {
        if (roomAdapter == null) {
            roomAdapter = new RoomHorizontalAdapter(presenter.meetRooms);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            rvRoom.setLayoutManager(layoutManager);
            rvRoom.setAdapter(roomAdapter);
            roomAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                    roomAdapter.setSelectedRoomId(presenter.meetRooms.get(position).getRoomid());
                    showRoom(presenter.meetRooms.get(position).getRoomid());
                }
            });
            if (!presenter.meetRooms.isEmpty()) {
                roomAdapter.setSelectedRoomId(presenter.meetRooms.get(0).getRoomid());
                showRoom(presenter.meetRooms.get(0).getRoomid());
            }
        } else {
            roomAdapter.notifyDataSetChanged();
        }
    }

    private void showRoom(int roomid) {
        currentRoomId = roomid;
        presenter.queryRoomIcon();
        presenter.queryRoomBg(roomid);
    }

    @Override
    public void updateShowIcon(boolean hidePic) {
        getActivity().runOnUiThread(() -> {
            cbDisplayIcon.setChecked(!hidePic);
            seatView.setHidePic(hidePic);
        });
    }

    @Override
    public void updateRoomBg(String filepath, int mediaId) {
        if (getActivity() == null) {
            return;
        }
        getActivity().runOnUiThread(() -> {
            currentMediaId = mediaId;
            Drawable drawable = Drawable.createFromPath(filepath);
            seatView.setBackground(drawable);
            Bitmap bitmap = BitmapFactory.decodeFile(filepath);
            if (bitmap != null) {
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
                seatView.setLayoutParams(params);
                seatView.setImgSize(width, height);
                LogUtils.e(TAG, "updateRoomBg 图片宽高 -->" + width + ", " + height);
                presenter.placeDeviceRankingInfo(roomAdapter.getSelectedRoomId());
                bitmap.recycle();
            }
        });
    }

    @Override
    public void cleanRoomBg() {
        getActivity().runOnUiThread(() -> {
            LogUtils.i(TAG, "清除会议室底图");
            seatView.setBackgroundColor(Color.WHITE);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(seatView.getViewWidth(), seatView.getViewHeight());
            seatView.setLayoutParams(params);
            seatView.setImgSize(seatView.getViewWidth(), seatView.getViewHeight());
            presenter.placeDeviceRankingInfo(roomAdapter.getSelectedRoomId());
        });
    }

    private List<SeatBean> seatBeans = new ArrayList<>();

    @Override
    public void updateSeatData(List<InterfaceRoom.pbui_Item_MeetRoomDevSeatDetailInfo> seatData) {
        getActivity().runOnUiThread(() -> {
            LogUtils.i(TAG, "updateSeatData ");
            seatBeans.clear();
            for (int i = 0; i < seatData.size(); i++) {
                InterfaceRoom.pbui_Item_MeetRoomDevSeatDetailInfo info = seatData.get(i);
                SeatBean seatBean = new SeatBean(info.getDevid(), info.getDevname().toStringUtf8(), info.getX(), info.getY(),
                        info.getDirection(), info.getMemberid(), info.getMembername().toStringUtf8(),
                        info.getIssignin(), info.getRole(), info.getFacestate());
                seatBeans.add(seatBean);
            }
            tvSeatCount.setText(getString(R.string.seat_count_, seatBeans.size()));
            seatView.addSeat(seatBeans);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == PICTURE_REQUEST_CODE) {
            Uri uri = data.getData();
            File file = UriUtils.uri2File(uri);
            if (file != null) {
                if (file.getName().endsWith(".png")) {
                    String absolutePath = file.getAbsolutePath();
                    LogUtils.i(TAG, "onActivityResult 上传新底图图片=" + absolutePath);
                    jni.uploadFile(0, 0, InterfaceMacro.Pb_MeetFileAttrib.Pb_MEETFILE_ATTRIB_BACKGROUND_VALUE,
                            file.getName(), absolutePath, 0, Constant.UPLOAD_BACKGROUND_IMAGE);
                } else {
                    ToastUtil.showShort(R.string.please_choose_png_file);
                }
            }
        }
    }
}
