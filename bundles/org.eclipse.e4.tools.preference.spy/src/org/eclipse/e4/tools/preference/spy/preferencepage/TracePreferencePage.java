/*******************************************************************************
 * Copyright (c) 2015 vogella GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Simon Scholz <simon.scholz@vogella.com> - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.tools.preference.spy.preferencepage;

import org.eclipse.e4.tools.preference.spy.Activator;
import org.eclipse.e4.tools.preference.spy.constants.PreferenceConstants;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class TracePreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public TracePreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Settings for the preference spy");
	}

	/**
	 * Creates the field editors
	 */
	@Override
	public void createFieldEditors() {
		addField(new BooleanFieldEditor(PreferenceConstants.TRACE_PREFERENCES, "&Trace preference values ",
				getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.HIERARCHICAL_LAYOUT, "&Use hierarchical layout in the tree",
				getFieldEditorParent()));
	}

	@Override
	public void init(IWorkbench workbench) {
	}
}
