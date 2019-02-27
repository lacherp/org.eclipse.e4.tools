/*******************************************************************************
 * Copyright (c) 2015 OPCoach.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Olivier Prouvost <olivier.prouvost@opcoach.com> - initial API and implementation (bug #451116)
 *******************************************************************************/
package org.eclipse.e4.tools.bundles.spy;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Activator of this bundle to be sure to have an bundle context (see
 * BundleSpyPart)
 */
public class BundleSpyActivator implements BundleActivator {

	private static BundleContext bContext;

	public static BundleContext getContext() {
		return bContext;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		bContext = context;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		bContext = null;

	}

}
