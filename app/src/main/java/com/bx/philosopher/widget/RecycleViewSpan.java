package com.bx.philosopher.widget;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.bx.philosopher.ui.adapter.view.ExploreExtraHolder.BOOK_GRAY_LINE;
import static com.bx.philosopher.ui.adapter.view.ExploreExtraHolder.BOOK_ITEM1;
import static com.bx.philosopher.ui.adapter.view.ExploreExtraHolder.BOOK_ITEM2;
import static com.bx.philosopher.ui.adapter.view.ExploreExtraHolder.BOOK_ITEM3;
import static com.bx.philosopher.ui.adapter.view.ExploreExtraHolder.BOOK_ITEM4;
import static com.bx.philosopher.ui.adapter.view.ExploreExtraHolder.BOOK_TAG;
import static com.bx.philosopher.ui.adapter.view.ExploreExtraHolder.IMAGE_ONE;
import static com.bx.philosopher.ui.adapter.view.ExploreExtraHolder.IMAGE_TWO;

/**
 * @ClassName: RecycleViewSpan
 * @Description: 用来控制recycleView 的子item所占的行数 spanCount保证为6
 * @Author: leeeeef
 * @CreateDate: 2019/5/28 14:59
 */
public class RecycleViewSpan extends GridLayoutManager.SpanSizeLookup {

    private RecyclerView.Adapter adapter;

    public RecycleViewSpan(RecyclerView.Adapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public int getSpanSize(int i) {

        switch (adapter.getItemViewType(i)) {
            case BOOK_GRAY_LINE:
            case BOOK_TAG:
            case BOOK_ITEM3:
            case IMAGE_ONE:
                return 6;
            case BOOK_ITEM1:
            case BOOK_ITEM4:
            case IMAGE_TWO:
                return 3;
            case BOOK_ITEM2:
                return 2;
        }
        return 6;
    }
}
