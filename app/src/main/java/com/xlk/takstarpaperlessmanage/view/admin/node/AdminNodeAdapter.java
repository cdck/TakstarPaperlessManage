package com.xlk.takstarpaperlessmanage.view.admin.node;

import com.chad.library.adapter.base.BaseNodeAdapter;
import com.chad.library.adapter.base.entity.node.BaseNode;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Created by xlk on 2021/4/20.
 * @desc
 */
public class AdminNodeAdapter extends BaseNodeAdapter {
    public static final int NODE_TYPE_PARENT = 0;
    public static final int NODE_TYPE_CHILD = 1;
    public static final int EXPAND_COLLAPSE_PAYLOAD = 110;
    private AdminParentProvider parentProvider;
    private AdminChildProvider childProvider;
    private NodeClickListener listener;

    public AdminNodeAdapter(@Nullable List<BaseNode> nodeList) {
        super(nodeList);
        this.parentProvider = new AdminParentProvider();
        addNodeProvider(parentProvider);
        this.childProvider = new AdminChildProvider();
        addNodeProvider(childProvider);
    }

    public void setNodeClickListener(NodeClickListener listener) {
        this.listener = listener;
    }

    public interface NodeClickListener {
        void onClick(int id);
    }

    public void click(int id) {
        if (listener != null) {
            listener.onClick(id);
        }
    }

    @Override
    protected int getItemType(@NotNull List<? extends BaseNode> list, int position) {
        BaseNode baseNode = list.get(position);
        if (baseNode instanceof AdminParentNode) {
            return NODE_TYPE_PARENT;
        } else if (baseNode instanceof AdminChildNode) {
            return NODE_TYPE_CHILD;
        } else {
            return -1;
        }
    }
}
