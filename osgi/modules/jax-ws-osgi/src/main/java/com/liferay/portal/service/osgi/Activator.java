package com.liferay.portal.service.osgi;

import com.liferay.portal.service.osgi.externalpolicy.LiferayWSPolicyFeature;
import com.liferay.portal.service.osgi.externalpolicy.LiferayExternalAttachmentProvider;
import com.liferay.portal.service.remote.JAXWSServiceImpl;
import com.liferay.portal.service.remote.JAXWSServiceImpl1;
import com.liferay.portal.service.remote.JAXWSServiceImpl2;
import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.jaxws22.spi.ProviderImpl;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;
import org.apache.cxf.ws.policy.WSPolicyFeature;
import org.apache.cxf.ws.policy.attachment.external.DomainExpressionBuilderRegistry;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.apache.ws.security.handler.WSHandlerConstants;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.xml.sax.SAXException;

import javax.servlet.ServletRegistration;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.ws.Endpoint;
import javax.xml.ws.spi.Provider;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Tomas Polesovsky
 */
public class Activator implements BundleActivator {

	private static final String WSU_NS
		= "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd";

	List<Endpoint> endpoints;
	Server httpServer;
	BundleContext _bundleContext;

	@Override
	public void start(BundleContext bundleContext) throws Exception {
		_bundleContext = bundleContext;

		_cxfServlet = new LiferayCXFOSGiServlet();

		initHTTPServer();

		LiferayExternalAttachmentProvider externalAttachmentProvider = new LiferayExternalAttachmentProvider(_cxfServlet.getBus());
		externalAttachmentProvider.setLocation(getClass().getResource("/policy-attachment.xml"));

		org.apache.jcp.xml.dsig.internal.dom.DOMKeyInfoFactory factory = null;
		if(factory != null){
			System.out.println("never reached");
		}

		try {
			initCXFEnv();
		} catch (Exception e) {
			stop(_bundleContext);
			e.printStackTrace();
		}
	}

	protected void initHTTPServer() throws Exception {
		ServletHolder servlet = new ServletHolder(_cxfServlet);
		servlet.setName("soap");
		servlet.setForcedPath("soap");

		httpServer = new Server(11112);
		ContextHandlerCollection contexts = new ContextHandlerCollection();
		httpServer.setHandler(contexts);
		ServletContextHandler root = new ServletContextHandler(contexts, "/", ServletContextHandler.SESSIONS);

		root.addServlet(servlet, "/soap/*");

		httpServer.start();
	}

	protected void initCXFEnv() throws Exception {

		endpoints = new ArrayList<Endpoint>();

		// register our Bus instance for the provider
		BusFactory.setThreadDefaultBus(_cxfServlet.getBus());

		ProviderImpl provider = new ProviderImpl();

		Endpoint endpoint;


		JAXWSServiceImpl serviceImpl = new JAXWSServiceImpl();
		endpoint = provider.createEndpoint(null, serviceImpl);
		addEndpointProperties(endpoint);
		endpoint.publish("/service");
		endpoints.add(endpoint);


		JAXWSServiceImpl1 serviceImpl1 = new JAXWSServiceImpl1();
		endpoint = provider.createEndpoint(null, serviceImpl1);
		addEndpointProperties(endpoint);
		endpoint.publish("/service1");
		endpoints.add(endpoint);

		JAXWSServiceImpl2 serviceImpl2 = new JAXWSServiceImpl2();
		endpoint = provider.createEndpoint(null, serviceImpl2);
		addEndpointProperties(endpoint);

		WSPolicyFeature wsPolicyFeature = new LiferayWSPolicyFeature();
		((EndpointImpl) endpoint).getFeatures().add(wsPolicyFeature);

		endpoint.publish("/service2");
		endpoints.add(endpoint);


//			//**** Policy ****
//			Policy policy = cxf.getBus().getExtension(PolicyBuilder.class).
//				getPolicy(getClass().getResourceAsStream("/test-policy.xml"));



//			((EndpointImpl) endpoint).getServerFactory().getFeatures().add(new WSPolicyFeature(policy));
//			((EndpointImpl) endpoint).getServiceFactory().getFeatures().add(new WSPolicyFeature(policy));
//			((EndpointImpl) endpoint).getFeatures().add(new WSPolicyFeature(policy));

//			addSecurity(endpoint, cxf.getBus());

	}

	protected void addEndpointProperties(Endpoint endpoint) {
		// *** jax-ws security configuration ***
		Map<String, Object> jaxWSProps = endpoint.getProperties();
		jaxWSProps.put("ws-security.callback-handler", new ServiceKeystorePasswordCallback());
		jaxWSProps.put("ws-security.encryption.properties", "serviceKeystore.properties");
		jaxWSProps.put("ws-security.signature.properties", "serviceKeystore.properties");
//		jaxWSProps.put("ws-security.encryption.username", "useReqSigCert");
		jaxWSProps.put("ws-security.encryption.username", "myservicekey");

	}

	@Override
	public void stop(BundleContext bundleContext) {
		if (endpoints != null) {
			for (Endpoint endpoint : endpoints) {
				endpoint.stop();
			}
			endpoints = null;
		}

		if (httpServer != null) {
			try {
				httpServer.stop();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		httpServer = null;
	}

	protected void addSecurity(Endpoint endpoint, Bus bus) throws ParserConfigurationException, SAXException, IOException {

//		boolean wsSecPolicy = true;
//		if(wsSecPolicy) {
//			Map<String, Object> properties = endpoint.getProperties();
//
//			properties.put("ws-security.callback-handler", new ServiceKeystorePasswordCallback());
//			properties.put("ws-security.encryption.properties", "serviceKeystore.properties");
//			properties.put("ws-security.signature.properties", "serviceKeystore.properties");
//			properties.put("ws-security.encryption.username", "useReqSigCert");
//
//			return;
//		}

		if (!(endpoint instanceof EndpointImpl)) {
			System.out.println("Not an CXF endpoint!");
		}

		EndpointImpl endpointImpl = (EndpointImpl) endpoint;
		org.apache.cxf.endpoint.Endpoint cxfEndpoint =
			endpointImpl.getServer().getEndpoint();

/////////////////////////
//
//		PolicyEngine pe = bus.getExtension(PolicyEngine.class);
//		if(pe == null || !pe.isEnabled()) {
//			throw new RuntimeException("PolicyEngine is not available!");
//		}
//
//		Policy policy = bus.getExtension(PolicyBuilder.class).getPolicy(getClass().getResourceAsStream("/message-out-policy.xml"));
//		EndpointPolicy pe = new EndpointPolicyImpl(policy, pe, false, null);
//
//		pe.setServerEndpointPolicy(cxfEndpoint.getEndpointInfo(), new EndpointPolicyImpl(policy));
//
//		if(true)return;
////////////////////////////

		// check incoming messages
		Map<String,Object> inProps= new HashMap<String,Object>();
//		WSS4JInInterceptor wssIn = new WSS4JInInterceptor(inProps);
//		cxfEndpoint.getInInterceptors().add(wssIn);

		// process outgoing messages
		Map<String,Object> outProps = new HashMap<String,Object>();
		WSS4JOutInterceptor wssOut = new WSS4JOutInterceptor(outProps);
		cxfEndpoint.getOutInterceptors().add(wssOut);

		outProps.put(WSHandlerConstants.ACTION, "Timestamp Signature");
		// use just TimeStamp with Signature
//		outProps.put(WSHandlerConstants.USER, "myAlias");
		outProps.put(WSHandlerConstants.SIGNATURE_USER, "myservicekey");
		outProps.put(WSHandlerConstants.PW_CALLBACK_CLASS,
			ServiceKeystorePasswordCallback.class.getName());
		outProps.put(WSHandlerConstants.SIG_PROP_FILE, "/serviceKeystore.properties");
		outProps.put(WSHandlerConstants.SIG_KEY_ID, "DirectReference");
		outProps.put(WSHandlerConstants.SIGNATURE_PARTS, "{Element}{" + WSU_NS + "}Timestamp;"
			+ "{Element}{http://schemas.xmlsoap.org/soap/envelope/}Body");
		outProps.put(WSHandlerConstants.SIG_ALGO, "http://www.w3.org/2000/09/xmldsig#rsa-sha1");

		// register our classloader to be able to
		// 1. load the keystore properties
		// 2, set context classloader to this project
//		bus.getExtension(ResourceManager.class).addResourceResolver(
//			new ClassLoaderResolver(Activator.class.getClassLoader()){
//				@Override
//				public <T> T resolve(String resourceName, Class<T> resourceType) {
//					// return current classloader when WSS4J looks for context classloader
//					if("".equals(resourceName) && ClassLoader.class.equals(resourceType)){
//						return resourceType.cast(Activator.class.getClassLoader());
//					}
//
//					// try to resolve resource using the current classloader
//					return super.resolve(resourceName, resourceType);
//				}
//			});



//		bus.getExtension(ResourceManager.class).addResourceResolver(new ResourceResolver() {
//			@Override
//			public <T> T resolve(String resourceName, Class<T> resourceType) {
//				if ("".equals(resourceName) && ClassLoader.class.equals(resourceType)) {
//					return resourceType.cast(Activator.class.getClassLoader());
//				}
//				return null;
//			}
//
//			@Override
//			public InputStream getAsStream(String name) {
//				return null;
//			}
//		});
//

/*
		Map<String, Object> outProps = new HashMap<String, Object>();
		outProps.put("action", "UsernameToken Timestamp Signature");

		outProps.put("signaturePropFile", "etc/Server_Decrypt.properties");
		outProps.put("signatureKeyIdentifier", "DirectReference");
		outProps.put("signatureParts", "{Element}{" + WSU_NS + "}Timestamp;"
			+ "{Element}{http://schemas.xmlsoap.org/soap/envelope/}Body");
		outProps.put("signatureAlgorithm", "http://www.w3.org/2000/09/xmldsig#rsa-sha1");

		impl.getOutInterceptors().add(new WSS4JOutInterceptor(outProps));

		Map<String, Object> inProps = new HashMap<String, Object>();

		inProps.put("action", "UsernameToken Timestamp Signature");
		inProps.put("passwordType", "PasswordDigest");
		inProps.put("passwordCallbackClass", "demo.wssec.server.UTPasswordCallback");

		inProps.put("signaturePropFile", "etc/Server_SignVerf.properties");
		inProps.put("signatureKeyIdentifier", "DirectReference");
		inProps.put("signatureAlgorithm", "http://www.w3.org/2000/09/xmldsig#rsa-sha1");

		impl.getInInterceptors().add(new WSS4JInInterceptor(inProps));

		// Check to make sure that the SOAP Body and Timestamp were signed
		DefaultCryptoCoverageChecker coverageChecker = new DefaultCryptoCoverageChecker();
		impl.getInInterceptors().add(coverageChecker);
		*/

	}

	private CXFNonSpringServlet _cxfServlet;
	private ServletRegistration.Dynamic _cxfServletRegistration;
}