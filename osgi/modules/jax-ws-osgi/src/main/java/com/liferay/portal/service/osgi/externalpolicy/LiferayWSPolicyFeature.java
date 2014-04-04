package com.liferay.portal.service.osgi.externalpolicy;

import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.ws.policy.PolicyEngine;
import org.apache.cxf.ws.policy.PolicyEngineImpl;
import org.apache.cxf.ws.policy.PolicyProvider;
import org.apache.cxf.ws.policy.ServiceModelPolicyUpdater;
import org.apache.cxf.ws.policy.WSPolicyFeature;
import org.apache.cxf.ws.policy.attachment.external.ExternalAttachmentProvider;
import org.apache.neethi.Policy;

/**
 * @author Tomas Polesovsky
 */
public class LiferayWSPolicyFeature extends WSPolicyFeature {
	public LiferayWSPolicyFeature() {
	}

	public LiferayWSPolicyFeature(Policy... ps) {
		super(ps);
	}

	@Override
	public void initialize(Server server, Bus bus) {
		super.initialize(server, bus);

		Endpoint endpoint = server.getEndpoint();
		EndpointInfo ei = endpoint.getEndpointInfo();
		PolicyEngine pe = bus.getExtension(PolicyEngine.class);

		ServiceModelPolicyUpdater pu = new ServiceModelPolicyUpdater(ei);
		for (PolicyProvider pp : ((PolicyEngineImpl) pe).getPolicyProviders()) {
			if (pp instanceof LiferayExternalAttachmentProvider) {
				pu.addPolicyAttachments(((LiferayExternalAttachmentProvider) pp).getAttachments());
			}
		}
	}

}
