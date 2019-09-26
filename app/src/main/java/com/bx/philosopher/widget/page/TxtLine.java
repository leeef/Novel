package com.bx.philosopher.widget.page;

import java.util.ArrayList;
import java.util.List;


/*
 *阅读页面 记录每行的文字的实体
 */
public class TxtLine {
    private int CurrentIndex;
    private List<TxtChar> chars = null;
    private boolean isParagraphEndLine = false;

    public TxtLine() {
    }

    public void addChar(TxtChar txtChar) {
        if (chars == null) {
            chars = new ArrayList<>();
        }
        chars.add(txtChar);

    }

    public void setParagraphEndLine(boolean paragraphEndLine) {
        isParagraphEndLine = paragraphEndLine;
    }

    public List<TxtChar> getTxtChars() {
        return chars;
    }


    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (TxtChar txtChar : chars) {
            stringBuilder.append(txtChar.Char);
        }
        return stringBuilder.toString();
    }
}
