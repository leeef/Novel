package com.bx.philosopher.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.fragment.app.Fragment;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.bx.philosopher.R;
import com.bx.philosopher.base.activity.BaseTabActivity;
import com.bx.philosopher.base.fragment.BaseLazyFragment;
import com.bx.philosopher.ui.fragment.tabfragment.BookshelfFragment;
import com.bx.philosopher.ui.fragment.tabfragment.ExploreFragment;
import com.bx.philosopher.ui.fragment.tabfragment.LibraryFragment;
import com.bx.philosopher.ui.fragment.tabfragment.PersonSettingFragment;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends BaseTabActivity implements EasyPermissions.PermissionCallbacks {
    /*************Constant**********/

    private static final String TAG = "MainActivity";
    private static final int WAIT_INTERVAL = 2000;
    private static final int PERMISSIONS_REQUEST_STORAGE = 1;

    static final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

//    LocationService locationService;


    /***************Object*********************/
    private final ArrayList<Fragment> mFragmentList = new ArrayList<>();
    /*****************Params*********************/
    private boolean isPrepareFinish = false;

    @Override
    protected int getContentId() {
        return R.layout.activity_base_tab;
    }

    private int tabIndex = 0;//当前展示的页面 默认第一个


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected List<Fragment> createTabFragments() {
        initFragment();
        return mFragmentList;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        tabIndex = getIntent().getIntExtra("tabIndex", 0);
    }

    public static void startActivity(Context context, int tabIndex) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("tabIndex", tabIndex);
        context.startActivity(intent);
    }

    private void initFragment() {
        Fragment exploreFragment = new ExploreFragment();
        Fragment libraryFragment = new LibraryFragment();
        Fragment bookshelfFragment = new BookshelfFragment();
        Fragment personSettingFragment = new PersonSettingFragment();
        mFragmentList.add(exploreFragment);
        mFragmentList.add(libraryFragment);
        mFragmentList.add(bookshelfFragment);
        mFragmentList.add(personSettingFragment);
    }

    @Override
    protected List<String> createTabTitles() {
        String[] titles = getResources().getStringArray(R.array.tab_title);
        return Arrays.asList(titles);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setTab(tabIndex);
    }


    @Override
    public boolean onPreparePanel(int featureId, View view, Menu menu) {
        if (menu != null && menu instanceof MenuBuilder) {
            try {
                Method method = menu.getClass().
                        getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                method.setAccessible(true);
                method.invoke(menu, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return super.onPreparePanel(featureId, view, menu);
    }


//    void startLocation() {
//        locationService = new LocationService();
//        locationService.registerListener(bdAbstractLocationListener);
//        locationService.start();
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (locationService != null) {
//            locationService.unregisterListener(bdAbstractLocationListener);
//        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @Override
    public void onBackPressed() {
        for (Fragment fragment : mFragmentList) {
            if (fragment != null) {
                if (fragment instanceof BaseLazyFragment) {
                    if (((BaseLazyFragment) fragment).onBackPressed()) return;
                }

            }
        }
        if (!isPrepareFinish) {
            mVp.postDelayed(
                    () -> isPrepareFinish = false, WAIT_INTERVAL
            );
            isPrepareFinish = true;
            Toast.makeText(this, "Press exit again", Toast.LENGTH_SHORT).show();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
//        switch (requestCode) {
//            case LOCATION_REQUEST:
//                startLocation();
//                break;
//        }

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }


    private BDAbstractLocationListener bdAbstractLocationListener = new BDAbstractLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果

            String addr = location.getAddrStr();    //获取详细地址信息
            String country = location.getCountry();    //获取国家
            String province = location.getProvince();    //获取省份
            String city = location.getCity();    //获取城市
            String district = location.getDistrict();    //获取区县
            String street = location.getStreet();    //获取街道信息

            Log.i(TAG, addr);
        }
    };
}
