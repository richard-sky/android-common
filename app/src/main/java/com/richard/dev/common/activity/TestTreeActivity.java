package com.richard.dev.common.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.richard.dev.common.R;
import com.richard.dev.common.databinding.ActivityTreeBinding;
import com.richard.library.basic.basic.BasicBindingActivity;
import com.richard.library.basic.basic.adapter.tree.TreeAdapter;
import com.richard.library.basic.basic.adapter.tree.TreeNode;
import com.richard.library.context.util.JsonKt;
import com.richard.library.context.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Richard
 * @createDate: 2024/6/17 11:46
 * @version: 1.0
 * @description: 树级列表控件例子
 */
public class TestTreeActivity extends BasicBindingActivity<ActivityTreeBinding> {

    private TreeAdapter treeAdapter;

    public static void start(Context context) {
        context.startActivity(new Intent(context, TestTreeActivity.class));
    }

    @Override
    public void initLayoutView() {
        setContentView(R.layout.activity_tree);
    }

    @Override
    public void initData() {
        navigationbar.setVisibility(View.VISIBLE);
        navigationbar.setTitle("树形列表");
        navigationbar.setTitleTextViewShow(true);

        //去掉部分动画
        RecyclerView.ItemAnimator itemAnimator = binding.srvView.getItemAnimator();
        if (itemAnimator != null) {
            itemAnimator.setAddDuration(120);
            itemAnimator.setChangeDuration(0);
            itemAnimator.setMoveDuration(120);
            itemAnimator.setRemoveDuration(120);
            ((SimpleItemAnimator) itemAnimator).setSupportsChangeAnimations(true);
        }

        treeAdapter = new TreeAdapter(false);
        treeAdapter.setMulSelect(true);
        binding.srvView.setAdapter(treeAdapter);

        List<TreeNode> nodeList = this.generateTreeNoteList(null);
        for (TreeNode item : nodeList) {
            item.setChildren(this.generateTreeNoteList(item));
        }

        treeAdapter.addTopNode(nodeList);
    }

    @Override
    public void bindListener() {
        treeAdapter.setCallback(new TreeAdapter.Callback() {

            @Override
            public void onOpen(TreeNode itemInfo, int position) {
//                List<TreeNode> child = generateTreeNoteList(itemInfo);
//                if (ObjectUtilKt.isEmpty(child)) {
//                    treeAdapter.notifyEmptyChild(itemInfo, position);
//                } else {
//                    treeAdapter.addChildNode(itemInfo, child);
//                }
            }

            @Override
            public void onItemClick(TreeNode itemInfo, int position) {

            }
        });

        binding.btnConfirm.setOnClickListener(v -> {
            getUIView().showMsgDialog(LogUtil.formatJson(JsonKt.toJson(treeAdapter.getSelectedTopTreeList().get(0).getId())));
        });
    }

    /**
     * 生成子几点
     *
     * @param selectedNode 当前点击选中的节点
     */
    private List<TreeNode> generateTreeNoteList(TreeNode selectedNode) {
        List<TreeNode> treeNodeList = new ArrayList<>();

        if (selectedNode != null && selectedNode.getTreeDepth() >= 3) {
            return treeNodeList;
        }

        for (int i = 0; i < 10; i++) {
            treeNodeList.add(new TreeNode(
                    TreeNode.ITEM_TYPE_PARENT
                    , "节点" + (i + 1)
                    , selectedNode != null ? selectedNode.getId() + selectedNode.getTreeDepth() + i : String.valueOf(i + 1)
                    , selectedNode == null ? 1 : selectedNode.getTreeDepth() + 1
                    , null
                    , null
            ));
        }
        return treeNodeList;
    }
}
