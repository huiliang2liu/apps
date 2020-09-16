package com.base.reflect;

import java.lang.reflect.Modifier;

/**
 * 反射管理类
 * @author xh E-mail:825378291@qq.com
 * @version 创建时间：2017-1-21 上午10:18:45
 *
 */
public class XHReflect {
    public static enum Type {
        /**
         * The <code>int</code> value representing the <code>public</code>
         * modifier.
         */
        PUBLIC(Modifier.PUBLIC),

        /**
         * The <code>int</code> value representing the <code>private</code>
         * modifier.
         */
        PRIVATE(Modifier.PRIVATE),

        /**
         * The <code>int</code> value representing the <code>protected</code>
         * modifier.
         */
        PROTECTED(Modifier.PROTECTED),

        /**
         * The <code>int</code> value representing the <code>static</code>
         * modifier.
         */
        STATIC(Modifier.STATIC),

        /**
         * The <code>int</code> value representing the <code>final</code>
         * modifier.
         */
        FINAL(Modifier.FINAL),

        /**
         * The <code>int</code> value representing the <code>synchronized</code>
         * modifier.
         */
        SYNCHRONIZED(Modifier.SYNCHRONIZED),

        /**
         * The <code>int</code> value representing the <code>volatile</code>
         * modifier.
         */
        VOLATILE(Modifier.VOLATILE),

        /**
         * The <code>int</code> value representing the <code>transient</code>
         * modifier.
         */
        TRANSIENT(Modifier.TRANSIENT),

        /**
         * The <code>int</code> value representing the <code>native</code>
         * modifier.
         */
        NATIVE(Modifier.NATIVE),

        /**
         * The <code>int</code> value representing the <code>interface</code>
         * modifier.
         */
        INTERFACE(Modifier.INTERFACE),

        /**
         * The <code>int</code> value representing the <code>abstract</code>
         * modifier.
         */
        ABSTRACT(Modifier.ABSTRACT),

        /**
         * The <code>int</code> value representing the <code>strictfp</code>
         * modifier.
         */
        STRICT(Modifier.STRICT);

        int type;
        private Type(int type) {
            // TODO Auto-generated constructor stub
            this.type=type;
        }
        protected  int getType(){
            return type;
        }
    }
    /**
     * 判断是否为boolean类型
     *
     * @param cl
     * @return
     */
    public static boolean isBoolean(Class cl) {
        return Boolean.class.isAssignableFrom(cl)||boolean.class.isAssignableFrom(cl);
    }

    /**
     * 判断整型
     *
     * @param cl
     * @return
     */
    public static boolean isInteger(Class cl) {
        return Byte.class.isAssignableFrom(cl)||byte.class.isAssignableFrom(cl)
                || Short.class.isAssignableFrom(cl)||short.class.isAssignableFrom(cl)
                || Integer.class.isAssignableFrom(cl)||int.class.isAssignableFrom(cl)
                || Long.class.isAssignableFrom(cl)||long.class.isAssignableFrom(cl);
    }

    /**
     * 判断浮点型
     *
     * @param cl
     * @return
     */
    public static boolean isFloat(Class cl) {
        return Float.class.isAssignableFrom(cl)||float.class.isAssignableFrom(cl)
                || Double.class.isAssignableFrom(cl)||double.class.isAssignableFrom(cl);
    }

    /**
     * 判断是是否为字符
     *
     * @param cl
     * @return
     */
    public static boolean isChar(Class cl) {
        return Character.class.isAssignableFrom(cl)||char.class.isAssignableFrom(cl);
    }

    /**
     * 是否被final修饰
     *
     * @param modifiers
     * @return
     */
    public static boolean isFinal(int modifiers) {
        return Modifier.isFinal(modifiers);
    }
    /**
     * 是否被abstract修饰
     *
     * @param modifiers
     * @return
     */
    public static boolean isAbstract(int modifiers) {
        return Modifier.isAbstract(modifiers);
    }
    /**
     * 是否被interface修饰
     *
     * @param modifiers
     * @return
     */
    public static boolean isInterface(int modifiers) {
        return Modifier.isInterface(modifiers);
    }
    /**
     * 是否被native修饰
     *
     * @param modifiers
     * @return
     */
    public static boolean isNative(int modifiers) {
        return Modifier.isNative(modifiers);
    }
    /**
     * 是否被private修饰
     *
     * @param modifiers
     * @return
     */
    public static boolean isPrivate(int modifiers) {
        return Modifier.isPrivate(modifiers);
    }
    /**
     * 是否被protected修饰
     *
     * @param modifiers
     * @return
     */
    public static boolean isProtected(int modifiers) {
        return Modifier.isProtected(modifiers);
    }
    /**
     * 是否被public修饰
     *
     * @param modifiers
     * @return
     */
    public static boolean isPublic(int modifiers) {
        return Modifier.isPublic(modifiers);
    }
    /**
     * 是否被static修饰
     *
     * @param modifiers
     * @return
     */
    public static boolean isStatic(int modifiers) {
        return Modifier.isStatic(modifiers);
    }
    /**
     * 是否被strict修饰
     *
     * @param modifiers
     * @return
     */
    public static boolean isStrict(int modifiers) {
        return Modifier.isStrict(modifiers);
    }
    /**
     * 是否被synchronized修饰
     *
     * @param modifiers
     * @return
     */
    public static boolean isSynchronized(int modifiers) {
        return Modifier.isSynchronized(modifiers);
    }
    /**
     * 是否被transient修饰
     *
     * @param modifiers
     * @return
     */
    public static boolean isTransient(int modifiers) {
        return Modifier.isTransient(modifiers);
    }
    /**
     * 是否被volatile修饰
     *
     * @param modifiers
     * @return
     */
    public static boolean isVolatile(int modifiers) {
        return Modifier.isVolatile(modifiers);
    }
}