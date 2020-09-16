package com.base.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;

/**
 * Class管理类
 * 
 * @author xh E-mail:825378291@qq.com
 * @version 创建时间：2017-1-21 上午10:18:45
 * 
 */
public class ClassManager extends XHReflect {
	/**
	 * 
	 * 2018 2018-5-9 上午9:45:49 annotation：更具名字获取类 author：liuhuiliang email
	 * ：825378291@qq.com
	 * 
	 * @param className
	 * @return Class
	 */
	public static Class forName(String className) {
		if (className==null||className.isEmpty())
			return null;
		try {
			return Class.forName(className);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 获取指定修饰符数组的内部类
	 * 
	 * @param cl
	 * @param modifiers
	 * @return
	 */
	public static Class[] modifiersClasss(Class cl, int[] modifiers) {
		if (modifiers == null || modifiers.length == 0)
			return null;
		int modifier = 0;
		for (int i : modifiers) {
			modifier |= i;
		}
		return modifierClasss(cl, modifier);
	}

	/**
	 * 获取指定修饰符数组的内部类
	 * 
	 * @param cl
	 * @param modifiers
	 * @return
	 */
	public static Class[] modifiersClasss(Class cl, Type[] modifiers) {
		if (modifiers == null || modifiers.length == 0)
			return null;
		int modifier = 0;
		for (Type type : modifiers) {
			modifier |= type.getType();
		}
		return modifierClasss(cl, modifier);
	}

	/**
	 * 获取指定修饰符的内部类
	 * 
	 * @param cl
	 * @param modifier
	 * @return
	 */
	public static Class[] modifierClasss(Class cl, Type modifier) {
		if (modifier == null)
			return null;
		return modifierClasss(cl, modifier.getType());
	}

	/**
	 * 获取指定修饰符的内部类
	 * 
	 * @param cl
	 * @param modifier
	 * @return
	 */
	public static Class[] modifierClasss(Class cl, int modifier) {
		Class[] cs = classs(cl);
		if (cs == null || cs.length == 0)
			return null;
		Class[] myClass = new Class[cs.length];
		int len = 0;
		for (int i = 0; i < myClass.length; i++) {
			Class c = cs[i];
			if ((c.getModifiers() & modifier) != 0) {
				myClass[len] = c;
				len++;
			}
		}
		Class[] rc = new Class[len];
		System.arraycopy(myClass, 0, rc, 0, len);
		return rc;
	}

	/**
	 * 获取所有公共的内部类
	 * 
	 * @param cl
	 * @return
	 */
	public static Class[] publicClasss(Class cl) {
		if (cl == null)
			return null;
		return cl.getClasses();
	}

	/**
	 * 获取所有的内部类
	 * 
	 * @param cl
	 * @return
	 */
	public static Class[] classs(Class cl) {
		if (cl == null)
			return null;
		return cl.getDeclaredClasses();
	}

	/**
	 * 获取内部类
	 * 
	 * @param cl
	 * @param name
	 * @return
	 */
	public static Class cl(Class cl, String name) {
		if (cl == null || name==null||name.isEmpty())
			return null;
		try {
			return Class.forName(cl.getName() + "$" + name);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	/**
	 * 创建类
	 * 
	 * @param c
	 * @return
	 * @throws Exception
	 */
	public static Object newObject(Class c) {
		// TODO Auto-generated catch block
		if (c == null)
			return null;
		try {
			return c.newInstance();
		} catch (Exception e) {
			// TODO: handle exception
			try {
				Constructor[] cs = c.getDeclaredConstructors();
				if (cs != null && cs.length > 0) {
					Constructor constructor = cs[0];
					if (!constructor.isAccessible())
						constructor.setAccessible(true);
					Class[] classes = constructor.getParameterTypes();
					if (classes == null || classes.length == 0) {
						return constructor.newInstance();
					} else {
						Object[] objects = new Object[classes.length];
						for (int i = 0; i < classes.length; i++) {
							Class class1 = classes[i];
							Object object = null;
							if (isBoolean(class1))
								object = true;
							else if (isInteger(class1))
								object = -1;
							else if (isChar(class1))
								object = 'a';
							else if (isFloat(class1))
								object = 1.0f;
							objects[i] = object;
						}
						return constructor.newInstance(objects);
					}
				}
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
		return null;
	}

	/**
	 * 获取所有的注释 author:xh email:825378291@qq.com time:2017-1-22 上午11:12:56
	 * 
	 * @param cl
	 * @return
	 */
	public static Annotation[] annotations(Class cl) {
		if (cl == null)
			return null;
		return cl.getAnnotations();
	}

	/**
	 * 获取指定的注释内 author:xh email:825378291@qq.com time:2017-1-22 上午11:16:00
	 * 
	 * @param cl
	 * @param acl
	 * @return
	 */
	public static Annotation annotation(Class cl,
			Class<? extends Annotation> acl) {
		if (cl == null || acl == null || !cl.isAnnotationPresent(acl))
			return null;
		return cl.getAnnotation(acl);
	}

	/**
	 * 获取父类所有泛型 author:xh email:825378291@qq.com time:2017-1-22 上午11:23:01
	 * 
	 * @param cl
	 * @return
	 */
	public static java.lang.reflect.Type[] genericsSuper(Class cl) {
		if (cl == null)
			return null;
		java.lang.reflect.Type type = cl.getGenericSuperclass();
		if (type instanceof ParameterizedType) {
			return ((ParameterizedType) type).getActualTypeArguments();
		}
		return null;
	}

	/**
	 * 获取父类泛型 author:xh email:825378291@qq.com time:2017-1-22 上午11:24:40
	 * 
	 * @param cl
	 * @return
	 */
	public static Class genericSuper(Class cl) {
		java.lang.reflect.Type[] cls = genericsSuper(cl);
		if (cls == null || cls.length == 0)
			return null;
		if (cls[0] instanceof Class)
			return (Class) cls[0];
		return null;
	}
}