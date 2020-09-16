package com.base.util;

import org.w3c.dom.NodeList;

import java.util.Collection;
import java.util.Map;

/**
 * 2018/7/30 10:41
 * instructions：检测空工具
 * author:liuhuiliang  email:825378291@qq.com
 **/
public class NullUtil {
    /**
     *判断集合是否为空
     * @param collection
     * @return
     */
    public static boolean isEmpty(Collection<?> collection) {
        if (collection == null || collection.size() <= 0)
            return true;
        return false;
    }

    /**
     *判断map是否为空
     * @param map
     * @return
     */
    public static boolean isEmpty(Map<?, ?> map) {
        if (map == null || map.size() <= 0)
            return true;
        return false;
    }

    /**
     * 判读数组是否为空
     * @param objects
     * @return
     */
    public static boolean isEmpty(Object[] objects) {
        if (objects == null || objects.length <= 0)
            return true;
        return false;
    }

    /**
     *判断String是否为空
     * @param string
     * @return
     */
    public static boolean isEmpty(String string) {
        if (string == null || string.trim().isEmpty())
            return true;
        return false;
    }

    public static boolean isEmpty(NodeList list) {
        if (list == null || list.getLength() <= 0)
            return true;
        return false;
    }

    /**
     *判断一个对象是否为空
     * @param object
     * @return
     */

    public static boolean isEmpty(Object object) {
        if (object == null)
            return true;
        return false;
    }
}
