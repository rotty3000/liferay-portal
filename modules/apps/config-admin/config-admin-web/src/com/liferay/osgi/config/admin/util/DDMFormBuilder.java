
package com.liferay.osgi.config.admin.util;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.dynamicdatamapping.io.DDMFormJSONSerializerUtil;
import com.liferay.portlet.dynamicdatamapping.model.DDMForm;
import com.liferay.portlet.dynamicdatamapping.render.DDMFormFieldRenderingContext;
import com.liferay.portlet.dynamicdatamapping.render.DDMFormRendererUtil;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
public class DDMFormBuilder {

	public String ddmFromContentHTML(
			String servicePID, ThemeDisplay themeDisplay,
			PortletRequest portletRequest, PortletResponse portletResponse)
		throws PortalException {

		DDMForm ddmForm = MetaTypeInfoUtil.attributeForm(servicePID);

		DDMFormFieldRenderingContext ddmFormFieldRenderingContext =
			new DDMFormFieldRenderingContext();

		ddmFormFieldRenderingContext.setHttpServletRequest(
			PortalUtil.getHttpServletRequest(portletRequest));

		ddmFormFieldRenderingContext.setHttpServletResponse(
			PortalUtil.getHttpServletResponse(portletResponse));

		ddmFormFieldRenderingContext.setPortletNamespace(
			portletResponse.getNamespace());
		ddmFormFieldRenderingContext.setNamespace("CA");

		ddmFormFieldRenderingContext.setLocale(themeDisplay.getLocale());

		if (_log.isDebugEnabled()) {
			_log.debug(
				"DDMForm: " + DDMFormJSONSerializerUtil.serialize(ddmForm));
		}

		return DDMFormRendererUtil.render(
			ddmForm, ddmFormFieldRenderingContext);
	}

	private static Log _log = LogFactoryUtil.getLog(DDMFormBuilder.class);

}