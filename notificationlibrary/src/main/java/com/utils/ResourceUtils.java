package com.utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ResourceUtils {
	public static Map<String, Object> anim;
	public static Map<String, Object> attr;
	public static Map<String, Object> color;
	public static Map<String, Object> dimen;
	public static Map<String, Object> drawable;
	public static Map<String, Object> id;
	public static Map<String, Object> layout;
	public static Map<String, Object> string;
	public static Map<String, Object> style;

	public static Map<String, Object> styleable;

	synchronized public static void Initialize(String packageName, String className) {
		if(anim!=null)
			return;
		try {
			Class c = Class.forName(packageName + "." + className);
			Class[] classes = c.getClasses();
			for (int i = 0; i < classes.length; i++) {
				String name = classes[i].getName().split("\\$")[1];
				Class desireClass = classes[i];
				if (name.equals("anim"))
					setAnim(desireClass);
				else if (name.equals("attr"))
					setAttr(desireClass);
				else if (name.equals("color"))
					setColor(desireClass);
				else if (name.equals("dimen"))
					setDimen(desireClass);
				else if (name.equals("drawable"))
					setDrawable(desireClass);
				else if (name.equals("id"))
					setId(desireClass);
				else if (name.equals("layout"))
					setLayout(desireClass);
				else if (name.equals("string"))
					setString(desireClass);
				else if (name.equals("style"))
					setStyle(desireClass);
				else if (name.equals("styleable"))
					setStyleable(desireClass);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private static void setStyleable(Class desireClass)
			throws IllegalArgumentException, IllegalAccessException {
		// TODO Auto-generated method stub
		if (desireClass == null)
			return;
		Field f[] = desireClass.getDeclaredFields();
		if (f == null || f.length <= 0)
			return;
		styleable = new HashMap<String, Object>();
		for (Field field : f) {
			styleable.put(field.getName(), field.get(desireClass));
		}
	}

	private static void setStyle(Class desireClass)
			throws IllegalArgumentException, IllegalAccessException {
		// TODO Auto-generated method stub
		if (desireClass == null)
			return;
		Field f[] = desireClass.getDeclaredFields();
		if (f == null || f.length <= 0)
			return;
		style = new HashMap<String, Object>();
		for (Field field : f) {
			style.put(field.getName(), field.get(desireClass));
		}
	}

	private static void setString(Class desireClass)
			throws IllegalArgumentException, IllegalAccessException {
		// TODO Auto-generated method stub
		if (desireClass == null)
			return;
		Field f[] = desireClass.getDeclaredFields();
		if (f == null || f.length <= 0)
			return;
		string = new HashMap<String, Object>();
		for (Field field : f) {
			string.put(field.getName(), field.get(desireClass));
		}
	}

	private static void setLayout(Class desireClass)
			throws IllegalArgumentException, IllegalAccessException {
		// TODO Auto-generated method stub
		if (desireClass == null)
			return;
		Field f[] = desireClass.getDeclaredFields();
		if (f == null || f.length <= 0)
			return;
		layout = new HashMap<String, Object>();
		for (Field field : f) {
			layout.put(field.getName(), field.get(desireClass));
		}
	}

	private static void setId(Class desireClass)
			throws IllegalArgumentException, IllegalAccessException {
		// TODO Auto-generated method stub
		if (desireClass == null)
			return;
		Field f[] = desireClass.getDeclaredFields();
		if (f == null || f.length <= 0)
			return;
		id = new HashMap<String, Object>();
		for (Field field : f) {
			id.put(field.getName(), field.get(desireClass));
		}
	}

	private static void setDrawable(Class desireClass)
			throws IllegalArgumentException, IllegalAccessException {
		// TODO Auto-generated method stub
		if (desireClass == null)
			return;
		Field f[] = desireClass.getDeclaredFields();
		if (f == null || f.length <= 0)
			return;
		drawable = new HashMap<String, Object>();
		for (Field field : f) {
			drawable.put(field.getName(), field.get(desireClass));
		}
	}

	private static void setDimen(Class desireClass)
			throws IllegalArgumentException, IllegalAccessException {
		// TODO Auto-generated method stub
		if (desireClass == null)
			return;
		Field f[] = desireClass.getDeclaredFields();
		if (f == null || f.length <= 0)
			return;
		dimen = new HashMap<String, Object>();
		for (Field field : f) {
			dimen.put(field.getName(), field.get(desireClass));
		}
	}

	private static void setColor(Class desireClass)
			throws IllegalArgumentException, IllegalAccessException {
		// TODO Auto-generated method stub
		if (desireClass == null)
			return;
		Field f[] = desireClass.getDeclaredFields();
		if (f == null || f.length <= 0)
			return;
		color = new HashMap<String, Object>();
		for (Field field : f) {
			color.put(field.getName(), field.get(desireClass));
		}
	}

	private static void setAttr(Class desireClass)
			throws IllegalArgumentException, IllegalAccessException {
		// TODO Auto-generated method stub
		if (desireClass == null)
			return;
		Field f[] = desireClass.getDeclaredFields();
		if (f == null || f.length <= 0)
			return;
		attr = new HashMap<String, Object>();
		for (Field field : f) {
			attr.put(field.getName(), field.get(desireClass));
		}
	}

	private static void setAnim(Class desireClass)
			throws IllegalArgumentException, IllegalAccessException {
		// TODO Auto-generated method stub
		if (desireClass == null)
			return;
		Field f[] = desireClass.getDeclaredFields();
		if (f == null || f.length <= 0)
			return;
		anim = new HashMap<String, Object>();
		for (Field field : f) {
			anim.put(field.getName(), field.get(desireClass));
		}
	}

	// anim; attr; color; dimen; drawable; id; layout;string; style;
	public static int getStyleable(String name) {
		return getInt(styleable, name);
	}

	public static int[] getStyleables(String name) {
		return getInts(styleable, name);
	}

	public static int getString(String name) {
		return getInt(string, name);
	}

	public static int getStyle(String name) {
		return getInt(style, name);
	}

	public static int getAnim(String name) {
		return getInt(anim, name);
	}

	public static int getAttr(String name) {
		return getInt(attr, name);
	}

	public static int getColor(String name) {
		return getInt(color, name);
	}

	public static int getDimen(String name) {
		return getInt(dimen, name);
	}

	public static int getDrawable(String name) {
		return getInt(drawable, name);
	}

	public static int getId(String name) {
		return getInt(id, name);
	}

	public static int getLayout(String name) {
		return getInt(layout, name);
	}

	private static int getInt(Map<String, Object> m, String name) {
		if (m != null) {
			Object o = m.get(name);
			if (o instanceof Integer)
				return (Integer) o;
		}
		return 0;
	}

	private static int[] getInts(Map<String, Object> m, String name) {
		if (m != null) {
			Object o = m.get(name);
			if (o instanceof int[])
				return (int[]) o;
		}
		return null;
	}
}
