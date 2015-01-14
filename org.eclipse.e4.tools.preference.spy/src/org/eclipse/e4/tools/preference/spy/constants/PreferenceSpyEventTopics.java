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
package org.eclipse.e4.tools.preference.spy.constants;

public interface PreferenceSpyEventTopics {

	public static final String PREFERENCESPY_PREFERENCE_ENTRIES_DELETE_ALL = "TOPIC_PREFERENCESPY/PREFERENCE_ENTRIES/DELETE_ALL";
	public static final String PREFERENCESPY_PREFERENCE_ENTRIES_DELETE = "TOPIC_PREFERENCESPY/PREFERENCE_ENTRIES/DELETE";

	public static final String PREFERENCESPY_PREFERENCE_CHANGED = "TOPIC_PREFERENCESPY/PREFERENCE/CHANGED";
	public static final String PREFERENCESPY_PREFERENCE_SHOW = "TOPIC_PREFERENCESPY/PREFERENCE/SHOW";
}
