package com.base.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * com.resource
 * 2018/10/10 10:42
 * instructions：
 * author:liuhuiliang  email:825378291@qq.com
 **/
public class ResourceUtil {
    private final static String TAG = "ResourceUtil";

    public static Drawable getDrawable(Resources resources, String name, String packageName, Resources.Theme theme) {
        return drawable(resources, drawable(resources, name, packageName), theme);
    }

    public static Drawable getMipmap(Resources resources, String name, String packageName, Resources.Theme theme) {
        return drawable(resources, mipmap(resources, name, packageName), theme);
    }

    public static Drawable drawable(Resources resources, int id, Resources.Theme theme) {
        if (resources == null)
            return null;
        try {
            if (Build.VERSION.SDK_INT >= 21 && theme != null)
                return resources.getDrawable(id, theme);
            return resources.getDrawable(id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String[] getStrings(Resources resources, String name, String packageName) {
        return strings(resources, array(resources, name, packageName));
    }

    public static String[] strings(Resources resources, int id) {
        if (resources == null)
            return null;
        try {
            return resources.getStringArray(id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getString(Resources resources, String name, String packageName) {
        return string(resources, string(resources, name, packageName));
    }

    public static String string(Resources resources, int id) {
        if (resources == null)
            return "";
        try {
            return resources.getString(id);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static ColorStateList getColorList(Resources resources, String name, String packageName, Resources.Theme theme) {
        return colorStateList(resources, color(resources, name, packageName), theme);
    }

    public static ColorStateList colorStateList(Resources resources, int id, Resources.Theme theme) {
        if (resources == null)
            return null;
        try {
            if (Build.VERSION.SDK_INT >= 23 && theme != null)
                return resources.getColorStateList(id, theme);
            return resources.getColorStateList(id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int getColor(Resources resources, String name, String packageName, Resources.Theme theme) {
        return color(resources, color(resources, name, packageName), theme);
    }

    public static int color(Resources resources, int id, Resources.Theme theme) {
        if (resources == null)
            return Color.argb(0x00, 0x00, 0x00, 0x00);
        try {
            if (Build.VERSION.SDK_INT >= 23 && theme != null)
                return resources.getColor(id, theme);
            return resources.getColor(id);
        } catch (Exception e) {
            e.printStackTrace();
            return Color.argb(0x00, 0x00, 0x00, 0x00);
        }
    }

    public static Animation getAnimation(Context context, String name) {
        return getAnimation(context, name, null);
    }


    public static Animation getAnimation(Context context, String name,
                                         String packageName) {
        return getAnimation(context, null, name, packageName);
    }

    public static Animation getAnimation(Context context, Resources resources,
                                         String name, String packageName) {
        if (context != null) {
            if (resources == null)
                resources = context.getResources();
            if (packageName == null || packageName.isEmpty())
                packageName = context.getPackageName();
        }
        return animation(context, anim(resources, name, packageName));
    }

    public static Animation animation(Context context, int id) {
        if (context == null)
            return null;
        try {
            return AnimationUtils.loadAnimation(context, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static View getView(Context context, String name) {
        return getView(context, name, null);
    }

    public static View getView(Context context, String name, String packageName) {
        return getView(context, null, name, packageName);
    }


    public static View getView(Context context, Resources resources,
                               String name, String packageName) {
        if (context != null) {
            if (resources == null)
                resources = context.getResources();
            if (packageName == null || packageName.isEmpty())
                packageName = context.getPackageName();
        }
        return view(context, layout(resources, name, packageName));
    }

    public static View view(Context context, int id) {
        return view(context, id, null);
    }

    public static View view(Context context, int id, ViewGroup group) {
        if (context == null)
            return null;
        try {
            return LayoutInflater.from(context).inflate(id, group);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int layout(Resources resources, String name,
                             String packageName) {
        return name2id(resources, name, "layout", packageName);
    }


    public static int drawable(Resources resources, String name,
                               String packageName) {
        return name2id(resources, name, "drawable", packageName);
    }

    public static int mipmap(Resources resources, String name,
                             String packageName) {
        return name2id(resources, name, "mipmap", packageName);
    }


    public static int id(Resources resources, String name, String packageName) {
        return name2id(resources, name, "id", packageName);
    }


    public static int string(Resources resources, String name,
                             String packageName) {
        return name2id(resources, name, "string", packageName);
    }


    public static int attr(Resources resources, String name, String packageName) {
        return name2id(resources, name, "attr", packageName);
    }


    public static int raw(Resources resources, String name, String packageName) {
        return name2id(resources, name, "raw", packageName);
    }

    public static int array(Resources resources, String name, String packageName) {
        return name2id(resources, name, "array", packageName);
    }


    public static int anim(Resources resources, String name, String packageName) {
        return name2id(resources, name, "anim", packageName);
    }


    public static int color(Resources resources, String name, String packageName) {
        return name2id(resources, name, "color", packageName);
    }


    public static int dimen(Resources resources, String name, String packageName) {
        return name2id(resources, name, "dimen", packageName);
    }


    public static int style(Resources resources, String name, String packageName) {
        return name2id(resources, name, "style", packageName);
    }


    public static int name2id(Resources resources, String name, String type,
                              String packageName) {
        if (resources == null || name == null || name.isEmpty()
                || type == null || type.isEmpty())
            return -1;
        try {
            return resources.getIdentifier(name, type, packageName);
        } catch (Exception e) {
            // TODO: handle exception
            Log.e(TAG, "not found this resources,type is " + type
                    + ",name is " + name + ", package is " + packageName);
            return -1;
        }
    }

    public static String id2name(Resources resources, int id) {
        try {
            return resources.getResourceName(id);
        } catch (Exception e) {
            return "";
        }
    }

    public static String id2type(Resources resources, int id) {
        try {
            return resources.getResourceTypeName(id);
        } catch (Exception e) {
            return "";
        }
    }
}
