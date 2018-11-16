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

package com.liferay.portal.osgi.web.wab.generator.internal.cdi;

import aQute.bnd.component.MergedRequirement;
import aQute.bnd.component.annotations.ReferenceCardinality;
import aQute.bnd.header.Attrs;
import aQute.bnd.header.OSGiHeader;
import aQute.bnd.header.Parameters;
import aQute.bnd.osgi.Analyzer;
import aQute.bnd.osgi.Clazz;
import aQute.bnd.osgi.Constants;
import aQute.bnd.osgi.Descriptors;
import aQute.bnd.osgi.Instruction;
import aQute.bnd.osgi.Instructions;
import aQute.bnd.osgi.Jar;
import aQute.bnd.osgi.Packages;
import aQute.bnd.osgi.Resource;
import aQute.bnd.service.AnalyzerPlugin;
import aQute.bnd.version.Version;

import aQute.lib.strings.Strings;

import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Node;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.kernel.xml.UnsecureSAXReaderUtil;

import java.lang.reflect.Modifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Analyze the class space for any classes that have an OSGi annotation for CCR.
 *
 * @author Raymond Augé
 */
public class CDIAnnotations implements AnalyzerPlugin {

	@Override
	public boolean analyzeJar(Analyzer analyzer) throws Exception {
		Parameters header = OSGiHeader.parseHeader(
			analyzer.getProperty(Constants.CDIANNOTATIONS, "*"));

		if (header.size() == 0) {
			return false;
		}

		Instructions instructions = new Instructions(header);

		Map<Descriptors.TypeRef, Clazz> classspace = analyzer.getClassspace();

		Collection<Clazz> list = classspace.values();

		Packages contained = analyzer.getContained();

		Parameters bcp = analyzer.getBundleClassPath();
		Jar currentJar = analyzer.getJar();

		List<String> beanArchives = new ArrayList<>();

		beanArchives.add(_name(currentJar.getName(), currentJar.getVersion()));

		for (Map.Entry<String, Attrs> entry : bcp.entrySet()) {
			String path = entry.getKey();

			Resource resource = currentJar.getResource(path);

			if (resource != null) {
				Jar jar = Jar.fromResource(path, resource);

				Resource beansResource = jar.getResource("META-INF/beans.xml");

				Discover discover = _findDiscoveryMode(beansResource);

				if (discover != Discover.none) {
					beanArchives.add(_name(path, jar.getVersion()));
				}
			}
		}

		List<String> names = new ArrayList<>();
		TreeSet<String> provides = new TreeSet<>();
		TreeSet<String> requires = new TreeSet<>();

		for (Clazz c : list) {
			Descriptors.TypeRef typeRef = c.getClassName();

			String fqn = typeRef.getFQN();

			if (c.isEnum() || c.isInterface() || c.isAnnotation() ||
				c.isSynthetic() ||
				!c.is(Clazz.QUERY.CONCRETE, null, analyzer) ||
				(fqn.contains("$") && !Modifier.isStatic(c.getAccess()))) {

				// These types cannot be managed beans so don't bother scanning
				// them. See
				// http://docs.jboss.org/cdi/spec/2.0/cdi-spec.html#what_classes_are_beans

				continue;
			}

			// It has to be a class inside the bundle

			Descriptors.PackageRef packageRef = typeRef.getPackageRef();

			Attrs packageAttrs = contained.get(packageRef);

			if (packageAttrs == null) {
				continue;
			}

			String isd = packageAttrs.get(Constants.INTERNAL_SOURCE_DIRECTIVE);

			if (!beanArchives.contains(isd)) {
				continue;
			}

			for (Map.Entry<Instruction, Attrs> entry :
					instructions.entrySet()) {

				Instruction instruction = entry.getKey();
				Attrs attrs = entry.getValue();

				if (instruction.matches(c.getFQN())) {
					if (instruction.isNegated()) {
						break;
					}

					String discover = attrs.get("discover");
					EnumSet<Discover> options = EnumSet.noneOf(Discover.class);

					try {
						Discover.parse(discover, options, this);
					}
					catch (IllegalArgumentException iae) {
						analyzer.error(
							"Unrecognized discover '%s', expected values are " +
								"%s",
							discover, EnumSet.allOf(Discover.class));
					}

					if (options.isEmpty()) {

						// set the default mode

						options.add(Discover.annotated_by_bean);
					}

					if (options.contains(Discover.none)) {
						break;
					}

					List<BeanDef> definitions =
						CDIAnnotationReader.getDefinition(c, analyzer, options);

					if (definitions == null) {
						break;
					}

					BeanDef beanDef = definitions.get(0);

					names.add(beanDef.implementation.getFQN());

					if (!attrs.containsKey("noservicecapabilities")) {
						for (BeanDef currentBeanDef : definitions) {
							if (!currentBeanDef.service.isEmpty()) {
								int length = currentBeanDef.service.size();

								String[] objectClass = new String[length];

								for (int i = 0; i < length; i++) {
									Descriptors.TypeRef tr =
										currentBeanDef.service.get(i);

									objectClass[i] = tr.getFQN();
								}

								Arrays.sort(objectClass);
								_addServiceCapability(objectClass, provides);
							}
						}
					}

					if (!attrs.containsKey("noservicerequirements")) {
						MergedRequirement serviceReqMerge =
							new MergedRequirement("osgi.service");

						for (ReferenceDef ref : beanDef.references) {
							_addServiceRequirement(ref, serviceReqMerge);
						}

						requires.addAll(serviceReqMerge.toStringList());
					}

					break;
				}
			}
		}

		if (!names.isEmpty()) {
			_addExtenderRequirement(requires, names, CDIAnnotationReader.V1_0);
		}

		_updateHeader(analyzer, Constants.REQUIRE_CAPABILITY, requires);
		_updateHeader(analyzer, Constants.PROVIDE_CAPABILITY, provides);

		return false;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

	private void _addExtenderRequirement(
		Set<String> requires, List<String> beans, Version version) {

		Version nextVersion = version.bumpMajor();
		Parameters p = new Parameters();
		Attrs a = new Attrs();

		a.put(
			Constants.FILTER_DIRECTIVE,
			StringBundler.concat(
				"\"(&(osgi.extender=osgi.cdi)(version>=", version.toString(),
				")(!(version>=", nextVersion.toString(), ")))\""));
		a.put("beans:List<String>", String.join(",", beans));

		p.put("osgi.extender", a);

		String s = p.toString();

		requires.add(s);
	}

	private void _addServiceCapability(
		String[] objectClass, Set<String> provides) {

		if (objectClass.length > 0) {
			Parameters p = new Parameters();

			Attrs a = new Attrs();

			StringBuilder sb = new StringBuilder();

			String sep = "";

			for (String oc : objectClass) {
				sb.append(sep);
				sb.append(oc);

				sep = ",";
			}

			a.put("objectClass:List<String>", sb.toString());

			p.put("osgi.service", a);

			String s = p.toString();

			provides.add(s);
		}
	}

	private void _addServiceRequirement(
		ReferenceDef ref, MergedRequirement requires) {

		String objectClass = ref.service;
		ReferenceCardinality cardinality = ref.cardinality;

		boolean optional = false;

		if ((cardinality == ReferenceCardinality.MULTIPLE) ||
			(cardinality == ReferenceCardinality.OPTIONAL)) {

			optional = true;
		}

		boolean multiple = false;

		if ((cardinality == ReferenceCardinality.AT_LEAST_ONE) ||
			(cardinality == ReferenceCardinality.MULTIPLE)) {

			multiple = true;
		}

		String filter = "(objectClass=" + objectClass + ")";

		requires.put(filter, "active", optional, multiple);
	}

	private Discover _findDiscoveryMode(Resource beansResource) {
		if (beansResource == null) {
			return Discover.none;
		}

		Document document = _readXMLResource(beansResource);

		if (!document.hasContent()) {
			return Discover.all;
		}

		Node versionNode = document.selectSingleNode("/beans/@version");

		if (versionNode != null) {
			Version version = Version.valueOf(versionNode.getStringValue());

			if (CDIAnnotationReader.CDI_ARCHIVE_VERSION.compareTo(version) <=
					0) {

				Node beanDisoveryModeNode = document.selectSingleNode(
					"/beans/@bean-discovery-mode");

				if (beanDisoveryModeNode != null) {
					return Discover.valueOf(
						beanDisoveryModeNode.getStringValue());
				}

				return Discover.annotated;
			}
		}

		return Discover.all;
	}

	private String _name(String name, String version) throws Exception {
		if (version == null) {
			version = "0.0.0";
		}

		return name + "-" + version;
	}

	private Document _readXMLResource(Resource resource) {
		try {
			return UnsecureSAXReaderUtil.read(resource.openInputStream());
		}
		catch (Throwable t) {
			return SAXReaderUtil.createDocument();
		}
	}

	/**
	 * Updates specified header, sorting and removing duplicates. Destroys
	 * contents of set parameter.
	 *
	 * @param analyzer
	 * @param name header name
	 * @param set values to add to header; contents are not preserved.
	 */
	private void _updateHeader(
		Analyzer analyzer, String name, TreeSet<String> set) {

		if (!set.isEmpty()) {
			String value = analyzer.getProperty(name);

			if (value != null) {
				Parameters p = OSGiHeader.parseHeader(value);

				for (Map.Entry<String, Attrs> entry : p.entrySet()) {
					StringBuilder sb = new StringBuilder(entry.getKey());

					Attrs attrs = entry.getValue();

					if (attrs != null) {
						sb.append(";");

						attrs.append(sb);
					}

					set.add(sb.toString());
				}
			}

			String header = Strings.join(set);

			analyzer.setProperty(name, header);
		}
	}

}