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

package com.liferay.sync.engine.upgrade.v3_4_2;

import com.liferay.sync.encryptor.SyncEncryptor;
import com.liferay.sync.engine.model.SyncAccount;
import com.liferay.sync.engine.service.SyncAccountService;
import com.liferay.sync.engine.upgrade.BaseUpgradeProcess;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Dennis Ju
 * @author Shinn Lok
 */
public class UpgradeProcess_3_4_2 extends BaseUpgradeProcess {

	@Override
	public int getThreshold() {
		return 3402;
	}

	@Override
	public void upgrade() throws Exception {
		for (SyncAccount syncAccount : SyncAccountService.findAll()) {
			try {
				String oAuthTokenSecret = SyncEncryptor.decrypt(
					syncAccount.getOAuthTokenSecret(), 1);

				syncAccount.setOAuthTokenSecret(
					SyncEncryptor.encrypt(oAuthTokenSecret, 2));

				String password = SyncEncryptor.decrypt(
					syncAccount.getPassword(), 1);

				syncAccount.setPassword(SyncEncryptor.encrypt(password, 2));

				SyncAccountService.update(syncAccount);
			}
			catch (Exception e) {
				_logger.error(e.getMessage(), e);
			}
		}
	}

	private static final Logger _logger = LoggerFactory.getLogger(
		UpgradeProcess_3_4_2.class);

}