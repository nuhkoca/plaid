/*
 * Copyright 2015 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.plaidapp.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import io.plaidapp.R;
import io.plaidapp.util.FontUtil;

/**
 * Created by nickbutcher on 12/16/14.
 */
public class CutoutTextView extends View {

    private final TextPaint textPaint;
    private Bitmap cutout;
    private int foregroundColor = 0xfffafafa;
    private String text;
    private float textSize;
    private float textY;
    private float textX;

    public CutoutTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.SUBPIXEL_TEXT_FLAG);

        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable
                .CutoutTextView, 0, 0);
        if (a.hasValue(R.styleable.CutoutTextView_font)) {
            textPaint.setTypeface(FontUtil.get(context, a.getString(R.styleable
                    .CutoutTextView_font)));
        }
        if (a.hasValue(R.styleable.CutoutTextView_android_foreground)) {
            foregroundColor = a.getColor(R.styleable.CutoutTextView_android_foreground,
                    foregroundColor);
        }
        if (a.hasValue(R.styleable.CutoutTextView_android_text)) {
            text = a.getString(R.styleable.CutoutTextView_android_text);
        }
        a.recycle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        calculateTextPosition();
        createBitmap();
    }

    private void calculateTextPosition() {
        float targetWidth = getWidth() / 1.6182f;
        textSize = getTextSize(targetWidth);
        textPaint.setTextSize(textSize);

        // measuring text is fun :] see: https://chris.banes.me/2014/03/27/measuring-text/
        textX = (getWidth() - textPaint.measureText(text)) / 2;
        Rect textBounds = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), textBounds);
        float textHeight = textBounds.height();
        textY = (getHeight() + textHeight) / 2; // note that when drawing text the 'Y' co-ord is
        // the bottom!?!
    }

    private float getTextSize(float targetWidth) {

        // todo calculate best text size for the given width
        return 392f;
    }

    private void createBitmap() {
        if (cutout != null && !cutout.isRecycled()) {
            cutout.recycle();
        }
        cutout = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        cutout.eraseColor(foregroundColor);
        cutout.setHasAlpha(true);
        Canvas cutoutCanvas = new Canvas(cutout);

        // this is the magic – Clear mode punches out the bitmap
        textPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        cutoutCanvas.drawText(text, textX, textY, textPaint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(cutout, 0, 0, null);
    }

    @Override
    public boolean hasOverlappingRendering() {
        return true;
    }
}