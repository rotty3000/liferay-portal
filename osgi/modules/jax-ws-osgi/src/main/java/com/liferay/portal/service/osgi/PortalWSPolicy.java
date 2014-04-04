package com.liferay.portal.service.osgi;

import org.apache.cxf.Bus;
import org.apache.cxf.BusException;
import org.apache.cxf.common.util.StringUtils;
import org.apache.cxf.staxutils.StaxUtils;
import org.apache.cxf.ws.policy.PolicyBean;
import org.apache.cxf.ws.policy.PolicyConstants;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;

/**
 * @author Tomas Polesovsky
 */
public class PortalWSPolicy extends PolicyBean {
	private Bus _bus;

	public PortalWSPolicy() throws BusException {
		this(null);
	}

	public PortalWSPolicy(Bus bus) throws BusException{
		this._bus = bus;

		try {
			initialize();
		} catch (Exception e) {
			throw new BusException(e);
		}
	}

	protected void initialize() throws Exception{
		XMLStreamReader reader = null;
		InputStream src = getClass().getResourceAsStream("/test-policy.xml");
		try {
			reader = StaxUtils.createXMLStreamReader(src);
			Document doc = StaxUtils.read(reader);

			String uri = getPolicyId(doc.getDocumentElement());
			if (StringUtils.isEmpty(uri)) {
				Attr att = doc.createAttributeNS(PolicyConstants.WSU_NAMESPACE_URI,
					"wsu:" + PolicyConstants.WSU_ID_ATTR_NAME);
				att.setNodeValue("TestPolicy");
				doc.getDocumentElement().setAttributeNodeNS(att);
			}

			setElement(doc.getDocumentElement());
		} finally {
			StaxUtils.close(reader);
		}
	}

	private String getPolicyId(Element element) {
		return element.getAttributeNS(PolicyConstants.WSU_NAMESPACE_URI,
			PolicyConstants.WSU_ID_ATTR_NAME);
	}
}
