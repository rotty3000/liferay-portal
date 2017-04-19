package com.liferay.messaging.internal.convert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.liferay.portal.kernel.util.MapUtil;

public class CompatibilityTest {

	@Test
	public void test() {
		Map<String, Object> values = new HashMap<String, Object>();
		values.put("int", new Integer(42));
		values.put("double", new Double(42.0));
		List<Integer> list = new ArrayList<Integer>();
		list.add(1);
		list.add(2);
		list.add(3);
		values.put("list", list);
		values.put("password", "password");
		String mapUtilString = MapUtil.toString(values, null, ".*[pP]assword.*");
		String conversionsString = Conversions.getString(values);
		Assert.assertEquals(mapUtilString, conversionsString);
	}

}
