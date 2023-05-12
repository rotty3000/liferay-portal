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

package com.liferay.batch.engine.internal.test;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.batch.engine.BatchEngineTaskContentType;
import com.liferay.batch.engine.service.BatchEngineImportTaskLocalService;
import com.liferay.batch.engine.unit.BatchEngineUnit;
import com.liferay.batch.engine.unit.BatchEngineUnitConfiguration;
import com.liferay.batch.engine.unit.BatchEngineUnitProcessor;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

import java.nio.charset.StandardCharsets;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Raymond Augé
 */
@RunWith(Arquillian.class)
public class BatchEngineUnitProcessorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	public boolean isBatchEngineTechnical(String urlPath) {
		if (urlPath.endsWith("batch-engine-data.json")) {
			return true;
		}

		return false;
	}

	@Before
	public void setUp() throws Exception {
		_bundle = FrameworkUtil.getBundle(BatchEngineUnitProcessorTest.class);
	}

	@Test
	public void testBatchEngineUnitProcessorBatch1() throws Exception {
		_testBatchEngineUnitProcessor("batch1", 1, 0, 1, 4, 0);
	}

	@Test
	public void testBatchEngineUnitProcessorBatch2() throws Exception {
		_testBatchEngineUnitProcessor("batch2", 1, 1, 0, 0, 1);
	}

	@Test
	public void testBatchEngineUnitProcessorBatch3() throws Exception {
		_testBatchEngineUnitProcessor("batch3", 2, 0, 2, 8, 0);
	}

	@Test
	public void testBatchEngineUnitProcessorBatch4() throws Exception {
		_testBatchEngineUnitProcessor("batch4", 3, 0, 3, 12, 0);
	}

	@Test
	public void testBatchEngineUnitProcessorBatch5() throws Exception {
		_testBatchEngineUnitProcessor("batch5", 1, 0, 1, 4, 0);
	}

	@Test
	public void testBatchEngineUnitProcessorBatch6() throws Exception {
		_testBatchEngineUnitProcessor("batch6", 2, 0, 2, 8, 0);
	}

	@Test
	public void testBatchEngineUnitProcessorBatch7() throws Exception {
		_testBatchEngineUnitProcessor("batch7", 1, 0, 1, 4, 0);
	}

	@Test
	public void testBatchEngineUnitProcessorBatch8() throws Exception {
		_testBatchEngineUnitProcessor("batch8", 5, 2, 3, 12, 2);
	}

	@Test
	public void testBatchEngineUnitProcessorBatch9() throws Exception {
		_testBatchEngineUnitProcessor("batch9", 2, 0, 1, 7, 0);
	}

	private String _getURLKey(URL url) {
		String urlPath = url.getPath();

		if (isBatchEngineTechnical(urlPath)) {
			return urlPath;
		}

		if (!urlPath.contains(StringPool.SLASH)) {
			return StringPool.BLANK;
		}

		return urlPath.substring(0, urlPath.lastIndexOf(StringPool.SLASH));
	}

	private void _testBatchEngineUnitProcessor(
			String batchName, int expectedCount, int invalidCount,
			int errorLogs, int infoLogs, int warnLogs)
		throws Exception {

		Collection<BatchEngineUnit> batchEngineUnits = _toBatchEngineUnits(
			batchName);

		Assert.assertEquals(
			batchEngineUnits.toString(), expectedCount,
			batchEngineUnits.size());

		Stream<BatchEngineUnit> stream1 = batchEngineUnits.stream();

		List<BatchEngineUnit> invalidBatchEngineUnits = stream1.filter(
			batchEngineUnit -> !batchEngineUnit.isValid()
		).collect(
			Collectors.toList()
		);

		Assert.assertEquals(
			invalidBatchEngineUnits.toString(), invalidCount,
			invalidBatchEngineUnits.size());

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.batch.engine.internal", LoggerTestUtil.ALL)) {

			_batchEngineUnitProcessor.processBatchEngineUnits(batchEngineUnits);

			Thread.sleep(3000);

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Stream<LogEntry> stream2 = logEntries.stream();

			Map<String, List<LogEntry>> logEntriesMap = stream2.collect(
				Collectors.groupingBy(LogEntry::getPriority));

			List<LogEntry> errorLogEntries = logEntriesMap.getOrDefault(
				"ERROR", Collections.emptyList());

			Assert.assertEquals(
				errorLogEntries.toString(), errorLogs, errorLogEntries.size());

			List<LogEntry> infoLogEntries = logEntriesMap.getOrDefault(
				"INFO", Collections.emptyList());

			Assert.assertEquals(
				infoLogEntries.toString(), infoLogs, infoLogEntries.size());

			List<LogEntry> warnLogEntries = logEntriesMap.getOrDefault(
				"WARN", Collections.emptyList());

			Assert.assertEquals(
				warnLogEntries.toString(), warnLogs, warnLogEntries.size());
		}
	}

	private Collection<BatchEngineUnit> _toBatchEngineUnits(String dirName)
		throws Exception {

		Map<String, BatchEngineUnit> batchEngineUnits = new TreeMap<>();

		String basePath = StringBundler.concat(
			"com/liferay/batch/engine/internal/test/dependencies/", dirName,
			StringPool.SLASH);

		Enumeration<URL> enumeration = _bundle.findEntries(basePath, "*", true);

		if (enumeration != null) {
			Map<String, URL> batchEngineURLs = new HashMap<>();

			while (enumeration.hasMoreElements()) {
				URL url = enumeration.nextElement();

				String urlPath = url.getPath();

				String fileName = FileUtil.getShortFileName(urlPath);

				if (urlPath.endsWith(StringPool.SLASH) ||
					(!_classicBatchDataExtensions.contains(
						FileUtil.getExtension(fileName)) &&
					 !Objects.equals(fileName, "batch-engine.json"))) {

					continue;
				}

				String key = _getURLKey(url);

				URL complementURL = batchEngineURLs.get(key);

				if (complementURL == null) {
					batchEngineURLs.put(key, url);

					batchEngineUnits.put(
						key, new TestBatchEngineUnit(false, dirName, url));

					continue;
				}

				batchEngineUnits.put(
					key,
					new TestBatchEngineUnit(true, dirName, url, complementURL));

				batchEngineURLs.remove(key);
			}
		}

		return batchEngineUnits.values();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BatchEngineUnitProcessorTest.class);

	private static final List<String> _classicBatchDataExtensions =
		Arrays.asList(
			BatchEngineTaskContentType.CSV.getFileExtension(),
			BatchEngineTaskContentType.JSON.getFileExtension(),
			BatchEngineTaskContentType.JSONL.getFileExtension(),
			BatchEngineTaskContentType.XLS.getFileExtension(),
			BatchEngineTaskContentType.XLSX.getFileExtension());

	@Inject
	private BatchEngineImportTaskLocalService
		_batchEngineImportTaskLocalService;

	@Inject
	private BatchEngineUnitProcessor _batchEngineUnitProcessor;

	private Bundle _bundle;

	@DeleteAfterTestRun
	private ClassName _className;

	private class TestBatchEngineUnit implements BatchEngineUnit {

		public TestBatchEngineUnit(
			boolean classicMode, String fileName, URL... urls) {

			_classicMode = classicMode;
			_fileName = fileName;

			if ((urls == null) || (urls.length > 2)) {
				return;
			}

			for (URL url : urls) {
				if (_isBatchEngineConfiguration(url.getPath())) {
					_configURL = url;

					continue;
				}

				_dataURL = url;
			}
		}

		@Override
		public BatchEngineUnitConfiguration getBatchEngineUnitConfiguration()
			throws IOException {

			URL configURL = _configURL;

			if (!_classicMode) {
				configURL = _dataURL;
			}

			try (InputStream inputStream = configURL.openStream()) {
				JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
					StringUtil.read(inputStream));

				if (!_classicMode) {
					jsonObject = jsonObject.getJSONObject("configuration");
				}

				ObjectMapper objectMapper = new ObjectMapper();

				return objectMapper.readValue(
					jsonObject.toString(), BatchEngineUnitConfiguration.class);
			}
			catch (JSONException jsonException) {
				throw new IOException(jsonException);
			}
		}

		@Override
		public InputStream getConfigurationInputStream() throws IOException {
			if (_classicMode) {
				return _configURL.openStream();
			}

			throw new UnsupportedOperationException();
		}

		@Override
		public String getDataFileName() {
			if (_dataURL == null) {
				return "null";
			}

			return _dataURL.getPath();
		}

		@Override
		public InputStream getDataInputStream() throws IOException {
			if (_classicMode) {
				return _dataURL.openStream();
			}

			try (InputStream inputStream = _dataURL.openStream()) {
				JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
					StringUtil.read(inputStream));

				String itemsString = String.valueOf(
					jsonObject.getJSONObject("items"));

				return new ByteArrayInputStream(
					itemsString.getBytes(StandardCharsets.UTF_8));
			}
			catch (JSONException jsonException) {
				throw new IOException(jsonException);
			}
		}

		@Override
		public String getFileName() {
			return _fileName;
		}

		@Override
		public boolean isValid() {
			if (_classicMode) {
				if ((_configURL == null) || (_dataURL == null)) {
					return false;
				}

				return true;
			}

			if (_dataURL == null) {
				return false;
			}

			try (InputStream inputStream = _dataURL.openStream()) {
				JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
					StringUtil.read(inputStream));

				return jsonObject.has("items");
			}
			catch (Exception exception) {
				_log.error(
					"Unable to get data in file " + _dataURL.getPath(),
					exception);
			}

			return false;
		}

		@Override
		public String toString() {
			return StringBundler.concat(
				"TestBatchEngineUnit [classicMode=",
				Boolean.toString(_classicMode), ", configURL=",
				String.valueOf(_configURL), ", dataURL=",
				String.valueOf(_dataURL), ", fileName=", _fileName, "]");
		}

		private boolean _isBatchEngineConfiguration(String urlPath) {
			if (Objects.equals(urlPath, "batch-engine.json") ||
				urlPath.endsWith("/batch-engine.json")) {

				return true;
			}

			return false;
		}

		private final boolean _classicMode;
		private URL _configURL;
		private URL _dataURL;
		private final String _fileName;

	}

}