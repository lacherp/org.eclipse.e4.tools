/*******************************************************************************
 * Copyright (c) 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Leo Denault <ldena023@uottawa.ca> - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.tools.orion.text.editor.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class OrionEditorTestSuite extends TestCase {

	public static Test suite() {
		TestSuite suite = new TestSuite(OrionEditorTestSuite.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(OrionEditorTest.class);
		//$JUnit-END$
		return suite;
	}
}
