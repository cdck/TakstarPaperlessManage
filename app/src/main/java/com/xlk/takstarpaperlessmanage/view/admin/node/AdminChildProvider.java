package com.xlk.takstarpaperlessmanage.view.admin.node;

import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.entity.node.BaseNode;
import com.chad.library.adapter.base.provider.BaseNodeProvider;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.model.Constant;
import com.xlk.takstarpaperlessmanage.model.EventMessage;
import com.xlk.takstarpaperlessmanage.model.EventType;
import com.xlk.takstarpaperlessmanage.model.GlobalValue;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

/**
 * @author Created by xlk on 2021/4/20.
 * @desc
 */
public class AdminChildProvider extends BaseNodeProvider {
    private int selectedId = -1;

    @Override
    public int getItemViewType() {
        return AdminNodeAdapter.NODE_TYPE_CHILD;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_admin_child;
    }

    @Override
    public void convert(@NotNull BaseViewHolder baseViewHolder, BaseNode node) {
        AdminChildNode childNode = (AdminChildNode) node;
        TextView item_tv_name = baseViewHolder.getView(R.id.item_tv_name);
        View item_root_child_view = baseViewHolder.getView(R.id.item_root_child_view);
        int currentId = childNode.getId();
//        LogUtils.i("convert 当前选中id=" + selectedId + ",当前id=" + currentId);
        item_root_child_view.setSelected(selectedId == currentId);
        String string = "";
        switch (currentId) {
            //设备管理
            case Constant.device_management: {
                string = getContext().getString(R.string.device_management);
                break;
            }
            //会议室管理
            case Constant.meeting_room_management: {
                string = getContext().getString(R.string.meeting_room_management);
                break;
            }
            //座位排布
            case Constant.seat_arrangement: {
                string = getContext().getString(R.string.seat_arrangement);
                break;
            }
            //秘书管理
            case Constant.secretary_management: {
                string = getContext().getString(R.string.secretary_management);
                break;
            }
            //常用参会人
            case Constant.commonly_participant: {
                string = getContext().getString(R.string.commonly_participant);
                break;
            }
            //其它设置
            case Constant.other_setting: {
                string = getContext().getString(R.string.other_setting);
                break;
            }
            //会议预约
            case Constant.meeting_reservation: {
                string = getContext().getString(R.string.meeting_reservation);
                break;
            }
            //会议管理
            case Constant.meeting_management: {
                string = getContext().getString(R.string.meeting_management);
                break;
            }
            //会议议程
            case Constant.meeting_agenda: {
                string = getContext().getString(R.string.meeting_agenda);
                break;
            }
            //参会人员
            case Constant.meeting_member: {
                string = getContext().getString(R.string.meeting_member);
                break;
            }
            //会议资料
            case Constant.meeting_material: {
                string = getContext().getString(R.string.meeting_material);
                break;
            }
            //摄像机管理
            case Constant.camera_management: {
                string = getContext().getString(R.string.camera_management);
                break;
            }
            //投票录入
            case Constant.vote_entry: {
                string = getContext().getString(R.string.vote_entry);
                break;
            }
            //选举录入
            case Constant.election_entry: {
                string = getContext().getString(R.string.election_entry);
                break;
            }
            //评分录入
            case Constant.score_entry: {
                string = getContext().getString(R.string.score_entry);
                break;
            }
            //座位绑定
            case Constant.seat_bind: {
                string = getContext().getString(R.string.seat_bind);
                break;
            }
            //桌牌显示
            case Constant.table_display: {
                string = getContext().getString(R.string.table_display);
                break;
            }
            //会议功能
            case Constant.meeting_function: {
                string = getContext().getString(R.string.meeting_function);
                break;
            }
            //设备控制
            case Constant.device_control: {
                string = getContext().getString(R.string.device_control);
                break;
            }
            //投票管理
            case Constant.vote_management: {
                string = getContext().getString(R.string.vote_management);
                break;
            }
            //选举管理
            case Constant.election_management: {
                string = getContext().getString(R.string.election_management);
                break;
            }
            //评分管理
            case Constant.score_management: {
                string = getContext().getString(R.string.score_management);
                break;
            }
            //会议交流
            case Constant.meeting_chat: {
                string = getContext().getString(R.string.meeting_chat);
                break;
            }
            //摄像头控制
            case Constant.camera_control: {
                string = getContext().getString(R.string.camera_control);
                break;
            }
            //屏幕管理
            case Constant.screen_management: {
                string = getContext().getString(R.string.screen_management);
                break;
            }
            //会议记录
            case Constant.meeting_minutes: {
                string = getContext().getString(R.string.meeting_minutes);
                break;
            }
            //签到情况
            case Constant.sign_in_info: {
                string = getContext().getString(R.string.sign_in_info);
                break;
            }
            //批注查看
            case Constant.annotation_view: {
                string = getContext().getString(R.string.annotation_view);
                break;
            }
            //投票结果
            case Constant.vote_result: {
                string = getContext().getString(R.string.vote_result);
                break;
            }
            //选举结果
            case Constant.election_result: {
                string = getContext().getString(R.string.election_result);
                break;
            }
            //会议归档
            case Constant.meeting_archive: {
                string = getContext().getString(R.string.meeting_archive);
                break;
            }
            //会议统计
            case Constant.meeting_statistics: {
                string = getContext().getString(R.string.meeting_statistics);
                break;
            }
            //评分查看
            case Constant.score_view: {
                string = getContext().getString(R.string.score_view);
                break;
            }
            //录像管理
            case Constant.video_management: {
                string = getContext().getString(R.string.video_management);
                break;
            }
            default:
                break;
        }
        item_tv_name.setText(string);
    }

    @Override
    public void onClick(@NotNull BaseViewHolder helper, @NotNull View view, BaseNode data, int position) {
        AdminNodeAdapter adapter = (AdminNodeAdapter) getAdapter();
        AdminChildNode childNode = (AdminChildNode) data;
        if (adapter != null) {
            int id = childNode.getId();
            if (GlobalValue.currentMeetingId == 0) {
                //当前设备还没有切换到会议编辑
                if (id > Constant.admin_current_meeting) {
                    //没有切换会议编辑的情况下点击了必须要切换会议编辑的功能
                    // TODO: 7/21/21 通知进行会议切换
                    EventBus.getDefault().post(new EventMessage.Builder().type(EventType.BUS_SWITCH_MEETING).build());
                } else {
                    selectedId = id;
                    adapter.click(selectedId);
                }
            } else {
                selectedId = id;
                adapter.click(selectedId);
            }
        }

//        LogUtils.d("onClick selectedId=" + selectedId);
//
//        if (adapter != null) {
//            if (selectedId > Constant.meeting_management) {
//                if (GlobalValue.currentMeetingId == 0) {
//                    int index = 0;
//                    if (selectedId < Constant.admin_current_meeting) {
//                        //说明当前点击的是会前设置中的功能
//
//                    } else {
//                        //说明当前点击的是会后管理或者会后查看中的功能
//
//                    }
//                }
//            } else {
//                adapter.click(selectedId);
//            }
//            adapter.click(selectedId);
//        }
    }
}
