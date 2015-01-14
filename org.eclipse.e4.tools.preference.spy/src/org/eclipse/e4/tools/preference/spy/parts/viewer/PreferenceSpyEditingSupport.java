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
package org.eclipse.e4.tools.preference.spy.parts.viewer;

import org.eclipse.e4.tools.preference.spy.model.PreferenceEntry;
import org.eclipse.e4.tools.preference.spy.model.PreferenceEntry.Fields;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class PreferenceSpyEditingSupport extends EditingSupport {

	private Fields field;

	public PreferenceSpyEditingSupport(ColumnViewer viewer, Fields field) {
		super(viewer);
		this.field = field;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		return new TextCellEditor((Composite) getViewer().getControl(),
				SWT.READ_ONLY);
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	@Override
	protected Object getValue(Object element) {
		String value = null;
		if (element instanceof PreferenceEntry) {
			PreferenceEntry preferenceEntry = (PreferenceEntry) element;
			switch (field) {
			case nodePath:
				value = preferenceEntry.getNodePath();
				break;
			case key:
				value = preferenceEntry.getKey();
				break;
			case oldValue:
				value = preferenceEntry.getOldValue();
				break;
			case newValue:
				value = preferenceEntry.getNewValue();
				break;
			default:
				break;
			}
		}

		return value;
	}

	@Override
	protected void setValue(Object element, Object value) {
	}

}
