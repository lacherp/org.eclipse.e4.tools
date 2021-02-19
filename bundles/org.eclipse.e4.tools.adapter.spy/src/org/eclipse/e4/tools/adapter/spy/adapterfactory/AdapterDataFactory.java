package org.eclipse.e4.tools.adapter.spy.adapterfactory;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.e4.tools.adapter.spy.model.AdapterData;
import org.eclipse.e4.tools.adapter.spy.model.AdapterElementType;

public class AdapterDataFactory implements IAdapterFactory {

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getAdapter(Object adaptableObject, Class<T> adapterType) {
		if ( adaptableObject instanceof IConfigurationElement)
		{	AdapterData data = new AdapterData((IConfigurationElement)adaptableObject,AdapterElementType.PLUGIN);
			data.buildchilds();
			return (T) data;
		}
		return null;
	}

	@Override
	public Class<?>[] getAdapterList() {
		return new Class[]{AdapterData.class };
	}

}
