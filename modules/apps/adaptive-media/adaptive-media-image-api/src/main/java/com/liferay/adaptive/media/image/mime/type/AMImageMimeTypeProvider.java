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

package com.liferay.adaptive.media.image.mime.type;

/**
 * Provides the supported Adaptive Media image MIME types.
 *
 * @author Sergio González
 */
public interface AMImageMimeTypeProvider {

	/**
	 * Returns the supported MIME types that generate Adaptive Media images.
	 *
	 * @return the supported MIME types that generate Adaptive Media images
	 */
	public String[] getSupportedMimeTypes();

	/**
	 * Returns <code>true</code> if the provided MIME type generates Adaptive
	 * Media images.
	 *
	 * @param  mimeType the MIME type
	 * @return <code>true</code> if the MIME type generates Adaptive Media
	 *         images; <code>false</code> otherwise
	 */
	public boolean isMimeTypeSupported(String mimeType);

}