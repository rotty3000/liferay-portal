package com.liferay.messaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.osgi.util.converter.Converter;
import org.osgi.util.converter.StandardConverter;

import com.liferay.portal.kernel.util.MapUtil;

public class FelixConverterMapUtilCompatibilityTest {

	@Test
	public void test() {
		Assert.assertEquals(1 + 1, 2);
	}
	
	@Test
	public void testStringConversion() {
		Map<String, Object> values = new HashMap<String, Object>();
		values.put("object", new Object());
		values.put("string", new String("abc"));
		values.put("integer", new Integer(42));
		values.put("double", new Double(4.2));
		values.put("float", 0.42f);
		List<String> strings = new ArrayList<String>();
		strings.add("ab");
		strings.add("cd");
		strings.add("ef");
		strings.add("gh");
		values.put("strings", strings);
		values.put("null", null);

		String mapString = MapUtil.toString(values, null, ".*[pP]assword.*");
		Converter converter = new StandardConverter();
		String converterString = converter.convert(values).to(String.class);
		Assert.assertEquals(mapString, converterString);
	}


}
