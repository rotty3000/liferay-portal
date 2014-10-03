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

package com.liferay.sso.cas.autologin;

import com.liferay.portal.NoSuchUserException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.CompanyConstants;
import com.liferay.portal.model.User;
import com.liferay.portal.security.auth.AutoLogin;
import com.liferay.portal.security.auth.BaseAutoLogin;
import com.liferay.portal.security.ldap.PortalLDAPImporterUtil;
import com.liferay.portal.security.sso.SSO;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portal.util.PropsValues;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;

/**
 * @author Brian Wing Shun Chan
 * @author Jorge Ferrer
 * @author Wesley Gong
 * @author Daeyoung Song
 * @author Raymond Augé
 */
@Component(
	immediate = true,
	configurationPid = "com.liferay.sso.cas",
	configurationPolicy = ConfigurationPolicy.REQUIRE,
	property = {
		"cas.import.from.ldap=false",
		"cas.login.url=https://localhost:8443/cas-web/login",
		"cas.logout.on.session.expiration=false",
		"cas.logout.url=https://localhost:8443/cas-web/logout",
		"cas.no.such.user.redirect.url=http://localhost:8080",
		"cas.server.name=localhost:8080",
		"cas.server.url=https://localhost:8443/cas-web",
		"cas.service.url="
	},
	service = {AutoLogin.class, CASAutoLogin.class, SSO.class}
)
public class CASAutoLogin extends BaseAutoLogin implements SSO {

	public static final String CAS_FORCE_LOGOUT = "CAS_FORCE_LOGOUT";

	public static final String CAS_IMPORT_FROM_LDAP = "cas.import.from.ldap";

	public static final String CAS_LOGIN = "CAS_LOGIN";

	public static final String CAS_LOGIN_URL = "cas.login.url";

	public static final String CAS_LOGOUT_ON_SESSION_EXPIRATION =
		"cas.logout.on.session.expiration";

	public static final String CAS_LOGOUT_URL = "cas.logout.url";

	public static final String CAS_NO_SUCH_USER_EXCEPTION =
		"CAS_NO_SUCH_USER_EXCEPTION";

	public static final String CAS_NO_SUCH_USER_REDIRECT_URL =
		"cas.no.such.user.redirect.url";

	public static final String CAS_SERVER_NAME = "cas.server.name";

	public static final String CAS_SERVER_URL = "cas.server.url";

	public static final String CAS_SERVICE_URL = "cas.service.url";

	public String getNoSuchUserRedirectUrl() {
		return _noSuchUserRedirectUrl;
	}

	public String getServerName() {
		return _serverName;
	}

	public String getServerUrl() {
		return _serverUrl;
	}

	public String getServiceUrl() {
		return _serviceUrl;
	}

	@Override
	public String getSessionExpirationRedirectUrl() {
		if (_logoutOnSessionExpiration) {
			return _logoutUrl;
		}

		return null;
	}

	@Override
	public String getSignInUrl() {
		return _loginUrl;
	}

	public String getSignOutUrl() {
		return _logoutUrl;
	}

	public boolean isImportFromLdap() {
		return _importFromLdap;
	}

	@Override
	public boolean isLoginRedirectRequired() {
		return true;
	}

	@Override
	public boolean isRedirectRequired() {
		return true;
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		_importFromLdap = MapUtil.getBoolean(properties, CAS_IMPORT_FROM_LDAP);
		_logoutOnSessionExpiration = MapUtil.getBoolean(
			properties, CAS_LOGOUT_ON_SESSION_EXPIRATION);
		_loginUrl = MapUtil.getString(properties, CAS_LOGIN_URL);
		_logoutUrl = MapUtil.getString(properties, CAS_LOGOUT_URL);
		_noSuchUserRedirectUrl = MapUtil.getString(
			properties, CAS_NO_SUCH_USER_REDIRECT_URL);
		_serverName = MapUtil.getString(properties, CAS_SERVER_NAME);
		_serverUrl = MapUtil.getString(properties, CAS_SERVER_URL);
		_serviceUrl = MapUtil.getString(properties, CAS_SERVICE_URL);
	}

	@Modified
	protected void modified(Map<String, Object> properties) {
		_importFromLdap = MapUtil.getBoolean(properties, CAS_IMPORT_FROM_LDAP);
		_logoutOnSessionExpiration = MapUtil.getBoolean(
			properties, CAS_LOGOUT_ON_SESSION_EXPIRATION);
		_loginUrl = MapUtil.getString(properties, CAS_LOGIN_URL);
		_logoutUrl = MapUtil.getString(properties, CAS_LOGOUT_URL);
		_noSuchUserRedirectUrl = MapUtil.getString(
			properties, CAS_NO_SUCH_USER_REDIRECT_URL);
		_serverName = MapUtil.getString(properties, CAS_SERVER_NAME);
		_serverUrl = MapUtil.getString(properties, CAS_SERVER_URL);
		_serviceUrl = MapUtil.getString(properties, CAS_SERVICE_URL);
	}

	@Override
	protected String[] doHandleException(
		HttpServletRequest request, HttpServletResponse response, Exception e) {

		HttpSession session = request.getSession();

		if (e instanceof NoSuchUserException) {
			session.removeAttribute(CAS_LOGIN);

			session.setAttribute(CAS_NO_SUCH_USER_EXCEPTION, Boolean.TRUE);
		}

		_log.error(e, e);

		return null;
	}

	@Override
	protected String[] doLogin(
			HttpServletRequest request, HttpServletResponse response)
		throws Exception {

		HttpSession session = request.getSession();

		long companyId = PortalUtil.getCompanyId(request);

		String login = (String)session.getAttribute(CAS_LOGIN);

		if (Validator.isNull(login)) {
			Object noSuchUserException = session.getAttribute(
				CAS_NO_SUCH_USER_EXCEPTION);

			if (noSuchUserException == null) {
				return null;
			}

			session.removeAttribute(CAS_NO_SUCH_USER_EXCEPTION);

			session.setAttribute(CAS_FORCE_LOGOUT, Boolean.TRUE);

			request.setAttribute(
				AutoLogin.AUTO_LOGIN_REDIRECT, _noSuchUserRedirectUrl);

			return null;
		}

		String authType = PrefsPropsUtil.getString(
			companyId, PropsKeys.COMPANY_SECURITY_AUTH_TYPE,
			PropsValues.COMPANY_SECURITY_AUTH_TYPE);

		User user = null;

		if (_importFromLdap) {
			try {
				if (authType.equals(CompanyConstants.AUTH_TYPE_SN)) {
					user = PortalLDAPImporterUtil.importLDAPUser(
						companyId, StringPool.BLANK, login);
				}
				else {
					user = PortalLDAPImporterUtil.importLDAPUser(
						companyId, login, StringPool.BLANK);
				}
			}
			catch (SystemException se) {
				if (_log.isErrorEnabled()) {
					_log.error(se.getMessage(), se);
				}
			}
		}

		if (user == null) {
			if (authType.equals(CompanyConstants.AUTH_TYPE_SN)) {
				user = UserLocalServiceUtil.getUserByScreenName(
					companyId, login);
			}
			else {
				user = UserLocalServiceUtil.getUserByEmailAddress(
					companyId, login);
			}
		}

		addRedirect(request);

		String[] credentials = new String[3];

		credentials[0] = String.valueOf(user.getUserId());
		credentials[1] = user.getPassword();
		credentials[2] = Boolean.TRUE.toString();

		return credentials;
	}

	private static Log _log = LogFactoryUtil.getLog(CASAutoLogin.class);

	private boolean _importFromLdap;
	private boolean _logoutOnSessionExpiration;
	private String _loginUrl;
	private String _logoutUrl;
	private String _noSuchUserRedirectUrl;
	private String _serverName;
	private String _serverUrl;
	private String _serviceUrl;

}