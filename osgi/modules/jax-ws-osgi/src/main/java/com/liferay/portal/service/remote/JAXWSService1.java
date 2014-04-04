package com.liferay.portal.service.remote;

import org.apache.cxf.annotations.Logging;
import org.apache.cxf.annotations.Policies;
import org.apache.cxf.annotations.Policy;

import javax.jws.WebService;

/**
 * @author Tomas Polesovsky
 */

@WebService
@org.apache.cxf.annotations.Policies({
	@org.apache.cxf.annotations.Policy(uri = "#com.liferay.portal.service.osgi.PortalWSPolicy",
		placement = Policy.Placement.BINDING)
})
@Logging(inLocation="<stdout>", outLocation="<stdout>")
public interface JAXWSService1 {

	public JAXWSServiceObject getObject();

	public void setObject(JAXWSServiceObject jaxwsServiceObject);
}
