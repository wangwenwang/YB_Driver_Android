package com.kaidongyuan.app.basemodule.utils.nomalutils;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * ${PEOJECT_NAME}
 * Created by Administrator on 2017/3/29.
 */
public class RecyclerViewUtils {
    public static void setListViewHeightBasedOnChildren(RecyclerView recyclerView) {
        // 获取ListView对应的Adapter
        RecyclerView.Adapter listAdapter = recyclerView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getItemCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem= recyclerView.getChildAt(i);

            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        params.height = totalHeight;
        recyclerView.setLayoutParams(params);
    }



}
