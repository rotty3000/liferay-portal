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

package com.liferay.portal.security.pacl.checker;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.security.pacl.util.StatementInfo;
import com.liferay.portal.security.pacl.util.StatementInfoExtractor;

import java.security.Permission;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * @author Brian Wing Shun Chan
 * @author Raymond Augé
 * @author Zsolt Berentey
 **/
public class SQLChecker extends BaseChecker {

	public void afterPropertiesSet() {
		initTableNames();
	}

	@Override
	public AuthorizationProperty generateAuthorizationProperty(
		Object... arguments) {

		if ((arguments == null) || (arguments.length != 1) ||
			!(arguments[0] instanceof String)) {

			return null;
		}

		String sql = (String)arguments[0];

		sql = StringUtil.trim(sql);

		sql = sql.toLowerCase();

		StatementInfo statementInfo = StatementInfoExtractor.getStatementInfo(
			sql);

		if (statementInfo == null) {
			_log.error("Unable to parse SQL " + sql);

			return null;
		}

		String objectType = statementInfo.getObjectType();

		if (objectType == null) {
			objectType = StringPool.BLANK;
		}
		else {
			objectType = StringPool.DASH + objectType;
		}

		String key = "security-manager-sql-tables-";
		String value = null;

		if (statementInfo.hasMainTable()) {
			key = key.concat(statementInfo.getOperation());
			key = key.concat(objectType);

			value = statementInfo.getMainTable();
		}
		else if (!statementInfo.isParseTables()) {

			// Non-table operations e.g. drop index

			key = key.concat(statementInfo.getOperation());
			key = key.concat(objectType);

			value = "yes";
		}

		if ((value != null) &&
			!isAllowedTable(statementInfo.getOperation() + objectType, value)) {

			AuthorizationProperty authorizationProperty =
				new AuthorizationProperty();

			authorizationProperty.setKey(key);
			authorizationProperty.setValue(value);

			return authorizationProperty;
		}

		List<String> readTables = statementInfo.getReadTables();

		if (readTables.isEmpty()) {
			return null;
		}

		if (!isAllowedTables("select", null, readTables)) {
			AuthorizationProperty authorizationProperty =
				new AuthorizationProperty();

			authorizationProperty.setKey("security-manager-sql-tables-select");
			authorizationProperty.setValue(StringUtil.merge(readTables));

			return authorizationProperty;
		}

		return null;
	}

	public boolean hasSQL(String sql) {
		sql = StringUtil.trim(sql);

		sql = sql.toLowerCase();

		StatementInfo statementInfo = StatementInfoExtractor.getStatementInfo(
			sql);

		if (statementInfo == null) {
			_log.error("Unable to parse SQL " + sql);

			return false;
		}

		if (statementInfo.hasMainTable()) {
			if (!isAllowedTable(
					statementInfo.getOperation(), statementInfo.getObjectType(),
					statementInfo.getMainTable())) {

				return false;
			}
		}
		else if (!statementInfo.isParseTables()) {

			// Non-table operations e.g. drop index

			if (!isAllowedTable(
					statementInfo.getOperation(), statementInfo.getObjectType(),
					"yes")) {

				return false;
			}
		}

		return isAllowedTables("select", null, statementInfo.getReadTables());
	}

	public boolean implies(Permission permission) {
		throw new UnsupportedOperationException();
	}

	protected void addTableNames(String key) {
		Set<TableNameWrapper> propertySet = new HashSet<TableNameWrapper>();

		for (String property : getPropertyArray(key)) {
			propertySet.add(new TableNameWrapper(property.toLowerCase()));
		}

		_tableNames.put(key.substring(28), propertySet);
	}

	protected void initTableNames() {
		Properties properties = getProperties();

		for (Enumeration<?> propertyNames = properties.propertyNames();
				propertyNames.hasMoreElements();) {

			String propertyName = (String)propertyNames.nextElement();

			if (propertyName.startsWith("security-manager-sql-tables-")) {
				addTableNames(propertyName);
			}
		}
	}

	protected boolean isAllowedTable(String key, String tableName) {
		Set<TableNameWrapper> tableNames = _tableNames.get(key);

		if (tableNames == null) {
			return false;
		}

		for (TableNameWrapper wrapper : tableNames) {
			if (wrapper.equals(tableName)) {
				return true;
			}
		}

		return false;
	}

	protected boolean isAllowedTable(
		String operation, String objectType, String tableName) {

		if (objectType == null) {
			objectType = StringPool.BLANK;
		}
		else {
			objectType = StringPool.DASH + objectType;
		}

		if (tableName == null) {
			return true;
		}

		if (isAllowedTable("all", tableName) ||
			isAllowedTable(operation + objectType, tableName)) {

			return true;
		}

		if (!objectType.isEmpty()) {
			if (isAllowedTable(operation + "-any", tableName)) {
				return true;
			}
		}

		return false;
	}

	protected boolean isAllowedTables(
		String operation, String objectType, List<String> tableNames) {

		for (String tableName : tableNames) {
			if (!isAllowedTable(operation, objectType, tableName)) {
				return false;
			}
		}

		return true;
	}

	private static Log _log = LogFactoryUtil.getLog(SQLChecker.class);

	private Map<String, Set<TableNameWrapper>> _tableNames =
		new HashMap<String, Set<TableNameWrapper>>();

	private class TableNameWrapper {

		public TableNameWrapper(String tableName) {
			if (tableName.contains(StringPool.STAR)) {
				_wildCardCheck = true;

				try {
					_tableNamePattern = Pattern.compile(
						tableName.replaceAll("\\*", ".*?"));
				}
				catch (PatternSyntaxException pse) {
					_wildCardCheck = false;
				}
			}

			_tableName = tableName;
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof String)) {
				return false;
			}

			String tableName = (String)obj;

			if (_wildCardCheck) {
				Matcher m = _tableNamePattern.matcher(tableName);

				return m.matches();
			}

			return _tableName.equals(tableName);
		}

		private String _tableName = null;
		private Pattern _tableNamePattern = null;
		private boolean _wildCardCheck = false;

	}

}