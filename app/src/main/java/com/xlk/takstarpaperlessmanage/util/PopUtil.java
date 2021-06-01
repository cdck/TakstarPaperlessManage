package com.xlk.takstarpaperlessmanage.util;

import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;

import com.blankj.utilcode.util.ScreenUtils;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.model.GlobalValue;


/**
 * @author Created by xlk on 2020/11/28.
 * @desc PopupWindow工具类
 * showAsDropDown方法是让PopupWindow相对于某个控件显示
 * showAtLocation是相对于整个窗口
 */
public class PopUtil {

    public static PopupWindow createBigPop(View contentView, View parent) {
        return createPopupWindow(contentView, ScreenUtils.getScreenWidth() * 2 / 3, ScreenUtils.getScreenHeight() * 2 / 3, parent);
    }

    public static PopupWindow createHalfPop(View contentView, View parent) {
        return createPopupWindow(contentView, ScreenUtils.getScreenWidth() / 2, ScreenUtils.getScreenHeight() / 2, parent);
    }

    /**
     * 创建默认居中显示的PopupWindow
     *
     * @param contentView 内容布局
     * @param width       弹框的宽
     * @param height      弹框的高
     * @param parent      父控件
     * @return PopupWindow
     */
    public static PopupWindow createPopupWindow(View contentView, int width, int height, View parent) {
        return createPopupWindow(contentView, width, height, parent, Gravity.CENTER, 0, 0);
    }

    /**
     * 创建默认点击外部弹框消失的PopupWindow
     *
     * @param contentView 内容布局
     * @param width       弹框的宽
     * @param height      弹框的高
     * @param parent      父控件
     * @param gravity     Gravity
     * @param x           x轴偏移
     * @param y           y轴偏移
     * @return PopupWindow
     */
    public static PopupWindow createPopupWindow(View contentView, int width, int height, View parent, int gravity, int x, int y) {
        return createPopupWindowAt(contentView, width, height, true, parent, gravity, x, y);
    }

    /**
     * 创建一个覆盖fragment内容区域的PopupWindow
     *
     * @return PopupWindow
     */
    public static PopupWindow createCoverPopupWindow(View contentView, View parent,int width, int height,  int x, int y) {
        return createPopupWindowAt(contentView, width, height, true, parent, Gravity.START | Gravity.TOP, x, y);
    }

    public static PopupWindow createPopupWindowAt(View contentView, int width, int height, boolean outside, View parent, int gravity, int x, int y) {
        PopupWindow popupWindow = new PopupWindow(contentView, width, height);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
        popupWindow.setTouchable(true);
        // true:设置触摸外面时消失
        popupWindow.setOutsideTouchable(outside);
        popupWindow.setFocusable(true);
        popupWindow.setAnimationStyle(R.style.pop_animation_t_b);
        popupWindow.showAtLocation(parent, gravity, x, y);
        return popupWindow;
    }

    public static PopupWindow createPopupWindowAs(View contentView, int width, int height, View parent, int xoff, int yoff) {
        return createPopupWindowAs(contentView, width, height, true, parent, xoff, yoff);
    }

    public static PopupWindow createPopupWindowAs(View contentView, int width, int height, boolean outside, View parent, int x, int y) {
        return createPopupWindowAs(contentView, width, height, outside, parent, Gravity.TOP | Gravity.START, x, y);
    }

    public static PopupWindow createPopupWindowAs(View contentView, int width, int height, boolean outside, View parent, int gravity, int x, int y) {
        PopupWindow popupWindow = new PopupWindow(contentView, width, height);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
        popupWindow.setTouchable(true);
        // true:设置触摸外面时消失
        popupWindow.setOutsideTouchable(outside);
        popupWindow.setFocusable(outside);
        popupWindow.setAnimationStyle(R.style.pop_animation_t_b);
        popupWindow.showAsDropDown(parent, x, y, gravity);
        return popupWindow;
    }
}
