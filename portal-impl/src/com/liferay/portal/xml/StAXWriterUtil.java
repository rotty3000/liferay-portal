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

package com.liferay.portal.xml;

import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;

/**
 * @author Daniel Kocsis
 */
public class StAXWriterUtil {

	public static void cleanUp(XMLEventWriter xmlEventWriter)
			throws XMLStreamException {

		xmlEventWriter.flush();
		xmlEventWriter.close();
	}

	public static XMLOutputFactory getXMLOutputFactory() {
		return _xmlInputFactory;
	}

	private static XMLOutputFactory _createXMLInputFactory() {
		XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();

		xmlOutputFactory.setProperty(
			XMLOutputFactory.IS_REPAIRING_NAMESPACES, Boolean.FALSE);

		return xmlOutputFactory;
	}

	private static XMLOutputFactory _xmlInputFactory = _createXMLInputFactory();

}