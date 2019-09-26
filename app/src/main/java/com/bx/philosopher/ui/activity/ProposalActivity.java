package com.bx.philosopher.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bx.philosopher.R;
import com.bx.philosopher.base.activity.BaseMVPActivity;
import com.bx.philosopher.presenter.ProposalPresenter;
import com.bx.philosopher.presenter.imp.ProposalImp;
import com.bx.philosopher.utils.Constant;
import com.bx.philosopher.utils.FileUtils;
import com.bx.philosopher.utils.PermissionUtils;
import com.bx.philosopher.utils.PhotoAlbumUtils;
import com.bx.philosopher.utils.ToastUtils;
import com.bx.philosopher.widget.LoadingDialog;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import pub.devrel.easypermissions.EasyPermissions;

//投诉建议
public class ProposalActivity extends BaseMVPActivity<ProposalPresenter> implements View.OnClickListener, EasyPermissions.PermissionCallbacks, ProposalImp.View {

    @BindView(R.id.feed_back)
    EditText feed_back;
    @BindView(R.id.phone_number)
    EditText phone_number;
    @BindView(R.id.count_text)
    TextView count_text;
    @BindView(R.id.addImage)
    ImageView addImage;
    @BindView(R.id.image_list)
    LinearLayout image_list;
    @BindView(R.id.image_count)
    TextView image_count;
    @BindView(R.id.submission)
    LinearLayout submission;

    private int COUNT_LIMIT = 500;
    private AlertDialog alertDialog;
    private View alertDialog_view;
    //权限
    private String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private File cameraSavePath;//拍照照片路径
    private Uri cameraUri;//拍照照片uri
    private static final int PHOTO = 22;
    private static final int ALBUM = 593;
    private List<String> imagePathList = new ArrayList<>();

    private LoadingDialog loadingDialog;


    @Override
    protected int getContentId() {
        return R.layout.activity_proposal;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);

    }

    @Override
    protected void initWidget() {
        super.initWidget();
        loadingDialog = new LoadingDialog(this, "uploading...", false);
    }

    @Override
    protected void initClick() {
        super.initClick();
        feed_back.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                count_text.setText(MessageFormat.format("{0}/500", s.toString().length()));
            }
        });
        addImage.setOnClickListener(this);
        submission.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addImage:
                if (image_list.getChildCount() >= 3) return;
                getPermission();
                break;
            case R.id.photo://调用拍照
                goCamera();
                dismissDialog();
                break;
            case R.id.album://从相册选取
                goAlbum();
                dismissDialog();
                break;
            case R.id.submission:
                if (imagePathList.size() == 0) {
                    ToastUtils.show("Please select picture");
                    return;
                }
                loadingDialog.show();
                StringBuilder stringBuilder = new StringBuilder();
                for (String path : imagePathList) {
                    stringBuilder.append(FileUtils.imageToBase64ByLocal(path));
                }
                mPresenter.loadImage(stringBuilder.toString());
                break;
        }
    }

    public void addImageDialog() {
        alertDialog_view = LayoutInflater.from(this).inflate(R.layout.take_photo_layout, null);
        alertDialog_view.findViewById(R.id.photo).setOnClickListener(this);
        alertDialog_view.findViewById(R.id.album).setOnClickListener(this);
        alertDialog = new AlertDialog.Builder(this)
                .setView(alertDialog_view).create();
        alertDialog.show();
    }

    private void dismissDialog() {
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if (requestCode == 1) {//允许权限后执行请求前的操作
            addImageDialog();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }

    //获取权限
    private void getPermission() {
        if (!EasyPermissions.hasPermissions(this, permissions)) {
            //没有打开相关权限、申请权限
            PermissionUtils.requestPermissions(this, "need access to your album, photos, and storage", 1, permissions);
        } else {
            addImageDialog();
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
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraSavePath = FileUtils.getFile(FileUtils.getCameraPath() + System.currentTimeMillis() + ".jpg");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//如果在Android7.0以上,使用FileProvider获取Uri
            cameraUri = FileProvider.getUriForFile(this, "com.bx.philosopher.fileProvider", cameraSavePath);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            cameraUri = Uri.fromFile(cameraSavePath);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
        startActivityForResult(intent, PHOTO);
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
            addImage(photoPath);
        } else if (requestCode == ALBUM && resultCode == RESULT_OK) {//相册
            photoPath = PhotoAlbumUtils.getRealPathFromUri(this, data.getData());
            addImage(photoPath);

        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    //动态添加图片
    private void addImage(String imagePath) {
        FileUtils.compressImage(imagePath);
        imagePathList.add(imagePath);
        ImageView imageView = new ImageView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(image_list.getWidth() / 3, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.rightMargin = 10;
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        image_list.addView(imageView, layoutParams);
        Glide.with(this).load(imagePath).into(imageView);
        image_count.setText(image_list.getChildCount() + "/3");
    }

    @Override
    public void showError(String errMsg) {
        Log.i(Constant.TAG, errMsg);
        loadingDialog.dismiss();
    }

    @Override
    public void complete() {
        loadingDialog.dismiss();
        this.finish();
    }

    @Override
    protected ProposalPresenter bindPresenter() {
        return new ProposalPresenter();
    }

    @Override
    public void upload(List<String> cover) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String image : cover) {
            stringBuilder.append(image + "philosopher");
        }
        mPresenter.submission(feed_back.getText().toString(), phone_number.getText().toString(), stringBuilder.toString());
    }
}
