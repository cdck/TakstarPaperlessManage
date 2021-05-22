package com.xlk.takstarpaperlessmanage.ui.calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.View;

import com.blankj.utilcode.util.LogUtils;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarUtil;
import com.haibin.calendarview.MonthView;
import com.mogujie.tt.protobuf.InterfaceMeet;

import java.util.List;

/**
 * @author Created by xlk on 2021/5/17.
 * @desc
 */
public class CustomMonthView extends MonthView {

    private int mRadius;

    /**
     * 自定义魅族标记的文本画笔
     */
    private Paint mTextPaint = new Paint();


    /**
     * 24节气画笔
     */
    private Paint mSolarTermTextPaint = new Paint();

    /**
     * 背景圆点
     */
    private Paint mPointPaint = new Paint();

    /**
     * 今天的背景色
     */
    private Paint mCurrentDayPaint = new Paint();

    /**
     * 圆点半径
     */
    private float mPointRadius;

    private int mPadding;

    private float mCircleRadius;
    /**
     * 自定义魅族标记的圆形背景
     */
    private Paint mSchemeBasicPaint = new Paint();

    private float mSchemeBaseLine;

    public CustomMonthView(Context context) {
        super(context);

        mTextPaint.setTextSize(dipToPx(context, 8));
        mTextPaint.setColor(0xffffffff);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setFakeBoldText(true);


        mSolarTermTextPaint.setColor(0xff489dff);
        mSolarTermTextPaint.setAntiAlias(true);
        mSolarTermTextPaint.setTextAlign(Paint.Align.CENTER);

        mSchemeBasicPaint.setAntiAlias(true);
        mSchemeBasicPaint.setStyle(Paint.Style.FILL);
        mSchemeBasicPaint.setTextAlign(Paint.Align.CENTER);
        mSchemeBasicPaint.setFakeBoldText(true);
        mSchemeBasicPaint.setColor(Color.WHITE);


        mCurrentDayPaint.setAntiAlias(true);
        mCurrentDayPaint.setStyle(Paint.Style.FILL);
        mCurrentDayPaint.setColor(0xFFeaeaea);

        mPointPaint.setAntiAlias(true);
        mPointPaint.setStyle(Paint.Style.FILL);
        mPointPaint.setTextAlign(Paint.Align.CENTER);
        mPointPaint.setColor(Color.RED);

        mCircleRadius = dipToPx(getContext(), 7);

        mPadding = dipToPx(getContext(), 3);

        mPointRadius = dipToPx(context, 2);

        Paint.FontMetrics metrics = mSchemeBasicPaint.getFontMetrics();
        mSchemeBaseLine = mCircleRadius - metrics.descent + (metrics.bottom - metrics.top) / 2 + dipToPx(getContext(), 1);
    }


    @Override
    protected void onPreviewHook() {
        mSolarTermTextPaint.setTextSize(mCurMonthLunarTextPaint.getTextSize());
        mRadius = Math.min(mItemWidth, mItemHeight) / 11 * 5;
    }

    @Override
    protected boolean onDrawSelected(Canvas canvas, Calendar calendar, int x, int y, boolean hasScheme) {
//        int cx = x + mItemWidth / 2;
//        int cy = y + mItemHeight / 2;
//        canvas.drawCircle(cx, cy, mRadius, mSelectedPaint);
        canvas.drawRect(x + mPadding, y + mPadding, x + mItemWidth - mPadding, y + mItemHeight - mPadding, mSelectedPaint);
        return true;
    }

    @Override
    protected void onDrawScheme(Canvas canvas, Calendar calendar, int x, int y) {

//        boolean isSelected = isSelected(calendar);
//        if (isSelected) {
//            mPointPaint.setColor(Color.WHITE);
//        } else {
//            mPointPaint.setColor(Color.GRAY);
//        }
//        canvas.drawCircle(x + mItemWidth / 2, y + mItemHeight - 3 * mPadding, mPointRadius, mPointPaint);
        if (calendar.hasScheme()) {
            mPointPaint.setColor(Color.argb(100, 52, 117, 211));
            //绘制小圆点
            canvas.drawCircle(x + mItemWidth - 3 * mPadding - mPointRadius, y + 3 * mPadding + mPointRadius, mPointRadius, mPointPaint);
        }
    }

//    day=5-18,mItemWidth=217,mItemHeight=112,x=434,y=336
//    day=5-19,mItemWidth=217,mItemHeight=112,x=651,y=336

    @SuppressWarnings("IntegerDivisionInFloatingPointContext")
    @Override
    protected void onDrawText(Canvas canvas, Calendar calendar, int x, int y, boolean hasScheme, boolean isSelected) {
        int cx = x + mItemWidth / 2;
        int cy = y + mItemHeight / 2;
        int top = y - mItemHeight / 6;

        if (calendar.isCurrentDay() && !isSelected) {
            canvas.drawCircle(cx, cy, mRadius, mCurrentDayPaint);
        }

//        if (hasScheme) {
//            float v = mSchemeTextPaint.measureText(calendar.getScheme()) / 2;
//            canvas.drawText(calendar.getScheme(), cx-v, mTextBaseLine + y + mItemHeight / 10, mSchemeTextPaint);
//        }

        //当然可以换成其它对应的画笔就不麻烦，
        if (calendar.isWeekend() && calendar.isCurrentMonth()) {
            mCurMonthTextPaint.setColor(0xFF489dff);
            mCurMonthLunarTextPaint.setColor(0xFF489dff);
            mSchemeTextPaint.setColor(0xFF489dff);
            mSchemeLunarTextPaint.setColor(0xFF489dff);
            mOtherMonthLunarTextPaint.setColor(0xFF489dff);
            mOtherMonthTextPaint.setColor(0xFF489dff);
        } else {
            mCurMonthTextPaint.setColor(0xff333333);
            mCurMonthLunarTextPaint.setColor(0xffCFCFCF);
            mSchemeTextPaint.setColor(0xff333333);
            mSchemeLunarTextPaint.setColor(0xffCFCFCF);

            mOtherMonthTextPaint.setColor(0xFFe1e1e1);
            mOtherMonthLunarTextPaint.setColor(0xFFe1e1e1);
        }

        if (isSelected) {
            canvas.drawText(String.valueOf(calendar.getDay()), cx, mTextBaseLine + top,
                    mSelectTextPaint);
            mSelectedLunarTextPaint.setColor(Color.argb(100, 52, 117, 211));
            canvas.drawText(calendar.getScheme(), cx, mTextBaseLine + y + mItemHeight / 10, mSelectedLunarTextPaint);
//            if (hasScheme) {
//                List<Calendar.Scheme> schemes = calendar.getSchemes();
//                InterfaceMeet.pbui_Item_MeetMeetInfo item = (InterfaceMeet.pbui_Item_MeetMeetInfo) schemes.get(0).getObj();
//                String meetingName = item.getName().toStringUtf8();
//                String orderName = item.getOrdername().toStringUtf8();
//                long startTime = item.getStartTime();
//                long endTime = item.getEndTime();
//                Paint paint = new Paint();
//                paint.setColor(Color.argb(100,228,228,228));
//                paint.setStyle(Paint.Style.FILL);
//                canvas.drawRect(x + mPadding, y + mItemHeight, x + mItemWidth - mPadding, y + mItemHeight * 2, paint);
//            }
        } else if (hasScheme) {

            canvas.drawText(String.valueOf(calendar.getDay()), cx, mTextBaseLine + top,
                    calendar.isCurrentMonth() ? mSchemeTextPaint : mOtherMonthTextPaint);

            mSchemeLunarTextPaint.setColor(Color.argb(100, 52, 117, 211));
            canvas.drawText(calendar.getScheme(), cx, mTextBaseLine + y + mItemHeight / 10,
                    mSchemeLunarTextPaint);
        } else {
            canvas.drawText(String.valueOf(calendar.getDay()), cx, mTextBaseLine + top,
                    calendar.isCurrentDay() ? mCurDayTextPaint :
                            calendar.isCurrentMonth() ? mCurMonthTextPaint : mOtherMonthTextPaint);

//            canvas.drawText(calendar.getLunar(), cx, mTextBaseLine + y + mItemHeight / 10,
//                    calendar.isCurrentDay() ? mCurDayLunarTextPaint :
//                            calendar.isCurrentMonth() ? !TextUtils.isEmpty(calendar.getSolarTerm()) ? mSolarTermTextPaint :
//                                    mCurMonthLunarTextPaint : mOtherMonthLunarTextPaint);
        }
    }

    /**
     * dp转px
     *
     * @param context context
     * @param dpValue dp
     * @return px
     */
    private static int dipToPx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
