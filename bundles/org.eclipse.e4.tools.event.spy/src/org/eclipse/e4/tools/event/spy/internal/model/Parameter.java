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
package org.eclipse.e4.tools.event.spy.internal.model;

import org.eclipse.e4.tools.event.spy.internal.util.MultilineFormatter;

public class Parameter implements IEventItem {
	private static final String EMPTY_VALUE = "";

	private final String name;
	private final Object value;

	private String formattedValue;

	public Parameter(String name, Object value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public Object getValue() {
		return value;
	}

	public String getParam1() {
		if (value == null) {
			return SpecialValue.Null.toString();
		}
		if (formattedValue == null) {
			formattedValue = MultilineFormatter.format(value.toString(), 70);
		}
		return formattedValue;
	}

	public String getParam2() {
		return EMPTY_VALUE;
	}
}
