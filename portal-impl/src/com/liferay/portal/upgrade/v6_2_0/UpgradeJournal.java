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

package com.liferay.portal.upgrade.v6_2_0;

import com.liferay.portal.kernel.upgrade.RenameUpgradePortletPreferences;
import com.liferay.portal.upgrade.v6_2_0.util.JournalFeedTable;

import java.sql.SQLException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Brian Wing Shun Chan
 * @author Marcellus Tavares
 */
public class UpgradeJournal extends RenameUpgradePortletPreferences {

	public UpgradeJournal() {
		_preferenceNamesMap.put("templateId", "ddmTemplateKey");
	}

	@Override
	protected void doUpgrade() throws Exception {
		try {
			runSQL(
				"alter_column_name JournalFeed feedType feedFormat " +
					"VARCHAR(75) null");
		}
		catch (SQLException sqle) {
			upgradeTable(
				JournalFeedTable.TABLE_NAME, JournalFeedTable.TABLE_COLUMNS,
				JournalFeedTable.TABLE_SQL_CREATE,
				JournalFeedTable.TABLE_SQL_ADD_INDEXES);
		}

		updatePortletPreferences();
	}

	@Override
	protected String[] getPortletIds() {
		return new String[] {"56_INSTANCE_%"};
	}

	@Override
	protected Map<String, String> getPreferenceNamesMap() {
		return _preferenceNamesMap;
	}

	private Map<String, String> _preferenceNamesMap =
		new HashMap<String, String>();

}