package com.bx.philosopher.ui.fragment.tabfragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bx.philosopher.R;
import com.bx.philosopher.base.activity.BaseContract;
import com.bx.philosopher.base.fragment.BaseLazyFragment;
import com.bx.philosopher.model.bean.response.BaseResponse;
import com.bx.philosopher.model.bean.response.GiftCardBean;
import com.bx.philosopher.net.BaseObserver;
import com.bx.philosopher.net.HttpUtil;
import com.bx.philosopher.net.RxScheduler;
import com.bx.philosopher.share.ShareUtil;
import com.bx.philosopher.ui.adapter.MyCardAdapter;
import com.bx.philosopher.utils.Constant;
import com.bx.philosopher.utils.LoadingUtil;
import com.bx.philosopher.utils.StringUtils;
import com.bx.philosopher.utils.ToastUtils;
import com.bx.philosopher.utils.login.LoginUtil;

import java.util.List;

import butterknife.BindView;

/**
 * @ClassName: CardFragment
 * @Description:
 * @Author: leeeeef
 * @CreateDate: 2019/6/15 10:17
 */
public class CardFragment extends BaseLazyFragment implements BaseContract.BaseView {

    private int type;//类型 1自己购买 2他人赠送

    @BindView(R.id.card_list)
    RecyclerView card_list;
    private MyCardAdapter myCardAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            type = bundle.getInt("type", 1);
        }
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initViewData(View v) {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_card;
    }

    @Override
    protected void onFirstUserVisible() {
        card_list.setLayoutManager(new LinearLayoutManager(getContext()));
        myCardAdapter = new MyCardAdapter();
        card_list.setAdapter(myCardAdapter);
        myCardAdapter.setOnItemClickListener((view, pos) -> {
            if (myCardAdapter.getItem(pos).getStatus() == 2) return;//已使用
            if (type == 2) {
                useCard(pos, myCardAdapter.getItem(pos).getId());
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            View alert = LayoutInflater.from(getContext()).inflate(R.layout.card_share_view, null);

            builder.setView(alert);
            AlertDialog alertDialog = builder.create();
            alertDialog.getWindow().setBackgroundDrawable(StringUtils.getDrawable(R.drawable.alert_bg));
            alertDialog.show();
            ImageView cancel = alert.findViewById(R.id.cancel);
            TextView card_money = alert.findViewById(R.id.card_money);
            cancel.setOnClickListener(v -> alertDialog.dismiss());

            int money = (int) Float.parseFloat(myCardAdapter.getItem(pos).getMoney());
            card_money.setText(money + "");
            TextView textView = alert.findViewById(R.id.share);
            textView.setOnClickListener(v -> {
                alertDialog.dismiss();
                String url = Constant.CARD_SHARE + "uid=" + LoginUtil.getUserIdStr() + "&uname=" +
                        LoginUtil.getName() + "&cid=" + myCardAdapter.getItem(pos).getId() + "&money=" + money;
                new ShareUtil().startShareAction(getActivity(), url, StringUtils.getString(R.string.card_share_title)
                        , StringUtils.getString(R.string.card_share_info));
            });
        });
        getCard();
    }

    private void getCard() {
        HttpUtil.getInstance().getRequestApi().getCardList(LoginUtil.getUserId(), type)
                .compose(RxScheduler.Obs_io_main())
                .subscribe(new BaseObserver<BaseResponse<List<GiftCardBean>>>(this) {
                    @Override
                    public void onSuccess(BaseResponse<List<GiftCardBean>> o) {
                        myCardAdapter.refreshItems(o.getData());
                        LoadingUtil.hide();
                    }

                    @Override
                    public void onError(String msg) {
                        showError(msg);
                    }
                });
    }

    private void useCard(int position, int cardId) {
        LoadingUtil.show(getContext());
        HttpUtil.getInstance().getRequestApi().useCard(LoginUtil.getUserId(), cardId)
                .compose(RxScheduler.Obs_io_main())
                .subscribe(new BaseObserver<BaseResponse<Object>>(this) {
                    @Override
                    public void onSuccess(BaseResponse<Object> o) {
                        showError(o.getMsg());
                        if (o.getData().equals(true)) {
                            myCardAdapter.getItem(position).setStatus(2);
                            myCardAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onError(String msg) {
                        showError(msg);
                    }
                });
    }

    @Override
    protected void onUserVisible() {

    }

    @Override
    protected void onFirstUserInvisible() {

    }

    @Override
    protected void onUserInvisible() {

    }

    @Override
    protected void onBindPresenter() {

    }

    @Override
    public void initImmersionBar() {

    }

    @Override
    public void showError(String msg) {
        ToastUtils.show(msg);
        LoadingUtil.hide();
    }

    @Override
    public void complete() {

    }

}
