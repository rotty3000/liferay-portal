package com.liferay.segments.web.internal.field.customizer;

import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ClassedModel;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.segments.context.Context;
import com.liferay.segments.field.Field;
import com.liferay.segments.field.customizer.SegmentsFieldCustomizer;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

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
public class FreeformTextSegmentsFieldCustomizer
	extends BaseSegmentsFieldCustomizer {

	public static final String KEY = "freeform";

	@Override
	public ClassedModel getClassedModel(String fieldValue) {
		return _getAssetVocabulary(fieldValue);
	}

	@Override
	public String getClassName() {
		return AssetVocabulary.class.getName();
	}

	@Override
	public List<String> getFieldNames() {
		return _fieldNames;
	}

	@Override
	public String getFieldValueName(String fieldValue, Locale locale) {
		AssetVocabulary assetVocabulary = _getAssetVocabulary(fieldValue);

		if (assetVocabulary == null) {
			return fieldValue;
		}

		return assetVocabulary.getName();
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public Field.SelectEntity getSelectEntity(PortletRequest portletRequest) {
		try {
			PortletURL portletURL = PortletProviderUtil.getPortletURL(
				portletRequest, AssetVocabulary.class.getName(),
				PortletProvider.Action.BROWSE);

			portletURL.setParameter("eventName", "selectEntity");
			portletURL.setWindowState(LiferayWindowState.POP_UP);

			return new Field.SelectEntity(
				"selectEntity",
				getSelectEntityTitle(
					_portal.getLocale(portletRequest),
					AssetVocabulary.class.getName()),
				portletURL.toString(), true);
		}
		catch (Exception e) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to get select entity", e);
			}

			return null;
		}
	}

	private AssetVocabulary _getAssetVocabulary(String fieldValue) {
		long _vocabularyId = GetterUtil.getLong(fieldValue);

		if (_vocabularyId == 0) {
			return null;
		}

		return _assetVocabularyLocalService.fetchAssetVocabulary(_vocabularyId);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FreeformTextSegmentsFieldCustomizer.class);

	private static final List<String> _fieldNames = Arrays.asList(
		"emailAddress", "firstName", "jobTitle", "lastName", "screenName",
		"userName", "country", "region", "name", "nameTreePath", "type",
		Context.BROWSER, Context.DEVICE_BRAND, Context.DEVICE_MODEL,
		Context.HOSTNAME, Context.REFERRER_URL, Context.URL, Context.USER_AGENT,
		Context.COOKIES, Context.REQUEST_PARAMETERS);

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Reference
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Reference
	private Portal _portal;

}