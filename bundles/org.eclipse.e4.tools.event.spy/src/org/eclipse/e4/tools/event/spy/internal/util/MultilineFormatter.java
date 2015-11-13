/*******************************************************************************
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
package org.eclipse.e4.tools.event.spy.internal.util;

import java.util.HashSet;
import java.util.Set;

public class MultilineFormatter {
	@SuppressWarnings("serial")
	private static Set<Character> LINE_DELIMITERS = new HashSet<Character>() {
		{
			add(',');
			add('(');
			add(')');
			add(';');
			add('-');
			add('=');
		}
	};

	public static String format(String value, int lineLength) {
		StringBuilder result = new StringBuilder();
		int counter = 0;

		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
			result.append(c);

			if (++counter >= lineLength && LINE_DELIMITERS.contains(c)) {
				result.append('\n');
				counter = 0;
			}
		}
		return result.toString();
	}
}
