package com.screen;

import android.app.UiModeManager;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.screen.iface.CancelAdapt;
import com.screen.iface.TextStyle;

import org.xmlpull.v1.XmlPullParser;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * User：liuhuiliang
 * Date：2019-12-20
 * Time：12:12
 * Descripotion：自定义LayoutInflater
 */
public class AppLayoutInflater extends LayoutInflater {
    private static final String TAG = "AppLayoutInflater";
    private static Method onCreateView;
    private static Method getStates;
    private static int state_focused = -1;
    private ScreenManager manager;
    private LayoutInflater systemLayoutInflater;

    static {
        Class cls = LayoutInflater.class;
        try {
            onCreateView = cls.getDeclaredMethod("onCreateView", String.class, AttributeSet.class);
            onCreateView.setAccessible(true);
        } catch (Exception e) {
        }
        try {
            getStates = ColorStateList.class.getDeclaredMethod("getStates");
            getStates.setAccessible(true);

        } catch (Exception e) {
        }
        try {
            cls = Class.forName("android.R$attr");
            Field field = cls.getDeclaredField("state_focused");
            field.setAccessible(true);
            state_focused = field.getInt(null);
        } catch (Exception e) {
        }
    }

    private static final String[] sClassPrefixList = {
            "android.widget.",
            "android.webkit.",
            "android.app."
    };
    private Object activity;
    private float scale = 1;
    private boolean px = false;

    {
        manager = ScreenManager.getInstance(getContext());
        systemLayoutInflater = manager.systemLayoutInflater.cloneInContext(getContext());
        super.setFactory2(new Factory2() {
            @Override
            public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
                try {
                    return AppLayoutInflater.this.onCreateView(parent, name, attrs);
                } catch (ClassNotFoundException e) {
                    return null;
                }
            }

            @Override
            public View onCreateView(String name, Context context, AttributeSet attrs) {
                try {
                    return AppLayoutInflater.this.onCreateView(name, attrs);
                } catch (ClassNotFoundException e) {
                    return null;
                }
            }
        });
    }

    protected AppLayoutInflater(Context activity) {
        super(activity);
        this.activity = activity;
        px = ScreenManager.getInstance(getContext()).px;
    }

    protected AppLayoutInflater(LayoutInflater original, Context newContext) {
        super(original, newContext);
    }

    public static AppLayoutInflater form(Context context) {
        return new AppLayoutInflater(context);
    }


    @Override
    public void setFactory(Factory factory) {
//        super.setFactory(factory);
    }

    @Override
    public void setFactory2(Factory2 factory) {
//        super.setFactory2(factory);
    }

    public void setPrivateFactory(Factory2 factory) {
//        if (mPrivateFactory == null) {
//            mPrivateFactory = factory;
//        } else {
//            mPrivateFactory = new FactoryMerger(factory, factory, mPrivateFactory, mPrivateFactory);
//        }
    }

    public void setScale(float scale) {
        this.scale = scale;
        Log.e(TAG, String.format("scale:%f", scale));
    }


    @Override
    public View inflate(XmlPullParser parser, ViewGroup root, boolean attachToRoot) {
        View view = super.inflate(parser, root, attachToRoot);
        if (!(activity instanceof CancelAdapt)) {
            if (px)
                zView(view);
        }
        return view;
    }

    private void zView(View view) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        setParams(layoutParams);
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++)
                zView(group.getChildAt(i));
        }
    }

    /**
     * description：重新设置布局大小，只有设置了大小的才可以缩放
     */
    private void setParams(ViewGroup.LayoutParams params) {
        if (params == null)
            return;
        if (params.width > 0)
            params.width = (int) (params.width * scale);
        if (params.height > 0) {
            params.height = (int) (params.height * scale);
        }
        if (params instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) params;
            mlp.topMargin = (int) (mlp.topMargin * scale);
            mlp.leftMargin = (int) (mlp.leftMargin * scale);
            mlp.rightMargin = (int) (mlp.rightMargin * scale);
            mlp.bottomMargin = (int) (mlp.bottomMargin * scale);
        }
    }

    @Override
    protected View onCreateView(View parent, String name, AttributeSet attrs) throws ClassNotFoundException {
        return super.onCreateView(parent, name, attrs);
    }

    @Override
    protected View onCreateView(String name, AttributeSet attrs) throws ClassNotFoundException {
        View view = null;
        try {
            view = (View) onCreateView.invoke(systemLayoutInflater, name, attrs);
        } catch (Exception e) {
        }
        if (view == null)
            if (ScreenManager.getInstance(getContext()).widget != null && ScreenManager.getInstance(getContext()).widget.length > 0)
                for (String prefix : ScreenManager.getInstance(getContext()).widget) {
                    try {
                        view = createView(name, prefix, attrs);
                        if (view != null) {
                            break;
                        }
                    } catch (ClassNotFoundException e) {
                        // In this case we want to let the base class take a crack
                        // at it.
                    }
                }
        if (view == null)
            for (String prefix : sClassPrefixList) {
                try {
                    view = createView(name, prefix, attrs);
                    if (view != null) {
                        break;
                    }
                } catch (ClassNotFoundException e) {
                    // In this case we want to let the base class take a crack
                    // at it.
                }
            }
        if (view == null) {
            view = super.onCreateView(name, attrs);
        }
        if (!(activity instanceof CancelAdapt)) {
            zoom(view);
        }
        paseAttrs(view, attrs);
        Drawable drawable = view.getBackground();
        if (drawable instanceof StateListDrawable) {
            view.setFocusable(true);
            view.setClickable(true);
        }
        return view;
    }



    /**
     * description：解析全局自定义属性，别设置是否可获取焦点和点击属性
     */
    private void paseAttrs(View view, AttributeSet attrs) {
        TypedArray a = getContext().getResources().obtainAttributes(attrs, R.styleable.Screen);
        if (view instanceof TextView) {
            TextView textView = (TextView) view;
            if (textView.getEllipsize() == null)
                textView.setEllipsize(manager.ellipsize);
            String textStyle = a.getString(R.styleable.Screen_textStyle);
            manager.setTextStyle(textView, textStyle);
            //字体大小适配不好，需要调
            if (a.getBoolean(R.styleable.Screen_chinese, false)) {
                Paint.FontMetricsInt fontMetricsInt = textView.getPaint().getFontMetricsInt();
                int top = (int) Math.ceil(Math.abs((fontMetricsInt.top - fontMetricsInt.ascent) / 2.0));
                textView.setPadding(textView.getPaddingLeft(), textView.getPaddingTop() - (Math.abs(fontMetricsInt.top - fontMetricsInt.ascent))
                        , textView.getPaddingRight(),
                        textView.getPaddingBottom() + fontMetricsInt.top - fontMetricsInt.ascent);
            }
            ColorStateList colorStateList = textView.getTextColors();
            try {
                if (containsAttribute((int[][]) getStates.invoke(colorStateList)
                        , state_focused)) {
                    view.setFocusable(true);
                    view.setClickable(true);
                }
            } catch (Exception e) {
            }
        }
        a.recycle();
    }

    /**
     * description：重新设置外边距和字体边距
     */
    private void zoom(View view) {
        if (px) {
            int t = (int) (view.getPaddingTop() * scale);
            int l = (int) (view.getPaddingLeft() * scale);
            int b = (int) (view.getPaddingBottom() * scale);
            int r = (int) (view.getPaddingRight() * scale);
            view.setPadding(l, t, r, b);
            if (view instanceof TextView) {
                TextView textView = (TextView) view;
                textView.setIncludeFontPadding(false);
                textView.setTextSize(textView.getTextSize() * scale);
                if (Build.VERSION.SDK_INT > 20)
                    textView.setLetterSpacing(textView.getLetterSpacing());
                if (Build.VERSION.SDK_INT > 15)
                    textView.setLineSpacing(textView.getLineSpacingExtra() * scale, textView.getLineSpacingMultiplier());
            }
            manager.onAttributeSetZoom(view);
        }
    }

    @Override
    public LayoutInflater cloneInContext(Context newContext) {
        return new AppLayoutInflater(this, newContext);
    }

    public static boolean containsAttribute(int[][] stateSpecs, int attr) {
        if (stateSpecs != null) {
            for (int[] spec : stateSpecs) {
                if (spec == null) {
                    break;
                }
                for (int specAttr : spec) {
                    if (specAttr == attr || -specAttr == attr) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
