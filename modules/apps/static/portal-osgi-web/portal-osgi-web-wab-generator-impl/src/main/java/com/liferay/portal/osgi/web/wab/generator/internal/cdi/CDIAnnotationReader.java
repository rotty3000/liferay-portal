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

import aQute.bnd.component.annotations.ReferenceCardinality;
import aQute.bnd.osgi.Analyzer;
import aQute.bnd.osgi.Annotation;
import aQute.bnd.osgi.ClassDataCollector;
import aQute.bnd.osgi.Clazz;
import aQute.bnd.osgi.Descriptors;
import aQute.bnd.osgi.Instruction;
import aQute.bnd.signatures.ClassResolver;
import aQute.bnd.signatures.ClassSignature;
import aQute.bnd.signatures.ClassTypeSignature;
import aQute.bnd.signatures.FieldResolver;
import aQute.bnd.signatures.FieldSignature;
import aQute.bnd.signatures.JavaTypeSignature;
import aQute.bnd.signatures.MethodResolver;
import aQute.bnd.signatures.MethodSignature;
import aQute.bnd.signatures.ReferenceTypeSignature;
import aQute.bnd.signatures.Result;
import aQute.bnd.signatures.VoidDescriptor;
import aQute.bnd.version.Version;

import java.lang.annotation.ElementType;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Raymond Augé
 */
public class CDIAnnotationReader extends ClassDataCollector {

	public static final Version CDI_ARCHIVE_VERSION = new Version(1, 1, 0);

	public static final Version V1_0 = new Version(1, 0, 0);

	public static List<BeanDef> getDefinition(
			Clazz c, Analyzer analyzer, EnumSet<Discover> options)
		throws Exception {

		CDIAnnotationReader r = new CDIAnnotationReader(analyzer, c, options);

		return r._defs();
	}

	@Override
	public void annotation(Annotation annotation) {
		try {
			Descriptors.TypeRef typeRef = annotation.getName();

			String fqn = typeRef.getFQN();

			if ("org.osgi.service.cdi.annotations.Bean".equals(fqn)) {
				BeanDef beanDef = _definitions.get(0);

				beanDef.marked = true;
			}
			else if ("org.osgi.service.cdi.annotations.Service".equals(fqn)) {
				_service(annotation);
			}
			else if ("org.osgi.service.cdi.annotations.MinimumCardinality".
						equals(fqn)) {

				int minimumCardinality = (int)annotation.get("value");

				if (minimumCardinality > 0) {
					if (_referenceDef == null) {
						_referenceDef = new ReferenceDef();
					}

					_referenceDef.cardinality =
						ReferenceCardinality.AT_LEAST_ONE;
				}
			}
			else if ("org.osgi.service.cdi.annotations.Reference".equals(fqn)) {
				_reference(annotation, _parameter);
			}
		}
		catch (Exception e) {
			_analyzer.exception(
				e, "During bean processing on class %s, exception %s", _clazz,
				e);
		}
	}

	@Override
	public void classBegin(int access, Descriptors.TypeRef name) {
		BeanDef beanDef = _definitions.get(0);

		beanDef.implementation = name;

		PackageDef packageDef = _packageInfos.computeIfAbsent(
			name.getPackageRef(),
			k -> {
				Clazz packageInfoClazz = _analyzer.getPackageInfo(k);

				if (packageInfoClazz != null) {
					try {
						PackageDef pd = new PackageDef();

						packageInfoClazz.parseClassFileWithCollector(pd);

						return pd;
					}
					catch (Exception e) {
						_analyzer.exception(
							e,
							"Error while processing package-info of class %s",
							_clazz);
					}
				}

				return new PackageDef();
			});

		if (packageDef.marked != null) {
			beanDef.marked = packageDef.marked.matches(name.getFQN());
		}
	}

	@Override
	public void classEnd() throws Exception {
		_member = null;
		_referenceDef = null;
	}

	@Override
	public void extendsClass(Descriptors.TypeRef name) {
		_extendsClass = name;
	}

	@Override
	public void field(Clazz.FieldDef field) {
		_member = field;
	}

	@Override
	public void implementsInterfaces(Descriptors.TypeRef[] interfaces) {
		_interfaces = interfaces;
	}

	@Override
	public void memberEnd() {
		_member = null;
		_referenceDef = null;
		_parameter = -1;
	}

	@Override
	public void method(Clazz.MethodDef method) {
		if (method.isAbstract() || method.isBridge() || method.isSynthetic()) {
			_member = null;

			return;
		}

		if (!_baseClass && method.isPrivate()) {
			_member = null;

			return;
		}

		_member = method;

		Descriptors.Descriptor descriptor = _member.getDescriptor();

		String signature = descriptor.toString();

		if (_member.getSignature() != null) {
			signature = _member.getSignature();
		}

		MethodSignature methodSig = _analyzer.getMethodSignature(signature);

		MethodResolver resolver = new MethodResolver(
			_classSignature, methodSig);

		if (methodSig.parameterTypes.length != 1) {
			return;
		}

		JavaTypeSignature parameterSig = resolver.resolveParameter(0);

		if (!(parameterSig instanceof ClassTypeSignature)) {
			return;
		}

		ClassTypeSignature param = (ClassTypeSignature)parameterSig;

		if ("org/osgi/service/cdi/reference/BindService".equals(param.binary) ||
			"org/osgi/service/cdi/reference/BindBeanServiceObjects".equals(
				param.binary) ||
			"org/osgi/service/cdi/reference/BindServiceReference".equals(
				param.binary)) {

			if (param.classType.typeArguments.length != 1) {
				_analyzer.error(
					"In bean %s, Bind parameter has wrong type arguments: %s",
					_clazz, param);

				return;
			}

			ReferenceTypeSignature inferred = resolver.resolveType(
				param.classType.typeArguments[0]);

			if (!(inferred instanceof ClassTypeSignature)) {
				_analyzer.error(
					"In bean %s, Bind parameter has unresolvable type " +
						"argument: %s",
					_clazz, inferred);

				return;
			}

			ClassTypeSignature classSig = (ClassTypeSignature)inferred;

			Descriptors.TypeRef typeRef = _analyzer.getTypeRef(classSig.binary);

			ReferenceDef referenceDef = new ReferenceDef();

			referenceDef.service = typeRef.getFQN();
			referenceDef.cardinality = ReferenceCardinality.MULTIPLE;

			BeanDef beanDef = _definitions.get(0);

			beanDef.references.add(referenceDef);
		}
	}

	@Override
	public void parameter(int p) {
		_parameter = p;
	}

	@Override
	public void typeuse(
		int targetType, int targetIndex, byte[] targetInfo, byte[] typePath) {

		if (targetType != 0x10) {
			targetIndex = Clazz.TYPEUSE_INDEX_NONE;

			return;
		}

		_targetIndex = targetIndex;
	}

	private CDIAnnotationReader(
		Analyzer analyzer, Clazz clazz, EnumSet<Discover> options) {

		_analyzer = Objects.requireNonNull(analyzer);
		_clazz = clazz;
		_options = options;

		_definitions.add(new BeanDef());

		String signature = clazz.getClassSignature();

		if (signature == null) {
			signature = "Ljava/lang/Object;";
		}

		_classSignature = analyzer.getClassSignature(signature);
	}

	private List<BeanDef> _defs() throws Exception {

		// if discovery mode is 'annotated', only classes annotated with a bean
		// defining annotation are considered. See
		// http://docs.jboss.org/cdi/spec/2.0/cdi-spec.html#bean_defining_annotations

		if (_options.contains(Discover.annotated)) {
			if (_clazz.is(Clazz.QUERY.ANNOTATED, _VETOED_INSTR, _analyzer) ||
				(!_clazz.is(
					Clazz.QUERY.INDIRECTLY_ANNOTATED, _COMPONENTSCOPED_INSTR,
					_analyzer) &&
				 !_clazz.is(
					 Clazz.QUERY.INDIRECTLY_ANNOTATED, _NORMALSCOPE_INSTR,
					 _analyzer) &&
				 !_clazz.is(
					 Clazz.QUERY.INDIRECTLY_ANNOTATED, _STEREOTYPE_INSTR,
					 _analyzer) &&
				 !_clazz.is(
					 Clazz.QUERY.ANNOTATED, _DEPENDENT_INSTR, _analyzer) &&
				 !_clazz.is(
					 Clazz.QUERY.ANNOTATED, _INTERCEPTOR_INSTR, _analyzer)) ||
				_clazz.is(
					Clazz.QUERY.IMPLEMENTS, _EXTENSION_INSTR, _analyzer)) {

				return null;
			}

			// check for @Vitoed package

			Descriptors.TypeRef typeRef = _clazz.getClassName();

			Clazz packageClazz = _analyzer.getPackageInfo(
				typeRef.getPackageRef());

			if ((packageClazz != null) &&
				packageClazz.is(
					Clazz.QUERY.ANNOTATED, _VETOED_INSTR, _analyzer)) {

				return null;
			}
		}

		_clazz.parseClassFileWithCollector(this);

		// the default discovery mode is 'annotated_by_bean' to indicate that
		// classes annotated with @Bean or in packages annotated with @Beans
		// are considered

		BeanDef beanDef = _definitions.get(0);

		if (_options.contains(Discover.annotated_by_bean) && !beanDef.marked) {
			return null;
		}

		return _definitions;
	}

	private void _reference(Annotation reference, int targetIndex) {
		Object value = reference.get("value");

		if (value != null) {
			Descriptors.TypeRef typeRef = (Descriptors.TypeRef)value;

			_referenceDef = new ReferenceDef();

			_referenceDef.service = typeRef.getFQN();
			_referenceDef.cardinality = ReferenceCardinality.MANDATORY;

			BeanDef beanDef = _definitions.get(0);

			beanDef.references.add(_referenceDef);

			return;
		}

		ClassTypeSignature type = null;
		ClassResolver resolver = null;

		Descriptors.Descriptor descriptor = _member.getDescriptor();

		String signature = descriptor.toString();

		ElementType elementType = reference.getElementType();

		if (ElementType.PARAMETER == elementType) {
			if (_member.getSignature() != null) {
				signature = _member.getSignature();
			}

			MethodSignature methodSig = _analyzer.getMethodSignature(signature);

			resolver = new MethodResolver(_classSignature, methodSig);

			JavaTypeSignature parameterType =
				((MethodResolver)resolver).resolveParameter(_parameter);

			if (!(parameterType instanceof ClassTypeSignature)) {
				_analyzer.error(
					"In bean %s, method %s, parameter %s with @Reference has " +
						"unresolved type %s",
					_classSignature, _member.getName(), targetIndex,
					parameterType);

				return;
			}

			type = (ClassTypeSignature)parameterType;
		}
		else {
			FieldSignature fieldSig = null;

			if (signature == null) {
				try {
					fieldSig = _analyzer.getFieldSignature(
						descriptor.toString());
				}
				catch (IllegalArgumentException iae) {
					fieldSig = null;
				}
			}
			else {
				fieldSig = _analyzer.getFieldSignature(signature);
			}

			if (fieldSig == null) {
				_analyzer.error(
					"In bean %s, field %s has an incompatible type for " +
						"@Reference: %s",
					_clazz, _member.getName(), _member.getDescriptor());

				return;
			}

			resolver = new FieldResolver(_classSignature, fieldSig);

			ReferenceTypeSignature inferred =
				((FieldResolver)resolver).resolveField();

			if (!(inferred instanceof ClassTypeSignature)) {
				_analyzer.error(
					"In bean %s, field %s with @Reference has unresolved " +
						"type: %s",
					_classSignature, _member.getName(), inferred);

				return;
			}

			type = (ClassTypeSignature)inferred;
		}

		Descriptors.TypeRef typeRef = _analyzer.getTypeRef(type.binary);

		String fqn = typeRef.getFQN();

		// unwrap Provider

		if (fqn.equals("javax.inject.Provider")) {
			ReferenceTypeSignature inferred = resolver.resolveType(
				type.classType.typeArguments[0]);

			if (!(inferred instanceof ClassTypeSignature)) {
				_analyzer.error(
					"In bean %s, in member %s with @Reference the type " +
						"argument of Provider can not be resolved: %s",
					_clazz, _member.getName(), inferred);

				return;
			}

			type = (ClassTypeSignature)inferred;

			fqn = Descriptors.binaryToFQN(type.binary);
		}

		// unwrap Collection, List, Optional

		ReferenceCardinality cardinality = ReferenceCardinality.MANDATORY;

		if (fqn.equals("java.util.Collection") ||
			fqn.equals("java.util.List")) {

			ReferenceTypeSignature inferred = resolver.resolveType(
				type.classType.typeArguments[0]);

			if (!(inferred instanceof ClassTypeSignature)) {
				_analyzer.error(
					"In bean %s, in member %s with @Reference the type " +
						"argument of Collection or List can not be resolved: " +
							"%s",
					_clazz, _member.getName(), inferred);

				return;
			}

			type = (ClassTypeSignature)inferred;

			fqn = Descriptors.binaryToFQN(type.binary);

			cardinality = ReferenceCardinality.MULTIPLE;
		}
		else if (fqn.equals("java.util.Optional")) {
			ReferenceTypeSignature inferred = resolver.resolveType(
				type.classType.typeArguments[0]);

			if (!(inferred instanceof ClassTypeSignature)) {
				_analyzer.error(
					"In bean %s, in member %s with @Reference the type " +
						"argument of Optional can not be resolved: %s",
					_clazz, _member.getName(), inferred);

				return;
			}

			type = (ClassTypeSignature)inferred;

			fqn = Descriptors.binaryToFQN(type.binary);

			cardinality = ReferenceCardinality.OPTIONAL;
		}

		if (fqn.equals("org.osgi.service.cdi.reference.BeanServiceObjects") ||
			fqn.equals("org.osgi.framework.ServiceReference")) {

			ReferenceTypeSignature inferred = resolver.resolveType(
				type.classType.typeArguments[0]);

			if (!(inferred instanceof ClassTypeSignature)) {
				_analyzer.error(
					"In bean %s, in member %s with @Reference the type " +
						"argument of BeanServiceObjects or ServiceReference " +
							"can not be resolved: %s",
					_clazz, _member.getName(), inferred);

				return;
			}

			type = (ClassTypeSignature)inferred;

			fqn = Descriptors.binaryToFQN(type.binary);
		}
		else if (fqn.equals("java.util.Map$Entry")) {
			ReferenceTypeSignature inferred = resolver.resolveType(
				type.innerTypes[0].typeArguments[1]);

			if (!(inferred instanceof ClassTypeSignature)) {
				_analyzer.error(
					"In bean %s, in member %s with @Reference the second " +
						"type argument of Map.Entry can not be resolved: %s",
					_clazz, _member.getName(), inferred);

				return;
			}

			type = (ClassTypeSignature)inferred;

			fqn = Descriptors.binaryToFQN(type.binary);
		}

		_referenceDef = new ReferenceDef();

		_referenceDef.service = fqn;
		_referenceDef.cardinality = cardinality;

		BeanDef beanDef = _definitions.get(0);

		beanDef.references.add(_referenceDef);
	}

	private void _service(Annotation annotation) {
		Descriptors.Descriptor descriptor = _member.getDescriptor();

		String signature = _member.getSignature();

		BeanDef beanDef;
		ClassTypeSignature classTypeSignature;
		Descriptors.TypeRef typeRef;

		ElementType elementType = annotation.getElementType();

		if (ElementType.FIELD == elementType) {
			FieldSignature fieldSig;

			if (signature == null) {
				try {
					fieldSig = _analyzer.getFieldSignature(
						descriptor.toString());
				}
				catch (IllegalArgumentException iae) {
					fieldSig = null;
				}
			}
			else {
				fieldSig = _analyzer.getFieldSignature(signature);
			}

			if (fieldSig == null) {
				_analyzer.error(
					"In bean %s, field %s has an incompatible type for " +
						"@Service: %s",
					_clazz, _member.getName(), descriptor);

				return;
			}

			FieldResolver fieldResolver = new FieldResolver(
				_classSignature, fieldSig);

			ReferenceTypeSignature type = fieldResolver.resolveField();

			if (!(type instanceof ClassTypeSignature)) {
				_analyzer.error(
					"In bean %s, field %s has an incompatible type for " +
						"@Service: %s",
					_clazz, _member.getName(), descriptor);

				return;
			}

			classTypeSignature = (ClassTypeSignature)type;

			typeRef = _analyzer.getTypeRef(classTypeSignature.binary);

			beanDef = new BeanDef();

			beanDef.service.add(typeRef);

			_definitions.add(beanDef);
		}
		else if (ElementType.METHOD == elementType) {
			MethodSignature methodSig;

			if (signature == null) {
				try {
					methodSig = _analyzer.getMethodSignature(
						descriptor.toString());
				}
				catch (IllegalArgumentException iae) {
					methodSig = null;
				}
			}
			else {
				methodSig = _analyzer.getMethodSignature(signature);
			}

			if (methodSig == null) {
				_analyzer.error(
					"In bean %s, method %s has an incompatible type for " +
						"@Service: %s",
					_clazz, _member.getName(), descriptor);

				return;
			}

			MethodResolver methodResolver = new MethodResolver(
				_classSignature, methodSig);

			Result result = methodResolver.resolveResult();

			if (result instanceof VoidDescriptor) {
				_analyzer.error(
					"In bean %s, method %s has @Service and returns void: %s",
					_clazz, _member.getName(), descriptor);

				return;
			}

			if (!(result instanceof ClassTypeSignature)) {
				_analyzer.error(
					"In bean %s, method %s has an incompatible return type " +
						"for @Service: %s",
					_clazz, _member.getName(), descriptor);

				return;
			}

			classTypeSignature = (ClassTypeSignature)result;

			typeRef = _analyzer.getTypeRef(classTypeSignature.binary);

			beanDef = new BeanDef();

			beanDef.service.add(typeRef);

			_definitions.add(beanDef);
		}
		else if (ElementType.TYPE_USE == elementType) {
			beanDef = _definitions.get(0);

			if ((beanDef.serviceOrigin != null) &&
				(beanDef.serviceOrigin == ElementType.TYPE)) {

				_analyzer.error(
					"In bean %s, @Service cannot be used both on TYPE and " +
						"TYPE_USE: %s",
					_clazz, _classSignature);

				return;
			}

			beanDef.serviceOrigin = ElementType.TYPE_USE;

			if (_targetIndex == Clazz.TYPEUSE_TARGET_INDEX_EXTENDS) {
				beanDef.service.add(_extendsClass);
			}
			else if (_targetIndex != Clazz.TYPEUSE_INDEX_NONE) {
				beanDef.service.add(_interfaces[_targetIndex]);
			}
		}
		else if (ElementType.TYPE == elementType) {
			beanDef = _definitions.get(0);

			if ((beanDef.serviceOrigin != null) &&
				(beanDef.serviceOrigin == ElementType.TYPE_USE)) {

				_analyzer.error(
					"In bean %s, @Service cannot be used both on TYPE and " +
						"TYPE_USE: %s",
					_clazz, _classSignature);

				return;
			}

			beanDef.serviceOrigin = ElementType.TYPE;

			Object[] serviceClasses = annotation.get("value");

			if ((serviceClasses != null) && (serviceClasses.length > 0)) {
				for (Object serviceClass : serviceClasses) {
					beanDef.service.add((Descriptors.TypeRef)serviceClass);
				}
			}
			else if ((_interfaces != null) && (_interfaces.length > 0)) {
				for (Descriptors.TypeRef inter : _interfaces) {
					beanDef.service.add(inter);
				}
			}
			else {
				beanDef.service.add(_clazz.getClassName());
			}
		}
	}

	private static final Instruction _COMPONENTSCOPED_INSTR = new Instruction(
		"org.osgi.service.cdi.annotations.ComponentScoped");

	private static final Instruction _DEPENDENT_INSTR = new Instruction(
		"javax.enterprise.context.Dependent");

	private static final Instruction _EXTENSION_INSTR = new Instruction(
		"javax.enterprise.inject.spi.Extension");

	private static final Instruction _INTERCEPTOR_INSTR = new Instruction(
		"javax.interceptor.Interceptor");

	private static final Instruction _NORMALSCOPE_INSTR = new Instruction(
		"javax.enterprise.context.NormalScope");

	private static final Instruction _STEREOTYPE_INSTR = new Instruction(
		"javax.enterprise.inject.Stereotype");

	private static final Instruction _VETOED_INSTR = new Instruction(
		"javax.enterprise.inject.Vetoed");

	private final Analyzer _analyzer;
	private boolean _baseClass = true;
	private final ClassSignature _classSignature;
	private final Clazz _clazz;
	private final List<BeanDef> _definitions = new ArrayList<>();
	private Descriptors.TypeRef _extendsClass;
	private Descriptors.TypeRef[] _interfaces;
	private Clazz.FieldDef _member;
	private final EnumSet<Discover> _options;
	private final Map<Descriptors.PackageRef, PackageDef> _packageInfos =
		new HashMap<>();
	private int _parameter = -1;
	private ReferenceDef _referenceDef;
	private int _targetIndex = Clazz.TYPEUSE_INDEX_NONE;

}