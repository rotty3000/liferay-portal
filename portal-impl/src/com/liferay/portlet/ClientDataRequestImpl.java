/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
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

package com.liferay.portlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import java.util.Collection;

import javax.portlet.ClientDataRequest;
import javax.portlet.PortletException;

import javax.servlet.http.Part;

/**
 * @author Brian Wing Shun Chan
 */
public abstract class ClientDataRequestImpl
	extends PortletRequestImpl implements ClientDataRequest {

	@Override
	public String getCharacterEncoding() {
		return getHttpServletRequest().getCharacterEncoding();
	}

	@Override
	public int getContentLength() {
		return getHttpServletRequest().getContentLength();
	}

	@Override
	public long getContentLengthLong() {

		// TODO portlet3
		return 0;
	}

	@Override
	public String getContentType() {
		return getHttpServletRequest().getContentType();
	}

	@Override
	public String getMethod() {
		return getHttpServletRequest().getMethod();
	}

	@Override
	public Part getPart(String name) throws IOException, PortletException {

		// TODO portlet3
		return null;
	}

	@Override
	public Collection<Part> getParts() throws IOException, PortletException {

		// TODO portlet3
		return null;
	}

	@Override
	public InputStream getPortletInputStream() throws IOException {
		return getHttpServletRequest().getInputStream();
	}

	@Override
	public BufferedReader getReader()
		throws IOException, UnsupportedEncodingException {

		_calledGetReader = true;

		return getHttpServletRequest().getReader();
	}

	@Override
	public void setCharacterEncoding(String enc)
		throws UnsupportedEncodingException {

		if (_calledGetReader) {
			throw new IllegalStateException();
		}

		getHttpServletRequest().setCharacterEncoding(enc);
	}

	private boolean _calledGetReader;

}