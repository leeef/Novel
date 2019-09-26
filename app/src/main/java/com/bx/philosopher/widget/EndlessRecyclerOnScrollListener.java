package com.bx.philosopher.widget;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


/**
 * @ClassName: EndlessRecyclerOnScrollListener
 * @Description: recycleView 加载更多监听器
 * @Author: leeeeef
 * @CreateDate: 2019/5/28 17:16
 */

public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {

    /**
     * 标记是否正在向上滑动
     */
    private boolean isSlidingUpward = false;
    /**
     * 最后一个可见的item的位置
     */
    private int lastVisibleItemPosition;

    @Override
    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

            lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager())
                    .findLastCompletelyVisibleItemPosition();

        } else if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            lastVisibleItemPosition = ((GridLayoutManager) recyclerView.getLayoutManager())
                    .findLastCompletelyVisibleItemPosition();
        }
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            //当状态是不滑动的时候
            int itemCount = recyclerView.getLayoutManager().getItemCount();

            if (lastVisibleItemPosition == (itemCount - 1) && isSlidingUpward) {
                onLoadMoreData();
            }
        }
    }


    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        // 大于0表示正在向上滑动，小于等于0表示停止或向下滑动
        isSlidingUpward = dy > 0;
    }

    /**
     * 加载更多数据的方法
     */
    public abstract void onLoadMoreData();

}

