package com.xlk.takstarpaperlessmanage.base;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupWindow;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.google.protobuf.InvalidProtocolBufferException;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.adapter.LocalFileAdapter;
import com.xlk.takstarpaperlessmanage.model.EventMessage;
import com.xlk.takstarpaperlessmanage.model.EventType;
import com.xlk.takstarpaperlessmanage.model.JniHelper;
import com.xlk.takstarpaperlessmanage.ui.RvItemDecoration;
import com.xlk.takstarpaperlessmanage.util.LogUtil;
import com.xlk.takstarpaperlessmanage.util.PopUtil;
import com.xlk.takstarpaperlessmanage.util.ToastUtil;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by xlk on 2021/4/21.
 * @desc
 */
public abstract class BaseActivity<T extends BasePresenter> extends AppCompatActivity implements BaseContract.View {
    protected String TAG = this.getClass().getSimpleName();
    protected JniHelper jni = JniHelper.getInstance();
    protected T presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        initView();
        presenter = initPresenter();
        init(savedInstanceState);
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
    }

    protected abstract int getLayoutId();

    protected abstract T initPresenter();

    public void initView() {

    }

    protected abstract void init(Bundle savedInstanceState);


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMessage(EventMessage msg) throws InvalidProtocolBufferException {
        switch (msg.getType()) {
            case EventType.CHOOSE_DIR_PATH: {
                Object[] objects = msg.getObjects();
                int dirType = (int) objects[0];
                String dirPath = (String) objects[1];
                showChooseDirPop(dirType, dirPath);
                break;
            }
            default:
                break;
        }
    }

    private List<File> currentFiles = new ArrayList<>();
    private LocalFileAdapter localFileAdapter;

    private FileFilter dirFilter = new FileFilter() {
        @Override
        public boolean accept(File pathname) {
            return pathname.isDirectory() && !pathname.getName().startsWith(".");
        }
    };

    private void showChooseDirPop(int dirType, String rootDir) {
        currentFiles.clear();
        currentFiles.addAll(FileUtils.listFilesInDirWithFilter(rootDir, dirFilter));
        View inflate = LayoutInflater.from(this).inflate(R.layout.pop_local_file, null);
        PopupWindow dirPop = PopUtil.createPopupWindow(inflate, ScreenUtils.getScreenWidth() / 2, ScreenUtils.getScreenHeight() /2, getWindow().getDecorView());
        EditText edt_current_dir = inflate.findViewById(R.id.edt_current_dir);
        edt_current_dir.setKeyListener(null);
        edt_current_dir.setText(rootDir);
        RecyclerView rv_current_file = inflate.findViewById(R.id.rv_current_file);
        localFileAdapter = new LocalFileAdapter(currentFiles);
        rv_current_file.setLayoutManager(new LinearLayoutManager(this));
        rv_current_file.addItemDecoration(new RvItemDecoration(this));
        rv_current_file.setAdapter(localFileAdapter);
        localFileAdapter.setOnItemClickListener((adapter, view, position) -> {
            File file = currentFiles.get(position);
            edt_current_dir.setText(file.getAbsolutePath());
            edt_current_dir.setSelection(edt_current_dir.getText().toString().length());
            List<File> files = FileUtils.listFilesInDirWithFilter(file, dirFilter);
            currentFiles.clear();
            currentFiles.addAll(files);
            localFileAdapter.notifyDataSetChanged();
        });
        inflate.findViewById(R.id.iv_back).setOnClickListener(v -> {
            String dirPath = edt_current_dir.getText().toString().trim();
            if (dirPath.equals(Environment.getExternalStorageDirectory().getAbsolutePath())) {
                ToastUtil.showShort(R.string.current_dir_root);
                return;
            }
            File file = new File(dirPath);
            File parentFile = file.getParentFile();
            edt_current_dir.setText(parentFile.getAbsolutePath());
            LogUtil.i(TAG, "showChooseDir 上一级的目录=" + parentFile.getAbsolutePath());
            List<File> files = FileUtils.listFilesInDirWithFilter(parentFile, dirFilter);
            currentFiles.clear();
            currentFiles.addAll(files);
            localFileAdapter.notifyDataSetChanged();
        });
        inflate.findViewById(R.id.btn_ensure).setOnClickListener(v -> {
            String dirPath = edt_current_dir.getText().toString();
            EventBus.getDefault().post(new EventMessage.Builder().type(EventType.RESULT_DIR_PATH).objects(dirType, dirPath).build());
            dirPop.dismiss();
        });
        inflate.findViewById(R.id.btn_cancel).setOnClickListener(v -> {
            dirPop.dismiss();
        });
    }

    protected void dismissPop(PopupWindow pop) {
        if (pop != null && pop.isShowing()) {
            pop.dismiss();
        }
    }

    /**
     * 打开选择本地文件
     *
     * @param requestCode 返回码
     */
    protected void chooseLocalFile(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
        if(EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
    }
}
