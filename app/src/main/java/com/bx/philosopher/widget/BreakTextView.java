package com.bx.philosopher.widget;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import androidx.appcompat.widget.AppCompatTextView;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.bx.philosopher.utils.StringUtils;

/**
 * 单个单词可以显示在两行的TextView 单词自动加连字符（中英文混合连字符不行 没做判断）
 */
public class BreakTextView extends AppCompatTextView {
    private int mLineY;

    private int mViewWidth;

    public static final String TWO_CHINESE_BLANK = "  ";


    private StringBuffer mText;
    private StringBuffer newText = null;
    private Paint mPaint;
    /**
     * VIEW的高度
     */
    private int mHeight = 0;
    /**
     * 行高
     */
    private static final int LINE_HEIGHT = 40;
    private int oneLine;//一行显示文字个数

    private int number_of_words;//显示的字数

    public BreakTextView(Context context) {
        super(context);
    }

    public BreakTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BreakTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }


    @Override
    protected void onDraw(Canvas canvas) {


        String text = getText().toString();// 获取文本内容
        if (StringUtils.isEmpty(text)) return;
        if (null == mText) {//以单例模式对文字进行拆分
            mText = new StringBuffer(text);
            TextPaint paint = getPaint();//获取画笔
            paint.setColor(getCurrentTextColor());// 获取文字颜色将其设置到画笔上
            paint.setTextSize(getTextSize());//设置文字大小
            paint.setTypeface(getTypeface());//设置字体，包括字体的类型，粗细，还有倾斜、颜色等
            paint.drawableState = getDrawableState();
            mViewWidth = getMeasuredWidth();//获取填写字数的宽
            mPaint = paint;
            caculateChangeLine();//对文字进行分行处理
        }
        mLineY = getPaddingTop();//设置头部内边距
        mLineY += getTextSize();
        Layout layout = getLayout();//避免出现空视图

        if (layout == null) {
            return;
        }
        Paint.FontMetrics fm = mPaint.getFontMetrics();

        int textHeight = (int) (Math.ceil(fm.descent - fm.ascent));
        textHeight = (int) (textHeight * layout.getSpacingMultiplier() + layout
                .getSpacingAdd());//获取文字的高度

        String[] split = newText.toString().split("\n");//将分割好滴文字进行排版
        if (null != split && split.length > 0) {//此处设置文本显示的高度，适配一些手机无法显示
            int i = (split.length) + 0;//多设置了几行以避免显示不全(看情况进行修改)
            int setheight = textHeight * i;
            setHeight(setheight);//设置textview高度
        }

        for (int i = 0; i < split.length; i++) {
            //此处为源例子上的写法，标点符号换行问题还是存在（楼主引用，ToDBC(aaa)的方法进行了修改，已解决这个bug）
//                layout.getLineCount()//获取显示的行数
//                int lineStart = layout.getLineStart(i);
//                int lineEnd = layout.getLineEnd(i);//获取每行要显示的字数
            String string = split[i];
            float width = StaticLayout.getDesiredWidth(string, 0,
                    string.length(), getPaint());

            if (null == string || TextUtils.isEmpty(string)) {
                continue;
            }
            int strWidth = (int) mPaint.measureText(string + "好好");//验证是否足够一个屏幕的宽度
            if (needScale(string) && string.trim().length() > number_of_words - 5 && mViewWidth < strWidth) {
                //判断是否足够一行显示的字数，足够久进行字的处理不够则直接画出来
                //，避免出现字数不够，字间距被画出来的字间距过大影响排版
                // 判断是否结尾处需要换行，并且不是文本最后一行
                drawScaledText(canvas, getPaddingLeft(), split[i], width, i);
            } else {

                canvas.drawText(split[i], getPaddingLeft(), mLineY, mPaint);// 将字符串直接画到控件上
            }
            mLineY += textHeight;
        }


    }

    /**
     * @Description:计算出一行显示的文字
     */
    private String caculateOneLine(String str) {

        if (str.charAt(0) == 32) str = str.substring(1);//去除首行空格
        //对一段没有\n的文字进行换行
        String returnStr = "";
        int strWidth = (int) mPaint.measureText(str);
        int len = str.length();
        int lineNum = strWidth / mViewWidth; //大概知道分多少行
        int tempWidth = 0;
        String lineStr;
        int returnInt = 0;
        if (lineNum == 0) {
            returnStr = str;
            mHeight += LINE_HEIGHT;
            return returnStr;
        } else {

            oneLine = len / (lineNum + 1);    //一行大概有多少个字
            if (number_of_words < oneLine) {
                number_of_words = oneLine;
            }

            lineStr = str.substring(0, oneLine);
            tempWidth = (int) mPaint.measureText(lineStr);


            if (tempWidth < mViewWidth) { //如果小了 找到大的那个
                while (tempWidth < mViewWidth) {
                    oneLine++;
                    lineStr = str.substring(0, oneLine);
                    tempWidth = (int) mPaint.measureText(lineStr);
                }
                returnInt = oneLine - 1;
                returnStr = lineStr.substring(0, lineStr.length() - 2);
                try {
                    if (lineStr.charAt(lineStr.length() - 3) != 32 && lineStr.charAt(lineStr.length() - 2) != 32) {
                        //本身和下一个字符不是空格说明是把单词分隔开了
                        returnStr = returnStr + "-";
                    }
                } catch (Exception e) {

                }


            } else {//大于宽找到小的

                while (tempWidth > mViewWidth) {
                    oneLine--;
                    lineStr = str.substring(0, oneLine);
                    tempWidth = (int) mPaint.measureText(lineStr);
                }
                returnStr = lineStr.substring(0, lineStr.length() - 1);
                try {
                    if (lineStr.charAt(lineStr.length() - 2) != 32 && lineStr.charAt(lineStr.length() - 1) != 32) {
                        //本身和下一个字符不是空格说明是把单词分隔开了
                        returnStr = returnStr + "-";
                    }
                } catch (Exception e) {

                }

                returnInt = oneLine;
            }
            mHeight += LINE_HEIGHT;
            returnStr += "\n" + caculateOneLine(str.substring(returnInt - 1));
        }
        return returnStr;
    }

    public void caculateChangeLine() {
        newText = new StringBuffer();
        String tempStr[] = mText.toString().split("\n");
        int len = tempStr.length;
        for (int i = 0; i < len; i++) {
            String caculateOneLine = caculateOneLine(tempStr[i]);
            if (!TextUtils.isEmpty(caculateOneLine)) {
                newText.append(caculateOneLine);
                newText.append("\n");
            }

        }
        this.setHeight(mHeight);
    }


    private void drawScaledText(Canvas canvas, int lineStart, String line,
                                float lineWidth, int currentline) {
        float x = 0;
        if (isFirstLineOfParagraph(lineStart, line)) {// 判断是否是第一行
            canvas.drawText(TWO_CHINESE_BLANK, x, mLineY, getPaint());
            float bw = StaticLayout.getDesiredWidth(TWO_CHINESE_BLANK, getPaint());
            x += bw;

            line = line.substring(3);
        }
        int gapCount = line.length() - 1;
        int i = 0;
        if (line.length() > 2 && line.charAt(0) == 12288
                && line.charAt(1) == 12288) {
            String substring = line.substring(0, 2);
            float cw = StaticLayout.getDesiredWidth(substring, getPaint());
            canvas.drawText(substring, x, mLineY, getPaint());
            x += cw;
            i += 2;
        }

        float d = (mViewWidth - lineWidth) / gapCount;
        for (; i < line.length(); i++) {
            String c = String.valueOf(line.charAt(i));
            float cw = StaticLayout.getDesiredWidth(c, getPaint());
            canvas.drawText(c, x, mLineY, getPaint());
            x += cw + d;
        }
    }

    private boolean isFirstLineOfParagraph(int lineStart, String line) {
        return line.length() > 3 && line.charAt(0) == ' '
                && line.charAt(1) == ' ';
    }

    private boolean needScale(String line) {// 判断是否需要换行
        if (line == null || line.length() == 0) {
            return false;
        } else {
            char charAt = line.charAt(line.length() - 1);
            return charAt != '\n';
        }
    }
}

