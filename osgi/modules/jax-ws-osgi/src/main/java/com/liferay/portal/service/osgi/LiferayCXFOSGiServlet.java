package com.liferay.portal.service.osgi;

import org.apache.cxf.Bus;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

/**
 * @author Tomas Polesovsky
 */
public class LiferayCXFOSGiServlet extends CXFNonSpringServlet {

	@Override
	protected void loadBus(ServletConfig sc) {
		super.loadBus(sc);

		// configure this classloader to be used as ContextClassLoader, so that all configuration files and classes are going to be loaded from this bundle as well

		getBus().setExtension(LiferayCXFOSGiServlet.class.getClassLoader(), ClassLoader.class);
	}
}
