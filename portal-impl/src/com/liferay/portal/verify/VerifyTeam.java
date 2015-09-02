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

package com.liferay.portal.verify;

import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.Team;
import com.liferay.portal.service.TeamLocalServiceUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Brian Wing Shun Chan
 */
public class VerifyTeam extends VerifyProcess {

	@Override
	protected void doVerify() throws Exception {
		moveStagingTeamsToLive();
	}

	protected void moveStagingTeamsToLive() throws Exception {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = DataAccess.getUpgradeOptimizedConnection();

			ps = con.prepareStatement(
				"select liveTeam.teamId liveTeamId, " +
				"stagingGroup.liveGroupId liveGroupId, " +
				"stagingTeam.teamId stagingTeamId " +
				"from Team stagingTeam " +
				"inner join Group_ stagingGroup " +
				"on stagingGroup.groupId = stagingTeam.groupId " +
				"and stagingGroup.liveGroupId != 0 " +
				"left join Team liveTeam " +
				"on liveTeam.groupId = stagingGroup.liveGroupId " +
				"and liveTeam.name = stagingTeam.name");

			rs = ps.executeQuery();

			while (rs.next()) {
				int liveTeamId = rs.getInt("liveTeamId");
				int liveGroupId = rs.getInt("liveGroupId");
				int stagingTeamId = rs.getInt("stagingTeamId");

				Team stagingTeam = TeamLocalServiceUtil.getTeam(stagingTeamId);

				if (liveTeamId > 0) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"Staged team \"" + stagingTeam.getName() +
								"\" already exists in live environment. " +
								"Permissions assigned in staging will not be " +
								"merged with live and will be deleted. " +
								"Please verify the team permissions are " +
								"correctly assigned to all unpublished " +
								"entities.");
					}

					TeamLocalServiceUtil.deleteTeam(stagingTeamId);
				}
				else {
					stagingTeam.setGroupId(liveGroupId);
					TeamLocalServiceUtil.updateTeam(stagingTeam);
				}
			}
		}
		finally {
			DataAccess.cleanUp(con, ps, rs);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(VerifyTeam.class);

}