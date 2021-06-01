package com.xlk.takstarpaperlessmanage.view.admin.fragment.e_after.vote;

import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceVote;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.adapter.SubmitMemberAdapter;
import com.xlk.takstarpaperlessmanage.adapter.VoteResultAdapter;
import com.xlk.takstarpaperlessmanage.base.BaseFragment;
import com.xlk.takstarpaperlessmanage.model.Constant;
import com.xlk.takstarpaperlessmanage.model.bean.ExportSubmitMember;
import com.xlk.takstarpaperlessmanage.ui.RvItemDecoration;
import com.xlk.takstarpaperlessmanage.util.DateUtil;
import com.xlk.takstarpaperlessmanage.util.JxlUtil;
import com.xlk.takstarpaperlessmanage.util.PopUtil;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author Created by xlk on 2021/5/28.
 * @desc 会后查看-投票结果/选举结果
 */
public class VoteResultFragment extends BaseFragment<VoteResultPresenter> implements VoteResultContract.View {
    private int voteType;
    private boolean isVote;
    private TextView tv_vote_or_election, tv_vote_type;
    private RecyclerView rv_content;
    private VoteResultAdapter voteResultAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_vote_result;
    }

    @Override
    protected void initView(View inflate) {
        tv_vote_or_election = inflate.findViewById(R.id.tv_vote_or_election);
        tv_vote_type = inflate.findViewById(R.id.tv_vote_type);
        rv_content = inflate.findViewById(R.id.rv_content);
    }

    @Override
    protected VoteResultPresenter initPresenter() {
        return new VoteResultPresenter(this);
    }

    @Override
    protected void initial() {
        voteType = getArguments().getInt("vote_type");
        isVote = voteType == InterfaceMacro.Pb_MeetVoteType.Pb_VOTE_MAINTYPE_vote_VALUE;
        tv_vote_or_election.setText(isVote ? getString(R.string.vote_count) : getString(R.string.election_count));
        tv_vote_type.setText(isVote ? getString(R.string.vote_content) : getString(R.string.election_content));
        presenter.setVoteType(voteType);
        presenter.queryMemberDetailed();
        presenter.queryVote();
    }

    @Override
    protected void onShow() {
        initial();
    }

    @Override
    public void updateVoteList() {
        if (voteResultAdapter == null) {
            voteResultAdapter = new VoteResultAdapter(presenter.voteInfos);
            voteResultAdapter.addChildClickViewIds(R.id.operation_view_1, R.id.operation_view_2, R.id.operation_view_3);
            rv_content.setLayoutManager(new LinearLayoutManager(getContext()));
            rv_content.addItemDecoration(new RvItemDecoration(getContext()));
            rv_content.setAdapter(voteResultAdapter);
            voteResultAdapter.setOnItemChildClickListener((adapter, view, position) -> {
                InterfaceVote.pbui_Item_MeetVoteDetailInfo item = presenter.voteInfos.get(position);
                int votestate = item.getVotestate();
                int mode = item.getMode();
                switch (view.getId()) {
                    //导出PDF
                    case R.id.operation_view_1: {
                        if (mode != InterfaceMacro.Pb_MeetVoteMode.Pb_VOTEMODE_agonymous_VALUE) {
                            if (votestate != InterfaceMacro.Pb_MeetVoteStatus.Pb_vote_notvote_VALUE) {
                                presenter.querySubmittedVoters(item, 2);
                                exportVoteSubmitMember(item);

                            } else {
                                ToastUtils.showShort(R.string.cannot_view_notvote);
                            }
                        } else {
                            ToastUtils.showShort(R.string.please_choose_registered_vote);
                        }
                        break;
                    }
                    //查看详情
                    case R.id.operation_view_2: {
                        if (mode != InterfaceMacro.Pb_MeetVoteMode.Pb_VOTEMODE_agonymous_VALUE) {
                            if (votestate != InterfaceMacro.Pb_MeetVoteStatus.Pb_vote_notvote_VALUE) {
                                presenter.querySubmittedVoters(item, 0);
                            } else {
                                ToastUtils.showShort(R.string.cannot_view_notvote);
                            }
                        } else {
                            ToastUtils.showShort(R.string.please_choose_registered_vote);
                        }
                        break;
                    }
                    //查看图表
                    case R.id.operation_view_3: {
                        if (votestate != InterfaceMacro.Pb_MeetVoteStatus.Pb_vote_notvote_VALUE) {
                            presenter.querySubmittedVoters(item, 1);
                        } else {
                            ToastUtils.showShort(R.string.cannot_view_notvote);
                        }
                        break;
                    }
                }
            });
        } else {
            voteResultAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void showSubmittedPop(InterfaceVote.pbui_Item_MeetVoteDetailInfo vote) {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.pop_vote_detail, null);
        View ll_content = getActivity().findViewById(R.id.ll_content);
        View rv_navigation = getActivity().findViewById(R.id.rv_navigation);
        int width = ll_content.getWidth();
        int height = ll_content.getHeight();
        int width1 = rv_navigation.getWidth();
        PopupWindow pop = PopUtil.createPopupWindow(inflate, width * 2 / 3, height * 2 / 3, rv_content, Gravity.CENTER, width1 / 2, 0);
        TextView tv_vote_title = inflate.findViewById(R.id.tv_vote_title);
        String type = Constant.getVoteType(getContext(), vote.getType());
        String mode = vote.getMode() == 1 ? getString(R.string.notation) : getString(R.string.anonymous);
        tv_vote_title.setText(vote.getContent().toStringUtf8() + "（" + type + "、" + mode + "）");
        RecyclerView rv_content = inflate.findViewById(R.id.rv_content);
        SubmitMemberAdapter submitMemberAdapter = new SubmitMemberAdapter(presenter.submitMembers);
        rv_content.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_content.addItemDecoration(new RvItemDecoration(getContext()));
        rv_content.setAdapter(submitMemberAdapter);
        inflate.findViewById(R.id.iv_close).setOnClickListener(v -> pop.dismiss());
        inflate.findViewById(R.id.btn_cancel).setOnClickListener(v -> pop.dismiss());
        inflate.findViewById(R.id.btn_export).setOnClickListener(v -> {
            exportVoteSubmitMember(vote);
        });
    }

    private void exportVoteSubmitMember(InterfaceVote.pbui_Item_MeetVoteDetailInfo vote) {
        String[] strings = presenter.queryYd(vote);
        String createTime = DateUtil.nowDate();
        ExportSubmitMember exportSubmitMember = new ExportSubmitMember(vote.getContent().toStringUtf8(),
                createTime, strings[0], strings[1], strings[2], strings[3], presenter.submitMembers);
        JxlUtil.exportSubmitMember(exportSubmitMember);
    }

    /**
     * 获取当前投票的总票数
     *
     * @return 总票数
     */
    private int getTotalVotes(List<InterfaceVote.pbui_SubItem_VoteItemInfo> vote) {
        int count = 0;
        for (int i = 0; i < vote.size(); i++) {
            InterfaceVote.pbui_SubItem_VoteItemInfo info = vote.get(i);
            count += info.getSelcnt();
        }
        LogUtils.e("getTotalVotes :  当前投票票数总数 --> " + count);
        return count;
    }

    @Override
    public void showChartPop(InterfaceVote.pbui_Item_MeetVoteDetailInfo vote) {
        List<InterfaceVote.pbui_SubItem_VoteItemInfo> itemList = vote.getItemList();
        int totalVotes = getTotalVotes(itemList);
        if (totalVotes == 0) {
            ToastUtils.showShort(R.string.current_vote_no_data);
            return;
        }
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.pop_vote_chart, null);
        View ll_content = getActivity().findViewById(R.id.ll_content);
        View rv_navigation = getActivity().findViewById(R.id.rv_navigation);
        int width = ll_content.getWidth();
        int height = ll_content.getHeight();
        int width1 = rv_navigation.getWidth();
        PopupWindow pop = PopUtil.createPopupWindow(inflate, width * 2 / 3, height * 2 / 3, rv_content, Gravity.CENTER, width1 / 2, 0);
        TextView tv_vote_title = inflate.findViewById(R.id.tv_vote_title);
        TextView tv_count = inflate.findViewById(R.id.tv_count);
        PieChart pie_chart = inflate.findViewById(R.id.pie_chart);
        /*
        RelativeLayout rl_single = inflate.findViewById(R.id.rl_single);
        //答案有投票数量的个数，=1表示几个答案中都选择了其中一个
        // eg:n个投票人员，都选择了反对票
        int count = 0;
        int index = 0;//记录有数量的答案所在索引
        for (int i = 0; i < itemList.size(); i++) {
            InterfaceVote.pbui_SubItem_VoteItemInfo info = itemList.get(i);
            int selcnt = info.getSelcnt();
            if (selcnt != 0) {
                count++;
                index = i;
            }
        }
        if (count == 1) {
            //n个投票人员都选择了其中一个选项，100%
            rl_single.setVisibility(View.VISIBLE);
            // TODO: 2021/5/28 圆形的背景  getResources().getColor(R.color.option_a)
            rl_single.setBackgroundColor();
            pie_chart.setVisibility(View.GONE);
        } else {
            rl_single.setVisibility(View.GONE);
            pie_chart.setVisibility(View.VISIBLE);
        }
        */
        String type = Constant.getVoteType(getContext(), vote.getType());
        String mode = vote.getMode() == 1 ? getString(R.string.notation) : getString(R.string.anonymous);
        tv_vote_title.setText(vote.getContent().toStringUtf8() + "（" + type + "、" + mode + "）");
        String[] strings = presenter.queryYd(vote);
        String countStr = "";
        for (String s : strings) {
            countStr += s;
        }
        tv_count.setText(countStr);
        configChart(vote, pie_chart);
        inflate.findViewById(R.id.iv_close).setOnClickListener(v -> pop.dismiss());
    }


    private void configChart(InterfaceVote.pbui_Item_MeetVoteDetailInfo vote, PieChart chart) {
        chart.setLogEnabled(true);
        //如果启用此功能，则图表内的值将以百分比而不是原始值绘制。 提供为 提供的值 ValueFormatter然后，以百分比形式 to格式
        chart.setUsePercentValues(true);
        //将此设置为true，以将入口标签绘制到饼片中（由PieEntry类的getLabel()方法提供）
        chart.setDrawEntryLabels(false);
        //设置额外的偏移量（在图表视图周围）附加到自动计算的偏移量上
        chart.setExtraOffsets(5, 5, 100, 5);
        //减速摩擦系数，以[0 ; 1]为区间，数值越大表示速度越慢，如设为0，则会立即停止，1为无效值，会自动转换为0.999f。1为无效值，将自动转换为0.999f
        chart.setDragDecelerationFrictionCoef(0.95f);
//        chart.setCenterText("中间的文本内容");
        //将此设置为 "true"，以使饼中心为空
        chart.setDrawHoleEnabled(true);
        //设置画在饼图中心的孔的颜色。(上一行设置为true生效)
        chart.setHoleColor(Color.WHITE);
        //设置透明圆的颜色
        chart.setTransparentCircleColor(Color.WHITE);
        //设置透明圆的透明度
        chart.setTransparentCircleAlpha(110);
        //设置饼图中心孔的半径，以最大半径的百分比为单位（max=整个饼图的半径），默认为50%
        chart.setHoleRadius(0f);
        //设置画在饼图中孔洞旁边的透明圆的半径，以最大半径的百分比为单位(max=整个图表的半径)，默认55%->表示比中心孔洞默认大5%
        chart.setTransparentCircleRadius(0f);
        //将此设置为 "true"，以绘制显示在饼图中心的文字
        chart.setDrawCenterText(false);
        //设置雷达图的旋转偏移量，单位为度。默认270f
        chart.setRotationAngle(0);
        //设置为 "true"，可以通过触摸来实现图表的旋转/旋转。设置为false则禁用。默认值：true
        chart.setRotationEnabled(true);
        //将此设置为false，以防止通过点击手势突出显示数值。值仍然可以通过拖动或编程方式高亮显示。默认值：true
        chart.setHighlightPerTapEnabled(false);

        //返回图表的Legend对象。本方法可以用来获取图例的实例，以便自定义自动生成的图例
        Legend l = chart.getLegend();
//        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
//        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(true);
        //在水平轴上设置图例条目之间的空间，单位为像素，内部转换为dp
        l.setXEntrySpace(7f);
        //在垂直轴上设置图例条目之间的空间，单位为像素，内部转换为dp
        l.setYEntrySpace(5f);
        l.setWordWrapEnabled(true);
        //设置此轴上标签的Y轴偏移量。对于图例来说，偏移量越大，意味着图例作为一个整体将被放置在离顶部越远的地方
        l.setYOffset(0f);

        // entry label styling
        //设置输入标签的颜色
        chart.setEntryLabelColor(Color.WHITE);
        //设置用于绘制条目标签的自定义字体
//        chart.setEntryLabelTypeface(tfRegular);
        //设置以dp为单位的条目标签的大小。默认值：13dp
        chart.setEntryLabelTextSize(14f);

        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();
        List<InterfaceVote.pbui_SubItem_VoteItemInfo> itemList = vote.getItemList();
        for (int i = 0; i < itemList.size(); i++) {
            InterfaceVote.pbui_SubItem_VoteItemInfo item = itemList.get(i);
            String s = item.getText().toStringUtf8();
            int selcnt = item.getSelcnt();
            PieEntry pieEntry = new PieEntry((float) selcnt, s);
            pieEntries.add(pieEntry);
            if (i == 0) {
                colors.add(getResources().getColor(R.color.option_a));
            } else if (i == 1) {
                colors.add(getResources().getColor(R.color.option_b));
            } else if (i == 2) {
                colors.add(getResources().getColor(R.color.option_c));
            } else if (i == 3) {
                colors.add(getResources().getColor(R.color.option_d));
            } else if (i == 4) {
                colors.add(getResources().getColor(R.color.option_e));
            }
        }
        PieDataSet dataSet = new PieDataSet(pieEntries, "");
        dataSet.setValueTextSize(14f);
        dataSet.setColors(colors);
        //设置百分比内容的文本颜色
        dataSet.setValueTextColor(Color.WHITE);

//        chart.setUsePercentValues(true);

        PieData pieData = new PieData(dataSet);
        MyPercentFormatter f = new MyPercentFormatter(chart);
        pieData.setValueFormatter(f);
        pieData.setDrawValues(true);
        pieData.setValueTextSize(14f);
        chart.setData(pieData);

        Description description = new Description();
        description.setText("");
        chart.setDescription(description);

        chart.invalidate();
    }

}
