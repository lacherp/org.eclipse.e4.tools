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

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.e4.tools.orion.editor.builder.css.CSSBuilder;
import org.eclipse.e4.tools.orion.editor.builder.html.HTMLBuilder;
import org.eclipse.e4.tools.orion.editor.builder.js.JSBuilder;
import org.eclipse.e4.tools.orion.text.editor.OrionEditor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.ErrorEditorPart;
import org.eclipse.ui.internal.part.NullEditorInput;
import org.eclipse.ui.tests.harness.util.ArrayUtil;
import org.eclipse.ui.tests.harness.util.FileUtil;
import org.eclipse.ui.tests.harness.util.UITestCase;

public class OrionEditorTest extends UITestCase {

	private static final String ORION_EDITOR_ID = "org.eclipse.e4.tools.orion.text.editor"; //$NON-NLS-1$

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

	private IFile createFileAndAssertDefaultEditor(String name)
			throws Throwable {
		IFile file = FileUtil.createFile(name, proj);
		// Check that the default editor for the file is the Orion Editor
		assertEquals(ORION_EDITOR_ID, fWorkbench.getEditorRegistry()
				.getDefaultEditor(file.getName()).getId());
		return file;
	}

	private IEditorPart openEditor(IFile file) throws Throwable {
		// Then check if the OrionEditor automatically opens for the file.
		IEditorPart editor = IDE.openEditor(fActivePage, file, true);
		assertTrue(ArrayUtil.contains(fActivePage.getEditors(), editor));
		assertEquals(fActivePage.getActiveEditor(), editor);
		assertEquals(editor.getSite().getId(), fWorkbench.getEditorRegistry()
				.getDefaultEditor(file.getName()).getId());
		assertEquals(editor.getTitle(), file.getName());
		return editor;
	}

	public void testOpenEditorForEmptyCssFile() throws Throwable {
		proj = FileUtil.createProject("testOpenEditor");

		IFile file = createFileAndAssertDefaultEditor("test.css");
		IEditorPart editor = openEditor(file);

		// Now make sure that the correct builder is used
		OrionEditor orionEditor = (OrionEditor) editor;
		assertTrue(orionEditor.getBuilder() instanceof CSSBuilder);
	}

	public void testOpenEditorForEmptyHtmlFile() throws Throwable {
		proj = FileUtil.createProject("testOpenEditor");

		IFile file = createFileAndAssertDefaultEditor("test.html");
		IEditorPart editor = openEditor(file);

		// Now make sure that the correct builder is used
		OrionEditor orionEditor = (OrionEditor) editor;
		assertTrue(orionEditor.getBuilder() instanceof HTMLBuilder);
	}

	public void testOpenEditorForEmptyJSFile() throws Throwable {
		proj = FileUtil.createProject("testOpenEditor");

		IFile file = createFileAndAssertDefaultEditor("test.js");
		IEditorPart editor = openEditor(file);

		// Now make sure that the correct builder is used
		OrionEditor orionEditor = (OrionEditor) editor;
		assertTrue(orionEditor.getBuilder() instanceof JSBuilder);
	}

	public void testOpenEditorForNonEmptyCssFile() throws Throwable {
		proj = FileUtil.createProject("testOpenEditor");
		String fileContents = ".someClass { background: #000000; }";

		// Insert text into the CSS file
		IFile file = FileUtil.createFile("test.css", proj);
		InputStream in = new ByteArrayInputStream(
				fileContents.getBytes("UTF-8"));
		file.setContents(in, IFile.NONE, null);

		IEditorPart editor = openEditor(file);

		// Check that the OrionEditorControl contains the text
		// that was in the CSS file.
		OrionEditor orionEditor = (OrionEditor) editor;
		assertEquals(fileContents, orionEditor.getContents());

		FileUtil.delete(file);
	}

	public void testOpenEditorForNonEmptyHtmlFile() throws Throwable {
		proj = FileUtil.createProject("testOpenEditor");
		String fileContents = "<!DOCTYPE html><html><body><h1>Some File</h1></body></html>";

		// Insert text into the CSS file
		IFile file = FileUtil.createFile("test.htm", proj);
		InputStream in = new ByteArrayInputStream(
				fileContents.getBytes("UTF-8"));
		file.setContents(in, IFile.NONE, null);

		IEditorPart editor = openEditor(file);

		// Check that the OrionEditorControl contains the text
		// that was in the CSS file.
		OrionEditor orionEditor = (OrionEditor) editor;
		assertEquals(fileContents, orionEditor.getContents());

		FileUtil.delete(file);
	}

	public void testOpenEditorForNonEmptyJSFile() throws Throwable {
		proj = FileUtil.createProject("testOpenEditor");
		String fileContents = "function doSomething() {var arr=[3,2,1];"
				+ "arr.sort(function(first,second){return first-second;});}";

		// Insert text into the CSS file
		IFile file = FileUtil.createFile("test.htm", proj);
		InputStream in = new ByteArrayInputStream(
				fileContents.getBytes("UTF-8"));
		file.setContents(in, IFile.NONE, null);

		IEditorPart editor = openEditor(file);

		// Check that the OrionEditorControl contains the text
		// that was in the CSS file.
		OrionEditor orionEditor = (OrionEditor) editor;
		assertEquals(fileContents, orionEditor.getContents());

		FileUtil.delete(file);
	}

	public void testSaveCssFile() throws Throwable {
		proj = FileUtil.createProject("testOpenEditor");
		String fileContents = ".someClass { background: #000000; }";

		IFile file = FileUtil.createFile("test.css", proj);
		IEditorPart editor = openEditor(file);

		OrionEditor orionEditor = (OrionEditor) editor;
		orionEditor.setContents(fileContents);
		assertTrue(orionEditor.isDirty());
		editor.doSave(null);
		assertEquals(fileContents,
				orionEditor.loadFile(file.getContents(), 1024));
	}

	public void testSaveHtmlFile() throws Throwable {
		proj = FileUtil.createProject("testOpenEditor");
		String fileContents = "<!DOCTYPE html><html><body><h1>Some File</h1></body></html>";

		IFile file = FileUtil.createFile("test.html", proj);
		IEditorPart editor = openEditor(file);

		OrionEditor orionEditor = (OrionEditor) editor;
		orionEditor.setContents(fileContents);
		assertTrue(orionEditor.isDirty());
		editor.doSave(null);
		assertEquals(fileContents,
				orionEditor.loadFile(file.getContents(), 1024));
	}

	public void testSaveJSFile() throws Throwable {
		proj = FileUtil.createProject("testOpenEditor");
		String fileContents = "function doSomething() {var arr=[3,2,1];"
				+ "arr.sort(function(first,second){return first-second;});}";

		IFile file = FileUtil.createFile("test.js", proj);
		IEditorPart editor = openEditor(file);

		OrionEditor orionEditor = (OrionEditor) editor;
		orionEditor.setContents(fileContents);
		assertTrue(orionEditor.isDirty());
		editor.doSave(null);
		assertEquals(fileContents,
				orionEditor.loadFile(file.getContents(), 1024));
	}

	public void testIsDirtyReturnsFalseWhenOrionEditorControlIsNull() {
		OrionEditor editor = new OrionEditor();
		assertFalse(editor.isDirty());
	}

	public void testIsDirtyReturnsFalseWhenOrionEditorControlIsDisposed()
			throws Throwable {
		proj = FileUtil.createProject("testOpenEditor");
		IFile file = FileUtil.createFile("test.css", proj);
		IEditorPart editor = openEditor(file);

		FileUtil.deleteProject(proj);
		proj = null;

		assertFalse(editor.isDirty());
	}

	@SuppressWarnings("restriction")
	public void testInitThrowsExceptionWithNonFileEditorInput() {
		try {
			IEditorPart editor = IDE.openEditor(fActivePage,
					new NullEditorInput(), ORION_EDITOR_ID);
			assertTrue(editor instanceof ErrorEditorPart);
		} catch (PartInitException e) {
			fail("The PartInitException should be caught internally.");
		}
	}
}
