package com.xlk.takstarpaperlessmanage.base;

import android.content.Intent;
import android.os.Bundle;
import android.widget.PopupWindow;

import com.xlk.takstarpaperlessmanage.model.JniHelper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
    }

    protected abstract int getLayoutId();

    protected abstract T initPresenter();

    public void initView() {

    }

    protected abstract void init(Bundle savedInstanceState);

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
    }
}
