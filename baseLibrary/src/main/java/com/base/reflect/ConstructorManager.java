package com.base.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;

/**
 * 构造方法管理类
 * author:xh
 * email:825378291@qq.com
 * time：2017-1-23 上午10:21:09
 *
 */
@SuppressWarnings("unchecked")
public class ConstructorManager extends XHReflect {
    /**
     * 获取所有的构造方法
     * author:xh
     * email:825378291@qq.com
     * time:2017-1-23 上午10:25:38
     * @param <T>
     * @param cl
     * @return
     */
    public static <T> Constructor<T>[] publicConstructors(Class<T> cl){
        if(cl==null)
            return null;
        return (Constructor<T>[]) cl.getConstructors();
    }
    /**
     * 获取指定参数的构造方法
     * author:xh
     * email:825378291@qq.com
     * time:2017-1-23 上午10:27:48
     * @param <T>
     * @param cl
     * @param classes
     * @return
     */
    public static <T> Constructor<T> publicConstructor(Class<T> cl,Class...classes){
        if(cl==null)
            return null;
        try {
            return cl.getConstructor(classes);
        } catch (Exception e) {
            // TODO: handle exception
        }
        return null;
    }
    /**
     * 获取所有的够着方法
     * author:xh
     * email:825378291@qq.com
     * time:2017-1-23 上午10:32:14
     * @param <T>
     * @param cl
     * @return
     */
    public static <T> Constructor<T>[] constructors(Class<T> cl){
        if(cl==null)
            return null;
        return (Constructor<T>[]) cl.getDeclaredConstructors();
    }
    /**
     * 获取指定的构造方法
     * author:xh
     * email:825378291@qq.com
     * time:2017-1-23 上午10:33:31
     * @param <T>
     * @param cl
     * @param classes
     * @return
     */
    public static <T> Constructor<T> constructor(Class<T> cl,Class...classes){
        if(cl==null)
            return null;
        try {
            cl.getDeclaredConstructor(classes);
        } catch (Exception e) {
            // TODO: handle exception
        }
        return null;
    }
    /**
     * 获取构造方法上的左右注释
     * author:xh
     * email:825378291@qq.com
     * time:2017-1-23 上午10:36:12
     * @param constructor
     * @return
     */
    public static  Annotation[] annotations(Constructor constructor){
        return constructor.getDeclaredAnnotations();
    }
    /**
     * 获取构造方法上指定的注释
     * author:xh
     * email:825378291@qq.com
     * time:2017-1-23 上午10:37:55
     * @param constructor
     * @param cl
     * @return
     */
    public static Annotation annotation(Constructor constructor ,Class<? extends Annotation> cl){
        if(constructor==null||cl==null||!constructor.isAnnotationPresent(cl))
            return null;
        return constructor.getAnnotation(cl);
    }
    /**
     * 构造对象
     * author:xh
     * email:825378291@qq.com
     * time:2017-1-23 上午10:39:41
     * @param <T>
     * @param constructor
     * @param objects
     * @return
     */
    public static <T> T newInstance(Constructor<T> constructor, Object[] objects){
        if(constructor==null)
            return null;
        try {
            return constructor.newInstance(objects);
        } catch (Exception e) {
            // TODO: handle exception
        }
        return null;
    }
}