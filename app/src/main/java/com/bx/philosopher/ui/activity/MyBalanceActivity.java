package com.bx.philosopher.ui.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bx.philosopher.R;
import com.bx.philosopher.base.activity.BaseMVPActivity;
import com.bx.philosopher.base.adapter.BaseListAdapter;
import com.bx.philosopher.base.adapter.IViewHolder;
import com.bx.philosopher.base.adapter.ViewHolderImpl;
import com.bx.philosopher.base.baseinterface.ItemSelectIml;
import com.bx.philosopher.presenter.MyBalancePresenter;
import com.bx.philosopher.presenter.imp.MyBalanceImp;
import com.bx.philosopher.share.PayH5Activity;
import com.bx.philosopher.ui.adapter.RechargeAdapter;
import com.bx.philosopher.utils.Constant;
import com.bx.philosopher.utils.LoadingUtil;
import com.bx.philosopher.utils.PermissionUtils;
import com.bx.philosopher.utils.ToastUtils;
import com.bx.philosopher.utils.login.LoginUtil;
import com.bx.philosopher.widget.itemdecoration.RecycleViewDivider;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import pub.devrel.easypermissions.EasyPermissions;

public class MyBalanceActivity extends BaseMVPActivity<MyBalancePresenter> implements MyBalanceImp.View, View.OnClickListener
        , EasyPermissions.PermissionCallbacks, IWXAPIEventHandler {
    @BindView(R.id.recharge_list)
    RecyclerView recharge_list;
    @BindView(R.id.recharge_type_list)
    RecyclerView recharge_type_list;

    @BindView(R.id.recharge_amount)
    TextView recharge_amount;
    @BindView(R.id.payment)
    LinearLayout payment;
    @BindView(R.id.balance)
    TextView balance;

    private RechargeAdapter rechargeAdapter;
    private RechargeTypeAdapter rechargeTypeAdapter;
    //选中金额的位置
    private int selectAmountPosition = 0;
    //选中的充值方式
    private int selectTypePosition = 0;

    //    private int[] rechare_pictures = {R.drawable.recharge_1, R.drawable.recharge_2, R.drawable.recharge_3, R.drawable.alipay_logo, R.drawable.weixin_pay};
    private int[] rechare_pictures = {R.drawable.recharge_1, R.drawable.recharge_2};
    //    private String[] recharge_names = {"LipaPay", "PayPal", "M-PESA", "支付宝", "微信"};
    private String[] recharge_names = {"LipaPay", "PayPal"};
    //定位权限
    private String[] permission = {Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private IWXAPI wxApi;

    private String LipaUrl = Constant.CLIENT_URL + "/pay/index.html";

    @Override
    protected int getContentId() {
        return R.layout.activity_my_balance;
    }


    @Override
    protected void initWidget() {
        super.initWidget();

        wxApi = WXAPIFactory.createWXAPI(this, "你的appid");
        wxApi.registerApp("");
        wxApi.handleIntent(getIntent(), this);

        recharge_list.setLayoutManager(new GridLayoutManager(this, 2));
        List<String> data = Arrays.asList(getResources().getStringArray(R.array.recharge_amount));
        rechargeAdapter = new RechargeAdapter(() -> selectAmountPosition);
        rechargeAdapter.addItems(data);
        recharge_list.setAdapter(rechargeAdapter);
        recharge_type_list.setLayoutManager(new LinearLayoutManager(this));
        rechargeTypeAdapter = new RechargeTypeAdapter(() -> selectTypePosition);
        recharge_type_list.addItemDecoration(new RecycleViewDivider(this, LinearLayout.HORIZONTAL, 1, getResources().getColor(R.color.color_eeeeee), 20, 20));
        recharge_type_list.setAdapter(rechargeTypeAdapter);
        for (int id : rechare_pictures) {
            rechargeTypeAdapter.addItem(id);
        }

        if (!EasyPermissions.hasPermissions(this, permission))
            PermissionUtils.requestPermissions(this, "Access to read and write memory CARDS is required", 1, permission);
        setBalance();
    }

    @Override
    protected void initClick() {
        super.initClick();
        rechargeAdapter.setOnItemClickListener((view, pos) -> {
            selectAmountPosition = pos;
            rechargeAdapter.notifyDataSetChanged();
            setRechargeAmount(rechargeAdapter.getItem(pos));
        });
        rechargeTypeAdapter.setOnItemClickListener((view, pos) -> {
            selectTypePosition = pos;
            rechargeTypeAdapter.notifyDataSetChanged();
        });

        setRechargeAmount(rechargeAdapter.getItem(selectAmountPosition));
    }

    //设置金额
    private void setRechargeAmount(String amount) {
        recharge_amount.setText("$" + amount);
    }

    @Override
    public void showError(String errMsg) {
        LoadingUtil.hide();
        ToastUtils.show(errMsg);
        Log.i(Constant.TAG, errMsg);
    }


    @Override
    public void complete() {
        mPresenter.getBalance();
        setBalance();
    }

    private void setBalance() {
        balance.setText("$ " + LoginUtil.getUserAccount());
    }

    @Override
    protected MyBalancePresenter bindPresenter() {
        return new MyBalancePresenter();
    }

    @OnClick({R.id.payment})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.payment:
                switch (selectTypePosition) {
                    case 0:

                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("Please select currency");
                        builder.setItems(new String[]{"KES", "NGN"}, (dialog, which) -> {
                            String param = "?uid=" + LoginUtil.getUserId() + "&total=" +
                                    Integer.parseInt(rechargeAdapter.getItem(selectAmountPosition)) + "&payway=LipaPay&nickname=" +
                                    LoginUtil.getName() + "&currency=";
                            switch (which) {
                                case 0:
                                    PayH5Activity.startActivity(this,  LipaUrl+param+"KES");
                                    break;
                                case 1:
                                    PayH5Activity.startActivity(this,  LipaUrl+param+"NGN");
                                    break;
                            }
                        });
                        builder.create().show();
                        break;
                    case 1://paypal
                        LoadingUtil.show(this);
                        mPresenter.pay(Integer.parseInt(rechargeAdapter.getItem(selectAmountPosition)), recharge_names[selectTypePosition]);
                        break;
//                    case 2:
//                    case 3:
////                        new ThirdPayUtil().ZFBPay(orderInfo, this, this);
//                        break;
//                    case 4:
////                        new ThirdPayUtil().WXPay(orderInfo, wxApi);
//                        break;
                }
                break;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void getpPayInfo(String orderInfo) {
        switch (selectTypePosition) {
            case 0:
                LoadingUtil.hide();
                break;
            case 1://paypal
                LoadingUtil.hide();
                PayH5Activity.startActivity(this, orderInfo);
                break;
//            case 2:
//                break;
//            case 3:
////                new ThirdPayUtil().ZFBPay(orderInfo, this, this);
//                break;
//            case 4:
////                new ThirdPayUtil().WXPay(orderInfo, wxApi);
//                break;
        }
    }

    @Override
    public void paySuccess() {

    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
    }

    class RechargeTypeAdapter extends BaseListAdapter<Integer> {
        private ItemSelectIml itemSelect;

        public RechargeTypeAdapter(ItemSelectIml itemSelect) {
            this.itemSelect = itemSelect;
        }

        @Override
        protected IViewHolder<Integer> createViewHolder(int viewType) {
            return new RechargeTypeHolder(itemSelect);
        }
    }

    class RechargeTypeHolder extends ViewHolderImpl<Integer> {

        private CheckBox recharge_checkbox;
        private ImageView recharge_picture;

        private ItemSelectIml itemSelect;

        public RechargeTypeHolder(ItemSelectIml itemSelect) {
            this.itemSelect = itemSelect;
        }

        @Override
        protected int getItemLayoutId() {
            return R.layout.recharge_mode_item;
        }

        @Override
        public void initView() {
            recharge_checkbox = findById(R.id.recharge_mode_check);
            recharge_picture = findById(R.id.recharge_picture);
            recharge_checkbox.setClickable(false);
        }


        @Override
        public void onBind(Integer data, int pos) {
            recharge_picture.setImageDrawable(getResources().getDrawable(data));
            if (itemSelect.getSelectedPosition() == pos) {
                recharge_checkbox.setChecked(true);
            } else {
                recharge_checkbox.setChecked(false);
            }
        }


    }
}
