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

package com.liferay.osgi.config.admin.util;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portlet.dynamicdatamapping.model.DDMForm;
import com.liferay.portlet.dynamicdatamapping.model.DDMFormField;
import com.liferay.portlet.dynamicdatamapping.model.DDMFormFieldOptions;
import com.liferay.portlet.dynamicdatamapping.model.LocalizedValue;
import com.liferay.portlet.dynamicdatamapping.storage.FieldConstants;

import java.util.List;
import java.util.Set;

import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype.ObjectClassDefinition;

/**
 * @author Kamesh Sampath TODO: add service tracker for MetaTypeService to
 *         update the map
 */
public class MetaTypeFormUtil {

	public static DDMForm attributeForm(
		ObjectClassDefinition objectClassDefinition) {

		AttributeDefinition[] requiredDefinitions =
			objectClassDefinition.getAttributeDefinitions(
				ObjectClassDefinition.REQUIRED);

		AttributeDefinition[] optionalDefinitions =
			objectClassDefinition.getAttributeDefinitions(
				ObjectClassDefinition.OPTIONAL);

		DDMForm ddmForm = new DDMForm();

		ddmForm.setAvailableLocales(
			SetUtil.fromArray(LanguageUtil.getAvailableLocales()));
		ddmForm.setDefaultLocale(LocaleUtil.getDefault());

		_addFieldToForm(ddmForm, requiredDefinitions, true);

		_addFieldToForm(ddmForm, optionalDefinitions, false);

		return ddmForm;
	}

	private static void _addFieldToForm(
		DDMForm ddmForm, AttributeDefinition[] attributeDefinitions,
		boolean required) {

		if (attributeDefinitions == null) {
			return;
		}

		List<DDMFormField> ddmFormFields = ddmForm.getDDMFormFields();

		for (AttributeDefinition attributeDefinition : attributeDefinitions) {
			String id = attributeDefinition.getID();
			String type = _attributeToDDMType(attributeDefinition);

			DDMFormField ddmFormField = new DDMFormField(id, type);

			ddmFormField.setDataType(
				_attributeToDDMDataType(attributeDefinition));

			ddmFormField.setDDMForm(ddmForm);
			ddmFormField.setLabel(_attributeToLabel(attributeDefinition));
			ddmFormField.setTip(_attributeToTip(attributeDefinition));
			ddmFormField.setLocalizable(true);

			//Default values
			LocalizedValue predefinedValue = _attributeDefaultValue(
				attributeDefinition);

			DDMFormFieldOptions ddmFormFieldOptions = _getDDMFieldOptions(
				attributeDefinition);

			if ((type.equals("radio") || type.equals("select")) &&
				_hasDDMFormFieldOptionsAvailable(
								ddmFormFieldOptions)) {

				_setDDMFormFieldOptions(
					type, ddmFormField, ddmFormFieldOptions);

				if (predefinedValue!= null) {
					String value = predefinedValue.getValues().get(
						LocaleUtil.getDefault());

					JSONArray defaultValueJSON =
									JSONFactoryUtil.createJSONArray();
					defaultValueJSON.put(value);

					LocalizedValue localizedDefaultValue = new LocalizedValue(
						LocaleUtil.getDefault());

					localizedDefaultValue.addString(
						LocaleUtil.getDefault(), defaultValueJSON.toString());

					ddmFormField.setPredefinedValue(localizedDefaultValue);
				}
			}
			else {
				//Set predefined value
				ddmFormField.setPredefinedValue(predefinedValue);
			}

			ddmFormField.setRequired(required);
			ddmFormField.setShowLabel(true);

			int cardinality = attributeDefinition.getCardinality();

			if (cardinality != 0) {
				ddmFormField.setRepeatable(true);
			}

			ddmFormFields.add(ddmFormField);
		}
	}

	private static LocalizedValue _attributeDefaultValue(
		AttributeDefinition attributeDefinition) {

		LocalizedValue value = new LocalizedValue(LocaleUtil.getDefault());

		String[] attributeValues = attributeDefinition.getDefaultValue();

		if (attributeValues!= null) {
			for (String attributeValue : attributeValues) {
				value.addString(LocaleUtil.getDefault(), attributeValue);
			}
		}

		return value;
	}

	private static String _attributeToDDMDataType(
		AttributeDefinition attributeDefinition) {

		int type = attributeDefinition.getType();

		switch (type) {
			case AttributeDefinition.DOUBLE: {
				return FieldConstants.DOUBLE;
			}

			case AttributeDefinition.FLOAT: {
				return FieldConstants.FLOAT;
			}

			case AttributeDefinition.INTEGER: {
				return FieldConstants.INTEGER;
			}

			case AttributeDefinition.LONG: {
				return FieldConstants.LONG;
			}

			case AttributeDefinition.SHORT: {
				return FieldConstants.SHORT;
			}

			case AttributeDefinition.BOOLEAN: {
				return FieldConstants.BOOLEAN;
			}

			default: {
				return FieldConstants.STRING;
			}
		}
	}

	private static String _attributeToDDMType(
		AttributeDefinition attributeDefinition) {

		int type = attributeDefinition.getType();

		switch (type) {
			case AttributeDefinition.BOOLEAN: {

				String[] optionLabels = attributeDefinition.getOptionLabels();

				if ( (optionLabels == null) || (optionLabels.length == 0)) {
					return "checkbox";
				}
				else {
					return "radio";
				}
			}

			default: {

				DDMFormFieldOptions ddmFormFieldOptions = _getDDMFieldOptions(
					attributeDefinition);

				if (_hasDDMFormFieldOptionsAvailable(ddmFormFieldOptions)) {
					return "select";
				}

				return "text";
			}
		}
	}

	private static LocalizedValue _attributeToLabel(
		AttributeDefinition attributeDefinition) {

		LocalizedValue localizedValue = new LocalizedValue(
			LocaleUtil.getDefault());

		localizedValue.addString(
			LocaleUtil.getDefault(), attributeDefinition.getName());

		return localizedValue;
	}

	private static LocalizedValue _attributeToTip(
		AttributeDefinition attributeDefinition) {

		LocalizedValue localizedValue = new LocalizedValue(
			LocaleUtil.getDefault());

		localizedValue.addString(
			LocaleUtil.getDefault(), attributeDefinition.getDescription());

		return localizedValue;
	}

	private static DDMFormFieldOptions _getDDMFieldOptions(
		AttributeDefinition attributeDefinition) {

		DDMFormFieldOptions ddmFormFieldOptions = new DDMFormFieldOptions();

		String[] labels = attributeDefinition.getOptionLabels();
		String[] values = attributeDefinition.getOptionValues();

		_setOptionFieldLabelsAndValues(ddmFormFieldOptions, labels, values);

		return ddmFormFieldOptions;
	}

	private static boolean _hasDDMFormFieldOptionsAvailable(
		DDMFormFieldOptions ddmFormFieldOptions) {

		Set<String> optionValues = ddmFormFieldOptions.getOptionsValues();

		if (optionValues.isEmpty()) {
			return false;
		}

		return true;
	}

	private static void _setDDMFormFieldOptions(
		String type, DDMFormField ddmFormField,
		DDMFormFieldOptions ddmFormFieldOptions) {

		ddmFormField.setType(type);

		if ("radio".equals(type)) {
			ddmFormField.setDataType(FieldConstants.BOOLEAN);
		}
		else {
			ddmFormField.setDataType(FieldConstants.STRING);
		}

		ddmFormField.setDDMFormFieldOptions(ddmFormFieldOptions);
	}

	private static void _setOptionFieldLabelsAndValues(
		DDMFormFieldOptions ddmFormFieldOptions, String[] labels,
		String[] values) {

		if ((labels != null) && (values != null)) {
			for (int i = 0; i < labels.length; i++) {
				String value = values[i];

				String label = labels[i];

				ddmFormFieldOptions.addOption(value);

				ddmFormFieldOptions.addOptionLabel(
					value, LocaleUtil.getDefault(), label);
			}
		}
	}

}