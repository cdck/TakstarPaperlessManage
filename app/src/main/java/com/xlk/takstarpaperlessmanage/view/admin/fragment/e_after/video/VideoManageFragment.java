package com.xlk.takstarpaperlessmanage.view.admin.fragment.e_after.video;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.mogujie.tt.protobuf.InterfaceFile;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.adapter.VideoFileAdapter;
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
 * @author Created by xlk on 2021/5/29.
 * @desc
 */
public class VideoManageFragment extends BaseFragment<VideoManagePresenter> implements VideoManageContract.View {

    private RecyclerView rv_content;
    private VideoFileAdapter videoFileAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_video_manage;
    }

    @Override
    protected void initView(View inflate) {
        rv_content = inflate.findViewById(R.id.rv_content);
        inflate.findViewById(R.id.btn_modify).setOnClickListener(v -> {
            InterfaceFile.pbui_Item_MeetDirFileDetailInfo videoFile = videoFileAdapter.getCheckedVideoFile();
            if (videoFile != null) {
                showModifyPop(videoFile);
            } else {
                ToastUtils.showShort(R.string.please_choose_file_first);
            }
        });
    }

    private void showModifyPop(InterfaceFile.pbui_Item_MeetDirFileDetailInfo videoFile) {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.pop_modify_file, null, false);
        View ll_content = getActivity().findViewById(R.id.ll_content);
        View rv_navigation = getActivity().findViewById(R.id.rv_navigation);
        int width = ll_content.getWidth();
        int height = ll_content.getHeight();
        int width1 = rv_navigation.getWidth();
        PopupWindow pop = PopUtil.createPopupWindow(inflate, width / 2, height / 2, rv_content, Gravity.CENTER, width1 / 2, 0);
        TextView tv_title = inflate.findViewById(R.id.tv_title);
        EditText edt_name = inflate.findViewById(R.id.edt_name);

        tv_title.setText(getString(R.string.modify_file_name));
        edt_name.setText(videoFile.getName().toStringUtf8());
        inflate.findViewById(R.id.iv_close).setOnClickListener(v -> pop.dismiss());
        inflate.findViewById(R.id.btn_cancel).setOnClickListener(v -> pop.dismiss());
        inflate.findViewById(R.id.btn_define).setOnClickListener(v -> {
            String newName = edt_name.getText().toString();
            if (newName.isEmpty()) {
                ToastUtil.showShort(R.string.please_enter_name_first);
                return;
            }
            String fileName = videoFile.getName().toStringUtf8();
            String suffix = fileName.substring(fileName.lastIndexOf("."));
            if (!newName.endsWith(suffix)) {
                ToastUtil.showShort(getString(R.string.file_name_hint, suffix));
                return;
            }
            InterfaceFile.pbui_Item_MeetDirFileDetailInfo build = InterfaceFile.pbui_Item_MeetDirFileDetailInfo.newBuilder()
                    .setName(s2b(newName))
                    .setUploaderRole(videoFile.getUploaderRole())
                    .setUploaderName(videoFile.getUploaderName())
                    .setUploaderid(videoFile.getUploaderid())
                    .setSize(videoFile.getSize())
                    .setMstime(videoFile.getMstime())
                    .setMediaid(videoFile.getMediaid())
                    .setFilepos(videoFile.getFilepos())
                    .setAttrib(videoFile.getAttrib())
                    .build();
            jni.modifyMeetDirFile(0, build);
            pop.dismiss();
        });
    }

    @Override
    protected VideoManagePresenter initPresenter() {
        return new VideoManagePresenter(this);
    }

    @Override
    protected void initial() {
        presenter.queryVideoFile();
    }

    @Override
    public void updateVideoFileList() {
        if (videoFileAdapter == null) {
            videoFileAdapter = new VideoFileAdapter(presenter.videoFiles);
            videoFileAdapter.addChildClickViewIds(R.id.operation_view_1, R.id.operation_view_2, R.id.operation_view_3);
            rv_content.setLayoutManager(new LinearLayoutManager(getContext()));
            rv_content.addItemDecoration(new RvItemDecoration(getContext()));
            rv_content.setAdapter(videoFileAdapter);
            videoFileAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                    videoFileAdapter.check(presenter.videoFiles.get(position).getMediaid());
                }
            });
            videoFileAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
                @Override
                public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                    InterfaceFile.pbui_Item_MeetDirFileDetailInfo item = presenter.videoFiles.get(position);
                    int mediaid = item.getMediaid();
                    String fileName = item.getName().toStringUtf8();
                    String filePath = Constant.record_video_dir + fileName;
                    switch (view.getId()) {
                        //下载
                        case R.id.operation_view_1: {
                            boolean fileExists = FileUtils.isFileExists(filePath);
                            if (fileExists) {
                                ToastUtils.showLong(getString(R.string.file_already_download));
                            } else {
                                jni.downloadFile(filePath, mediaid, 1, 0, Constant.DOWNLOAD_RECORD_VIDEO);
                            }
                            break;
                        }
                        //共享
                        case R.id.operation_view_2: {

                            break;
                        }
                        //删除
                        case R.id.operation_view_3: {
                            showDeletePop(item);
                            break;
                        }
                    }
                }
            });
        } else {
            videoFileAdapter.notifyDataSetChanged();
        }
    }

    private void showDeletePop(InterfaceFile.pbui_Item_MeetDirFileDetailInfo item) {
        /** 删除PopupWindow **/
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.pop_delete, null, false);
        View ll_content = getActivity().findViewById(R.id.ll_content);
        View rv_navigation = getActivity().findViewById(R.id.rv_navigation);
        int width = ll_content.getWidth();
        int height = ll_content.getHeight();
        int width1 = rv_navigation.getWidth();
        PopupWindow pop = PopUtil.createPopupWindow(inflate, width / 2, height / 2, rv_content, Gravity.CENTER, width1 / 2, 0);
        TextView tv_title = inflate.findViewById(R.id.tv_title);
        TextView tv_hint = inflate.findViewById(R.id.tv_hint);
        tv_hint.setText(R.string.delete_file_hint);
        ImageView iv_icon = inflate.findViewById(R.id.iv_icon);
        inflate.findViewById(R.id.iv_close).setOnClickListener(v -> pop.dismiss());
        inflate.findViewById(R.id.btn_cancel).setOnClickListener(v -> pop.dismiss());
        inflate.findViewById(R.id.btn_define).setOnClickListener(v -> {
            jni.deleteMeetDirFile(0, item);
            pop.dismiss();
        });
    }
}
