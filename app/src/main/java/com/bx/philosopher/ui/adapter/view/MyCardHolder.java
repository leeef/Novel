package com.bx.philosopher.ui.adapter.view;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.widget.ImageView;
import android.widget.TextView;

import com.bx.philosopher.R;
import com.bx.philosopher.base.GlideApp;
import com.bx.philosopher.base.adapter.ViewHolderImpl;
import com.bx.philosopher.model.bean.response.GiftCardBean;
import com.bx.philosopher.utils.Constant;
import com.bx.philosopher.utils.StringUtils;

/**
 * @ClassName: MyCardHolder
 * @Description:
 * @Author: leeeeef
 * @CreateDate: 2019/6/12 10:27
 */
public class MyCardHolder extends ViewHolderImpl<GiftCardBean> {
    private ImageView card_cover;
    private TextView card_name;
    private TextView use;


    @Override
    protected int getItemLayoutId() {
        return R.layout.my_card_item;
    }

    @Override
    public void initView() {
        card_cover = findById(R.id.card_cover);
        card_name = findById(R.id.card_name);
        use = findById(R.id.use);
    }

    @Override
    public void onBind(GiftCardBean data, int pos) {
        card_name.setText("Reading with friends");
        switch (data.getStatus()) {
            case 1:
                GlideApp.with(getContext())
                        .load(Constant.getCardCover(data.getMoney()))
                        .into(card_cover);
                use.setText("Use");
                card_cover.clearColorFilter();
                break;
            case 2:
                GlideApp.with(getContext())
                        .load(Constant.getCardCoverGray(data.getMoney()))
                        .into(card_cover);
                card_cover.setColorFilter(Color.parseColor("#8729292A"), PorterDuff.Mode.MULTIPLY);

                use.setText("Used");
                use.setBackground(StringUtils.getDrawable(R.drawable.more_bg));
                break;
        }
    }
}
