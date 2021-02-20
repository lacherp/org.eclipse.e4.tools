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
package org.eclipse.e4.tools.adapter.spy.adapterfactory;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.tools.adapter.spy.model.AdapterData;
import org.eclipse.e4.tools.adapter.spy.model.AdapterRepository;
import org.eclipse.e4.tools.adapter.spy.tools.AdapterHelper;

/**
 * Adapter to transform IConfigurationElement from adapters extension
 * @author pascal
 *
 */
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
