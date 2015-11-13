/*******************************************************************************
 * Copyright (c) 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.tools.event.spy.internal.ui;

import java.util.Collection;

import org.eclipse.e4.tools.event.spy.internal.model.CapturedEventFilter;

public class SpyPartMemento {
	private String baseTopic;

	private Collection<CapturedEventFilter> filters;

	public void setBaseTopic(String baseTopic) {
		this.baseTopic = baseTopic;
	}

	public String getBaseTopic() {
		return baseTopic;
	}

	public void setFilters(Collection<CapturedEventFilter> filters) {
		this.filters = filters;
	}

	public Collection<CapturedEventFilter> getFilters() {
		return filters;
	}
}
