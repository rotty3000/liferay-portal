package com.liferay.segments.web.internal.field.customizer;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ClassedModel;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.segments.constants.SegmentsPortletKeys;
import com.liferay.segments.context.Context;
import com.liferay.segments.field.Field;
import com.liferay.segments.field.customizer.SegmentsFieldCustomizer;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import java.util.List;
import java.util.Locale;

@Component(
	immediate = true,
	property = {
		"segments.field.customizer.entity.name=User",
		"segments.field.customizer.entity.name=Organization",
		"segments.field.customizer.entity.name=Context",
		"segments.field.customizer.key=" + FreeformTextSegmentsFieldCustomizer.KEY,
		"segments.field.customizer.priority:Integer=50"
	},
	service = SegmentsFieldCustomizer.class
)
public class FreeformTextSegmentsFieldCustomizer extends BaseSegmentsFieldCustomizer{

	public static final String KEY = "freeform";

	@Override
	public ClassedModel getClassedModel(
		String fieldValue) {

		//Add condition based on fieldValue
		if(true) {
			return _getVocabulary(fieldValue);
		}
		else {
			return _getCategory(fieldValue);
		}
	}

	@Override
	public String getClassName() {

		//Add correct condition here
		if (true) {
			return AssetVocabulary.class.getName();
		}
		else {
			return AssetCategory.class.getName();
		}
	}

	@Override
	public List<String> getFieldNames() {
		return _fieldNames;
	}

	@Override
	public String getFieldValueName(String fieldValue, Locale locale) {
		return null;
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public Field.SelectEntity getSelectEntity(
		PortletRequest portletRequest) {
		try {
			PortletURL portletURL = PortletProviderUtil.getPortletURL(
				portletRequest, AssetCategory.class.getName(),
				PortletProvider.Action.BROWSE);

			//Add correct portletURL parameters to open Vocab and Category selector
			portletURL.setParameter("eventName", "selectEntity");
//			portletURL.setParameter(
//				"groupIds", String.valueOf(companyGroup.getGroupId()));
//			portletURL.setParameter("mvcPath", "/view.jsp");
			portletURL.setWindowState(LiferayWindowState.POP_UP);

			return new Field.SelectEntity(
				"selectEntity",
				getSelectEntityTitle(
					_portal.getLocale(portletRequest), AssetCategory.class.getName()),
				portletURL.toString(), true);
		}
		catch (Exception e) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to get select entity", e);
			}

			return null;
		}
	}

	private AssetVocabulary _getVocabulary(String fieldValue) {
		return null;
	}

	private AssetCategory _getCategory(String fieldValue) {
		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(FreeformTextSegmentsFieldCustomizer.class);

	private static final List<String> _fieldNames = ListUtil.fromArray(new String[] {"emailAddress", "firstName", "jobTitle", "lastName", "screenName", "userName", "country", "region", "name", "nameTreePath", "type",
		Context.BROWSER, Context.DEVICE_BRAND, Context.DEVICE_MODEL, Context.HOSTNAME, Context.REFERRER_URL, Context.URL, Context.USER_AGENT, Context.COOKIES, Context.REQUEST_PARAMETERS});

	@Reference
	private Portal _portal;

	@Reference
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;
}
