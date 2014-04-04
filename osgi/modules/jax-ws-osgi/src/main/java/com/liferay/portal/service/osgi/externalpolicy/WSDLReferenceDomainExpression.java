package com.liferay.portal.service.osgi.externalpolicy;

import org.apache.cxf.service.model.BindingFaultInfo;
import org.apache.cxf.service.model.BindingMessageInfo;
import org.apache.cxf.service.model.BindingOperationInfo;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.service.model.ServiceInfo;
import org.apache.cxf.ws.policy.attachment.external.DomainExpression;

import javax.xml.namespace.QName;
import java.util.regex.Pattern;

/**
 * @author Tomas Polesovsky
 */
public class WSDLReferenceDomainExpression implements DomainExpression {
	private Pattern _endpointRegExp;
	private QName serviceReference;
	private QName endpointReference;
	private QName bindingOperationReference;
	private QName bindingMessageReference;
	private QName bindingFaultReference;

	public WSDLReferenceDomainExpression() {
	}

	@Override
	public boolean appliesTo(BindingFaultInfo bfi) {
		return bfi.getFaultInfo().getOperation().getInterface().getName().equals(getBindingFaultReference());
	}

	@Override
	public boolean appliesTo(BindingMessageInfo bmi) {
		return bmi.getMessageInfo().getOperation().getInterface().getName().equals(getBindingMessageReference());
	}

	@Override
	public boolean appliesTo(BindingOperationInfo boi) {
		return boi.getOperationInfo().getInterface().getName().equals(getBindingOperationReference());
	}

	@Override
	public boolean appliesTo(EndpointInfo ei) {
//		return _endpointRegExp.matcher(ei.getAddress()).matches(); // EndpointPolicyImpl:151
		return ei.getInterface().getName().equals(getEndpointReference());
	}

	@Override
	public boolean appliesTo(ServiceInfo si) {
		return si.getInterface().getName().equals(getServiceReference());
	}

	public void setServiceReference(QName serviceReference) {
		this.serviceReference = serviceReference;
	}

	public QName getServiceReference() {
		return serviceReference;
	}

	public void setEndpointReference(QName endpointReference) {
		this.endpointReference = endpointReference;
	}

	public QName getEndpointReference() {
		return endpointReference;
	}

	public void setBindingOperationReference(QName bindingOperationReference) {
		this.bindingOperationReference = bindingOperationReference;
	}

	public QName getBindingOperationReference() {
		return bindingOperationReference;
	}

	public void setBindingMessageReference(QName bindingMessageReference) {
		this.bindingMessageReference = bindingMessageReference;
	}

	public QName getBindingMessageReference() {
		return bindingMessageReference;
	}

	public void setBindingFaultReference(QName bindingFaultReference) {
		this.bindingFaultReference = bindingFaultReference;
	}

	public QName getBindingFaultReference() {
		return bindingFaultReference;
	}
}
