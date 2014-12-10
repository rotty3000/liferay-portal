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
package com.liferay.site.map.web.configuration;

import aQute.bnd.annotation.metatype.Meta;

/**
 * @author Raymond Augé
 * @author Peter Fellwock
 */
@Meta.OCD(id = "com.liferay.site.map.web", localization = "content.Language")
public interface SiteMapConfiguration {

	@Meta.AD(deflt = "", id = "displayDepth", required = false)
	public String getDisplayDepth();

	@Meta.AD(deflt = "", id = "displayStyle", required = false)
	public String getDisplayStyle();

	@Meta.AD(id = "displayStyleGroupId", required = false)
	public String getDisplayStyleGroupId();

	@Meta.AD(deflt = "", id = "rootLayoutUuid", required = false)
	public String getRootLayoutUuid();

	@Meta.AD(deflt = "false", id = "includeRootInTree", required = false)
	public String includeRootInTree();

	@Meta.AD(deflt = "false", id = "showCurrentPage", required = false)
	public String showCurrentPage();

	@Meta.AD(deflt = "false", id = "showHiddenPages", required = false)
	public String showHiddenPages();

	@Meta.AD(deflt = "false", id = "useHtmlTitle", required = false)
	public String useHtmlTitle();

}