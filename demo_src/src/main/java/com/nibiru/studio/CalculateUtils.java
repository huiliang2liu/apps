package com.nibiru.studio;

import java.math.BigDecimal;

public class CalculateUtils {

    //设置所有背景的颜色
    //Set all the background colors
    public static final String bgColor = "#99e2e2ff";
    //设置所有文本的颜色
    //Set all the text colors
    public static final String textColor = "#ffffff";
    //MR
    public static final float CENTER_Z = -1.49f;
    public static final float CENTER_Z_BG = -1.5f;
//    public static final float CENTER_Z = -.29f;
//    public static final float CENTER_Z_BG = -.3f;

    public static final float AA = 5.55f;
    public static final float BB = 0.39f;
    public static final float CC = 1.65f;
    public static final float DD = 0.375f;

    //z轴坐标
    //Coordination on z axis
    public static float Z = -1.5f;
    //面片整体高度
    //The general height of the patch
    public static float maxH = 1080f;

    private static float mFactor;

    static {
        float ps = 0.0f;
        float z = 14.41f + Z;
        float maxHeight = maxH;
        if (z <= 10) {
            ps = sub(AA, (mul(BB, z)));
        } else {
            ps = sub(CC, mul(DD, sub(z, 10)));
        }
        mFactor = maxHeight / ps;
    }

    private static float getpx(float px) {
        float ps = 0.0f;
        float z = 14.41f + Z;
        float maxHeight = maxH;
        if (z <= 10) {
            ps = sub(AA, (mul(BB, z)));
        } else {
            ps = sub(CC, mul(DD, sub(z, 10)));
        }
        float factor = maxHeight / ps;
        return px / factor;
    }

    public static float transformSize(float px) {
        return px / mFactor;
    }

    /**
     *
     * @param width
     * @param marginLeft 左上顶点到X边界的距离 marginLeft the distance from top left vertex to x margin
     * @return
     */
    public static float transformCenterX(float width, float marginLeft) {

        return (width/2f + marginLeft - 2560/2f) / mFactor;
    }

    /**
     *
     * @param width
     * @param marginLeft marginLeft the distance to the left side of parent control
     * @param referWidth referWidth the width of parent control
     * @return
     */
    public static float transformCenterX(float width, float marginLeft, float referWidth) {

        return (width/2f + marginLeft - referWidth/2f) / mFactor;
    }

    /**
     *
     * @param height
     * @param marginTop the distance from top left vertex to y margin
     * @return
     */
    public static float transformCenterY(float height, float marginTop) {
        return -(height/2f + marginTop - 1440/2f) / mFactor;
    }

    /**
     *
     * @param height
     * @param marginTop marginTop the distance to the top margin of parent control
     * @param referHeight referHeight the height of parent control
     * @return
     */
    public static float transformCenterY(float height, float marginTop, float referHeight) {
        return -(height/2f + marginTop - referHeight/2f) / mFactor;
    }

    private static float getpx(float maxHeight, float px) {
        float ps = 0.0f;
        float z = 13.71f;
        if (z <= 10) {
            ps = sub(AA, (mul(BB, z)));
        } else {
            ps = sub(CC, mul(DD, sub(z, 10)));
        }
        float factor = maxHeight / ps;
        return px / factor;
    }

    private static float getpx(float z, float maxHeight, float px) {
        float ps = 0.0f;
        if (z <= 10) {
            ps = sub(AA, (mul(BB, z)));
        } else {
            ps = sub(CC, mul(DD, sub(z, 10)));
        }
        float factor = maxHeight / ps;
        return px / factor;
    }

    private static float getMaxCenterY(float height) {
        float xheight = getpx(height);
        float centery = sub(0.2872f, mul(xheight, 0.5f));
        return centery;
    }

    private static float getRedomCenterY(float headheight, float redomheight) {
        float maxCenterY = getMaxCenterY(headheight);
        float getpx = mul(getpx(headheight), 0.5f);
        float face = getpx(14);
        float xheight = mul(getpx(redomheight), 0.5f);
        float center = maxCenterY - getpx - face - xheight;
        return center;
    }

    private static float getbootCenterY(float headheight, float redomheight, float height) {
        float redomCenterY = getRedomCenterY(headheight, redomheight);
        float xheight = mul(getpx(redomheight), 0.5f);
        float face = getpx(14);
        float bootheight = mul(getpx(height), 0.5f);
        float centery = redomCenterY - xheight - face - bootheight;
        return centery;
    }

    private static float getfootCenterY(float headheight, float redomheight, float height, float bootheight) {
        float redomCenterY = getbootCenterY(headheight, redomheight, height);
        float xheight = mul(getpx(height), 0.5f);
        float face = getpx(14);
        float heighter = mul(getpx(bootheight), 0.5f);
        float centery = redomCenterY - xheight - face - heighter;
        return centery;
    }

    private static float mul(float value1, float value2) {
        BigDecimal b1 = new BigDecimal(Float.toString(value1));
        BigDecimal b2 = new BigDecimal(Float.toString(value2));
        return b1.multiply(b2).floatValue();
    }

    private static float sub(float value1, float value2) {
        BigDecimal b1 = new BigDecimal(Float.toString(value1));
        BigDecimal b2 = new BigDecimal(Float.toString(value2));
        return b1.subtract(b2).floatValue();
    }

    private static float add(float value1, float value2) {
        BigDecimal b1 = new BigDecimal(Float.toString(value1));
        BigDecimal b2 = new BigDecimal(Float.toString(value2));
        return b1.add(b2).floatValue();
    }
}
