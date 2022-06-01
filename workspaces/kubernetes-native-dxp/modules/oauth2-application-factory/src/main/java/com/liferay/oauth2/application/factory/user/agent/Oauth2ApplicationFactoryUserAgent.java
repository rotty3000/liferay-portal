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

package com.liferay.oauth2.application.factory.user.agent;

import com.liferay.k8s.agent.ExtensionConfigMapModifier;
import com.liferay.k8s.agent.constants.K8sAgentConstants;
import com.liferay.oauth2.application.factory.CompanyDomainProvider;
import com.liferay.oauth2.application.factory.OAuth2ApplicationFactoryConstants;
import com.liferay.oauth2.application.factory.user.agent.configuration.v1.OAuth2ApplicationUserAgentConfiguration;
import com.liferay.oauth2.provider.constants.ClientProfile;
import com.liferay.oauth2.provider.constants.GrantType;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.service.OAuth2ApplicationLocalService;
import com.liferay.oauth2.provider.util.OAuth2SecureRandomGenerator;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.util.PropsValues;

import java.io.InputStream;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.framework.Constants;
import org.osgi.service.component.ComponentConstants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Raymond Augé
 */
@Component(
	configurationPid = "com.liferay.oauth2.application.factory.user.agent.configuration.v1.OAuth2ApplicationUserAgentConfiguration",
	configurationPolicy = ConfigurationPolicy.REQUIRE, immediate = true
)
public class Oauth2ApplicationFactoryUserAgent {

	@Activate
	public Oauth2ApplicationFactoryUserAgent(
			@Reference CompanyDomainProvider companyDomainProvider,
			@Reference CompanyLocalService companyLocalService,
			@Reference ExtensionConfigMapModifier extensionConfigMapModifier,
			@Reference OAuth2ApplicationLocalService
				oAuth2ApplicationLocalService,
			@Reference UserLocalService userLocalService,
			Map<String, Object> properties)
		throws Exception {

		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"Creating or Updating User Agent profile request with: ",
					properties));
		}

		_companyLocalService = companyLocalService;
		_extensionConfigMapModifier = extensionConfigMapModifier;
		_oAuth2ApplicationLocalService = oAuth2ApplicationLocalService;
		_userLocalService = userLocalService;

		_oAuth2ApplicationUserAgentConfiguration =
			ConfigurableUtil.createConfigurable(
				OAuth2ApplicationUserAgentConfiguration.class, properties);

		_serviceId = GetterUtil.getString(
			properties.get(K8sAgentConstants.K8S_SERVICE_ID));

		if (Validator.isNull(_serviceId)) {
			throw new IllegalArgumentException(
				StringBundler.concat(
					"property ", K8sAgentConstants.K8S_SERVICE_ID,
					" must be set"));
		}

		String externalReferenceCode = _getExternalReferenceCode(properties);

		long companyId = GetterUtil.getLong(properties.get("companyId"));

		Company company = _companyLocalService.getCompanyById(companyId);

		List<String> companyDomains =
			companyDomainProvider.getCompanyDomains(company.getCompanyId());

		String protocol = GetterUtil.getString(
			PropsValues.WEB_SERVER_PROTOCOL, Http.HTTP);

		String serviceAddress = StringBundler.concat(
			protocol, "://", companyDomains.get(0));

		List<String> redirectURIsList = Collections.singletonList(
			GetterUtil.getString(
				_oAuth2ApplicationUserAgentConfiguration.redirectURL(),
				serviceAddress.concat("/o/builtin/oauth2/redirect")));

		OAuth2Application oAuth2Application = _getOrAddOAuth2Application(
			company, redirectURIsList);

		if (!Objects.equals(
				oAuth2Application.getDescription(),
				_oAuth2ApplicationUserAgentConfiguration.description()) ||
			!Objects.equals(
				oAuth2Application.getHomePageURL(),
				_oAuth2ApplicationUserAgentConfiguration.homePageURL()) ||
			!Objects.equals(
				oAuth2Application.getPrivacyPolicyURL(),
				_oAuth2ApplicationUserAgentConfiguration.privacyPolicyURL()) ||
			!Objects.equals(
				oAuth2Application.getRedirectURIsList(), redirectURIsList)) {

			oAuth2Application =
				_oAuth2ApplicationLocalService.updateOAuth2Application(
					oAuth2Application.getOAuth2ApplicationId(),
					oAuth2Application.getOAuth2ApplicationScopeAliasesId(),
					oAuth2Application.getAllowedGrantTypesList(),
					oAuth2Application.getClientAuthenticationMethod(),
					oAuth2Application.getClientCredentialUserId(),
					oAuth2Application.getClientId(),
					oAuth2Application.getClientProfile(),
					oAuth2Application.getClientSecret(),
					_oAuth2ApplicationUserAgentConfiguration.description(),
					oAuth2Application.getFeaturesList(),
					_oAuth2ApplicationUserAgentConfiguration.homePageURL(),
					oAuth2Application.getIconFileEntryId(),
					oAuth2Application.getJwks(), oAuth2Application.getName(),
					_oAuth2ApplicationUserAgentConfiguration.privacyPolicyURL(),
					redirectURIsList, oAuth2Application.getRememberDevice(),
					oAuth2Application.getTrustedApplication());
		}

		List<String> scopeAliasesList = ListUtil.fromArray(
			_oAuth2ApplicationUserAgentConfiguration.scopes());

		oAuth2Application = _oAuth2ApplicationLocalService.updateScopeAliases(
			oAuth2Application.getUserId(), oAuth2Application.getUserName(),
			oAuth2Application.getOAuth2ApplicationId(), scopeAliasesList);

		_extensionProperties = HashMapBuilder.put(
			externalReferenceCode.concat(".oauth2.authorization.uri"),
			serviceAddress.concat("/o/oauth2/authorize")
		).put(
			externalReferenceCode.concat(".oauth2.introspection.uri"),
			serviceAddress.concat("/o/oauth2/introspect")
		).put(
			externalReferenceCode.concat(".oauth2.redirect.uris"),
			StringUtil.merge(redirectURIsList, StringPool.COMMA)
		).put(
			externalReferenceCode.concat(".oauth2.token.uri"),
			serviceAddress.concat("/o/oauth2/token")
		).put(
			externalReferenceCode.concat(".oauth2.user.agent.client.id"),
			oAuth2Application.getClientId()
		).put(
			externalReferenceCode.concat(".oauth2.user.agent.scopes"),
			StringUtil.merge(scopeAliasesList, StringPool.COMMA)
		).build();

		_extensionConfigMapModifier.modifyExtensionConfigMap(
			data -> _extensionProperties.forEach(data::put),
			_serviceId);

		_oAuth2Application = oAuth2Application;

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Created User Agent application: ".concat(
					_oAuth2Application.toString()));
		}
	}

	@Deactivate
	protected void deactivate(Integer reason) throws PortalException {
		if (reason ==
				ComponentConstants.DEACTIVATION_REASON_CONFIGURATION_DELETED) {

			if (_log.isDebugEnabled()) {
				_log.debug(
					"Deleting User Agent application: ".concat(
						_oAuth2Application.toString()));
			}

			_extensionConfigMapModifier.modifyExtensionConfigMap(
				data -> _extensionProperties.forEach(data::remove),
				_serviceId);

			_oAuth2ApplicationLocalService.deleteOAuth2Application(
				_oAuth2Application);
		}
	}

	private String _getExternalReferenceCode(Map<String, Object> properties) {
		String externalReferenceCode = GetterUtil.getString(
			properties.get(Constants.SERVICE_PID));

		int pos = externalReferenceCode.indexOf('~');

		if (pos > 0) {
			externalReferenceCode = externalReferenceCode.substring(pos + 1);
		}

		return externalReferenceCode;
	}

	private String _getName() {
		return StringBundler.concat(
			_oAuth2ApplicationUserAgentConfiguration.name(),
			OAuth2ApplicationFactoryConstants.USER_AGENT_SUBDOMAIN);
	}

	private OAuth2Application _getOrAddOAuth2Application(
			Company company, List<String> redirectURIsList)
		throws Exception {

		DynamicQuery dynamicQuery =
			_oAuth2ApplicationLocalService.dynamicQuery();

		Property companyIdProperty = PropertyFactoryUtil.forName("companyId");

		dynamicQuery.add(companyIdProperty.eq(company.getCompanyId()));

		Property nameProperty = PropertyFactoryUtil.forName("name");

		dynamicQuery.add(nameProperty.eq(_getName()));

		List<OAuth2Application> oAuth2Applications =
			_oAuth2ApplicationLocalService.dynamicQuery(dynamicQuery);

		if (!oAuth2Applications.isEmpty()) {
			return oAuth2Applications.get(0);
		}

		User user = _userLocalService.getDefaultUser(company.getCompanyId());

		OAuth2Application oAuth2Application =
			_oAuth2ApplicationLocalService.addOAuth2Application(
				company.getCompanyId(), user.getUserId(), user.getScreenName(),
				ListUtil.fromArray(
					GrantType.AUTHORIZATION_CODE_PKCE, GrantType.JWT_BEARER),
				"none", user.getUserId(),
				OAuth2SecureRandomGenerator.generateClientId(),
				ClientProfile.USER_AGENT_APPLICATION.id(), null,
				_oAuth2ApplicationUserAgentConfiguration.description(),
				Arrays.asList("token.introspection"),
				_oAuth2ApplicationUserAgentConfiguration.homePageURL(), 0,
				null, _getName(),
				_oAuth2ApplicationUserAgentConfiguration.privacyPolicyURL(),
				redirectURIsList, false, true, null,
				new ServiceContext());

		Class<?> clazz = getClass();

		InputStream inputStream = clazz.getResourceAsStream(
			"dependencies/logo.png");

		return _oAuth2ApplicationLocalService.updateIcon(
			oAuth2Application.getOAuth2ApplicationId(), inputStream);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		Oauth2ApplicationFactoryUserAgent.class);

	private final CompanyLocalService _companyLocalService;
	private final ExtensionConfigMapModifier _extensionConfigMapModifier;
	private final HashMap<String, String> _extensionProperties;
	private final OAuth2Application _oAuth2Application;
	private final OAuth2ApplicationLocalService _oAuth2ApplicationLocalService;
	private final OAuth2ApplicationUserAgentConfiguration
		_oAuth2ApplicationUserAgentConfiguration;
	private final String _serviceId;
	private final UserLocalService _userLocalService;

}