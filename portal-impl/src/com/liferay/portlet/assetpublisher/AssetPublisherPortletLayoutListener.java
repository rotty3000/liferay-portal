/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

package com.liferay.portlet.assetpublisher;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.BasePortletLayoutListener;
import com.liferay.portal.kernel.portlet.PortletLayoutListener;
import com.liferay.portal.kernel.portlet.PortletLayoutListenerException;
import com.liferay.portal.model.Layout;
import com.liferay.portal.service.LayoutLocalServiceUtil;
import com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil;

/**
 * @author Zsolt Berentey
 */
public class AssetPublisherPortletLayoutListener
	extends BasePortletLayoutListener implements PortletLayoutListener {

	@Override
	public void onRemoveFromLayout(String portletId, long plid)
		throws PortletLayoutListenerException {

		if (_log.isDebugEnabled()) {
			_log.debug("Remove " + portletId + " from layout " + plid);
		}

		try {
			Layout layout = LayoutLocalServiceUtil.getLayout(plid);

			JournalArticleLocalServiceUtil.deleteLayoutArticleReferences(
				layout.getGroupId(), layout.getUuid());
		}
		catch (Exception e) {
			throw new PortletLayoutListenerException(e);
		}
	}

	private static Log _log = LogFactoryUtil.getLog(
		AssetPublisherPortletLayoutListener.class);

}