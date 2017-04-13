package com.liferay.messaging;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.osgi.util.converter.StandardConverter;

import com.liferay.portal.kernel.util.GetterUtil;

public class FelixConverterGetterUtilCompatibilityTest {
	
	@Before
	public void setUp() {
		_converter = new StandardConverter();
	}

	@Test
	public void testStringConversion() {
		Object object = new Object();
		String getterString = GetterUtil.getString(object);
		String converterString = _converter.convert(object).to(String.class);
		Assert.assertEquals(getterString, converterString);
	}
	
	private StandardConverter _converter;

}
