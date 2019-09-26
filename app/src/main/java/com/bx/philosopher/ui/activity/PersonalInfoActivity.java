package com.bx.philosopher.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bx.philosopher.R;
import com.bx.philosopher.base.GlideApp;
import com.bx.philosopher.base.activity.BaseActivity;
import com.bx.philosopher.base.activity.BaseContract;
import com.bx.philosopher.model.bean.response.BaseResponse;
import com.bx.philosopher.net.BaseObserver;
import com.bx.philosopher.net.HttpUtil;
import com.bx.philosopher.net.RxScheduler;
import com.bx.philosopher.utils.FileUtils;
import com.bx.philosopher.utils.LoadingUtil;
import com.bx.philosopher.utils.PermissionUtils;
import com.bx.philosopher.utils.PhotoAlbumUtils;
import com.bx.philosopher.utils.ToastUtils;
import com.bx.philosopher.utils.login.LoginUtil;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import pub.devrel.easypermissions.EasyPermissions;

import static com.bx.philosopher.ui.activity.InformationSetActivity.SET_NICKNAME;

/**
 * 个人信息页面
 */
public class PersonalInfoActivity extends BaseActivity implements View.OnClickListener, EasyPermissions.PermissionCallbacks, BaseContract.BaseView {
    @BindView(R.id.head_layout)
    RelativeLayout head_layout;
    @BindView(R.id.nick_name_layout)
    RelativeLayout nick_name_layout;
    @BindView(R.id.nick_name)
    TextView nick_name;
    @BindView(R.id.bind_phone)
    TextView bind_phone;
    @BindView(R.id.bind_phone_layout)
    RelativeLayout bind_phone_layout;
    @BindView(R.id.head)
    ImageView head;


    private AlertDialog alertDialog;
    //权限
    private String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private File cameraSavePath;//拍照照片路径
    private Uri cameraUri;//拍照照片uri
    private static final int PHOTO = 22;
    private static final int ALBUM = 593;
    private static final int CORP = 480;

    @Override
    protected int getContentId() {
        return R.layout.activity_personal_info;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);

    }

    @Override
    protected void initClick() {
        super.initClick();
        head_layout.setOnClickListener(this);
        nick_name_layout.setOnClickListener(this);
        bind_phone_layout.setOnClickListener(this);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        initAlertDialog();
        GlideApp.with(this).load(LoginUtil.getHead())
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .error(R.drawable.appicon)
                .into(head);
    }

    @Override
    protected void onResume() {
        super.onResume();
        nick_name.setText(LoginUtil.getName());
        bind_phone.setText(LoginUtil.getPhone());
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.head_layout:
                checkPermission();
                break;
            case R.id.nick_name_layout://修改昵称
                InformationSetActivity.startActivity(this, SET_NICKNAME, "");
                break;
            case R.id.bind_phone_layout://绑定新手机号
                RegisterActivity.startActivity(this, RegisterActivity.REBIND);
                break;
            case R.id.photo://调用拍照
                goCamera();
                alertDialog.dismiss();
                break;
            case R.id.album://从相册选取
                goAlbum();
                alertDialog.dismiss();
                break;
        }
    }


    void initAlertDialog() {
        View alertDialog_view = LayoutInflater.from(this).inflate(R.layout.take_photo_layout, null);
        alertDialog_view.findViewById(R.id.photo).setOnClickListener(this);
        alertDialog_view.findViewById(R.id.album).setOnClickListener(this);
        alertDialog = new AlertDialog.Builder(this)
                .setView(alertDialog_view).create();
    }

    void checkPermission() {
        //获取权限
        if (!EasyPermissions.hasPermissions(this, permissions)) {
            //没有打开相关权限、申请权限
            PermissionUtils.requestPermissions(this, "need access to your album, photos, and storage", 1, permissions);
        } else {
            alertDialog.show();
        }

    }

    //打开相册操作
    private void goAlbum() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, ALBUM);
    }


    //打开相机操作
    private void goCamera() {
        cameraSavePath = FileUtils.getFile(FileUtils.getCameraPath() + System.currentTimeMillis() + ".jpg");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//如果在Android7.0以上,使用FileProvider获取Uri
            cameraUri = FileProvider.getUriForFile(this, "com.bx.philosopher.fileProvider", cameraSavePath);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            cameraUri = Uri.fromFile(cameraSavePath);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
        startActivityForResult(intent, PHOTO);
    }

    /**
     * 裁剪图片
     *
     * @param data
     */
    private void cropPic(Uri data) {
        if (data == null) {
            return;
        }
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        cropIntent.setDataAndType(data, "image/*");

        // 开启裁剪：打开的Intent所显示的View可裁剪
        cropIntent.putExtra("crop", "true");
        // 裁剪宽高比
        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);
        // 裁剪输出大小
        cropIntent.putExtra("outputX", 320);
        cropIntent.putExtra("outputY", 320);
        cropIntent.putExtra("scale", true);
        cropIntent.putExtra("circleCorp", true);
        /**
         * return-data
         * 这个属性决定我们在 onActivityResult 中接收到的是什么数据，
         * 如果设置为true 那么data将会返回一个bitmap
         * 如果设置为false，则会将图片保存到本地并将对应的uri返回，当然这个uri得有我们自己设定。
         * 系统裁剪完成后将会将裁剪完成的图片保存在我们所这设定这个uri地址上。我们只需要在裁剪完成后直接调用该uri来设置图片，就可以了。
         */
        cropIntent.putExtra("return-data", false);
        // 当 return-data 为 false 的时候需要设置这句
        cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
        // 图片输出格式
//        cropIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        // 头像识别 会启动系统的拍照时人脸识别
//        cropIntent.putExtra("noFaceDetection", true);
        startActivityForResult(cropIntent, CORP);
    }

    /**
     * 设置头像
     *
     * @Description:
     */
    void setHead(String imagePath) {

        FileUtils.compressImage(imagePath);
        LoadingUtil.show(this);
        HttpUtil.getInstance().getRequestApi().upLoadImage(FileUtils.imageToBase64ByLocal(imagePath))
                .compose(RxScheduler.Obs_io_main())
                .subscribe(new BaseObserver<BaseResponse<List<String>>>(this) {
                    @Override
                    public void onSuccess(BaseResponse<List<String>> o) {

                        Glide.with(PersonalInfoActivity.this).load(imagePath)
                                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                                .into(head);
                        HttpUtil.getInstance().getRequestApi().updateHead(LoginUtil.getUserId(),o.getData().get(0))
                                .compose(RxScheduler.Obs_io_main())
                                .subscribe(new BaseObserver<BaseResponse<Object>>(PersonalInfoActivity.this) {
                                    @Override
                                    public void onSuccess(BaseResponse<Object> o) {
                                        LoadingUtil.hide();
                                        ToastUtils.show(o.getMsg());
                                    }

                                    @Override
                                    public void onError(String msg) {
                                        showError(msg);
                                        LoadingUtil.hide();
                                    }
                                });
                    }

                    @Override
                    public void onError(String msg) {
                        LoadingUtil.hide();
                        showError(msg);
                    }

                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        String photoPath;
        if (requestCode == PHOTO && resultCode == RESULT_OK) {//拍照

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                photoPath = String.valueOf(cameraSavePath);
            } else {
                photoPath = cameraUri.getEncodedPath();
            }
            setHead(photoPath);
        } else if (requestCode == ALBUM && resultCode == RESULT_OK) {//相册
            photoPath = PhotoAlbumUtils.getRealPathFromUri(this, data.getData());
            setHead(photoPath);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if (requestCode == 1) {//允许权限后执行请求前的操作
            alertDialog.show();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        ToastUtils.show("Allow the camera permission to set the head");
    }

    @Override
    public void showError(String msg) {
        LoadingUtil.hide();
        ToastUtils.show(msg);
    }

    @Override
    public void complete() {

    }
}
