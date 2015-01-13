package com.liferay.portal.kernel.template;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface TaglibFactoryUtil {
	
	public void addTaglibSupportFTL(
			Map<String, Object> contextObjects, HttpServletRequest request,
			HttpServletResponse response) throws Exception;
	
	public void addStaticClassSupportFTL(
			Map<String, Object> contextObjects, String variableName,
			Class<?> variableClass);
	
	public String processFTL(
			HttpServletRequest request, HttpServletResponse response,
			Template template) throws Exception;

	public void includeTagLib(ServletContext servletContext, String string,
			Template template);

	public void includeContextHashModel(ServletContext servletContext,
			HttpServletRequest request, HttpServletResponse response,
			Template template);


}
