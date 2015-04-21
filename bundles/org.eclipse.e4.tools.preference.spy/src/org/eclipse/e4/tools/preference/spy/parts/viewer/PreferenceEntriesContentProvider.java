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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.observable.masterdetail.IObservableFactory;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.e4.tools.preference.spy.model.PreferenceEntry;
import org.eclipse.e4.tools.preference.spy.model.PreferenceNodeEntry;
import org.eclipse.jface.databinding.viewers.ObservableSetTreeContentProvider;
import org.eclipse.jface.databinding.viewers.TreeStructureAdvisor;

public class PreferenceEntriesContentProvider extends ObservableSetTreeContentProvider {

	private boolean hierarchicalLayout;

	public PreferenceEntriesContentProvider(IObservableFactory setFactory, TreeStructureAdvisor structureAdvisor) {
		super(setFactory, structureAdvisor);
	}

	@Override
	public Object[] getElements(Object inputElement) {
		Object[] children = super.getElements(inputElement);
		if (isHierarchicalLayout()) {
			return children;
		}

		List<PreferenceEntry> childList = new ArrayList<PreferenceEntry>();

		for (Object object : children) {
			getChildren(object, childList);
		}

		return childList.toArray();
	}


	private void getChildren(Object element, List<PreferenceEntry> childList) {
		if (element instanceof PreferenceNodeEntry) {
			IObservableSet preferenceEntries = ((PreferenceNodeEntry) element).getPreferenceEntries();
			for (Object object : preferenceEntries) {
				getChildren(object, childList);
			}
		} else if (element instanceof PreferenceEntry) {
			childList.add((PreferenceEntry) element);
		}
	}

	public boolean isHierarchicalLayout() {
		return hierarchicalLayout;
	}

	public void setHierarchicalLayout(boolean hierarchicalLayout) {
		this.hierarchicalLayout = hierarchicalLayout;
	}
}
