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
package org.eclipse.e4.tools.orion.text.editor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.e4.tools.orion.editor.builder.IHTMLBuilder;
import org.eclipse.e4.tools.orion.editor.builder.css.CSSBuilder;
import org.eclipse.e4.tools.orion.editor.builder.html.HTMLBuilder;
import org.eclipse.e4.tools.orion.editor.builder.js.JSBuilder;
import org.eclipse.e4.tools.orion.editor.swt.IDirtyListener;
import org.eclipse.e4.tools.orion.editor.swt.OrionEditorControl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.ISaveablePart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;

public class OrionEditor extends EditorPart implements IDirtyListener {

	private static final String CSS_EXTENSION = "css";
	private static final String JS_EXTENSION = "js";

	private OrionEditorControl control;
	private IFile source;
	private IHTMLBuilder builder;

	@Override
	public void doSave(IProgressMonitor monitor) {
		if (isDirty()) {
			boolean success = true;
			try {
				InputStream contentStream = new ByteArrayInputStream(control
						.getText().getBytes("UTF-8"));
				source.setContents(contentStream, IFile.KEEP_HISTORY, monitor);
			} catch (Exception e) {
				success = false;
				Activator
						.getDefault()
						.getLog()
						.log(new Status(IStatus.ERROR, Activator.PLUGIN_ID,
								"Failed to save CSS", e));
			}

			if (success) {
				control.setDirty(false);
			} else {
				monitor.setCanceled(true);
			}
		} else {
			monitor.done();
		}
	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		if (!(input instanceof FileEditorInput)) {
			throw new PartInitException(new Status(IStatus.ERROR,
					Activator.PLUGIN_ID,
					"Expected editor input to be of type FileEditorInput"));
		}

		setSite(site);
		setInput(input);
		setPartName(input.getName());

		FileEditorInput fileInput = ((FileEditorInput) input);
		if (fileInput != null) {
			source = fileInput.getFile();
		}
	}

	@Override
	public boolean isDirty() {
		return (control == null || control.isDisposed()) ? false : control
				.isDirty();
	}

	@Override
	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
		try {
			String text = "";
			// Assume HTML context for files other than CSS and JS
			builder = new HTMLBuilder();

			if (source != null) {
				String extension = source.getFileExtension();

				if (extension.equals(CSS_EXTENSION)) {
					builder = new CSSBuilder("");
				} else if (extension.equals(JS_EXTENSION)) {
					builder = new JSBuilder();
				}

				text = loadFile(source.getContents(), 1024);
			}

			control = new OrionEditorControl(parent, SWT.NONE, builder);
			control.addDirtyListener(this);
			control.setText(text);
		} catch (Exception e) {
			Activator
					.getDefault()
					.getLog()
					.log(new Status(IStatus.ERROR, Activator.PLUGIN_ID,
							"Failed to load CSS", e));
		}
	}

	@Override
	public void setFocus() {
		if (control != null) {
			control.setFocus();
		}
	}

	public String getContents() {
		return control != null ? control.getText() : "";
	}

	public void setContents(String contents) {
		if (control != null && !control.getText().equals(contents)) {
			control.setText(contents);
			control.setDirty(true);
		}
	}

	public IHTMLBuilder getBuilder() {
		return builder;
	}

	/**
	 * Loads the content of the given InputStream into a String and returns it.
	 * 
	 * @param in
	 *            The InputStream of the file to read from.
	 * @param bufferSize
	 *            The size of the buffer used to transfer the contents of the
	 *            InputStream to the String.
	 * @return A String containing the contents of the file.
	 * @throws IOException
	 *             If there was an error reading from the file.
	 */
	public String loadFile(final InputStream in, final int bufferSize)
			throws IOException {
		final char[] buffer = new char[bufferSize];
		final StringBuilder out = new StringBuilder();
		final Reader reader = new InputStreamReader(in, "UTF-8");
		try {
			int size = reader.read(buffer, 0, buffer.length);
			while (size > 0) {
				out.append(buffer, 0, size);
				size = reader.read(buffer, 0, buffer.length);
			}
		} finally {
			reader.close();
		}
		return out.toString();
	}

	@Override
	public void dirtyChanged(boolean dirty) {
		firePropertyChange(ISaveablePart.PROP_DIRTY);
	}
}
