package com.liferay.messaging.internal.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.Queue;

/**
 * @author Shuyang Zhou
 */
public class AnnotationLocator {

	public static <T extends Annotation> T locate(
		Method method, Class<?> targetClass, Class<T> annotationClass) {

		Queue<Class<?>> queue = new LinkedList<>();

		if (targetClass == null) {
			queue.offer(method.getDeclaringClass());
		}
		else {
			queue.offer(targetClass);
		}

		Class<?> clazz = null;

		while ((clazz = queue.poll()) != null) {
			T annotation = null;

			try {
				Method specificMethod = clazz.getDeclaredMethod(
					method.getName(), method.getParameterTypes());

				annotation = specificMethod.getAnnotation(annotationClass);

				if (annotation != null) {
					return annotation;
				}
			}
			catch (Exception e) {
			}

			try {

				// Ensure the class has a publicly inherited method

				clazz.getMethod(method.getName(), method.getParameterTypes());

				annotation = clazz.getAnnotation(annotationClass);
			}
			catch (Exception e) {
			}

			if (annotation == null) {
				_queueSuperTypes(queue, clazz);
			}
			else {
				return annotation;
			}
		}

		return null;
	}

	private static void _queueSuperTypes(
		Queue<Class<?>> queue, Class<?> clazz) {

		Class<?> supperClass = clazz.getSuperclass();

		if ((supperClass != null) && (supperClass != Object.class)) {
			queue.offer(supperClass);
		}

		Class<?>[] interfaceClasses = clazz.getInterfaces();

		for (Class<?> interfaceClass : interfaceClasses) {
			if (!queue.contains(interfaceClass)) {
				queue.offer(interfaceClass);
			}
		}
	}

}
