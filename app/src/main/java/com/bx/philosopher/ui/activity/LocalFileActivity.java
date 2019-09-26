package com.bx.philosopher.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.bx.philosopher.R;
import com.bx.philosopher.base.activity.BaseActivity;
import com.bx.philosopher.base.adapter.BaseListAdapter;
import com.bx.philosopher.model.bean.daobean.CollBookBean;
import com.bx.philosopher.ui.adapter.LocalFileAdapter;
import com.bx.philosopher.utils.Constant;
import com.bx.philosopher.utils.FileUtils;
import com.bx.philosopher.utils.MD5Utils;
import com.bx.philosopher.utils.PermissionUtils;
import com.bx.philosopher.utils.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import pub.devrel.easypermissions.EasyPermissions;

public class LocalFileActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks, View.OnClickListener {
    @BindView(R.id.book_list)
    RecyclerView book_list;
    @BindView(R.id.confirm_button)
    LinearLayout confirm_button;

    private LocalFileAdapter localFileAdapter;

    //权限
    private String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected int getContentId() {
        return R.layout.activity_local_file;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);

    }

    @Override
    protected void initWidget() {
        super.initWidget();
        localFileAdapter = new LocalFileAdapter();
        book_list.setLayoutManager(new LinearLayoutManager(this));
        book_list.setAdapter(localFileAdapter);
        getPermission();
        localFileAdapter.setOnItemClickListener(new BaseListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {
                localFileAdapter.setCheckedItem(pos);
            }
        });
    }


    //获取权限
    private void getPermission() {
        if (!EasyPermissions.hasPermissions(this, permissions)) {
            //没有打开相关权限、申请权限
            PermissionUtils.requestPermissions(this, "Access to read and write memory CARDS is required", 1, permissions);
        } else {
            List<File> temp = new ArrayList<>();
            temp = FileUtils.getSupportFileList(this, new String[]{"txt"});
            Collections.sort(temp, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    if (o1.length() > o2.length()) {
                        return -1;
                    } else if (o1.length() < o2.length()) {
                        return 1;
                    }
                    return 0;
                }
            });
            localFileAdapter.addItems(temp);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults,this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }

    @OnClick(R.id.confirm_button)
    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.confirm_button:
                //转换成CollBook,并存储
//                List<CollBookBean> collBooks = convertCollBook(localFileAdapter.getCheckedFiles());
////                BookDaoManager.getInstance()
////                        .saveCollBooks(collBooks);
//                intent = new Intent(this,ReadingActivity.class);
//                intent.putExtra(ReadingActivity.EXTRA_COLL_BOOK, BookDaoManager.getInstance().getCollBook(collBooks.get(0).get_id()));
//                startActivity(intent);
                break;
        }
    }


    /**
     * 将文件转换成CollBook
     *
     * @param files:需要加载的文件列表
     * @return
     */
    private List<CollBookBean> convertCollBook(List<File> files) {
        List<CollBookBean> collBooks = new ArrayList<>(files.size());
        for (File file : files) {
            //判断文件是否存在
            if (!file.exists()) continue;

            CollBookBean collBook = new CollBookBean();
            collBook.set_id(MD5Utils.strToMd5By16(file.getAbsolutePath()));
            collBook.setTitle(file.getName().replace(".txt", ""));
            collBook.setAuthor("");
            collBook.setShortIntro("无");
            collBook.setCover(file.getAbsolutePath());
            collBook.setLocal(true);
            collBook.setLastChapter("开始阅读");
            collBook.setUpdated(StringUtils.dateConvert(file.lastModified(), Constant.FORMAT_BOOK_DATE));
            collBook.setLastRead(StringUtils.
                    dateConvert(System.currentTimeMillis(), Constant.FORMAT_BOOK_DATE));
            collBooks.add(collBook);
        }
        return collBooks;
    }

}
