/**
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.kernel.jndi;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.HashMap;
import java.util.Map;

import javax.naming.Context;
import javax.naming.NamingException;

/**
 * @author Brian Wing Shun Chan
 * @author Sandeep Soni
 */
public class JNDIUtil {

	public static Object lookup(Context ctx, String location)
		throws NamingException {

		return lookup(ctx, location, false);
	}

	public static Object lookup(Context ctx, String location, boolean cache)
		throws NamingException {

		Object obj = null;

		if (cache) {
			obj = _cache.get(location);

			if (obj == null) {
				obj = _lookup(ctx, location);

				_cache.put(location, obj);
			}
		}
		else {
			obj = _lookup(ctx, location);
		}

		return obj;
	}

	private static Object _lookup(Context ctx, String location)
		throws NamingException {

		if (_log.isDebugEnabled()) {
			_log.debug("Lookup " + location);
		}

		Object obj = null;

		try {
			obj = ctx.lookup(location);
		}
		catch (NamingException ne1) {

			// java:comp/env/ObjectName to ObjectName

			if (location.indexOf("java:comp/env/") != -1) {
				try {
					String newLocation = StringUtil.replace(
						location, "java:comp/env/", "");

					if (_log.isDebugEnabled()) {
						_log.debug(ne1.getMessage());
						_log.debug("Attempt " + newLocation);
					}

					obj = ctx.lookup(newLocation);
				}
				catch (NamingException ne2) {

					// java:comp/env/ObjectName to java:ObjectName

					String newLocation = StringUtil.replace(
						location, "comp/env/", "");

					if (_log.isDebugEnabled()) {
						_log.debug(ne2.getMessage());
						_log.debug("Attempt " + newLocation);
					}

					obj = ctx.lookup(newLocation);
				}
			}

			// java:ObjectName to ObjectName

			else if (location.indexOf("java:") != -1) {
				try {
					String newLocation = StringUtil.replace(
						location, "java:", "");

					if (_log.isDebugEnabled()) {
						_log.debug(ne1.getMessage());
						_log.debug("Attempt " + newLocation);
					}

					obj = ctx.lookup(newLocation);
				}
				catch (NamingException ne2) {

					// java:ObjectName to java:comp/env/ObjectName

					String newLocation = StringUtil.replace(
						location, "java:", "java:comp/env/");

					if (_log.isDebugEnabled()) {
						_log.debug(ne2.getMessage());
						_log.debug("Attempt " + newLocation);
					}

					obj = ctx.lookup(newLocation);
				}
			}

			// ObjectName to java:ObjectName

			else if (location.indexOf("java:") == -1) {
				try {
					String newLocation = "java:" + location;

					if (_log.isDebugEnabled()) {
						_log.debug(ne1.getMessage());
						_log.debug("Attempt " + newLocation);
					}

					obj = ctx.lookup(newLocation);
				}
				catch (NamingException ne2) {

					// ObjectName to java:comp/env/ObjectName

					String newLocation = "java:comp/env/" + location;

					if (_log.isDebugEnabled()) {
						_log.debug(ne2.getMessage());
						_log.debug("Attempt " + newLocation);
					}

					obj = ctx.lookup(newLocation);
				}
			}
			else {
				throw new NamingException();
			}
		}

		return obj;
	}

	private static Log _log = LogFactoryUtil.getLog(JNDIUtil.class);

	private static Map<String, Object> _cache = new HashMap<String, Object>();

}