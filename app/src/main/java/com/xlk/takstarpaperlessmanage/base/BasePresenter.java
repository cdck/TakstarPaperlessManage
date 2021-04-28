package com.xlk.takstarpaperlessmanage.base;

import com.google.protobuf.InvalidProtocolBufferException;
import com.xlk.takstarpaperlessmanage.model.EventMessage;
import com.xlk.takstarpaperlessmanage.model.JniHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * @author Created by xlk on 2021/4/21.
 * @desc
 */
public abstract class BasePresenter<V extends BaseContract.View> implements BaseContract.Presenter {
    protected String TAG = this.getClass().getSimpleName() + "-->";
    protected JniHelper jni = JniHelper.getInstance();
    public V mView;

    public BasePresenter(V view) {
        mView = view;
        register();
    }

    public void register() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    public void unregister() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    public void onDestroy() {
        unregister();
    }

    /**
     * EventBus发送的消息交给子类去处理
     *
     * @param msg 消息数据
     * @throws InvalidProtocolBufferException byte数组转指定结构体时的异常，避免子类中一直try catch
     */
    protected abstract void busEvent(EventMessage msg) throws InvalidProtocolBufferException;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMessage(EventMessage msg) throws InvalidProtocolBufferException {
        busEvent(msg);
    }
}
