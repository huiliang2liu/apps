package com.base.widget;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;

import com.base.R;
import com.base.animation.ViewEmbellish;
import com.base.util.L;

public class RefreshView extends FrameLayout implements GestureDetector.OnGestureListener {
    private static final String TAG = "RefreshView";
    private static final long ANIMATOR_TIME = 200;
    private boolean load = false;
    private View mChildView;
    private LayoutParams childParams;
    private ViewEmbellish childEmbllish;
    private LoadView headView;
    private LayoutParams headParams;
    private int loadHeight;
    private GestureDetector detector;
    private ViewEmbellish headEmbllish;
    private LoadView footerView;
    private View header;
    private View footer;
    private LayoutParams footerParams;
    private ViewEmbellish footerEmbllish;
    private RefreshListener refreshListener;
    private ChangeListener changeListener;
    private int headerLayout;
    private int footerLayout;
    private int color;
    private int waveHeight;
    private LayoutInflater layoutInflater;
    private boolean embed = true;

    public RefreshView(@NonNull Context context) {
        super(context);
        init(null, 0);
    }

    public RefreshView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public RefreshView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    public RefreshView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyleAttr) {
        if (attrs != null) {
            TypedArray a = getResources().obtainAttributes(attrs, R.styleable.RefreshView);
            loadHeight = a.getDimensionPixelOffset(R.styleable.RefreshView_loadHeight, 50);
            detector = new GestureDetector(getContext(), this);
            waveHeight = a.getDimensionPixelOffset(R.styleable.RefreshView_waveHeight, loadHeight * 3 / 2);
            color = a.getColor(R.styleable.RefreshView_waveColor, Color.argb(0x80, 0, 0, 0));
            headerLayout = a.getResourceId(R.styleable.RefreshView_headerLayout, -1);
            footerLayout = a.getResourceId(R.styleable.RefreshView_footerLayout, -1);
            embed = a.getBoolean(R.styleable.RefreshView_embed, false);
            a.recycle();
        } else {
            loadHeight = 50;
            detector = new GestureDetector(getContext(), this);
            waveHeight = loadHeight * 3 / 2;
            color = Color.argb(0x80, 0, 0, 0);
            headerLayout = -1;
            footerLayout = -1;
            embed = false;
        }
        layoutInflater = LayoutInflater.from(getContext());
    }

    public void setRefreshListener(RefreshListener listener) {
        this.refreshListener = listener;
    }

    public void setChangeListener(ChangeListener listener) {
        this.changeListener = listener;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mChildView = getChildAt(0);
        childParams = (LayoutParams) mChildView.getLayoutParams();
        childEmbllish = new ViewEmbellish(mChildView);
        addHeaderView();
        addFooterView();
    }

    private void addHeaderView() {
        headView = new LoadView(getContext(), true);
        if (headerLayout > 0) {
            LayoutParams headParams = new LayoutParams(LayoutParams.MATCH_PARENT, 0);
            headParams.gravity = Gravity.CENTER_VERTICAL;
            header = layoutInflater.inflate(headerLayout, null);
            this.headView.addView(header, headParams);
        }
        this.headParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        addView(this.headView, this.headParams);
        headEmbllish = new ViewEmbellish(this.headView);
    }

    private void addFooterView() {
        footerView = new LoadView(getContext(), false);
        if (footerLayout > 0) {
            LayoutParams footerParams = new LayoutParams(LayoutParams.MATCH_PARENT, 0);
            footerParams.gravity = Gravity.CENTER_VERTICAL;
            footer = layoutInflater.inflate(footerLayout, null);
            this.footerView.addView(footer, footerParams);
        }
        this.footerParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        this.footerParams.gravity = Gravity.BOTTOM;
        addView(this.footerView, this.footerParams);
        footerEmbllish = new ViewEmbellish(this.footerView);
    }

    private float mTouchY;
    private float nextY;
    private float mTouchX;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        L.d(TAG, String.format("onInterceptTouchEvent:%d", ev.getAction()));
        if (load)
            return true;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchY = ev.getY();
                mTouchX = ev.getX();
                nextY = mTouchY;
                break;
            case MotionEvent.ACTION_MOVE:
                float mX = ev.getX() - mTouchX;
                float mY = ev.getY() - mTouchY;
                mX = Math.abs(mX);
                mY = Math.abs(mY);
                if (mX < 1 && mY < 1)//判断是否滑动
                    return false;
                if (Math.abs(mX) > Math.abs(mY))//判断横向滑动比竖向滑动大
                    return false;
                return true;
        }
        return false;
    }


    public void stopLoad() {
        load = false;
        if (headParams.height != 0) {
            if (embed) {
                ObjectAnimator animator1 = headEmbllish.heightAnimator(ANIMATOR_TIME, headParams.height, 0);
                ObjectAnimator animator = childEmbllish.topMarginAnimator(ANIMATOR_TIME, childParams.topMargin, 0);
                AnimatorSet set = new AnimatorSet();
                AnimatorSet.Builder builder = set.play(animator1);
                builder.with(animator);
                set.start();
            } else
                headEmbllish.height(ANIMATOR_TIME, headParams.height, 0);
            if (header != null && header instanceof ChangeListener)
                ((ChangeListener) header).onLoaded();
        } else if (footerParams.height != 0) {
            if (embed) {
                ObjectAnimator animator1 = footerEmbllish.heightAnimator(ANIMATOR_TIME, footerParams.height, 0);
                ObjectAnimator animator = childEmbllish.translationYAnimator(ANIMATOR_TIME, -footerParams.height, 0);
                AnimatorSet set = new AnimatorSet();
                AnimatorSet.Builder builder = set.play(animator1);
                builder.with(animator);
                set.start();
            } else
                footerEmbllish.height(ANIMATOR_TIME, footerParams.height, 0);
            if (footer != null && footer instanceof ChangeListener)
                ((ChangeListener) footer).onLoaded();
        }

        if (changeListener != null)
            changeListener.onLoaded();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (load)
            return true;
        if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
            if (headParams.height != 0) {
                if (headParams.height > loadHeight && refreshListener != null) {
                    load = true;
                    refreshListener.onRefresh();
                    if (changeListener != null)
                        changeListener.onLoad();
                    if (header != null && header instanceof ChangeListener)
                        ((ChangeListener) header).onLoad();
                    if (embed) {
                        ObjectAnimator animator1 = headEmbllish.heightAnimator(ANIMATOR_TIME, headParams.height, loadHeight);
                        ObjectAnimator animator = childEmbllish.topMarginAnimator(ANIMATOR_TIME, childParams.topMargin, loadHeight);
                        AnimatorSet set = new AnimatorSet();
                        AnimatorSet.Builder builder = set.play(animator1);
                        builder.with(animator);
                        set.start();
                    } else
                        headEmbllish.height(ANIMATOR_TIME, headParams.height, loadHeight);
                } else {
                    if (embed) {
                        ObjectAnimator animator1 = headEmbllish.heightAnimator(ANIMATOR_TIME, headParams.height, 0);
                        ObjectAnimator animator = childEmbllish.topMarginAnimator(ANIMATOR_TIME, childParams.topMargin, 0);
                        AnimatorSet set = new AnimatorSet();
                        AnimatorSet.Builder builder = set.play(animator1);
                        builder.with(animator);
                        set.start();
                    } else
                        headEmbllish.height(ANIMATOR_TIME, headParams.height, 0);
                }

            } else if (footerParams.height != 0) {
                if (footerParams.height > loadHeight && refreshListener != null) {
                    load = true;
                    refreshListener.onLoadMore();
                    if (changeListener != null)
                        changeListener.onLoad();
                    if (footer != null && footer instanceof ChangeListener)
                        ((ChangeListener) footer).onLoad();
                    if (embed) {
                        ObjectAnimator animator1 = footerEmbllish.heightAnimator(ANIMATOR_TIME, footerParams.height, loadHeight);
                        ObjectAnimator animator = childEmbllish.translationYAnimator(ANIMATOR_TIME, -footerParams.height, -loadHeight);
                        AnimatorSet set = new AnimatorSet();
                        AnimatorSet.Builder builder = set.play(animator1);
                        builder.with(animator);
                        set.start();
                    } else
                        footerEmbllish.height(ANIMATOR_TIME, footerParams.height, loadHeight);
                } else {
                    if (embed) {
                        ObjectAnimator animator1 = footerEmbllish.heightAnimator(ANIMATOR_TIME, footerParams.height, 0);
                        ObjectAnimator animator = childEmbllish.translationYAnimator(ANIMATOR_TIME, -footerParams.height, 0);
                        AnimatorSet set = new AnimatorSet();
                        AnimatorSet.Builder builder = set.play(animator1);
                        builder.with(animator);
                        set.start();
                    } else
                        footerEmbllish.height(ANIMATOR_TIME, footerParams.height, 0);
                }

            }
            if (mChildView != null)
                mChildView.onTouchEvent(event);
        }
        return detector.onTouchEvent(event);
    }


    public boolean canChildScrollUp() {
        if (mChildView == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT < 14) {
            if (mChildView instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mChildView;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            } else {
                return ViewCompat.canScrollVertically(mChildView, -1) || mChildView.getScrollY() > 0;
            }
        } else {
            return ViewCompat.canScrollVertically(mChildView, -1);
        }
    }

    public boolean canChildScrollDown() {
        if (mChildView == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT < 14) {
            if (mChildView instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mChildView;
                if (absListView.getChildCount() > 0) {
                    int lastChildBottom = absListView.getChildAt(absListView.getChildCount() - 1).getBottom();
                    return absListView.getLastVisiblePosition() == absListView.getAdapter().getCount() - 1 && lastChildBottom <= absListView.getMeasuredHeight();
                } else {
                    return false;
                }

            } else {
                return ViewCompat.canScrollVertically(mChildView, 1) || mChildView.getScrollY() > 0;
            }
        } else {
            return ViewCompat.canScrollVertically(mChildView, 1);
        }
    }

    @Override
    public boolean onDown(MotionEvent e) {
//        L.d(TAG, "onDown");
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
//        L.d(TAG, "点击");
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//        L.d(TAG, "滑动");
        float dy = nextY - e2.getY();
        if (dy > 0) {
            if (canChildScrollDown()) {
                mChildView.onTouchEvent(e2);
                nextY = e2.getY();
            } else {
                footerParams.height = (int) (dy);
                footerView.requestLayout();
            }
        } else if (dy < 0) {
            if (canChildScrollUp()) {
                nextY = e2.getY();
                mChildView.onTouchEvent(e2);
            } else {
                headParams.height = (int) (-dy);
                headView.requestLayout();
            }

        }
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    private class LoadView extends FrameLayout {

        private ViewGroup.LayoutParams params;
        private WaveView waveView;
        private boolean change = false;
        private boolean isHeader = false;

        public LoadView(@NonNull Context context, boolean header) {
            super(context);
            waveView = new WaveView(context, header);
            isHeader = header;
            addView(waveView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }

        @Override
        public void addView(View child, ViewGroup.LayoutParams params) {
            super.addView(child, params);
            if (!(child instanceof WaveView)) {
                this.params = params;
                waveView.loadHeight = loadHeight;
            }
        }

        @Override
        public void requestLayout() {
            ViewGroup.LayoutParams layoutParams = getLayoutParams();
            if (layoutParams != null && params != null) {
                if (layoutParams.height < loadHeight) {
                    params.height = layoutParams.height;
                    if (change) {
                        change = false;
                        if (isHeader) {
                            if (header != null && header instanceof ChangeListener)
                                ((ChangeListener) header).onChange(change);
                        } else {
                            if (footer != null && footer instanceof ChangeListener)
                                ((ChangeListener) footer).onChange(change);
                        }
                    }
                } else {
                    params.height = loadHeight;
                    if (layoutParams.height > loadHeight + waveHeight)
                        layoutParams.height = loadHeight + waveHeight;
                    if (!change) {
                        change = true;
                        if (isHeader) {
                            if (header != null && header instanceof ChangeListener)
                                ((ChangeListener) header).onChange(change);
                        } else {
                            if (footer != null && footer instanceof ChangeListener)
                                ((ChangeListener) footer).onChange(change);
                        }
                    }
                }
            }
            if (embed && childParams != null) {
                if (isHeader) {
                    if (layoutParams != null)
                        childParams.topMargin = layoutParams.height;
                } else {
                    if (layoutParams != null) {
                        mChildView.setTranslationY(-layoutParams.height);
                    }

                }
            }
            super.requestLayout();

        }
    }

    private class WaveView extends View {
        private Path path;
        private Paint paint;
        private boolean header;
        private int loadHeight = 20;

        public WaveView(@NonNull Context context, boolean header) {
            super(context);
            path = new Path();
            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStrokeWidth(6);
            this.header = header;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            path.reset();
            paint.setColor(color);
            if (header) {
                path.lineTo(0, loadHeight);
                path.quadTo(getMeasuredWidth() / 2, 2 * getMeasuredHeight() - loadHeight, getMeasuredWidth(), loadHeight);
                path.lineTo(getMeasuredWidth(), 0);
                canvas.drawPath(path, paint);
            } else {
                path.moveTo(0, getMeasuredHeight());
                path.lineTo(0, getMeasuredHeight() - loadHeight);
                path.quadTo(getMeasuredWidth() / 2, loadHeight - getMeasuredHeight(), getMeasuredWidth(), getMeasuredHeight() - loadHeight);
                path.lineTo(getMeasuredWidth(), getMeasuredHeight());
                canvas.drawPath(path, paint);
            }
        }
    }

    public interface RefreshListener {
        void onRefresh();

        void onLoadMore();
    }

    public interface ChangeListener {

        void onLoaded();

        void onLoad();

        void onChange(boolean load);
    }
}
