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

import org.eclipse.e4.tools.orion.editor.builder.AbstractHTMLBuilder;
import org.eclipse.e4.tools.orion.editor.builder.IHTMLBuilder;

/**
 * {@link IHTMLBuilder} to build the Orion HTML editor for HTML mode.
 * 
 */
public class HTMLBuilder extends AbstractHTMLBuilder {

	/**
	 * Constructor with {@link HTMLOptions}.
	 * 
	 * @param options
	 *            the HTML options.
	 */
	public HTMLBuilder(HTMLOptions options) {
		super(options);
	}

	/**
	 * Constructor with file base dir.
	 * 
	 * @param baseDir
	 *            base directory of the CSS and JS.
	 */
	public HTMLBuilder(File baseDir) {
		this(new HTMLOptions(baseDir));
	}

	/**
	 * Constructor to use only on OSGi context.
	 * 
	 * @throws IOException
	 * 
	 */
	public HTMLBuilder() throws IOException {
		this(new HTMLOptions());
	}
}
