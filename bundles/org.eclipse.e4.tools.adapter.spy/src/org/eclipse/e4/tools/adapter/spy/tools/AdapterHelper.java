/*******************************************************************************
 * Copyright (c)  Lacherp.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Lacherp - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.tools.adapter.spy.tools;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.internal.services.EclipseAdapter;
import org.eclipse.e4.core.services.adapter.Adapter;
import org.eclipse.e4.tools.adapter.spy.hook.EclipseAdapterHook;
import org.eclipse.e4.ui.internal.workbench.E4Workbench;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
/**
 * Helper class
 * @author pascal
 *
 */
@SuppressWarnings("restriction")
public final class AdapterHelper {

	// Image keys constants
	public static final String BUNDLE_IMG_KEY = "icons/osgi.png";
	public static final String SOURCE_TYPE_IMG_KEY = "icons/from_type.png";
	public static final String DESTINATION_TYPE_IMG_KEY = "icons/to_type.png";
	
	// extension constant string 
	public static final String EXT_POINT_ID = "org.eclipse.core.runtime.adapters";
	public static final String EXT_POINT_ATTR_ADAPTABLE_TYPE = "adaptableType";
	public static final String EXT_POINT_ATTR_CLASS = "class";
	public static final String EXT_POINT_ATTR_ADAPTER = "adapter";
	public static final String EXT_POINT_ATTR_TYPE = "type";
	
	private static EclipseAdapter originalEclipseAdpater;

	private static BundleContext bcontext;
	
	private AdapterHelper() {
		// do nothing
	}

	

	public static void wrapperEclipseAdapter() {
		IEclipseContext serviceContext = E4Workbench.getServiceContext();
		if (serviceContext == null) {
			System.err.println("service contextr is null, unable to wrap eclipse adapter");
			return;
		}
		EclipseAdapter eclipseAdapter = (EclipseAdapter) serviceContext.get(Adapter.class);
		if (originalEclipseAdpater == null) {
			originalEclipseAdpater = (EclipseAdapter) serviceContext.get(Adapter.class);
		}
		if (!(eclipseAdapter instanceof EclipseAdapterHook)) {
			serviceContext.set(Adapter.class, ContextInjectionFactory.make(EclipseAdapterHook.class, serviceContext));
		}
	}

	public static void restoreOriginalEclipseAdapter() {
		IEclipseContext serviceContext = E4Workbench.getServiceContext();
		if (serviceContext != null && originalEclipseAdpater != null) {
			serviceContext.set(Adapter.class, originalEclipseAdpater);
		}
	}

	public static IEclipseContext getServicesContext() {
		return EclipseContextFactory.getServiceContext(bcontext);
	}

	public static ImageRegistry getImageRegistry(Object instance) {
		Bundle b = FrameworkUtil.getBundle(instance.getClass());
		bcontext = b.getBundleContext();
		ImageRegistry imgReg = new ImageRegistry();
		imgReg.put(BUNDLE_IMG_KEY, ImageDescriptor.createFromURL(b.getEntry(BUNDLE_IMG_KEY)));
		imgReg.put(SOURCE_TYPE_IMG_KEY, ImageDescriptor.createFromURL(b.getEntry(SOURCE_TYPE_IMG_KEY)));
		imgReg.put(DESTINATION_TYPE_IMG_KEY, ImageDescriptor.createFromURL(b.getEntry(DESTINATION_TYPE_IMG_KEY)));
		return imgReg;
	}

}
