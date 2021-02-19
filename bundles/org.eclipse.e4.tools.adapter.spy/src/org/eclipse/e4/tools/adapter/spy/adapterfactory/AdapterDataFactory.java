package org.eclipse.e4.tools.adapter.spy.adapterfactory;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.tools.adapter.spy.model.AdapterData;
import org.eclipse.e4.tools.adapter.spy.model.AdapterRepository;
import org.eclipse.e4.tools.adapter.spy.tools.AdapterHelper;

public class AdapterDataFactory implements IAdapterFactory {

	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getAdapter(Object adaptableObject, Class<T> adapterType) {
		
		IEclipseContext context = AdapterHelper.getServicesContext();
		AdapterRepository adapterRepo = context.get(AdapterRepository.class);
		if (adaptableObject instanceof IConfigurationElement) {
			AdapterData data = adapterRepo.getSourceType((IConfigurationElement) adaptableObject);
			return (T) data;
		}
		return null;
	}

	@Override
	public Class<?>[] getAdapterList() {
		return new Class[] { AdapterData.class };
	}

}
