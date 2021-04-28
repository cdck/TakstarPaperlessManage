package com.xlk.takstarpaperlessmanage.view.admin.node;

import com.chad.library.adapter.base.entity.node.BaseNode;

import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Created by xlk on 2021/4/20.
 * @desc
 */
public class AdminChildNode extends BaseNode {
    private int id;

    public AdminChildNode(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Nullable
    @Override
    public List<BaseNode> getChildNode() {
        return null;
    }
}
