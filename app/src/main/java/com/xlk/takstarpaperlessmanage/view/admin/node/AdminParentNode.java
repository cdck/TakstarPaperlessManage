package com.xlk.takstarpaperlessmanage.view.admin.node;

import com.chad.library.adapter.base.entity.node.BaseExpandNode;
import com.chad.library.adapter.base.entity.node.BaseNode;

import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Created by xlk on 2021/4/20.
 * @desc
 */
public class AdminParentNode extends BaseExpandNode {
    private int id;
    private List<BaseNode> childNodes;

    public AdminParentNode(int id) {
        this.id = id;
        setExpanded(false);
    }

    public int getId() {
        return id;
    }

    public void setChildNodes(List<BaseNode> childNodes) {
        this.childNodes = childNodes;
    }

    @Nullable
    @Override
    public List<BaseNode> getChildNode() {
        return childNodes;
    }
}
