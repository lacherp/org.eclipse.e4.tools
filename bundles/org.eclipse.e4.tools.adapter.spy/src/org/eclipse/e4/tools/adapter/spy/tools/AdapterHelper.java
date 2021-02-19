package org.eclipse.e4.tools.adapter.spy.tools;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.internal.services.EclipseAdapter;
import org.eclipse.e4.core.services.adapter.Adapter;
import org.eclipse.e4.tools.adapter.spy.hook.EclipseAdapterHook;
import org.eclipse.e4.ui.internal.workbench.Activator;
import org.eclipse.e4.ui.internal.workbench.E4Workbench;

@SuppressWarnings("restriction")
public final class AdapterHelper {
	
	private static EclipseAdapter originalEclipseAdpater;
	
	private AdapterHelper() {
		// do nothing
	}
	
	public static final String EXT_POINT_ID ="org.eclipse.core.runtime.adapters";

	public static final String EXT_POINT_ATTR_ADAPTABLE_TYPE ="adaptableType";
	
	public static final String EXT_POINT_ATTR_CLASS ="class";
	
	public static final String EXT_POINT_ATTR_ADAPTER ="adapter";
	
	public static final String EXT_POINT_ATTR_TYPE ="type";
	
	public static void wrapperEclipseAdapter(){
		IEclipseContext serviceContext = E4Workbench.getServiceContext();
		if(serviceContext == null)
		{	System.err.println("service contextr is null, unable to wrap eclipse adapter");
			return ;
		}
		EclipseAdapter eclipseAdapter = (EclipseAdapter) serviceContext.get(Adapter.class);
		if ( originalEclipseAdpater == null) {
			originalEclipseAdpater = (EclipseAdapter) serviceContext.get(Adapter.class);
		}
		if( !(eclipseAdapter instanceof EclipseAdapterHook)) {
			serviceContext.set(Adapter.class, ContextInjectionFactory.make(EclipseAdapterHook.class, serviceContext));
		}
	}
	public static void restoreOriginalEclipseAdapter(){
		IEclipseContext serviceContext = E4Workbench.getServiceContext();
		if (serviceContext != null && originalEclipseAdpater != null) {
			serviceContext.set(Adapter.class,originalEclipseAdpater);
		}
	}
	
	
	public static IEclipseContext getServicesContext() {
		return EclipseContextFactory.getServiceContext(Activator.getDefault().getContext());
	}
	
	
	
}
