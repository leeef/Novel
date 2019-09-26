package com.bx.philosopher.widget.page;

import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 *
 */

public class DefaultLeftSlider extends Slider {
    @Override
    public float getX(float dx) {
        return Right + dx ;
    }

    @Override
    public float getY(float dy) {
        return Top + dy ;
    }

    @Override
    public Path getPath(TxtChar txtChar, Path path) {
        if (txtChar != null) {
            Path p = path;
            p.reset();
            p.moveTo(txtChar.Left, txtChar.Bottom);
            p.lineTo(txtChar.Left, txtChar.Bottom + SliderWidth);
            Rect rect = new Rect(txtChar.Left - SliderWidth * 2, txtChar.Bottom, txtChar.Left, txtChar.Bottom + SliderWidth * 2);
            p.addArc(new RectF(rect), 0, 270);
            p.lineTo(txtChar.Left, txtChar.Bottom);
            return p;
        } else {
            return null;
        }
    }

}
