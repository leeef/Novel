package com.bx.philosopher.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bx.philosopher.R;
import com.bx.philosopher.base.activity.BaseActivity;
import com.bx.philosopher.base.activity.BaseContract;
import com.bx.philosopher.model.bean.response.BaseResponse;
import com.bx.philosopher.net.BaseObserver;
import com.bx.philosopher.net.HttpUtil;
import com.bx.philosopher.net.RxScheduler;
import com.bx.philosopher.utils.StringUtils;
import com.bx.philosopher.utils.ToastUtils;
import com.bx.philosopher.utils.login.LoginUtil;

import java.text.MessageFormat;

import butterknife.BindView;

public class NoteEditActivity extends BaseActivity {

    @BindView(R.id.book_note_content)
    TextView book_note_content;
    @BindView(R.id.feed_back)
    EditText feed_back;
    @BindView(R.id.count_text)
    TextView count_text;
    @BindView(R.id.note_complete)
    LinearLayout note_complete;

    private String chapter_content;
    private String chapter;
    private int startSite;
    private int endSite;
    private int bookid;

    private int COUNT_LIMIT = 500;

    @Override
    protected int getContentId() {
        return R.layout.activity_note_edit;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        chapter_content = getIntent().getStringExtra("content");
        chapter = getIntent().getStringExtra("chapter");
        startSite = getIntent().getIntExtra("startSite", -1);
        endSite = getIntent().getIntExtra("endSite", -1);
        bookid = getIntent().getIntExtra("bookid", -1);
        if (StringUtils.isTrimEmpty(chapter_content)) chapter_content = "";
    }

    public static void startActivity(Context context, String content, int startSite, int endSite
            , int bookid, String chapter) {
        Intent intent = new Intent(context, NoteEditActivity.class);
        intent.putExtra("content", content);
        intent.putExtra("startSite", startSite);
        intent.putExtra("endSite", endSite);
        intent.putExtra("bookid", bookid);
        intent.putExtra("chapter", chapter);
        context.startActivity(intent);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        book_note_content.setText(chapter_content);
        note_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpUtil.getInstance().getRequestApi().setBookNote(LoginUtil.getUserId(), bookid, chapter
                        , startSite, endSite, chapter_content, feed_back.getText().toString())
                        .compose(RxScheduler.Obs_io_main())
                        .subscribe(new BaseObserver<BaseResponse<Boolean>>(new BaseContract.BaseView() {
                            @Override
                            public void showError(String msg) {

                            }

                            @Override
                            public void complete() {

                            }
                        }) {
                            @Override
                            public void onSuccess(BaseResponse<Boolean> o) {
                                if (o.getData()) {
                                    ToastUtils.show(o.getMsg());
                                    NoteEditActivity.this.finish();
                                }
                            }

                            @Override
                            public void onError(String msg) {

                            }
                        });
            }
        });
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
    }
}
