package com.bx.philosopher.ui.adapter.view;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bx.philosopher.R;
import com.bx.philosopher.base.adapter.ViewHolderImpl;
import com.bx.philosopher.base.baseinterface.ItemSelectIml;

public class RechargeHolder extends ViewHolderImpl<String> {

    private TextView recharge_amount;
    private TextView recharge_amount_text;
    private TextView price_text;
    private LinearLayout charge_item_container;
    private ItemSelectIml itemSelect;

    public RechargeHolder(ItemSelectIml itemSelect) {
        this.itemSelect = itemSelect;
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.charge_item;
    }

    @Override
    protected View getItemView() {
        return super.getItemView();
    }

    @Override
    public void initView() {
        recharge_amount = findById(R.id.recharge_amount);
        price_text = findById(R.id.price_text);
        recharge_amount_text = findById(R.id.recharge_amount_text);
        charge_item_container = findById(R.id.charge_item_container);
    }

    @Override
    public void onBind(String data, int pos) {
        recharge_amount.setText(data);
        recharge_amount_text.setText(data);
        if (pos == itemSelect.getSelectedPosition()) {
            setSelectBG();
        } else {
            setBG();
        }
    }

    private void setSelectBG() {
        recharge_amount.setTextColor(getContext().getResources().getColor(R.color.white));
        recharge_amount_text.setTextColor(getContext().getResources().getColor(R.color.white));
        price_text.setTextColor(getContext().getResources().getColor(R.color.white));
        charge_item_container.setBackground(getContext().getResources().getDrawable(R.drawable.charge_item_click_bg));
    }

    private void setBG() {
        recharge_amount.setTextColor(getContext().getResources().getColor(R.color.color_666666));
        recharge_amount_text.setTextColor(getContext().getResources().getColor(R.color.color_666666));
        price_text.setTextColor(getContext().getResources().getColor(R.color.color_666666));
        charge_item_container.setBackground(getContext().getResources().getDrawable(R.drawable.charge_item_bg));

    }

}
