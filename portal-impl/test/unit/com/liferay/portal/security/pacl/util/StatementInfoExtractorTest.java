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

package com.liferay.portal.security.pacl.util;

import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.Tuple;
import com.liferay.portal.security.pacl.sql.InvalidStatementException;
import com.liferay.portal.util.TableRegistryUtil;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import org.testng.Assert;

/**
 * @author Zsolt Berentey
 */
public class StatementInfoExtractorTest {

	@Before
	public void setUp() throws Exception {
		PortalClassLoaderUtil.setClassLoader(
			Thread.currentThread().getContextClassLoader());

		TableRegistryUtil.init();
	}

	@Test
	public void testCreateTableStatements() {
		StatementInfo info;

		for (Tuple match : _createTableStatements) {
			String sql = (String)match.getObject(0);

			info = getStatementInfo(sql);

			assertMainTable(sql, info, true, (String)match.getObject(1));
		}
	}

	@Test
	public void testMainTable() {
		String sql = "drop index index_name";

		StatementInfo info = getStatementInfo(sql);

		assertMainTable(sql, info, false, null);

		sql = "delete from users_usergroups where exists (select 1 from user_)";

		info = getStatementInfo(sql);

		assertMainTable(sql, info, true, "users_usergroups");

		sql =
			"select * from Users_UserGroups where id = 1 and exists " +
				"(select 1 from usergroup inner join usergroupgrouprole)";

		info = getStatementInfo(sql);

		assertMainTable(sql, info, false, null);
	}

	@Test
	public void testObjectType() {
		StatementInfo info = getStatementInfo(
			"create unique index 'database' on usergroup (col1, col2)");

		assertObjectType(info, "index");

		info = getStatementInfo("select * from Users_UserGroups where id = 1");

		assertObjectType(info, null);

		info = getStatementInfo("alter table user_ do something here");

		assertObjectType(info, "table");
	}

	@Test
	public void testReadTables() {
		StatementInfo info = getStatementInfo(
			"select * from user_, usergroup where username = 'assetentry' or " +
				"username like 'some % assetentry %' and exists " +
					"(select 1 from usergroup inner join usergroupgrouprole)");

		assertReadTables(
			info, new String[] {"user_", "usergroup", "usergroupgrouprole"});

		info = getStatementInfo("drop index index_name");

		assertReadTables(info, null);

		info = getStatementInfo("select * from Counter");

		assertReadTables(info, new String[] {"counter"});
	}

	@Test
	public void testTableNameMatching() {
		StatementInfo info;

		for (Tuple match : _companyMatches) {
			String sql = (String)match.getObject(0);

			info = getStatementInfo(sql);

			assertReadTablesContains(
				sql, info, "company", (Boolean)match.getObject(1));
		}
	}

	protected void assertMainTable(
		String sql, StatementInfo info, boolean hasMain, String expectedName) {

		Assert.assertEquals(info.hasMainTable(), hasMain, sql);

		if (expectedName == null) {
			Assert.assertNull(info.getMainTable(), sql);
		}
		else {
			Assert.assertEquals(info.getMainTable(), expectedName, sql);
		}
	}

	protected void assertObjectType(StatementInfo info, String expectedType) {
		if (expectedType == null) {
			Assert.assertNull(info.getObjectType());
		}
		else {
			Assert.assertEquals(info.getObjectType(), expectedType);
		}
	}

	protected void assertReadTables(
		StatementInfo info, String[] expectedNames) {

		List<String> readTables = info.getReadTables();

		if (expectedNames == null) {
			Assert.assertEquals(readTables.size(), 0);
		}
		else {
			Assert.assertEquals(readTables.size(), expectedNames.length);

			for (String expectedName : expectedNames) {
				Assert.assertTrue(readTables.contains(expectedName));
			}
		}
	}

	protected void assertReadTablesContains(
		String sql, StatementInfo info, String tableName, boolean contains) {

		List<String> readTables = info.getReadTables();

		Assert.assertEquals(
			readTables.contains(tableName), contains, "SQL: " + sql);
	}

	protected StatementInfo getStatementInfo(String sql) {
		try {
			return StatementInfoExtractor.getStatementInfo(sql);
		}
		catch (InvalidStatementException ise) {
			Assert.fail();

			return new StatementInfo();
		}
	}

	private static Tuple[] _companyMatches = {
		new Tuple("select * from user_ where user_.companyId = 1", false),
		new Tuple(
			"select * from user_ where schema.company.name = 'something'",
			true),
		new Tuple("select * from user_,`company` where ...", true),
		new Tuple("select * from user_,company where ...", true),
		new Tuple(
			"select * from user_,schema.company where ...",
			true),
		new Tuple("select * from user_ where mycompany=1234", false),
		new Tuple("select * from user_ where company=1234", false),
		new Tuple(
			"select * from user_ where user_.companyId=[company].companyId",
			true),
		new Tuple(
			"select * from user_ where user_.name='No company.'", false)
	};

	private static Tuple[] _createTableStatements = {
		new Tuple("create table schema.foobar(", "foobar"),
		new Tuple("create table schema.[foobar] (", "foobar"),
		new Tuple("create table schema.[foo bar] (", "foo bar"),
		new Tuple("create table schema.\"foo bar\" (", "foo bar"),
		new Tuple("create table schema.`foo bar` (", "foo bar"),
		new Tuple("create table [db schema].[foo bar](", "foo bar"),
		new Tuple("create table \"db schema\".foobar (", "foobar"),
		new Tuple("create table `db schema`.[foo bar](", "foo bar")
	};

}