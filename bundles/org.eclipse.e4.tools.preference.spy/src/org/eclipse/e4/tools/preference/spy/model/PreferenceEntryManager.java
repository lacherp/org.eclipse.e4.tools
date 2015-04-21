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

import java.util.HashMap;
import java.util.Map;

public class PreferenceEntryManager extends PreferenceNodeEntry {

	private Map<String, PreferenceNodeEntry> recentPreferenceEntries = new HashMap<String, PreferenceNodeEntry>();

	public PreferenceEntryManager() {
	}

	public PreferenceNodeEntry getRecentPreferenceNodeEntry(String nodePath) {
		return recentPreferenceEntries.get(nodePath);
	}

	public PreferenceNodeEntry removeRecentPreferenceNodeEntry(String nodePath) {
		return recentPreferenceEntries.remove(nodePath);
	}

	public void clearRecentPreferenceNodeEntry() {
		recentPreferenceEntries.clear();
	}

	public void putRecentPreferenceEntry(String nodePath, PreferenceNodeEntry preferenceNodeEntry) {
		recentPreferenceEntries.put(nodePath, preferenceNodeEntry);
	}

}
