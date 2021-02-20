package org.eclipse.e4.tools.adapter.spy.tools;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.internal.services.EclipseAdapter;
import org.eclipse.e4.core.services.adapter.Adapter;
import org.eclipse.e4.tools.adapter.spy.hook.EclipseAdapterHook;
import org.eclipse.e4.ui.internal.workbench.Activator;
import org.eclipse.e4.ui.internal.workbench.E4Workbench;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

@SuppressWarnings("restriction")
public final class AdapterHelper {

	// Image keys constants
	public static final String BUNDLE_IMG_KEY = "icons/osgi.png";
	public static final String FROM_TYPE_IMG_KEY = "icons/from_type.png";
	public static final String TO_TYPE_IMG_KEY = "icons/to_type.png";
	
	// extension constant string 
	public static final String EXT_POINT_ID = "org.eclipse.core.runtime.adapters";
	public static final String EXT_POINT_ATTR_ADAPTABLE_TYPE = "adaptableType";
	public static final String EXT_POINT_ATTR_CLASS = "class";
	public static final String EXT_POINT_ATTR_ADAPTER = "adapter";
	public static final String EXT_POINT_ATTR_TYPE = "type";
	
	private static EclipseAdapter originalEclipseAdpater;

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
		return EclipseContextFactory.getServiceContext(Activator.getDefault().getContext());
	}

	public static ImageRegistry getImageRegistry(Object instance) {
		Bundle b = FrameworkUtil.getBundle(instance.getClass());
		ImageRegistry imgReg = new ImageRegistry();
		imgReg.put(BUNDLE_IMG_KEY, ImageDescriptor.createFromURL(b.getEntry(BUNDLE_IMG_KEY)));
		imgReg.put(FROM_TYPE_IMG_KEY, ImageDescriptor.createFromURL(b.getEntry(FROM_TYPE_IMG_KEY)));
		imgReg.put(TO_TYPE_IMG_KEY, ImageDescriptor.createFromURL(b.getEntry(TO_TYPE_IMG_KEY)));
		return imgReg;
	}

}
