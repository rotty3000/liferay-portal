package com.liferay.portal.service.osgi.externalpolicy;

import org.apache.cxf.ws.policy.attachment.external.DomainExpression;
import org.apache.cxf.ws.policy.attachment.external.DomainExpressionBuilder;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.namespace.QName;
import java.util.Arrays;
import java.util.Collection;

/**
 * @author Tomas Polesovsky
 */
public class WSDLReferenceDomainExpressionBuilder implements DomainExpressionBuilder {

	private static final QName SERVICE_REFERENCE =
		new QName("http://liferay.com/2014/04/addressing", "Service");

	private static final QName ENDPOINT_REFERENCE =
		new QName("http://liferay.com/2014/04/addressing", "Endpoint");

	private static final QName BINDING_OPERATION_REFERENCE =
		new QName("http://liferay.com/2014/04/addressing", "BindingOperation");

	private static final QName BINDING_MESSAGE_REFERENCE =
		new QName("http://liferay.com/2014/04/addressing", "BindingMessage");

	private static final QName BINDING_FAULT_REFERENCE =
		new QName("http://liferay.com/2014/04/addressing", "BindingFault");

	private static final Collection<QName> SUPPORTED_TYPES = Arrays.asList(
		SERVICE_REFERENCE, ENDPOINT_REFERENCE, BINDING_OPERATION_REFERENCE,
		BINDING_MESSAGE_REFERENCE, BINDING_FAULT_REFERENCE);

	@Override
	public DomainExpression build(Element e) {
		QName referenceType = new QName(e.getNamespaceURI(), e.getLocalName());

		WSDLReferenceDomainExpression expression = new WSDLReferenceDomainExpression();

		if (referenceType.equals(SERVICE_REFERENCE)) {
			buildServiceReference(e, expression);
		}
		if (referenceType.equals(ENDPOINT_REFERENCE)) {
			buildEndpointReference(e, expression);
		}
		if (referenceType.equals(BINDING_OPERATION_REFERENCE)) {
			buildBindingOperationReference(e, expression);
		}
		if (referenceType.equals(BINDING_MESSAGE_REFERENCE)) {
			buildBindingMessageReference(e, expression);
		}
		if (referenceType.equals(BINDING_FAULT_REFERENCE)) {
			buildBindingFaultReference(e, expression);
		}

		return expression;
	}

	@Override
	public Collection<QName> getDomainExpressionTypes() {
		return SUPPORTED_TYPES;
	}

	protected void buildBindingFaultReference(Element e, WSDLReferenceDomainExpression expression) {
		Node n = getFirstNonTextChild(e);
		if (n == null) {
			return;
		}
		QName serviceName = new QName(n.getNamespaceURI(), n.getLocalName());
		expression.setBindingFaultReference(serviceName);
	}

	protected void buildBindingMessageReference(Element e, WSDLReferenceDomainExpression expression) {
		Node n = getFirstNonTextChild(e);
		if (n == null) {
			return;
		}
		QName serviceName = new QName(n.getNamespaceURI(), n.getLocalName());
		expression.setBindingMessageReference(serviceName);
	}

	protected void buildBindingOperationReference(Element e, WSDLReferenceDomainExpression expression) {
		Node n = getFirstNonTextChild(e);
		if (n == null) {
			return;
		}
		QName serviceName = new QName(n.getNamespaceURI(), n.getLocalName());
		expression.setBindingOperationReference(serviceName);
	}

	protected void buildEndpointReference(Element e, WSDLReferenceDomainExpression expression) {
		Node n = getFirstNonTextChild(e);
		if (n == null) {
			return;
		}
		QName serviceName = new QName(n.getNamespaceURI(), n.getLocalName());
		expression.setEndpointReference(serviceName);
	}

	protected void buildServiceReference(Element e, WSDLReferenceDomainExpression expression) {
		Node n = getFirstNonTextChild(e);
		if (n == null) {
			return;
		}
		QName serviceName = new QName(n.getNamespaceURI(), n.getLocalName());
		expression.setServiceReference(serviceName);
	}

	protected Node getFirstNonTextChild(Node parent) {
		NodeList children = parent.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				return child;
			}
		}
		return null;
	}
}
