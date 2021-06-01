package com.xlk.takstarpaperlessmanage.view.admin.fragment.e_after.vote;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.DecimalFormat;

/**
 * @author Created by xlk on 2020/12/10.
 * @desc
 */
public class MyPercentFormatter extends ValueFormatter {
    public DecimalFormat mFormat;
    private PieChart pieChart;

    public MyPercentFormatter() {
        mFormat = new DecimalFormat("###,###,##0.0");
    }

    // Can be used to remove percent signs if the chart isn't in percent mode
    public MyPercentFormatter(PieChart pieChart) {
        this();
        this.pieChart = pieChart;
    }

    @Override
    public String getFormattedValue(float value) {
        return value <= 0 ? "" : mFormat.format(value) + " %";
    }

    @Override
    public String getPieLabel(float value, PieEntry pieEntry) {
        if (pieChart != null && pieChart.isUsePercentValuesEnabled()) {
            // Converted to percent
            return getFormattedValue(value);
        } else {
            // raw value, skip percent sign
            return value <= 0 ? "" : mFormat.format(value);
        }
    }
}
