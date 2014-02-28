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
package org.eclipse.e4.tools.orion.editor.builder.html;

import java.io.File;
import java.io.IOException;

import org.eclipse.e4.tools.orion.editor.builder.EditorOptions;

/**
 * HTML Editor options.
 * 
 */
public class HTMLOptions extends EditorOptions {

	private static final String HTML_LANG = "html";

	/**
	 * Constructor of the editor options with URL of CSS and JS.
	 * 
	 * @param editorJsUrl
	 *            the full URL of "built-editor.js".
	 * @param editorCssUrl
	 *            the full URL of "built-editor.css".
	 */
	public HTMLOptions(String editorJsUrl, String editorCssUrl) {
		super(editorJsUrl, editorCssUrl, HTML_LANG);
		createEditor();
	}

	/**
	 * Constructor of the editor options with the base URL of CSS and JS.
	 * 
	 * @param baseURL
	 *            base URL of the CSS and JS.
	 */
	public HTMLOptions(String baseURL) {
		super(baseURL, HTML_LANG);
		createEditor();
	}

	/**
	 * Constructor of the editor options with the file base dir of CSS and JS.
	 * 
	 * @param baseDir
	 *            file base directory of the CSS and JS.
	 */
	public HTMLOptions(File baseDir) {
		super(baseDir, HTML_LANG);
		createEditor();
	}

	/**
	 * Constructor of the editor options to use only on OSGi context.
	 * 
	 * @throws IOException
	 */
	public HTMLOptions() throws IOException {
		super(HTML_LANG);
		createEditor();
	}

	/**
	 * Create the Orion editor with edit function.
	 * 
	 */
	private void createEditor() {
		String htmlEdit = new HTMLEdit().generate(null);
		super.addScript(htmlEdit);
	}

}
