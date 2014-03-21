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
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.e4.tools.orion.editor.builder.IHTMLBuilder;
import org.eclipse.e4.tools.orion.editor.builder.css.CSSBuilder;
import org.eclipse.e4.tools.orion.editor.builder.html.HTMLBuilder;
import org.eclipse.e4.tools.orion.editor.builder.js.JSBuilder;
import org.eclipse.e4.tools.orion.editor.swt.IDirtyListener;
import org.eclipse.e4.tools.orion.editor.swt.OrionEditorControl;
import org.eclipse.e4.tools.orion.text.editor.handlers.RevertOrionEditorHandler;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.ISaveablePart;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.handlers.IHandlerService;
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
			performSave(source, monitor);
		} else {
			monitor.done();
		}
	}

	@Override
	public void doSaveAs() {
		if (isSaveAsAllowed()) {
			SaveAsDialog dialog = new SaveAsDialog(getSite().getShell());

			if (source != null) {
				dialog.setOriginalFile(source);
			}

			IProgressMonitor monitor = getStatusLineManager()
					.getProgressMonitor();
			if (dialog.open() == SaveAsDialog.CANCEL) {
				monitor.setCanceled(true);
			} else {
				IWorkspace workspace = ResourcesPlugin.getWorkspace();
				IFile file = workspace.getRoot().getFile(dialog.getResult());

				if (performSave(file, monitor)) {
					source = file;
					setPartName(file.getName());
					setInput(new FileEditorInput(file));
				}
			}
		}
	}

	/**
	 * Attempts to save the contents of the editor to the specified
	 * {@link IFile} using the given {@link IProgressMonitor}.
	 * 
	 * @param file
	 *            The file to which the contents of the editor should be saved.
	 * @param monitor
	 *            The progress monitor to use.
	 * @return True if the save operation succeeds, false otherwise.
	 */
	private boolean performSave(IFile file, IProgressMonitor monitor) {
		boolean success = true;
		try {
			InputStream contentStream = new ByteArrayInputStream(control
					.getText().getBytes("UTF-8"));
			if (file.exists()) {
				file.setContents(contentStream, IFile.KEEP_HISTORY, monitor);
			} else {
				file.create(contentStream, IFile.KEEP_HISTORY, monitor);
			}
		} catch (Exception e) {
			success = false;
			logError("Failed to save file", e);
		}

		if (success) {
			control.setDirty(false);
		} else {
			monitor.setCanceled(true);
		}
		return success;
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

		// Set up the revert file handler
		IHandlerService handlerService = (IHandlerService) site
				.getService(IHandlerService.class);
		handlerService.activateHandler(IWorkbenchCommandConstants.FILE_REVERT,
				new RevertOrionEditorHandler(this));

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
		return !(control == null || control.isDisposed());
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
			logError("Failed to load file", e);
		}
	}

	@Override
	public void setFocus() {
		if (control != null) {
			control.setFocus();
		}
	}

	@Override
	public void dispose() {
		if (control != null) {
			control.dispose();
		}
		super.dispose();
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

	public void revert() {
		if (control != null) {
			if (source == null) {
				control.setText("");
			} else {
				try {
					control.setText(loadFile(source.getContents(), 1024));
					control.setDirty(false);
				} catch (Exception e) {
					logError("Failed to revert file", e);
				}
			}
		}
	}

	private void logError(String message, Exception e) {
		Activator
				.getDefault()
				.getLog()
				.log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, message, e));
	}

	private IStatusLineManager getStatusLineManager() {
		return getEditorSite().getActionBars().getStatusLineManager();
	}
}
