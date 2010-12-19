package com.inzyme.util;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.inzyme.exception.ExceptionUtils;
import com.inzyme.text.StringUtils;

/**
 * Handy Reflection-based utility methods.
 * 
 * @author Mike Schrag
 */
public class ReflectionUtils {
	/**
	 * If you give getShortName "org.jempeg.model.BasicContainer" it will return
	 * "BasicContainer".
	 *
	 * @param _clazz the class to get a short name for
	 * @return String "BasicContainer" for "org.jempeg.model.BasicContainer"
	 */
	public static String getShortName(Class _clazz) {
		String className = _clazz.getName();
		int dotIndex = className.lastIndexOf('.');
		if (dotIndex != -1) {
			className = className.substring(dotIndex + 1);
		}
		return className;
	}

	/**
	 * Returns the standard format toString for an object.  For instance a
	 * BasicContainer might look like [BasicContainer: name=containerName;
	 * size=10]
	 *
	 * @param _obj the object to make a toString
	 * @return String the toString
	 */
	public static String toString(Object _obj) {
		StringBuffer toString = new StringBuffer();

		Class clazz = _obj.getClass();
		toString.append("[");
		toString.append(getShortName(clazz));
		Method[] methods = clazz.getMethods();

		int numMethodsDisplayed = 0;
		for (int i = 0; i < methods.length; i ++) {
			try {
				Method method = methods[i];
				String methodName = method.getName();
				int modifiers = method.getModifiers();
				if (Modifier.isPublic(modifiers) && !Modifier.isStatic(modifiers) && !methodName.equals("getClass") && method.getParameterTypes().length == 0) {
					int nameIndex;
					if (methodName.startsWith("get")) {
						nameIndex = 3;
					} else if (methodName.startsWith("is")) {
						nameIndex = 2;
					} else {
						nameIndex = -1;
					}
					if (nameIndex != -1) {
						String name = StringUtils.uncapitalize(methodName.substring(nameIndex));
						Object valueObj = method.invoke(_obj, null);

						String value;
						if (valueObj == null) {
							value = "null";
						} else if (valueObj.getClass().isArray()) {
							value = "[" + valueObj.getClass().getComponentType() + "[] of length " + Array.getLength(valueObj) + "]";
//							StringBuffer sb = new StringBuffer();
//							sb.append("[");
//							for (int j = 0; j < objs.length; j ++) {
//								sb.append(objs[j]);
//							}
//							sb.append("]");
//							value = sb.toString();
						} else {
							value = valueObj.toString();
						}

						if (numMethodsDisplayed != 0) {
							toString.append("; ");
						} else {
							toString.append(": ");
						}

						numMethodsDisplayed ++;

						toString.append(name);
						toString.append("=");
						toString.append(value);
					}
				}
			}
			catch (Throwable t) {
				ExceptionUtils.printChainedStackTrace(t);
			}
		}

		toString.append("]");
		return toString.toString();
	}
}
