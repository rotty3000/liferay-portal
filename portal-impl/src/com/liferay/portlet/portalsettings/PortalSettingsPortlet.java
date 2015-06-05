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
package com.liferay.portlet.portalsettings;

import java.io.IOException;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.liferay.portal.AccountNameException;
import com.liferay.portal.AddressCityException;
import com.liferay.portal.AddressStreetException;
import com.liferay.portal.AddressZipException;
import com.liferay.portal.CompanyMxException;
import com.liferay.portal.CompanyVirtualHostException;
import com.liferay.portal.CompanyWebIdException;
import com.liferay.portal.DuplicatePasswordPolicyException;
import com.liferay.portal.EmailAddressException;
import com.liferay.portal.LocaleException;
import com.liferay.portal.NoSuchCountryException;
import com.liferay.portal.NoSuchListTypeException;
import com.liferay.portal.NoSuchPasswordPolicyException;
import com.liferay.portal.NoSuchRegionException;
import com.liferay.portal.PasswordPolicyNameException;
import com.liferay.portal.PhoneNumberException;
import com.liferay.portal.RequiredPasswordPolicyException;
import com.liferay.portal.WebsiteURLException;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PropertiesParamUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.Address;
import com.liferay.portal.model.EmailAddress;
import com.liferay.portal.model.Phone;
import com.liferay.portal.model.Website;
import com.liferay.portal.security.auth.PrincipalException;
import com.liferay.portal.service.CompanyServiceUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portal.util.PropsValues;
import com.liferay.portlet.documentlibrary.service.DLAppLocalServiceUtil;
import com.liferay.portlet.usersadmin.util.UsersAdminUtil;

/**
 * @author Philip Jones
 */
public class PortalSettingsPortlet extends MVCPortlet {

	public void updateCompany(
		ActionRequest actionRequest, ActionResponse actionRespons) 
		throws Exception {
		
		long companyId = PortalUtil.getCompanyId(actionRequest);

		String virtualHostname = ParamUtil.getString(
			actionRequest, "virtualHostname");
		String mx = ParamUtil.getString(actionRequest, "mx");
		String homeURL = ParamUtil.getString(actionRequest, "homeURL");
		boolean deleteLogo = ParamUtil.getBoolean(actionRequest, "deleteLogo");

		byte[] logoBytes = null;

		long fileEntryId = ParamUtil.getLong(actionRequest, "fileEntryId");

		if (fileEntryId > 0) {
			FileEntry fileEntry = DLAppLocalServiceUtil.getFileEntry(
				fileEntryId);

			logoBytes = FileUtil.getBytes(fileEntry.getContentStream());
		}

		String name = ParamUtil.getString(actionRequest, "name");
		String legalName = ParamUtil.getString(actionRequest, "legalName");
		String legalId = ParamUtil.getString(actionRequest, "legalId");
		String legalType = ParamUtil.getString(actionRequest, "legalType");
		String sicCode = ParamUtil.getString(actionRequest, "sicCode");
		String tickerSymbol = ParamUtil.getString(
			actionRequest, "tickerSymbol");
		String industry = ParamUtil.getString(actionRequest, "industry");
		String type = ParamUtil.getString(actionRequest, "type");
		String size = ParamUtil.getString(actionRequest, "size");
		String languageId = ParamUtil.getString(actionRequest, "languageId");
		String timeZoneId = ParamUtil.getString(actionRequest, "timeZoneId");
		List<Address> addresses = UsersAdminUtil.getAddresses(actionRequest);
		List<EmailAddress> emailAddresses = UsersAdminUtil.getEmailAddresses(
			actionRequest);
		List<Phone> phones = UsersAdminUtil.getPhones(actionRequest);
		List<Website> websites = UsersAdminUtil.getWebsites(actionRequest);
		UnicodeProperties properties = PropertiesParamUtil.getProperties(
			actionRequest, "settings--");

		CompanyServiceUtil.updateCompany(
			companyId, virtualHostname, mx, homeURL, !deleteLogo, logoBytes,
			name, legalName, legalId, legalType, sicCode, tickerSymbol,
			industry, type, size, languageId, timeZoneId, addresses,
			emailAddresses, phones, websites, properties);

		PortalUtil.resetCDNHosts();
	}

	public void validateCAS(
		ActionRequest actionRequest, ActionResponse actionResponse) {
		
		boolean casEnabled = ParamUtil.getBoolean(
			actionRequest, "settings--" + PropsKeys.CAS_AUTH_ENABLED + "--");

		if (!casEnabled) {
			return;
		}

		String casLoginURL = ParamUtil.getString(
			actionRequest, "settings--" + PropsKeys.CAS_LOGIN_URL + "--");
		String casLogoutURL = ParamUtil.getString(
			actionRequest, "settings--" + PropsKeys.CAS_LOGOUT_URL + "--");
		String casServerName = ParamUtil.getString(
			actionRequest, "settings--" + PropsKeys.CAS_SERVER_NAME + "--");
		String casServerURL = ParamUtil.getString(
			actionRequest, "settings--" + PropsKeys.CAS_SERVER_URL + "--");
		String casServiceURL = ParamUtil.getString(
			actionRequest, "settings--" + PropsKeys.CAS_SERVICE_URL + "--");
		String casNoSuchUserRedirectURL = ParamUtil.getString(
			actionRequest,
			"settings--" + PropsKeys.CAS_NO_SUCH_USER_REDIRECT_URL + "--");

		if (!Validator.isUrl(casLoginURL)) {
			SessionErrors.add(actionRequest, "casLoginURLInvalid");
		}

		if (!Validator.isUrl(casLogoutURL)) {
			SessionErrors.add(actionRequest, "casLogoutURLInvalid");
		}

		if (Validator.isNull(casServerName)) {
			SessionErrors.add(actionRequest, "casServerNameInvalid");
		}

		if (!Validator.isUrl(casServerURL)) {
			SessionErrors.add(actionRequest, "casServerURLInvalid");
		}

		if (Validator.isNotNull(casServiceURL) &&
			!Validator.isUrl(casServiceURL)) {

			SessionErrors.add(actionRequest, "casServiceURLInvalid");
		}

		if (Validator.isNotNull(casNoSuchUserRedirectURL) &&
			!Validator.isUrl(casNoSuchUserRedirectURL)) {

			SessionErrors.add(actionRequest, "casNoSuchUserURLInvalid");
		}
	}

	public void validateLDAP(
		ActionRequest actionRequest, ActionResponse actionResponse) {
		
		if (!PropsValues.LDAP_IMPORT_USER_PASSWORD_AUTOGENERATED) {
			return;
		}

		boolean ldapExportEnabled = ParamUtil.getBoolean(
			actionRequest, "settings--" + PropsKeys.LDAP_EXPORT_ENABLED + "--");
		boolean ldapImportEnabled = ParamUtil.getBoolean(
			actionRequest, "settings--" + PropsKeys.LDAP_IMPORT_ENABLED + "--");

		if (ldapExportEnabled && ldapImportEnabled) {
			SessionErrors.add(
				actionRequest, "ldapExportAndImportOnPasswordAutogeneration");
		}
	}

	public void validateSocialInteractions(ActionRequest actionRequest) {
		boolean socialInteractionsEnabled = ParamUtil.getBoolean(
			actionRequest, "settings--socialInteractionsEnabled--");

		if (!socialInteractionsEnabled) {
			return;
		}

		boolean socialInteractionsAnyUserEnabled = ParamUtil.getBoolean(
			actionRequest, "settings--socialInteractionsAnyUserEnabled--");

		if (socialInteractionsAnyUserEnabled) {
			return;
		}

		boolean socialInteractionsSocialRelationTypesEnabled =
			ParamUtil.getBoolean(
				actionRequest,
				"settings--socialInteractionsSocialRelationTypesEnabled--");
		String socialInteractionsSocialRelationTypes = ParamUtil.getString(
			actionRequest, "settings--socialInteractionsSocialRelationTypes--");

		if (socialInteractionsSocialRelationTypesEnabled &&
			Validator.isNull(socialInteractionsSocialRelationTypes)) {

			SessionErrors.add(
				actionRequest, "socialInteractionsSocialRelationTypes");
		}

		boolean socialInteractionsSitesEnabled = ParamUtil.getBoolean(
			actionRequest, "settings--socialInteractionsSitesEnabled--");

		if (!socialInteractionsSocialRelationTypesEnabled &&
			!socialInteractionsSitesEnabled) {

			SessionErrors.add(actionRequest, "socialInteractionsInvalid");
		}
	}

	@Override
	protected void doDispatch(
		RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {
	
		if (SessionErrors.contains(
			renderRequest, DuplicatePasswordPolicyException.class.getName()) ||
			SessionErrors.contains(
				renderRequest, NoSuchPasswordPolicyException.class.getName()) ||
			SessionErrors.contains(
				renderRequest, PrincipalException.class.getName()) ||
			SessionErrors.contains(
				renderRequest, PasswordPolicyNameException.class.getName()) ||
			SessionErrors.contains(
				renderRequest, PrincipalException.class.getName()) ||
			SessionErrors.contains(
				renderRequest, 
				RequiredPasswordPolicyException.class.getName()) ||
			SessionErrors.contains( 
				renderRequest, AddressCityException.class.getName()) ||
			SessionErrors.contains( 
				renderRequest, AccountNameException.class.getName()) ||
			SessionErrors.contains( 
				renderRequest, AddressStreetException.class.getName()) ||
 			SessionErrors.contains( 
				renderRequest, AddressZipException.class.getName()) ||
 			SessionErrors.contains( 
				renderRequest, CompanyMxException.class.getName()) ||
 			SessionErrors.contains( 
				renderRequest, CompanyVirtualHostException.class.getName()) ||
 			SessionErrors.contains( 
				renderRequest, CompanyWebIdException.class.getName()) ||
 			SessionErrors.contains( 
				renderRequest, EmailAddressException.class.getName()) ||
			SessionErrors.contains( 
				renderRequest, LocaleException.class.getName()) ||
 			SessionErrors.contains( 
				renderRequest, NoSuchCountryException.class.getName()) ||
 			SessionErrors.contains( 
				renderRequest, NoSuchListTypeException.class.getName()) ||
 			SessionErrors.contains( 
				renderRequest, NoSuchRegionException.class.getName()) ||
 			SessionErrors.contains( 
				renderRequest, PhoneNumberException.class.getName()) ||
			SessionErrors.contains( 
 				renderRequest, WebsiteURLException.class.getName()) ||
  			SessionErrors.contains( 
 				renderRequest, NoSuchListTypeException.class.getName())) {
				
			include("/error.jsp", renderRequest, renderResponse);
		}
		else {
			super.doDispatch(renderRequest, renderResponse);
		}
	}
		
	@Override
	protected boolean isSessionErrorException(Throwable cause) {
		if(cause instanceof DuplicatePasswordPolicyException ||
		   cause instanceof NoSuchPasswordPolicyException ||
		   cause instanceof PrincipalException ||
		   cause instanceof PasswordPolicyNameException ||
		   cause instanceof PrincipalException ||
		   cause instanceof RequiredPasswordPolicyException || 
		   cause instanceof AddressCityException ||
           cause instanceof AccountNameException ||
           cause instanceof AddressStreetException ||
           cause instanceof AddressZipException ||
           cause instanceof CompanyMxException ||
           cause instanceof CompanyVirtualHostException ||
           cause instanceof CompanyWebIdException ||
           cause instanceof EmailAddressException ||
           cause instanceof LocaleException ||
           cause instanceof NoSuchCountryException ||
           cause instanceof NoSuchListTypeException ||
           cause instanceof NoSuchRegionException ||
           cause instanceof PhoneNumberException ||
           cause instanceof WebsiteURLException || 
           cause instanceof NoSuchListTypeException) {
				return true;
		}
		return false;
	}				
}
