/*******************************************************************************
 * Copyright (c) 2013 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.tools.event.spy.internal.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.JavaUI;
import org.osgi.framework.FrameworkUtil;

public class JDTUtils {
	private final static Pattern CLASS_NAME_PATTERN = Pattern.compile("(([a-zA-Z_]+[0-9]*\\.)+[a-zA-Z_]+[a-z0-9]*)");

	public static boolean containsClassName(String name) {
		return CLASS_NAME_PATTERN.matcher(name).find();
	}

	public static void openClass(String clsName) throws ClassNotFoundException {
		Matcher matcher = CLASS_NAME_PATTERN.matcher(clsName);
		if (!matcher.find()) {
			return;
		}
		try {
			Class<?> cls = FrameworkUtil.getBundle(JDTUtils.class).loadClass(matcher.group(1).trim());
			IProject project = findProjectFor(cls);

			if (project != null) {
				openInEditor(JavaCore.create(project), cls.getName());
			}
		} catch (ClassNotFoundException exc) {
			throw new ClassNotFoundException("Class not found in the bundle classpath: " + clsName);
		}
	}

	private static IProject findProjectFor(Class<?> cls) {
		for (IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
			if (project.getFile(cls.getName()) != null) {
				return project;
			}
		}
		return null;
	}

	private static void openInEditor(IJavaProject project, String clazz) throws ClassNotFoundException {
		if (project == null) {
			return;
		}
		try {
			IType type = project.findType(clazz);
			JavaUI.openInEditor(type, false, true);
		} catch (Exception e) {
			throw new ClassNotFoundException(e.getMessage());
		}
	}
}
