package com.xlk.takstarpaperlessmanage.view.admin.fragment.e_after.annotate;

import android.view.View;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.mogujie.tt.protobuf.InterfaceFile;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.adapter.AnnotateFileAdapter;
import com.xlk.takstarpaperlessmanage.base.BaseFragment;
import com.xlk.takstarpaperlessmanage.model.Constant;
import com.xlk.takstarpaperlessmanage.ui.RvItemDecoration;
import com.xlk.takstarpaperlessmanage.util.FileUtil;

import java.util.List;
import java.util.function.Consumer;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author Created by xlk on 2021/5/27.
 * @desc
 */
public class AnnotateFragment extends BaseFragment<AnnotatePresenter> implements AnnotateContract.View {

    private RecyclerView rv_content;
    private AnnotateFileAdapter annotateFileAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_annotate;
    }

    @Override
    protected void initView(View inflate) {
        rv_content = inflate.findViewById(R.id.rv_content);
        inflate.findViewById(R.id.btn_delete).setOnClickListener(v -> {
            List<InterfaceFile.pbui_Item_MeetDirFileDetailInfo> checkedFiles = annotateFileAdapter.getCheckedFiles();
            if (checkedFiles.isEmpty()) {
                ToastUtils.showShort(R.string.please_choose_file_first);
                return;
            }
            jni.deleteMeetDirFile(Constant.ANNOTATION_DIR_ID, checkedFiles);
        });
        inflate.findViewById(R.id.btn_download).setOnClickListener(v -> {
            List<InterfaceFile.pbui_Item_MeetDirFileDetailInfo> checkedFiles = annotateFileAdapter.getCheckedFiles();
            if (checkedFiles.isEmpty()) {
                ToastUtils.showShort(R.string.please_choose_file_first);
                return;
            }
            boolean hasDownload = false;
            for (int i = 0; i < checkedFiles.size(); i++) {
                InterfaceFile.pbui_Item_MeetDirFileDetailInfo item = checkedFiles.get(i);
                String filePath = Constant.file_dir + item.getName().toStringUtf8();
                boolean fileExists = FileUtils.isFileExists(filePath);
                if (!fileExists) {
                    jni.downloadFile(filePath, item.getMediaid(), 1, 0, Constant.DOWNLOAD_NORMAL);
                    hasDownload = true;
                }
            }
            if (!hasDownload) {
                ToastUtils.showShort(R.string.file_exists_tip);
            }
        });
    }

    @Override
    protected AnnotatePresenter initPresenter() {
        return new AnnotatePresenter(this);
    }

    @Override
    protected void initial() {
        presenter.queryFile();
    }

    @Override
    public void updateFileList() {
        if (annotateFileAdapter == null) {
            annotateFileAdapter = new AnnotateFileAdapter(presenter.files);
            annotateFileAdapter.addChildClickViewIds(R.id.operation_view_1);
            rv_content.setLayoutManager(new LinearLayoutManager(getContext()));
            rv_content.addItemDecoration(new RvItemDecoration(getContext()));
            rv_content.setAdapter(annotateFileAdapter);
            annotateFileAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                    annotateFileAdapter.check(presenter.files.get(position).getMediaid());
                }
            });
            annotateFileAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
                @Override
                public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                    InterfaceFile.pbui_Item_MeetDirFileDetailInfo item = presenter.files.get(position);
                    String fileName = item.getName().toStringUtf8();
                    String filePath = Constant.file_dir + fileName;
                    boolean fileExists = FileUtils.isFileExists(filePath);
                    if (fileExists) {
                        FileUtil.openFile(getContext(), filePath);
                    } else {
                        jni.downloadFile(filePath, item.getMediaid(), 1, 0, Constant.DOWNLOAD_OPEN_FILE);
                    }
                }
            });
        } else {
            annotateFileAdapter.notifyDataSetChanged();
        }
    }
}
