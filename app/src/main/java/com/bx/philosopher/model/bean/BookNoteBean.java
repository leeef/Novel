package com.bx.philosopher.model.bean;

import com.bx.philosopher.model.bean.response.BaseBean;

public class BookNoteBean extends BaseBean {
    private String note_id;
    private String note_chapter;
    private String note_type;//笔记还是下划线
    private String note;//笔记内容
    private String note_content;//笔记或下划线的章节内容


    public String getNote_type() {
        return note_type;
    }

    public String getNote_id() {
        return note_id;
    }

    public void setNote_id(String note_id) {
        this.note_id = note_id;
    }

    public String getNote_chapter() {
        return note_chapter;
    }

    public void setNote_chapter(String note_chapter) {
        this.note_chapter = note_chapter;
    }

    public void setNote_type(String note_type) {
        this.note_type = note_type;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getNote_content() {
        return note_content;
    }

    public void setNote_content(String note_content) {
        this.note_content = note_content;
    }

    public BookNoteBean(int viewType) {
        super(viewType);
    }
}
