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
package org.eclipse.e4.tools.preference.spy.model;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.dialogs.PatternFilter;

public class PreferenceEntryPatternFilter extends PatternFilter {

	public PreferenceEntryPatternFilter() {
		super();
	}

	@Override
	protected boolean isLeafMatch(Viewer viewer, Object element) {

		if (element instanceof PreferenceEntry) {
			PreferenceEntry preferenceEntry = (PreferenceEntry) element;
			if (wordMatches(preferenceEntry.getNodePath()) || wordMatches(preferenceEntry.getKey())
					|| wordMatches(preferenceEntry.getOldValue()) || wordMatches(preferenceEntry.getNewValue())) {
				return true;
			}
		}

		return super.isLeafMatch(viewer, element);
	}

}
