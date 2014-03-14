/**
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved.
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

package com.liferay.osgi.http.internal.holder;


import javax.servlet.Servlet;

import org.osgi.service.http.runtime.ServletDTO;

/**
 * @author Raymond Augé
 */
public class ServletHolder extends Holder<Servlet, ServletDTO> {

	public ServletHolder(Servlet servlet, ServletDTO rervletDTO) {
		super(servlet, rervletDTO);
	}

	@Override
	public void destroy() {
		t.destroy();
	}

	@Override
	public Servlet match(String requestURI, String name) {
		if (name != null) {
			if (d.name.equals(name)) {
				return t;
			}

			return null;
		}

		String[] patterns = d.patterns;

		for (String pattern : patterns) {
			if ((pattern != null) && match(pattern, requestURI, true)) {
				return t;
			}
		}

		return null;
	}

}