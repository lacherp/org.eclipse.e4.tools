/*******************************************************************************
 * Copyright (c) 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Leo Denault <ldena023@uottawa.ca> - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.tools.orion.text.editor.test;

import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.tests.harness.util.FileUtil;
import org.eclipse.ui.tests.harness.util.UITestCase;

public class OrionEditorTest extends UITestCase {

	private IWorkbenchPage fActivePage;

	private IWorkbenchWindow fWin;

	private IProject proj;

	public OrionEditorTest(String testName) {
		super(testName);
	}


	@Override
	protected void doSetUp() throws Exception {
		super.doSetUp();
		fWin = openTestWindow();
		fActivePage = fWin.getActivePage();
	}

	@Override
	protected void doTearDown() throws Exception {
		super.doTearDown();
		if (proj != null) {
			FileUtil.deleteProject(proj);
			proj = null;
		}
	}

	public void testOpenEditor() {

	}
}
