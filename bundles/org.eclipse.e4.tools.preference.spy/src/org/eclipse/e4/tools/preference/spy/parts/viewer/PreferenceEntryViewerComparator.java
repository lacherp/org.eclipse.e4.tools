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

import java.util.Comparator;

import org.eclipse.e4.tools.preference.spy.model.PreferenceEntry;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

public class PreferenceEntryViewerComparator extends ViewerComparator {

	public PreferenceEntryViewerComparator() {
	}

	public PreferenceEntryViewerComparator(Comparator<?> comparator) {
		super(comparator);
	}

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		if (e1 instanceof PreferenceEntry && e2 instanceof PreferenceEntry) {
			PreferenceEntry entry1 = (PreferenceEntry) e1;
			PreferenceEntry entry2 = (PreferenceEntry) e2;

			long time = entry1.getTime();
			long time2 = entry2.getTime();

			if (time != 0 && time2 != 0) {
				return (int) (time2 - time);
			}
		}

		return super.compare(viewer, e1, e2);
	}

	@Override
	public int category(Object element) {
		if (element instanceof PreferenceEntry) {
			return ((PreferenceEntry) element).isRecentlyChanged() ? 0 : 1;
		}
		return 2;
	}

}
