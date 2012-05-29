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

package com.liferay.portal.service.persistence.lar;

import com.liferay.portal.kernel.lar.PortletDataContext;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.model.Image;
import com.liferay.portal.service.persistence.BaseLarPersistence;
import com.liferay.portal.service.persistence.impl.BaseLarPersistenceImpl;
import com.liferay.portal.service.persistence.lar.ImageLarPersistence;

/**
 * @author Mate Thurzo
 */
public class ImageLarPersistenceImpl extends BaseLarPersistenceImpl<Image>
	implements ImageLarPersistence {

	public void deserialize(Document document) {
	}

	public void serialize(Image image, PortletDataContext portletDataContext)
		throws Exception {

	}
}
