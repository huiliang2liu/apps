package com.base.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


/**
 * 方法管理类 author:xh email:825378291@qq.com time：2017-1-23 上午09:33:48
 *
 */
public class MethodManager extends XHReflect {
    /**
     * 获取所有公共方法 author:xh email:825378291@qq.com time:2017-1-23 上午09:36:24
     *
     * @param cl
     * @return
     */
    public static Method[] publicMethods(Class cl) {
        if (cl == null)
            return null;
        return cl.getMethods();
    }

    /**
     * 获取公共方法 author:xh email:825378291@qq.com time:2017-1-23 上午09:39:35
     *
     * @param cl
     * @param name
     * @param parameterTypes
     * @return
     */
    public static Method publicMethod(Class cl, String name,
                                       Class... parameterTypes) {
        if (cl == null || name==null||name.isEmpty())
            return null;
        try {
            cl.getMethod(name, parameterTypes);
        } catch (Exception e) {
            // TODO: handle exception
        }
        return null;
    }

    /**
     * 获取指定名字的公共方法 author:xh email:825378291@qq.com time:2017-1-23 上午11:17:44
     *
     * @param cl
     * @param name
     * @return
     */
    public static Method[] publicMethod(Class cl, String name) {
        if (cl == null || name==null||name.isEmpty())
            return null;
        Method methods[] = publicMethods(cl);
        if (methods == null || methods.length == 0)
            return null;
        List<Method> list = new ArrayList<Method>();
        for (Method method : methods) {
            if (method.getName().equals(name))
                list.add(method);
        }
        return list.toArray(new Method[list.size()]);
    }

    /**
     * 获取所有方法 author:xh email:825378291@qq.com time:2017-1-23 上午09:37:55
     *
     * @param cl
     * @return
     */
    public static Method[] methods(Class cl) {
        if (cl == null)
            return null;
        return cl.getDeclaredMethods();
    }

    /**
     * 获取的指定名字的所有方法 author:xh email:825378291@qq.com time:2017-1-23 上午11:20:31
     *
     * @param cl
     * @param name
     * @return
     */
    public static Method[] methods(Class cl, String name) {
        if (cl == null ||name==null||name.isEmpty())
            return null;
        Method methods[] = methods(cl);
        if (methods == null || methods.length == 0)
            return null;
        List<Method> list = new ArrayList<Method>();
        for (Method method : methods) {
            if (method.getName().equals(name))
                list.add(method);
        }
        return list.toArray(new Method[list.size()]);
    }

    /**
     * 获取方法 author:xh email:825378291@qq.com time:2017-1-23 上午09:40:50
     *
     * @param cl
     * @param name
     * @param parameterTypes
     * @return
     */
    public static Method method(Class cl, String name, Class... parameterTypes) {
        if (cl == null || name==null||name.isEmpty()){
        	return null;
        }
        try {
            return cl.getDeclaredMethod(name, parameterTypes);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取所有的注释 author:xh email:825378291@qq.com time:2017-1-23 上午09:48:56
     *
     * @param method
     * @return
     */
    public static Annotation[] annotations(Method method) {
        if (method == null)
            return null;
        return method.getDeclaredAnnotations();
    }

    /**
     * 获取指定的注释 author:xh email:825378291@qq.com time:2017-1-23 上午09:51:50
     *
     * @param method
     * @param cl
     * @return
     */
    public static Annotation annotation(Method method,
                                        Class<? extends Annotation> cl) {
        if (method == null || cl == null || !method.isAnnotationPresent(cl))
            return null;
        return method.getAnnotation(cl);
    }

    /**
     * 执行方法 author:xh email:825378291@qq.com time:2017-1-23 上午10:10:06
     *
     * @param method
     * @param object
     * @param objects
     * @return
     */
    public static Object invoke(Method method, Object object, Object...objects) {
        if (method == null){
            return null;
        }

        try {
            if (!method.isAccessible())
                method.setAccessible(true);
            return method.invoke(object, objects);
        } catch (Exception e) {
            // TODO: handle exception
        	e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取方法参数 author:xh email:825378291@qq.com time:2017-1-23 上午11:23:59
     *
     * @param method
     * @return
     */
    public static Class[] parameterTypes(Method method) {
        if (method == null)
            return null;
        return method.getParameterTypes();
    }
}