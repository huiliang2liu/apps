package com.electricity;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;

public class TextView extends android.widget.TextView {
    int width = 0;
    int height = 0;
    private Paint mPaint = this.getPaint();
    private Rect mBounds = new Rect();
    private LinearGradient mLinearGradient;
    private Matrix mGradientMatrix;
    private int mTranslate;
    Paint.FontMetricsInt fontMetricsInt;
    Boolean bRunText = false;
    Boolean bRemoveDefaultPadding = false;
    String fontPath = null;
    Boolean bGradient = false;
    int gradientStartColor;
    int gradientCenterColor;
    int gradietendColor;
    Typeface typeface;

    {
        Log.e("textView", "======");
    }

    public TextView(Context context) {
        super(context);
    }

    public TextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initAttributes(context, attrs);
    }

    public TextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initAttributes(context, attrs);
    }

    protected void onDraw(Canvas canvas) {
        this.init();
        this.option(canvas);
        this.drawText(canvas);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.width = MeasureSpec.getSize(widthMeasureSpec);
        this.height = MeasureSpec.getSize(heightMeasureSpec);
        if (this.bRemoveDefaultPadding) {
            this.calculateTextParams();
            this.setMeasuredDimension(this.mBounds.right - this.mBounds.left, -this.mBounds.top + this.mBounds.bottom);
        }

    }

    private void init() {
        this.mLinearGradient = new LinearGradient(0.0F, 0.0F, (float) this.getMeasuredWidth(), 0.0F, new int[]{this.gradientStartColor, this.gradientCenterColor, this.gradietendColor}, (float[]) null, Shader.TileMode.MIRROR);
        this.mGradientMatrix = new Matrix();
        Log.e("TextView", "gradientStartColor:" + this.gradientStartColor + "gradietendColor:" + this.gradietendColor);
    }

    private void initAttributes(Context context, AttributeSet attrs) {
//        TypedArray ta = context.obtainStyledAttributes(attrs, styleable.MultiplTextView);
//        this.bRunText = ta.getBoolean(styleable.MultiplTextView_runText, false);
//        this.bGradient = ta.getBoolean(styleable.MultiplTextView_gradient, false);
//        this.bRemoveDefaultPadding = ta.getBoolean(styleable.MultiplTextView_removeDefaultPadding, false);
//        this.gradientStartColor = ta.getColor(styleable.MultiplTextView_gradientStartColor, this.getCurrentTextColor());
//        this.gradientCenterColor = ta.getColor(styleable.MultiplTextView_gradientCenterColor, this.getCurrentTextColor());
//        this.gradietendColor = ta.getColor(styleable.MultiplTextView_gradientEndColor, -16777216);
//        this.fontPath = ta.getString(styleable.MultiplTextView_textFont);
//        if (!TextUtils.isEmpty(this.fontPath)) {
//            this.typeface = Typeface.createFromAsset(this.getResources().getAssets(), this.fontPath);
//            this.mPaint.setTypeface(this.typeface);
//        }
//
//        ta.recycle();
    }

    private void option(Canvas canvas) {
        if (this.bGradient) {
            this.mPaint.setShader(this.mLinearGradient);
        }

        if (this.bRunText) {
            this.runText();
        }

    }

    public void runText() {
        if (this.mGradientMatrix != null) {
            this.mTranslate += this.width / 5;
            if (this.mTranslate > 2 * this.width) {
                this.mTranslate = -this.width;
            }

            this.mGradientMatrix.setTranslate((float) this.mTranslate, 0.0F);
            this.mLinearGradient.setLocalMatrix(this.mGradientMatrix);
            this.postInvalidateDelayed(100L);
        }

    }

    private String calculateTextParams() {
        String text = this.getText().toString();
        int textLength = text.length();
        this.mPaint.getTextBounds(text, 0, textLength, this.mBounds);
        if (textLength == 0) {
            this.mBounds.right = this.mBounds.left;
        }

        return text;
    }

    private void drawText(Canvas canvas) {
        String text = this.calculateTextParams();
        int left = this.mBounds.left;
        int bottom = this.mBounds.bottom;
        this.mBounds.offset(-this.mBounds.left, -this.mBounds.top);
        this.mPaint.setAntiAlias(true);
        this.mPaint.setColor(this.getCurrentTextColor());
        canvas.drawText(text, (float) (-left), (float) (this.mBounds.bottom - bottom), this.mPaint);
    }
}
