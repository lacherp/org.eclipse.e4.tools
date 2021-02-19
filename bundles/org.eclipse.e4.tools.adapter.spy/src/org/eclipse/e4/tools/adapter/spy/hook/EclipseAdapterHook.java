package org.eclipse.e4.tools.adapter.spy.hook;

import org.eclipse.e4.core.internal.services.EclipseAdapter;

@SuppressWarnings("restriction")
public class EclipseAdapterHook extends EclipseAdapter {

	
	
	
	
	@Override
	public <T> T adapt(Object element, Class<T> adapterType) {
		System.out.println(element)
		;
		System.out.println(adapterType);
		return super.adapt(element, adapterType);
	}
	
	
}
