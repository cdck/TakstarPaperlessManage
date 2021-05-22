package com.xlk.takstarpaperlessmanage.view.admin.fragment.c_pre.function;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.mogujie.tt.protobuf.InterfaceMeetfunction;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.adapter.FunctionAdapter;
import com.xlk.takstarpaperlessmanage.base.BaseFragment;
import com.xlk.takstarpaperlessmanage.model.bean.FunctionBean;
import com.xlk.takstarpaperlessmanage.ui.RvItemDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author Created by xlk on 2021/5/22.
 * @desc
 */
public class FunctionFragment extends BaseFragment<FunctionPresenter> implements FunctionContract.View {
    private LinearLayout rootView;
    private RecyclerView rvCurrent;
    private RecyclerView rvHide;
    private FunctionAdapter meetFunctionAdapter;
    private FunctionAdapter hideFunctionAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_function;
    }

    @Override
    protected void initView(View inflate) {
        rootView = (LinearLayout) inflate.findViewById(R.id.root_view);
        rvCurrent = (RecyclerView) inflate.findViewById(R.id.rv_current);
        rvHide = (RecyclerView) inflate.findViewById(R.id.rv_hide);
        inflate.findViewById(R.id.btn_add_all).setOnClickListener(this);
        inflate.findViewById(R.id.btn_add).setOnClickListener(this);
        inflate.findViewById(R.id.btn_remove).setOnClickListener(this);
        inflate.findViewById(R.id.btn_remove_all).setOnClickListener(this);
        inflate.findViewById(R.id.btn_move_up).setOnClickListener(this);
        inflate.findViewById(R.id.btn_move_down).setOnClickListener(this);
        inflate.findViewById(R.id.btn_cancel).setOnClickListener(this);
        inflate.findViewById(R.id.btn_define).setOnClickListener(this);
    }

    @Override
    protected FunctionPresenter initPresenter() {
        return new FunctionPresenter(this);
    }

    @Override
    protected void initial() {
        presenter.queryMeetingFunction();
    }

    @Override
    public void updateFunctionList() {
        if (meetFunctionAdapter == null) {
            meetFunctionAdapter = new FunctionAdapter(presenter.meetFunction);
            rvCurrent.setLayoutManager(new LinearLayoutManager(getContext()));
            rvCurrent.addItemDecoration(new RvItemDecoration(getContext()));
            rvCurrent.setAdapter(meetFunctionAdapter);
            meetFunctionAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                    meetFunctionAdapter.setSelected(presenter.meetFunction.get(position).getFuncode());
                }
            });
        } else {
            meetFunctionAdapter.notifyDataSetChanged();
        }
        if (hideFunctionAdapter == null) {
            hideFunctionAdapter = new FunctionAdapter(presenter.hideMeetFunction);
            rvHide.setLayoutManager(new LinearLayoutManager(getContext()));
            rvHide.addItemDecoration(new RvItemDecoration(getContext()));
            rvHide.setAdapter(hideFunctionAdapter);
            hideFunctionAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                    hideFunctionAdapter.setSelected(presenter.hideMeetFunction.get(position).getFuncode());
                }
            });
        } else {
            hideFunctionAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_all: {
                addAll();
                break;
            }
            case R.id.btn_add: {
                add();
                break;
            }
            case R.id.btn_remove: {
                remove();
                break;
            }
            case R.id.btn_remove_all: {
                removeAll();
                break;
            }
            case R.id.btn_move_up: {
                moveUp();
                break;
            }
            case R.id.btn_move_down: {
                moveDown();
                break;
            }
            case R.id.btn_cancel: {
                presenter.queryMeetingFunction();
                break;
            }
            case R.id.btn_define: {
                save();
                break;
            }
        }
    }

    private void save() {
        List<InterfaceMeetfunction.pbui_Item_MeetFunConfigDetailInfo> items = new ArrayList<>();
        for (int i = 0; i < presenter.meetFunction.size(); i++) {
            InterfaceMeetfunction.pbui_Item_MeetFunConfigDetailInfo build = InterfaceMeetfunction.pbui_Item_MeetFunConfigDetailInfo.newBuilder()
                    .setFuncode(presenter.meetFunction.get(i).getFuncode())
                    .setPosition(presenter.meetFunction.get(i).getPosition())
                    .build();
            items.add(build);
        }
        jni.modifyMeetFunction(InterfaceMeetfunction.Pb_FunCon_ModifyFlag.Pb_FUNCONFIG_MODFLAG_SETDEFAULT_VALUE, items);
    }

    private void remove() {
        FunctionBean selected = meetFunctionAdapter.getSelected();
        if (selected == null) {
            ToastUtils.showShort(R.string.please_choose_function_first);
            return;
        }
        List<FunctionBean> hideMeetFunction = presenter.hideMeetFunction;
        List<FunctionBean> meetFunction = presenter.meetFunction;
        selected.setPosition(hideMeetFunction.size());
        hideMeetFunction.add(selected);
        meetFunction.remove(selected);
        for (int i = 0; i < meetFunction.size(); i++) {
            meetFunction.get(i).setPosition(i);
        }
        Collections.sort(hideMeetFunction);
        meetFunctionAdapter.notifyDataSetChanged();
        hideFunctionAdapter.notifyDataSetChanged();
    }

    private void removeAll() {
        if (presenter.meetFunction.isEmpty()) {
            return;
        }
        final int size = presenter.hideMeetFunction.size();
        for (int i = 0; i < presenter.meetFunction.size(); i++) {
            FunctionBean bean = presenter.meetFunction.get(i);
            bean.setPosition(size + i);
            presenter.hideMeetFunction.add(bean);
        }
        presenter.meetFunction.clear();
        Collections.sort(presenter.hideMeetFunction);
        meetFunctionAdapter.notifyDataSetChanged();
        hideFunctionAdapter.notifyDataSetChanged();
    }

    private void add() {
        FunctionBean selected = hideFunctionAdapter.getSelected();
        if (selected == null) {
            ToastUtils.showShort(R.string.please_choose_function_first);
            return;
        }
        selected.setPosition(presenter.meetFunction.size());
        presenter.meetFunction.add(selected);
        presenter.hideMeetFunction.remove(selected);
        Collections.sort(presenter.meetFunction);
        meetFunctionAdapter.notifyDataSetChanged();
        hideFunctionAdapter.notifyDataSetChanged();
    }

    private void addAll() {
        if (presenter.hideMeetFunction.isEmpty()) {
            return;
        }
        List<FunctionBean> meetFunction = presenter.meetFunction;
        final int size = meetFunction.size();
        for (int i = 0; i < presenter.hideMeetFunction.size(); i++) {
            FunctionBean hideItem = presenter.hideMeetFunction.get(i);
            hideItem.setPosition(size + i);
            meetFunction.add(hideItem);
        }
        presenter.hideMeetFunction.clear();
        Collections.sort(meetFunction);
        meetFunctionAdapter.notifyDataSetChanged();
        hideFunctionAdapter.notifyDataSetChanged();
    }

    private void moveUp() {
        FunctionBean current = meetFunctionAdapter.getSelected();
        if (current == null) {
            ToastUtils.showShort(R.string.please_choose_function_first);
            return;
        }
        int position = current.getPosition();
        if (position == 0) {
            //已经在最顶端就不需要上移了
            return;
        }
        FunctionBean previous = presenter.meetFunction.get(position - 1);
        current.setPosition(position - 1);
        previous.setPosition(position);
        Collections.sort(presenter.meetFunction);
        meetFunctionAdapter.notifyDataSetChanged();
    }

    private void moveDown() {
        FunctionBean current = meetFunctionAdapter.getSelected();
        if (current == null) {
            ToastUtils.showShort(R.string.please_choose_function_first);
            return;
        }
        int position = current.getPosition();
        if (position == presenter.meetFunction.size() - 1) {
            //已经是最低端则不需要再下移
            return;
        }
        FunctionBean next = presenter.meetFunction.get(position + 1);
        current.setPosition(position + 1);
        next.setPosition(position);
        Collections.sort(presenter.meetFunction);
        meetFunctionAdapter.notifyDataSetChanged();
    }
}
