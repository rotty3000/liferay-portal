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

package com.liferay.portal.model;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portlet.PortalPreferences;

import java.util.List;

/**
 * @author Brian Wing Shun Chan
 * @author Raymond Augé
 */
public interface LayoutTypePortlet extends LayoutType {

	/**
	 * Use this include constant for including the embedded portlets when
	 * querying the layout portlets with the {@link #getPortlets(int)} method
	 */
	public static final int PORTLET_INCLUDE_EMBEDDED = 1;

	/**
	 * Use this include constant for including the embedded system portlets when
	 * querying the layout portlets with the {@link #getPortlets(int)} method.
	 * Please not that this include constant is only effective with the {@link
	 * #PORTLET_INCLUDE_EMBEDDED}
	 */
	public static final int PORTLET_INCLUDE_EMBEDDED_SYSTEM = 2;

	/**
	 * Use this include constant for including only the manually added portlets
	 * when querying the layout portlets with the {@link #getPortlets(int)}
	 * method. The method {@link #getPortlets()} is a shortcut for calling
	 * <code>geetPortlets(LayoutTypePortlet.PORTLET_INCLUDE_LAYOUT)</code>. This
	 * include constant is implicitly added to every call
	 */
	public static final int PORTLET_INCLUDE_LAYOUT = 0;

	/**
	 * Use this include constant for including the static portlets when querying
	 * the layout portlets with the {@link #getPortlets(int)} method
	 */
	public static final int PORTLET_INCLUDE_STATIC = 4;

	public void addModeAboutPortletId(String portletId);

	public void addModeConfigPortletId(String portletId);

	public void addModeEditDefaultsPortletId(String portletId);

	public void addModeEditGuestPortletId(String portletId);

	public void addModeEditPortletId(String portletId);

	public void addModeHelpPortletId(String portletId);

	public void addModePreviewPortletId(String portletId);

	public void addModePrintPortletId(String portletId);

	public String addPortletId(long userId, String portletId)
		throws PortalException, SystemException;

	public String addPortletId(
			long userId, String portletId, boolean checkPermission)
		throws PortalException, SystemException;

	public String addPortletId(
			long userId, String portletId, String columnId, int columnPos)
		throws PortalException, SystemException;

	public String addPortletId(
			long userId, String portletId, String columnId, int columnPos,
			boolean checkPermission)
		throws PortalException, SystemException;

	public void addPortletIds(
			long userId, String[] portletIds, boolean checkPermission)
		throws PortalException, SystemException;

	public void addPortletIds(
			long userId, String[] portletIds, String columnId,
			boolean checkPermission)
		throws PortalException, SystemException;

	public void addStateMaxPortletId(String portletId);

	public void addStateMinPortletId(String portletId);

	public List<Portlet> addStaticPortlets(
			List<Portlet> portlets, List<Portlet> startPortlets,
			List<Portlet> endPortlets)
		throws SystemException;

	/**
	 * Returns all portlets associated with the layout represented by this
	 * class. The set of all portlets includes the static, embedded, and
	 * embedded system portlets as well besides the portlets added to the
	 * layout. This method is equivalent of using the {@link #getPortlets(int)}
	 * method with the following parameter:
	 * <code>LayoutTypePortlet.PORTLET_INCLUDE_EMBEDDED |
	 * LayoutTypePortlet.PORTLET_INCLUDE_EMBEDDED_SYSTEM |
	 * LayoutTypePortlet.PORTLET_INCLUDE_STATIC</code>
	 *
	 * @return a list of portlets associated with the layout represented by this
	 *         class
	 * @throws SystemException if a system exception occurred
	 * @see    #PORTLET_INCLUDE_EMBEDDED
	 * @see    #PORTLET_INCLUDE_EMBEDDED_SYSTEM
	 * @see    #PORTLET_INCLUDE_LAYOUT
	 * @see    #PORTLET_INCLUDE_STATIC
	 */
	public List<Portlet> getAllPortlets() throws SystemException;

	/**
	 * Returns all portlets associated with the layout represented by this class
	 * and in a specified column.
	 *
	 * @param  columnId a specific column ID to get the portlets from
	 * @return a list of portlets in the specified column of the layout
	 *         represented by this class
	 * @throws SystemException
	 */
	public List<Portlet> getAllPortlets(String columnId) throws SystemException;

	public Layout getLayoutSetPrototypeLayout();

	public String getLayoutSetPrototypeLayoutProperty(String key);

	public LayoutTemplate getLayoutTemplate();

	public String getLayoutTemplateId();

	public String getModeAbout();

	public String getModeConfig();

	public String getModeEdit();

	public String getModeEditDefaults();

	public String getModeEditGuest();

	public String getModeHelp();

	public String getModePreview();

	public String getModePrint();

	public int getNumOfColumns();

	public PortalPreferences getPortalPreferences();

	/**
	 * Return the portlet IDs of the portlets manually added to this layout. If
	 * only the portlet IDs are needed and the portlet objects are not in the
	 * caller environment this method should be used instead of {@link
	 * #getPortlets()}, this method's implementation is more effecient in these
	 * cases.
	 *
	 * @return a list of the portlet IDs manually added to this layout
	 */
	public List<String> getPortletIds();

	/**
	 * Returns the portlets associtated with the layout represented by this
	 * class. The returned portlets does not include the embedded or the system
	 * portlets, this method is used to get the manually added portlets. To get
	 * all portlets from the layout use the {@link #getAllPortlets()} method. If
	 * only the portlet IDs are needed in the caller environment the {@link
	 * #getPortletIds()} method should be used, it has a more efficient
	 * implementation for these cases.
	 *
	 * @return the portlets manually added to the layout represented by this
	 *         class
	 * @throws SystemException if a system exception occurred
	 */
	public List<Portlet> getPortlets() throws SystemException;

	/**
	 * Returns the portlets associated with the layout represented by this class
	 * with additional includes specified by the method parameter.
	 *
	 * @param  includes a bitwise OR combination of the portlet include
	 *         constants
	 * @return a list of portlets based on the includes parameter plus the
	 *         portlets manually added to the layout
	 * @throws SystemException
	 * @see    #PORTLET_INCLUDE_EMBEDDED
	 * @see    #PORTLET_INCLUDE_EMBEDDED_SYSTEM
	 * @see    #PORTLET_INCLUDE_LAYOUT
	 * @see    #PORTLET_INCLUDE_STATIC
	 */
	public List<Portlet> getPortlets(int includes) throws SystemException;

	public String getStateMax();

	public String getStateMaxPortletId();

	public String getStateMin();

	public boolean hasDefaultScopePortletId(long groupId, String portletId)
		throws PortalException, SystemException;

	public boolean hasModeAboutPortletId(String portletId);

	public boolean hasModeConfigPortletId(String portletId);

	public boolean hasModeEditDefaultsPortletId(String portletId);

	public boolean hasModeEditGuestPortletId(String portletId);

	public boolean hasModeEditPortletId(String portletId);

	public boolean hasModeHelpPortletId(String portletId);

	public boolean hasModePreviewPortletId(String portletId);

	public boolean hasModePrintPortletId(String portletId);

	public boolean hasModeViewPortletId(String portletId);

	public boolean hasPortletId(String portletId)
		throws PortalException, SystemException;

	public boolean hasStateMax();

	public boolean hasStateMaxPortletId(String portletId);

	public boolean hasStateMin();

	public boolean hasStateMinPortletId(String portletId);

	public boolean hasStateNormalPortletId(String portletId);

	public boolean hasUpdatePermission();

	public boolean isColumnCustomizable(String columnId);

	public boolean isColumnDisabled(String columnId);

	public boolean isCustomizable();

	public boolean isCustomizedView();

	public boolean isDefaultUpdated();

	public boolean isPortletCustomizable(String portletId);

	public void movePortletId(
			long userId, String portletId, String columnId, int columnPos)
		throws PortalException, SystemException;

	public void removeModeAboutPortletId(String portletId);

	public void removeModeConfigPortletId(String portletId);

	public void removeModeEditDefaultsPortletId(String portletId);

	public void removeModeEditGuestPortletId(String portletId);

	public void removeModeEditPortletId(String portletId);

	public void removeModeHelpPortletId(String portletId);

	public void removeModePreviewPortletId(String portletId);

	public void removeModePrintPortletId(String portletId);

	public void removeModesPortletId(String portletId);

	public void removeNestedColumns(String portletNamespace);

	public void removePortletId(long userId, String portletId);

	public void removePortletId(
		long userId, String portletId, boolean modeAndState);

	public void removeStateMaxPortletId(String portletId);

	public void removeStateMinPortletId(String portletId);

	public void removeStatesPortletId(String portletId);

	public void reorganizePortlets(
		List<String> newColumns, List<String> oldColumns);

	public void resetModes();

	public void resetStates();

	public void resetUserPreferences();

	public void setCustomizedView(boolean customizedView);

	public void setLayoutTemplateId(long userId, String newLayoutTemplateId);

	public void setLayoutTemplateId(
		long userId, String newLayoutTemplateId, boolean checkPermission);

	public void setModeAbout(String modeAbout);

	public void setModeConfig(String modeConfig);

	public void setModeEdit(String modeEdit);

	public void setModeEditDefaults(String modeEditDefaults);

	public void setModeEditGuest(String modeEditGuest);

	public void setModeHelp(String modeHelp);

	public void setModePreview(String modePreview);

	public void setModePrint(String modePrint);

	public void setPortalPreferences(PortalPreferences portalPreferences);

	public void setStateMax(String stateMax);

	public void setStateMin(String stateMin);

	public void setUpdatePermission(boolean updatePermission);

}