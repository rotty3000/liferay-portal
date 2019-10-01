package com.liferay.segments.web.internal.servlet.taglib.ui;

import java.util.Locale;

import javax.servlet.ServletContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.servlet.taglib.ui.BaseJSPFormNavigatorEntry;
import com.liferay.portal.kernel.servlet.taglib.ui.FormNavigatorConstants;
import com.liferay.portal.kernel.servlet.taglib.ui.FormNavigatorEntry;
import com.liferay.portal.kernel.util.Portal;

@Component(
	property = "form.navigator.entry.order:Integer=90",
	service = FormNavigatorEntry.class
)
public class LayoutSegmentFormNavigatorEntry
	extends BaseJSPFormNavigatorEntry<Layout> {

	@Override
	public String getCategoryKey() {
		return SEGMENTS;
	}

	@Override
	public String getFormNavigatorId() {
		return FormNavigatorConstants.FORM_NAVIGATOR_ID_LAYOUT;
	}

	@Override
	public String getKey() {
		return SEGMENTS;
	}

	@Override
	public String getLabel(Locale locale) {
		return LanguageUtil.get(locale, getKey());
	}

	@Override
	public boolean isVisible(User user, Layout layout) {
		return super.isVisible(user, layout);
	}

	@Override
	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.segments.web)",
		unbind = "-"
	)
	public void setServletContext(ServletContext servletContext) {
		super.setServletContext(servletContext);
	}

	@Override
	protected String getJspPath() {
		return "/layout/bind_segments.jsp";
	}

	public static final String SEGMENTS = "segments";

	@Reference
	protected LayoutLocalService layoutLocalService;

	@Reference
	protected Portal portal;

}