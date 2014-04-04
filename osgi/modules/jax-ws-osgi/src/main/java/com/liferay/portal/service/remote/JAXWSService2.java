package com.liferay.portal.service.remote;

import org.apache.cxf.annotations.Logging;
import org.apache.cxf.annotations.Policies;
import org.apache.cxf.annotations.Policy;

import javax.jws.WebService;

/**
 * @author Tomas Polesovsky
 */

@WebService
public interface JAXWSService2 {

	public JAXWSServiceObject getObject();

	public void setObject(JAXWSServiceObject jaxwsServiceObject);
}
