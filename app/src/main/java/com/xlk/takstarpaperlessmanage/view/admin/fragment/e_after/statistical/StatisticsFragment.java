package com.xlk.takstarpaperlessmanage.view.admin.fragment.e_after.statistical;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.LogUtils;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.mogujie.tt.protobuf.InterfaceStatistic;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.base.BaseFragment;

/**
 * @author Created by xlk on 2021/5/29.
 * @desc 后台查看-会议统计
 */
public class StatisticsFragment extends BaseFragment<StatisticsPresenter> implements StatisticsContract.View {

    private HorizontalBarChart chart;
    private LinearLayout rootView;
    private LinearLayout ll1;
    private TextView tvStream;
    private LinearLayout ll2;
    private TextView tvSameScreenRequest;
    private LinearLayout ll3;
    private TextView tvFileRequest;
    private LinearLayout ll4;
    private TextView tvChatRequest;
    private LinearLayout ll5;
    private TextView tvServerRequest;
    private LinearLayout ll6;
    private TextView tvArtRequest;
    private LinearLayout ll7;
    private TextView tvArtChat;
    private LinearLayout ll8;
    private TextView tvVoteLaunch;
    private LinearLayout ll9;
    private TextView tvElectionLaunched;
    private LinearLayout ll10;
    private TextView tvQuestionnaire;
    private LinearLayout ll11;
    private TextView tvBulletinLaunch;
    private TextView tvShow;
    private ImageView iv_1, iv_2, iv_3, iv_4, iv_5, iv_6, iv_7, iv_8, iv_9, iv_10, iv_11;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_meeting_statistics;
    }

    @Override
    protected void initView(View inflate) {
        chart = inflate.findViewById(R.id.chart);
//        initialChart();
        inflate.findViewById(R.id.btn_export).setOnClickListener(v -> {

        });
        rootView = (LinearLayout) inflate.findViewById(R.id.root_view);
        ll1 = (LinearLayout) inflate.findViewById(R.id.ll_1);
        tvStream = (TextView) inflate.findViewById(R.id.tv_stream);
        iv_1 = (ImageView) inflate.findViewById(R.id.iv_1);
        ll2 = (LinearLayout) inflate.findViewById(R.id.ll_2);
        tvSameScreenRequest = (TextView) inflate.findViewById(R.id.tv_same_screen_request);
        iv_2 = (ImageView) inflate.findViewById(R.id.iv_2);
        ll3 = (LinearLayout) inflate.findViewById(R.id.ll_3);
        tvFileRequest = (TextView) inflate.findViewById(R.id.tv_file_request);
        iv_3 = (ImageView) inflate.findViewById(R.id.iv_3);
        ll4 = (LinearLayout) inflate.findViewById(R.id.ll_4);
        tvChatRequest = (TextView) inflate.findViewById(R.id.tv_chat_request);
        iv_4 = (ImageView) inflate.findViewById(R.id.iv_4);
        ll5 = (LinearLayout) inflate.findViewById(R.id.ll_5);
        tvServerRequest = (TextView) inflate.findViewById(R.id.tv_server_request);
        iv_5 = (ImageView) inflate.findViewById(R.id.iv_5);
        ll6 = (LinearLayout) inflate.findViewById(R.id.ll_6);
        tvArtRequest = (TextView) inflate.findViewById(R.id.tv_art_request);
        iv_6 = (ImageView) inflate.findViewById(R.id.iv_6);
        ll7 = (LinearLayout) inflate.findViewById(R.id.ll_7);
        tvArtChat = (TextView) inflate.findViewById(R.id.tv_art_chat);
        iv_7 = (ImageView) inflate.findViewById(R.id.iv_7);
        ll8 = (LinearLayout) inflate.findViewById(R.id.ll_8);
        tvVoteLaunch = (TextView) inflate.findViewById(R.id.tv_vote_launch);
        iv_8 = (ImageView) inflate.findViewById(R.id.iv_8);
        ll9 = (LinearLayout) inflate.findViewById(R.id.ll_9);
        tvElectionLaunched = (TextView) inflate.findViewById(R.id.tv_election_launched);
        iv_9 = (ImageView) inflate.findViewById(R.id.iv_9);
        ll10 = (LinearLayout) inflate.findViewById(R.id.ll_10);
        tvQuestionnaire = (TextView) inflate.findViewById(R.id.tv_questionnaire);
        iv_10 = (ImageView) inflate.findViewById(R.id.iv_10);
        ll11 = (LinearLayout) inflate.findViewById(R.id.ll_11);
        tvBulletinLaunch = (TextView) inflate.findViewById(R.id.tv_bulletin_launch);
        iv_11 = (ImageView) inflate.findViewById(R.id.iv_11);
        tvShow = (TextView) inflate.findViewById(R.id.tv_show);
    }

    private void initialChart() {
//        chart.setOnChartValueSelectedListener(this);
        // chart.setHighlightEnabled(false);
        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(true);
        chart.getDescription().setEnabled(false);
        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        chart.setMaxVisibleValueCount(60);
        // scaling can now only be done on x- and y-axis separately
        chart.setPinchZoom(false);
        // draw shadows for each bar that show the maximum value
        chart.setDrawBarShadow(true);
        //set this to true to draw the grid background, false if not
        chart.setDrawGridBackground(false);

        XAxis xl = chart.getXAxis();
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
//        xl.setTypeface(tfLight);
        xl.setDrawAxisLine(true);
        xl.setDrawGridLines(false);
        xl.setGranularity(10f);

        YAxis yl = chart.getAxisLeft();
//        yl.setTypeface(tfLight);
        yl.setDrawAxisLine(true);
        yl.setDrawGridLines(true);
        yl.setAxisMinimum(0f); // this replaces setStartAtZero(true)
//        yl.setInverted(true);

        YAxis yr = chart.getAxisRight();
//        yr.setTypeface(tfLight);
        yr.setDrawAxisLine(true);
        yr.setDrawGridLines(false);
        yr.setAxisMinimum(0f); // this replaces setStartAtZero(true)
//        yr.setInverted(true);

        chart.setFitBars(true);
        chart.animateY(2500);

        // setting data
//        seekBarY.setProgress(50);
//        seekBarX.setProgress(12);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setFormSize(8f);
        l.setXEntrySpace(4f);
    }

    @Override
    protected StatisticsPresenter initPresenter() {
        return new StatisticsPresenter(this);
    }

    @Override
    protected void initial() {
        presenter.queryMeetingStatistics();
    }

    @Override
    public void updateCount(InterfaceStatistic.pbui_Type_MeetStatisticInfo info) {
        int total = 0, max, min;
        int count1 = info.getStreamgetcount();
        tvStream.setText(getString(R.string.count_, count1));
        total += count1;
        max = count1;
        min = count1;
        int count2 = info.getScreengetcount();
        tvSameScreenRequest.setText(getString(R.string.count_, count2));
        total += count2;
        max = Math.max(max, count2);
        min = Math.min(min, count2);
        int count3 = info.getFilegetcount();
        tvFileRequest.setText(getString(R.string.count_, count3));
        total += count3;
        max = Math.max(max, count3);
        min = Math.min(min, count3);
        int count4 = info.getChatcount();
        tvChatRequest.setText(getString(R.string.count_, count4));
        total += count4;
        max = Math.max(max, count4);
        min = Math.min(min, count4);
        int count5 = info.getServicegetcount();
        tvServerRequest.setText(getString(R.string.count_, count5));
        total += count5;
        max = Math.max(max, count5);
        min = Math.min(min, count5);
        int count6 = info.getWhiteboardopencount();
        tvArtRequest.setText(getString(R.string.count_, count6));
        total += count6;
        max = Math.max(max, count6);
        min = Math.min(min, count6);
        int count7 = info.getWhiteboardusecount();
        tvArtChat.setText(getString(R.string.count_, count7));
        total += count7;
        max = Math.max(max, count7);
        min = Math.min(min, count7);
        int count8 = info.getVotecount();
        tvVoteLaunch.setText(getString(R.string.count_, count8));
        total += count8;
        max = Math.max(max, count8);
        min = Math.min(min, count8);
        int count9 = info.getElectioncount();
        tvElectionLaunched.setText(getString(R.string.count_, count9));
        total += count9;
        max = Math.max(max, count9);
        min = Math.min(min, count9);
        int count10 = info.getQuestioncount();
        tvQuestionnaire.setText(getString(R.string.count_, count10));
        total += count10;
        max = Math.max(max, count10);
        min = Math.min(min, count10);
        int count11 = info.getBulletcount();
        tvBulletinLaunch.setText(getString(R.string.count_, count11));
        total += count11;
        max = Math.max(max, count11);
        min = Math.min(min, count11);
        StringBuilder sb = new StringBuilder();
        sb.append(count1).append("、");
        sb.append(count2).append("、");
        sb.append(count3).append("、");
        sb.append(count4).append("、");
        sb.append(count5).append("、");
        sb.append(count6).append("、");
        sb.append(count7).append("、");
        sb.append(count8).append("、");
        sb.append(count9).append("、");
        sb.append(count10).append("、");
        sb.append(count11).append("、");
        sb.append("最大值和最小值=").append(max).append("、").append(min);
        LogUtils.i(sb.toString());
        ll1.setWeightSum(max);
        ll2.setWeightSum(max);
        ll3.setWeightSum(max);
        ll4.setWeightSum(max);
        ll5.setWeightSum(max);
        ll6.setWeightSum(max);
        ll7.setWeightSum(max);
        ll8.setWeightSum(max);
        ll9.setWeightSum(max);
        ll10.setWeightSum(max);
        ll11.setWeightSum(max);
        int height = getResources().getDimensionPixelSize(R.dimen.meeting_statistics_chart_height);
        iv_1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, height, count1));
        iv_2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, height, count2));
        iv_3.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, height, count3));
        iv_4.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, height, count4));
        iv_5.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, height, count5));
        iv_6.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, height, count6));
        iv_7.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, height, count7));
        iv_8.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, height, count8));
        iv_9.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, height, count9));
        iv_10.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, height, count10));
        iv_11.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, height, count11));
        tvShow.setText(getString(R.string.low_carbon_behaviour, total));
    }

    @Override
    public void updateCountByTime(InterfaceStatistic.pbui_Type_MeetQuarterStatisticInfo info) {

    }
}
