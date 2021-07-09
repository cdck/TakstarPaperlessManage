package com.xlk.takstarpaperlessmanage.view.admin.fragment.e_after.sign;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;
import com.mogujie.tt.protobuf.InterfaceMeet;
import com.mogujie.tt.protobuf.InterfaceMember;
import com.mogujie.tt.protobuf.InterfaceRoom;
import com.mogujie.tt.protobuf.InterfaceSignin;
import com.xlk.takstarpaperlessmanage.App;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.adapter.SignInAdapter;
import com.xlk.takstarpaperlessmanage.base.BaseFragment;
import com.xlk.takstarpaperlessmanage.model.Constant;
import com.xlk.takstarpaperlessmanage.model.EventMessage;
import com.xlk.takstarpaperlessmanage.model.EventType;
import com.xlk.takstarpaperlessmanage.model.GlobalValue;
import com.xlk.takstarpaperlessmanage.model.bean.PdfSignBean;
import com.xlk.takstarpaperlessmanage.model.bean.SignInBean;
import com.xlk.takstarpaperlessmanage.ui.RvItemDecoration;
import com.xlk.takstarpaperlessmanage.util.DateUtil;
import com.xlk.takstarpaperlessmanage.util.JxlUtil;
import com.xlk.takstarpaperlessmanage.util.PdfUtil;
import com.xlk.takstarpaperlessmanage.util.PopUtil;
import com.xlk.takstarpaperlessmanage.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author Created by xlk on 2021/5/27.
 * @desc
 */
public class SignFragment extends BaseFragment<SignPresenter> implements SignContract.View {
    private LinearLayout rootView;
    private RecyclerView rvContent;
    private TextView tvDue;
    private TextView tvAlready;
    private TextView tvUnchecked;
    private SignInAdapter signInAdapter;
    private EditText edt_save_address;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_sign;
    }

    @Override
    protected void initView(View inflate) {
        rootView = (LinearLayout) inflate.findViewById(R.id.root_view);
        rvContent = (RecyclerView) inflate.findViewById(R.id.rv_content);
        tvDue = (TextView) inflate.findViewById(R.id.tv_due);
        tvAlready = (TextView) inflate.findViewById(R.id.tv_already);
        tvUnchecked = (TextView) inflate.findViewById(R.id.tv_unchecked);
        inflate.findViewById(R.id.btn_delete).setOnClickListener(v -> {
            List<Integer> checkIds = signInAdapter.getCheckIds();
            if (checkIds.isEmpty()) {
                ToastUtil.showShort(R.string.please_choose_member_first);
                return;
            }
            presenter.deleteSignIn(checkIds);
        });
        inflate.findViewById(R.id.btn_export).setOnClickListener(v -> {
            showExportFilePop();
        });
    }

    private void showExportFilePop() {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.pop_export_config, null);
        PopupWindow pop = PopUtil.createHalfPop(inflate, rvContent);
        EditText edt_file_name = inflate.findViewById(R.id.edt_file_name);
        edt_save_address = inflate.findViewById(R.id.edt_save_address);
        edt_save_address.setKeyListener(null);
        TextView tv_suffix = inflate.findViewById(R.id.tv_suffix);
        tv_suffix.setText(".pdf");
        inflate.findViewById(R.id.btn_choose_dir).setOnClickListener(v -> {
            String currentDirPath = edt_save_address.getText().toString().trim();
            if (currentDirPath.isEmpty()) {
                currentDirPath = Constant.root_dir;
            }
            EventBus.getDefault().post(new EventMessage.Builder().type(EventType.CHOOSE_DIR_PATH).objects(Constant.CHOOSE_DIR_TYPE_EXPORT_SIGN_IN_PDF, currentDirPath).build());
        });
        inflate.findViewById(R.id.iv_close).setOnClickListener(v -> pop.dismiss());
        inflate.findViewById(R.id.btn_cancel).setOnClickListener(v -> pop.dismiss());
        inflate.findViewById(R.id.btn_define).setOnClickListener(v -> {
            String fileName = edt_file_name.getText().toString().trim();
            String addr = edt_save_address.getText().toString().trim();
            if (fileName.isEmpty() || addr.isEmpty()) {
                ToastUtil.showShort(R.string.please_enter_file_name_and_addr);
                return;
            }
            PdfUtil.exportSignIn(addr, fileName, presenter.getPdfData());
            pop.dismiss();
        });
    }

    @Override
    public void updateExportDirPath(String dirPath) {
        if (edt_save_address != null) {
            edt_save_address.setText(dirPath);
        }
    }

    @Override
    protected SignPresenter initPresenter() {
        return new SignPresenter(this);
    }

    @Override
    protected void initial() {
        presenter.queryMember();
    }

    @Override
    public void updateSignList(int signInCount) {
        if (signInAdapter == null) {
            signInAdapter = new SignInAdapter(presenter.signInBeans);
            rvContent.setLayoutManager(new LinearLayoutManager(getContext()));
            rvContent.addItemDecoration(new RvItemDecoration(getContext()));
            rvContent.setAdapter(signInAdapter);
            signInAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                    signInAdapter.check(presenter.signInBeans.get(position).getMember().getPersonid());
                }
            });
        } else {
            signInAdapter.notifyDataSetChanged();
        }
        tvDue.setText(getString(R.string.sign_due_, presenter.signInBeans.size()));
        tvAlready.setText(getString(R.string.already_checked_in_, signInCount));
        tvUnchecked.setText(getString(R.string.unchecked_, presenter.signInBeans.size() - signInCount));
    }
}
