package com.base.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 字段管理类 author:xh email:825378291@qq.com time：2017-1-22 上午10:25:23
 * 
 */
public class FieldManager extends XHReflect {
	/**
	 * 获取变量 author:xh email:825378291@qq.com time:2017-1-22 上午10:28:48
	 * 
	 * @param cl
	 * @param name
	 * @return
	 */
	public static Field field(Class cl, String name) {
		if (cl == null || name==null||name.isEmpty())
			return null;
		try {
			Field field = cl.getDeclaredField(name);
			field.setAccessible(true);
			return field;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	/**
	 * 获取公共变量 author:xh email:825378291@qq.com time:2017-1-22 上午10:32:40
	 * 
	 * @param cl
	 * @param name
	 * @return
	 */
	public static Field publicField(Class cl, String name) {
		if (cl == null ||name==null||name.isEmpty())
			return null;
		try {
			return cl.getField(name);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	/**
	 * 获取所有的变量 author:xh email:825378291@qq.com time:2017-1-22 上午10:36:40
	 * 
	 * @param cl
	 * @return
	 */
	public static Field[] fields(Class cl) {
		if (cl == null)
			return null;
		try {
			return cl.getDeclaredFields();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	/**
	 * 获取所有的公共变量 author:xh email:825378291@qq.com time:2017-1-22 上午10:37:41
	 * 
	 * @param cl
	 * @return
	 */
	public static Field[] publicFields(Class cl) {
		if (cl == null)
			return null;
		try {
			return cl.getFields();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	/**
	 * 获取指点修饰符的变量 author:xh email:825378291@qq.com time:2017-1-22 上午10:42:52
	 * 
	 * @param cl
	 * @param modifier
	 * @return
	 */
	public static Field[] modifierFields(Class cl, int modifier) {
		if (cl == null)
			return null;
		Field[] fields = fields(cl);
		if (fields == null || fields.length == 0 || modifier == 0)
			return null;
		List<Field> list = new ArrayList<Field>();
		for (Field field : fields) {
			if ((field.getModifiers() & modifier) != 0)
				list.add(field);
		}
		return list.toArray(new Field[list.size()]);
	}

	/**
	 * 获取指点修饰符的变量 author:xh email:825378291@qq.com time:2017-1-22 上午10:42:52
	 * 
	 * @param cl
	 * @param modifier
	 * @return
	 */
	public static Field[] modifierFields(Class cl, Type modifier) {
		if (modifier == null)
			return null;
		return modifierFields(cl, modifier.getType());
	}

	/**
	 * 获取指定修饰符以外的变量 author:xh email:825378291@qq.com time:2017-1-22 上午10:46:51
	 * 
	 * @param cl
	 * @param modifier
	 * @return
	 */
	public static Field[] noModifierFields(Class cl, int modifier) {
		if (cl == null)
			return null;
		Field[] fields = fields(cl);
		if (fields == null || fields.length == 0 || modifier == 0)
			return null;
		List<Field> list = new ArrayList<Field>();
		for (Field field : fields) {
			if ((field.getModifiers() & modifier) != 0)
				continue;
			list.add(field);
		}
		return list.toArray(new Field[list.size()]);
	}

	/**
	 * 获取指定修饰符以外的变量 author:xh email:825378291@qq.com time:2017-1-22 上午10:46:51
	 * 
	 * @param cl
	 * @param modifier
	 * @return
	 */
	public static Field[] noModifierFields(Class cl, Type modifier) {
		if (modifier == null)
			return null;
		return noModifierFields(cl, modifier.getType());
	}

	/**
	 * 设置变量的值 author:xh email:825378291@qq.com time:2017-1-22 上午10:51:50
	 * 
	 * @param object
	 * @param field
	 * @param value
	 * @return
	 */
	public static boolean setField(Object object, Field field, Object value) {
		if (field == null) {
			return false;
		}
		try {
			if (!field.isAccessible())
				field.setAccessible(true);
			field.set(object, value);
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 获取变量的值 author:xh email:825378291@qq.com time:2017-1-22 上午10:54:05
	 * 
	 * @param object
	 * @param field
	 * @return
	 */
	public static Object getField(Object object, Field field) {
		if (field == null)
			return null;
		try {
			if (!field.isAccessible())
				field.setAccessible(true);
			return field.get(object);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	/**
	 * 获取变量名 author:xh email:825378291@qq.com time:2017-1-22 上午10:55:39
	 * 
	 * @param field
	 * @return
	 */
	public static String name(Field field) {
		if (field == null)
			return "";
		return field.getName();
	}

	/**
	 * 获取数据所对应的类型 author:xh email:825378291@qq.com time:2017-1-22 上午11:26:31
	 * 
	 * @param field
	 * @return
	 */
	public static Class fieldClass(Field field) {
		if (field == null)
			return null;
		return field.getType();
	}

	/**
	 * 获取所有的注释 author:xh email:825378291@qq.com time:2017-1-22 上午11:28:14
	 * 
	 * @param field
	 * @return
	 */
	public static Annotation[] annotations(Field field) {
		if (field == null)
			return null;
		return field.getAnnotations();
	}

	/**
	 * 获取对应注释 author:xh email:825378291@qq.com time:2017-1-22 上午11:30:01
	 * 
	 * @param field
	 * @param acl
	 * @return
	 */
	public static Annotation annotation(Field field,
			Class<? extends Annotation> acl) {
		if (field == null || acl == null || !field.isAnnotationPresent(acl))
			return null;
		return field.getAnnotation(acl);
	}

	/**
	 * 获取父类数据泛型 author:xh email:825378291@qq.com time:2017-1-22 上午11:31:33
	 * 
	 * @param field
	 * @return
	 */
	public static java.lang.reflect.Type[] genericsSuper(Field field) {
		return ClassManager.genericsSuper(fieldClass(field));
	}

	/**
	 * 获取父类数据泛型 author:xh email:825378291@qq.com time:2017-1-22 上午11:32:33
	 * 
	 * @param field
	 * @return
	 */
	public static Class genericSuper(Field field) {
		return ClassManager.genericSuper(fieldClass(field));
	}

	/**
	 * 获取数据泛型 author:xh email:825378291@qq.com time:2017-1-22 下午04:51:49
	 * 
	 * @param field
	 * @return
	 */
	public static Class[] generics(Field field) {
		if (field == null)
			return null;
		String[] types = field.getGenericType().toString().split("<");
		if (types.length == 1)
			return null;
		String ss = types[1].split(">")[0];
		String t[] = ss.split(",");
		Class[] cs = new Class[t.length];
		for (int i = 0; i < cs.length; i++) {
			try {
				cs[i] = Class.forName(t[i].trim());
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
			}
		}
		return cs;
	}

	/**
	 * 获取数据泛型 author:xh email:825378291@qq.com time:2017-1-22 下午04:53:25
	 * 
	 * @param field
	 * @return
	 */
	public static Class generic(Field field) {
		Class[] cs = generics(field);
		if (cs == null || cs.length == 0)
			return null;
		return cs[0];
	}
}