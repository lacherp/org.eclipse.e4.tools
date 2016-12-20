/*******************************************************************************
 * Copyright (c) 2011, 2016 Manumitting Technologies, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Brian de Alwis (MT) - initial API and implementation
 *     Lars Vogel <Lars.Vogel@vogella.com> - Bug 509506
 *******************************************************************************/
package org.eclipse.e4.tools.css.spy;

public class Util {

	public static void join(StringBuilder sb, String[] elements, String glue) {
		for (int i = 0; i < elements.length; i++) {
			sb.append(elements[i]);
			if (i < elements.length - 1) {
				sb.append(glue);
			}
		}
	}
}
