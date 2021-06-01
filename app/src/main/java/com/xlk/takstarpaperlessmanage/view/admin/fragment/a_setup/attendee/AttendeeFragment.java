package com.xlk.takstarpaperlessmanage.view.admin.fragment.a_setup.attendee;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.UriUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.itextpdf.text.pdf.PdfReader;
import com.mogujie.tt.protobuf.InterfacePerson;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.adapter.AttendeeAdapter;
import com.xlk.takstarpaperlessmanage.base.BaseFragment;
import com.xlk.takstarpaperlessmanage.ui.RvItemDecoration;
import com.xlk.takstarpaperlessmanage.util.JxlUtil;
import com.xlk.takstarpaperlessmanage.util.PopUtil;
import com.xlk.takstarpaperlessmanage.util.ToastUtil;

import java.io.File;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.xlk.takstarpaperlessmanage.model.Constant.s2b;

/**
 * @author Created by xlk on 2021/5/13.
 * @desc
 */
public class AttendeeFragment extends BaseFragment<AttendeePresenter> implements AttendeeContract.View {

    private RecyclerView rv_attendee;
    private AttendeeAdapter attendeeAdapter;
    private PopupWindow deletePop;
    private PopupWindow modifyPop;
    private final int IMPORT_ATTENDEE_REQUEST_CODE = 1;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_attendee;
    }

    @Override
    protected void initView(View inflate) {
        rv_attendee = inflate.findViewById(R.id.rv_attendee);
        inflate.findViewById(R.id.btn_add).setOnClickListener(v -> {
            showAddorModifyPop(null);
        });
        inflate.findViewById(R.id.btn_import).setOnClickListener(v -> {
            chooseLocalFile(IMPORT_ATTENDEE_REQUEST_CODE);
        });
        inflate.findViewById(R.id.btn_export).setOnClickListener(v -> {
            presenter.exportAttendee();
        });
    }

    @Override
    protected AttendeePresenter initPresenter() {
        return new AttendeePresenter(this);
    }

    @Override
    protected void initial() {
        presenter.queryAttendee();
    }

    @Override
    protected void onShow() {
        initial();
    }

    @Override
    public void updateAttendeeList() {
        if (attendeeAdapter == null) {
            attendeeAdapter = new AttendeeAdapter(presenter.attendees);
            attendeeAdapter.addChildClickViewIds(R.id.tv_delete, R.id.tv_modify);
            rv_attendee.setLayoutManager(new LinearLayoutManager(getContext()));
            rv_attendee.addItemDecoration(new RvItemDecoration(getContext()));
            rv_attendee.setAdapter(attendeeAdapter);
            attendeeAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
                @Override
                public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                    InterfacePerson.pbui_Item_PersonDetailInfo item = presenter.attendees.get(position);
                    if (view.getId() == R.id.tv_delete) {
                        showDeletePop(item);
                    } else {
                        showAddorModifyPop(item);
                    }
                }
            });
        } else {
            attendeeAdapter.notifyDataSetChanged();
        }
    }

    private void showAddorModifyPop(InterfacePerson.pbui_Item_PersonDetailInfo item) {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.pop_modify_attendee, null, false);
        View ll_content = getActivity().findViewById(R.id.ll_content);
        View rv_navigation = getActivity().findViewById(R.id.rv_navigation);
        int width = ll_content.getWidth();
        int height = ll_content.getHeight();
        int width1 = rv_navigation.getWidth();
        modifyPop = PopUtil.createPopupWindow(inflate, width / 2, height / 2, rv_attendee, Gravity.CENTER, width1 / 2, 0);
        TextView tv_title = inflate.findViewById(R.id.tv_title);
        boolean isAdd = item == null;
        tv_title.setText(isAdd ? getString(R.string.add) : getString(R.string.modify));
        EditText edt_name = inflate.findViewById(R.id.edt_name);
        EditText edt_unit = inflate.findViewById(R.id.edt_unit);
        EditText edt_job = inflate.findViewById(R.id.edt_job);
        EditText edt_phone = inflate.findViewById(R.id.edt_phone);
        EditText edt_email = inflate.findViewById(R.id.edt_email);
        EditText edt_password = inflate.findViewById(R.id.edt_password);
        EditText edt_remark = inflate.findViewById(R.id.edt_remark);
        if (!isAdd) {
            edt_name.setText(item.getName().toStringUtf8());
            edt_unit.setText(item.getCompany().toStringUtf8());
            edt_job.setText(item.getJob().toStringUtf8());
            edt_phone.setText(item.getPhone().toStringUtf8());
            edt_email.setText(item.getEmail().toStringUtf8());
            edt_password.setText(item.getPassword().toStringUtf8());
            edt_remark.setText(item.getComment().toStringUtf8());
        }
        inflate.findViewById(R.id.iv_close).setOnClickListener(v -> modifyPop.dismiss());
        inflate.findViewById(R.id.btn_cancel).setOnClickListener(v -> modifyPop.dismiss());
        inflate.findViewById(R.id.btn_define).setOnClickListener(v -> {
            String name = edt_name.getText().toString().trim();
            String unit = edt_unit.getText().toString().trim();
            String job = edt_job.getText().toString().trim();
            String phone = edt_phone.getText().toString().trim();
            String email = edt_email.getText().toString().trim();
            String password = edt_password.getText().toString().trim();
            String remark = edt_remark.getText().toString().trim();
            if (TextUtils.isEmpty(name)) {
                ToastUtil.showShort(R.string.please_enter_the_content_to_be_modified);
                return;
            }
            InterfacePerson.pbui_Item_PersonDetailInfo.Builder builder = InterfacePerson.pbui_Item_PersonDetailInfo.newBuilder();
            builder.setName(s2b(name));
            builder.setComment(s2b(remark));
            builder.setJob(s2b(job));
            builder.setCompany(s2b(unit));
            builder.setPhone(s2b(phone));
            builder.setEmail(s2b(email));
            builder.setPassword(s2b(password));
            if (isAdd) {
                jni.addAttendee(builder.build());
            } else {
                builder.setPersonid(item.getPersonid());
                jni.modifyAttendee(builder.build());
            }
            modifyPop.dismiss();
        });
    }

    private void showDeletePop(InterfacePerson.pbui_Item_PersonDetailInfo item) {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.pop_delete, null, false);
        View ll_content = getActivity().findViewById(R.id.ll_content);
        View rv_navigation = getActivity().findViewById(R.id.rv_navigation);
        int width = ll_content.getWidth();
        int height = ll_content.getHeight();
        int width1 = rv_navigation.getWidth();
        deletePop = PopUtil.createPopupWindow(inflate, width / 2, height / 2, rv_attendee, Gravity.CENTER, width1 / 2, 0);
        TextView tv_hint = inflate.findViewById(R.id.tv_hint);
        tv_hint.setText(getString(R.string.delete_attendee_hint));
        inflate.findViewById(R.id.iv_close).setOnClickListener(v -> deletePop.dismiss());
        inflate.findViewById(R.id.btn_cancel).setOnClickListener(v -> deletePop.dismiss());
        inflate.findViewById(R.id.btn_define).setOnClickListener(v -> {
            jni.delAttendee(item);
            deletePop.dismiss();
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMPORT_ATTENDEE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            File file = UriUtils.uri2File(uri);
            if (file != null) {
                String realPath = file.getAbsolutePath();
                LogUtils.e(TAG, "onActivityResult 获取的文件路径=" + realPath);
                if (realPath.endsWith(".xls")) {
                    List<InterfacePerson.pbui_Item_PersonDetailInfo> memberXls = JxlUtil.readMemberXls(realPath);
                    if (!memberXls.isEmpty()) {
                        jni.addAttendee(memberXls);
                    } else {
                        LogUtils.e(TAG, "onActivityResult 读取表格结果为空");
                    }
                } else {
                    ToastUtils.showShort(R.string.please_choose_xls_file);
                }
            }
        }
    }
}
