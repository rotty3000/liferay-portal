package com.liferay.portal.service.remote;

import org.apache.cxf.annotations.Policies;
import org.apache.cxf.annotations.Policy;

import javax.jws.Oneway;
import javax.jws.WebService;

/**
 * @author Tomas Polesovsky
 */

@WebService
public interface JAXWSService {

	@Policies({
		@org.apache.cxf.annotations.Policy(uri = "/message-out-policy.xml",
			placement = Policy.Placement.BINDING_OPERATION_OUTPUT),
		@org.apache.cxf.annotations.Policy(uri = "/message-in-policy.xml",
			placement = Policy.Placement.PORT_TYPE_OPERATION_INPUT)
	})
	public JAXWSServiceObject getObject();

	@Oneway
	public void setObject(JAXWSServiceObject jaxwsServiceObject);
}
