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

package com.liferay.dynamic.data.mapping.util;

import com.liferay.dynamic.data.mapping.BaseDDMTestCase;
import com.liferay.portal.bean.BeanPropertiesImpl;
import com.liferay.portal.kernel.bean.BeanPropertiesUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portlet.dynamicdatamapping.model.DDMForm;
import com.liferay.portlet.dynamicdatamapping.model.DDMFormField;
import com.liferay.portlet.dynamicdatamapping.model.DDMFormFieldOptions;
import com.liferay.portlet.dynamicdatamapping.model.LocalizedValue;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Leonardo Barros
 */
public class DDMFormDeepCopyTest extends BaseDDMTestCase {

	@Before
	public void setUp() {
		setUpBeanPropertiesUtil();
	}

	@Test
	public void testNestedFields() throws Exception {
		DDMForm ddmForm = createDDMForm();

		DDMFormField nameDDMFormField = new DDMFormField("Name", "textarea");

		ddmForm.addDDMFormField(nameDDMFormField);

		DDMFormField phoneDDMFormField = new DDMFormField("Phone", "text");

		nameDDMFormField.addNestedDDMFormField(phoneDDMFormField);

		DDMFormField contactDDMFormField = new DDMFormField("Name", "text");

		nameDDMFormField.addNestedDDMFormField(contactDDMFormField);

		DDMFormField addressDDMFormField = new DDMFormField("ZipCode", "text");

		contactDDMFormField.addNestedDDMFormField(addressDDMFormField);

		com.liferay.portlet.dynamicdatamapping.DDMForm copyDDMForm =
			BeanPropertiesUtil.deepCopyProperties(
				ddmForm, com.liferay.portlet.dynamicdatamapping.DDMForm.class);

		List<com.liferay.portlet.dynamicdatamapping.DDMFormField>
			copyDDMFormFields = copyDDMForm.getDDMFormFields();

		Assert.assertEquals(1, copyDDMFormFields.size());

		com.liferay.portlet.dynamicdatamapping.DDMFormField copyDDMFormField =
			copyDDMFormFields.get(0);

		Assert.assertEquals("Name", copyDDMFormField.getName());
		Assert.assertEquals("textarea", copyDDMFormField.getType());

		List<com.liferay.portlet.dynamicdatamapping.DDMFormField>
			copyNestedDDMFormFields = copyDDMFormField.getNestedDDMFormFields();

		Assert.assertEquals(2, copyNestedDDMFormFields.size());

		com.liferay.portlet.dynamicdatamapping.DDMFormField
			copyNestedDDMFormField = copyNestedDDMFormFields.get(0);

		Assert.assertEquals("Phone", copyNestedDDMFormField.getName());
		Assert.assertEquals("text", copyNestedDDMFormField.getType());

		copyNestedDDMFormField = copyNestedDDMFormFields.get(1);

		Assert.assertEquals("Name", copyNestedDDMFormField.getName());
		Assert.assertEquals("text", copyNestedDDMFormField.getType());

		Assert.assertEquals(
			1, copyNestedDDMFormField.getNestedDDMFormFields().size());

		copyNestedDDMFormField =
			copyNestedDDMFormField.getNestedDDMFormFields().get(0);

		Assert.assertEquals("ZipCode", copyNestedDDMFormField.getName());
		Assert.assertEquals("text", copyNestedDDMFormField.getType());
	}

	@Test
	public void testSomeFieldProperties() throws Exception {
		DDMForm ddmForm = createDDMForm();

		DDMFormField nameDDMFormField = new DDMFormField("Name", "textarea");

		ddmForm.addDDMFormField(nameDDMFormField);

		nameDDMFormField.setFieldNamespace("namespace");
		nameDDMFormField.setIndexType("indexType");

		LocalizedValue label = new LocalizedValue(LocaleUtil.BRAZIL);

		label.addString(LocaleUtil.BRAZIL, "teste");

		nameDDMFormField.setLabel(label);

		nameDDMFormField.setLocalizable(true);
		nameDDMFormField.setMultiple(true);
		nameDDMFormField.setReadOnly(true);
		nameDDMFormField.setRepeatable(true);
		nameDDMFormField.setRequired(true);
		nameDDMFormField.setShowLabel(true);

		LocalizedValue style = new LocalizedValue(LocaleUtil.ENGLISH);

		style.addString(LocaleUtil.ENGLISH, "style");

		nameDDMFormField.setStyle(style);

		LocalizedValue tip = new LocalizedValue(LocaleUtil.FRANCE);

		tip.addString(LocaleUtil.FRANCE, "tip");

		nameDDMFormField.setTip(tip);
		nameDDMFormField.setVisibilityExpression("expression");

		DDMFormFieldOptions ddmFormFieldOptions = new DDMFormFieldOptions();

		ddmFormFieldOptions.setDefaultLocale(LocaleUtil.BRAZIL);

		ddmFormFieldOptions.addOptionLabel(
			"teste", LocaleUtil.ENGLISH, "label");

		nameDDMFormField.setDDMFormFieldOptions(ddmFormFieldOptions);

		com.liferay.portlet.dynamicdatamapping.DDMForm copyDDMForm =
			BeanPropertiesUtil.deepCopyProperties(
				ddmForm, com.liferay.portlet.dynamicdatamapping.DDMForm.class);

		com.liferay.portlet.dynamicdatamapping.DDMFormField copyDDMFormField =
			copyDDMForm.getDDMFormFields().get(0);

		Assert.assertEquals("Name", copyDDMFormField.getName());
		Assert.assertEquals("textarea", copyDDMFormField.getType());
		Assert.assertEquals("namespace", copyDDMFormField.getFieldNamespace());
		Assert.assertEquals("indexType", copyDDMFormField.getIndexType());

		com.liferay.portlet.dynamicdatamapping.LocalizedValue copyLabel =
			copyDDMFormField.getLabel();

		Assert.assertEquals(LocaleUtil.BRAZIL, copyLabel.getDefaultLocale());
		Assert.assertEquals("teste", copyLabel.getString(LocaleUtil.BRAZIL));

		Assert.assertTrue(copyDDMFormField.isLocalizable());
		Assert.assertTrue(copyDDMFormField.isMultiple());
		Assert.assertTrue(copyDDMFormField.isReadOnly());
		Assert.assertTrue(copyDDMFormField.isRepeatable());
		Assert.assertTrue(copyDDMFormField.isRequired());
		Assert.assertTrue(copyDDMFormField.isShowLabel());

		com.liferay.portlet.dynamicdatamapping.LocalizedValue copyStyle =
			copyDDMFormField.getStyle();

		Assert.assertEquals(LocaleUtil.ENGLISH, copyStyle.getDefaultLocale());
		Assert.assertEquals("style", copyStyle.getString(LocaleUtil.ENGLISH));

		com.liferay.portlet.dynamicdatamapping.LocalizedValue copyTip =
			copyDDMFormField.getTip();

		Assert.assertEquals(LocaleUtil.FRANCE, copyTip.getDefaultLocale());
		Assert.assertEquals("tip", copyTip.getString(LocaleUtil.FRANCE));

		Assert.assertEquals(
			"expression", copyDDMFormField.getVisibilityExpression());

		com.liferay.portlet.dynamicdatamapping.DDMFormFieldOptions
			copyDDMFormFieldOptions = copyDDMFormField.getDDMFormFieldOptions();

		Assert.assertEquals(
			LocaleUtil.BRAZIL, copyDDMFormFieldOptions.getDefaultLocale());

		Map<String, com.liferay.portlet.dynamicdatamapping.LocalizedValue>
			copyOptions = copyDDMFormFieldOptions.getOptions();

		Assert.assertTrue(copyOptions.containsKey("teste"));

		com.liferay.portlet.dynamicdatamapping.LocalizedValue
			copyTesteOptionLabel = copyOptions.get("teste");

		Assert.assertEquals(
			"label", copyTesteOptionLabel.getString(LocaleUtil.ENGLISH));
	}

	@Test
	public void testWithFields() throws Exception {
		DDMForm ddmForm = createDDMForm();

		DDMFormField nameDDMFormField = new DDMFormField("Name", "textarea");

		DDMFormField phoneDDMFormField = new DDMFormField("Phone", "text");

		ddmForm.addDDMFormField(nameDDMFormField);
		ddmForm.addDDMFormField(phoneDDMFormField);

		com.liferay.portlet.dynamicdatamapping.DDMForm copyDDMForm =
			BeanPropertiesUtil.deepCopyProperties(
				ddmForm, com.liferay.portlet.dynamicdatamapping.DDMForm.class);

		List<com.liferay.portlet.dynamicdatamapping.DDMFormField>
			copyDDMFormFields = copyDDMForm.getDDMFormFields();

		Assert.assertEquals(2, copyDDMFormFields.size());

		com.liferay.portlet.dynamicdatamapping.DDMFormField copyDDMFormField =
			copyDDMFormFields.get(0);

		Assert.assertEquals("Name", copyDDMFormField.getName());
		Assert.assertEquals("textarea", copyDDMFormField.getType());

		copyDDMFormField = copyDDMFormFields.get(1);

		Assert.assertEquals("Phone", copyDDMFormField.getName());
		Assert.assertEquals("text", copyDDMFormField.getType());
	}

	@Test
	public void testWithoutFields() throws Exception {
		DDMForm ddmForm = createDDMForm();

		com.liferay.portlet.dynamicdatamapping.DDMForm copyDDMForm =
			BeanPropertiesUtil.deepCopyProperties(
				ddmForm, com.liferay.portlet.dynamicdatamapping.DDMForm.class);

		Set<Locale> availableLocales = copyDDMForm.getAvailableLocales();

		Assert.assertFalse(availableLocales.isEmpty());
		Assert.assertEquals(availableLocales.toArray()[0], LocaleUtil.US);
	}

	protected void setUpBeanPropertiesUtil() {
		BeanPropertiesUtil beanPropertiesUtil = new BeanPropertiesUtil();

		beanPropertiesUtil.setBeanProperties(new BeanPropertiesImpl());
	}

}