package com.liferay.portal.service.remote;

import java.util.Date;

/**
 * @author Tomas Polesovsky
 */
public class JAXWSServiceImpl implements JAXWSService {
	@Override
	public JAXWSServiceObject getObject() {
		JAXWSServiceObject result = new JAXWSServiceObject();

		result.setDateParam(new Date());
		result.setIntParam(42);
		result.setStringParam("Hello service!");

		return result;
	}

	@Override
	public void setObject(JAXWSServiceObject jaxwsServiceObject) {
		if (jaxwsServiceObject == null) {
			throw new RuntimeException("Object is null!");
		}

		System.out.println(jaxwsServiceObject.toString());
	}
}
