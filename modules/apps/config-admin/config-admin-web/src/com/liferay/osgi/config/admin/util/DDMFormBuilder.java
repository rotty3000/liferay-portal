
package com.liferay.osgi.config.admin.util;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.dynamicdatamapping.io.DDMFormJSONSerializerUtil;
import com.liferay.portlet.dynamicdatamapping.model.DDMForm;
import com.liferay.portlet.dynamicdatamapping.render.DDMFormFieldRenderingContext;
import com.liferay.portlet.dynamicdatamapping.render.DDMFormRendererUtil;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import org.osgi.service.metatype.ObjectClassDefinition;
public class DDMFormBuilder {

	public DDMFormBuilder(ObjectClassDefinition objectClassDefinition) {
		_objectClassDefinition = objectClassDefinition;
	}

	public String renderServiceConfigurationForm(
		String servicePID, PortletRequest portletRequest,
		PortletResponse portletResponse) throws PortalException {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		DDMForm ddmForm = MetaTypeInfoUtil.attributeForm(
			_objectClassDefinition);

		DDMFormFieldRenderingContext ddmFormFieldRenderingContext =
			new DDMFormFieldRenderingContext();

		ddmFormFieldRenderingContext.setHttpServletRequest(
			PortalUtil.getHttpServletRequest(portletRequest));

		ddmFormFieldRenderingContext.setHttpServletResponse(
			PortalUtil.getHttpServletResponse(portletResponse));

		ddmFormFieldRenderingContext.setPortletNamespace(
			portletResponse.getNamespace());

		ddmFormFieldRenderingContext.setLocale(themeDisplay.getLocale());

		if (_log.isDebugEnabled()) {
			_log.debug(
				"DDMForm: " + DDMFormJSONSerializerUtil.serialize(ddmForm));
		}

		return DDMFormRendererUtil.render(
			ddmForm, ddmFormFieldRenderingContext);
	}

	private static Log _log = LogFactoryUtil.getLog(DDMFormBuilder.class);

	private ObjectClassDefinition _objectClassDefinition;

}