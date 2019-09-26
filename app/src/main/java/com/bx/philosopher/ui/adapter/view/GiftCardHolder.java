package com.bx.philosopher.ui.adapter.view;

import android.widget.LinearLayout;
import android.widget.TextView;

import com.bx.philosopher.R;
import com.bx.philosopher.base.adapter.ViewHolderImpl;
import com.bx.philosopher.base.baseinterface.ItemSelectIml;
import com.bx.philosopher.utils.StringUtils;

/**
 * @Description:
 * @Author: leeeef
 * @CreateDate: 2019/5/22 17:21
 */
public class GiftCardHolder extends ViewHolderImpl<Integer> {

    TextView card_count;
    TextView car_count_text;

    private ItemSelectIml itemSelectIml;
    private LinearLayout purchase;
    private TextView login_button_text;

    public GiftCardHolder(ItemSelectIml itemSelectIml) {
        this.itemSelectIml = itemSelectIml;
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.gift_card_item;
    }

    @Override
    public void initView() {
        card_count = findById(R.id.card_count);
        car_count_text = findById(R.id.car_count_text);
        purchase = findById(R.id.purchase);
        login_button_text = findById(R.id.login_button_text);
    }

    @Override
    public void onBind(Integer data, int pos) {
        card_count.setText(String.format("%.2f", Float.parseFloat(data + "")));
        car_count_text.setText("Rechargeable gift card $" + data);
        if (pos == itemSelectIml.getSelectedPosition()) {
            purchase.setBackground(StringUtils.getDrawable(R.drawable.login_button_bg));
            login_button_text.setTextColor(StringUtils.getColor(R.color.white));
        } else {
            purchase.setBackground(StringUtils.getDrawable(R.drawable.more_bg));
            login_button_text.setTextColor(StringUtils.getColor(R.color.color_d2d2d2));
        }
    }
}
