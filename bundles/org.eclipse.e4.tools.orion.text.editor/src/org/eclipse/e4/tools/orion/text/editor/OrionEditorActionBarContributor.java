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

import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorActionBarContributor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;

public class OrionEditorActionBarContributor implements
		IEditorActionBarContributor {

	@Override
	public void init(IActionBars bars, IWorkbenchPage page) {
		// Intentionally empty.
	}

	@Override
	public void setActiveEditor(IEditorPart targetEditor) {
		// Intentionally empty.
	}

	@Override
	public void dispose() {
		// Intentionally empty.
	}

}
