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
package org.eclipse.e4.tools.adapter.spy.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.tools.adapter.spy.tools.AdapterHelper;
/**
 * Repository adpater class is used to store AdapaterData model object
 * during transformation From IConfigurationElement and AdapterData 
 * @author pascal
 *
 */
@Creatable
@Singleton
public class AdapterRepository {

	@Inject
	IExtensionRegistry extensionRegistry;

	Map<String, AdapterData> sourceTypeToAdapterDataMap = new HashMap<>();

	Map<String, AdapterData> destinationTypeToAdapterDataMap = new HashMap<>();

	
	
	public IConfigurationElement[] getAdapters() {
		return extensionRegistry.getConfigurationElementsFor(AdapterHelper.EXT_POINT_ID);
	}

	public Optional<IConfigurationElement> findIfTargetIsSourceType(IConfigurationElement target) {
		final String targetType = target.getAttribute(AdapterHelper.EXT_POINT_ATTR_TYPE);
		List<IConfigurationElement> adaptersList = Arrays.asList(getAdapters());
		return adaptersList.stream()
				.filter(conf -> conf.getAttribute(AdapterHelper.EXT_POINT_ATTR_ADAPTABLE_TYPE).equals(targetType))
				.findAny();
	}

	public AdapterData getSourceType(IConfigurationElement element) {
		String sourceType = element.getAttribute(AdapterHelper.EXT_POINT_ATTR_ADAPTABLE_TYPE);
		
		AdapterData adapterData = new AdapterData(element, AdapterElementType.SOURCE_TYPE);
		sourceTypeToAdapterDataMap.put(sourceType, adapterData);
		buildDestinationType(adapterData);
		return adapterData;
	}

	public void buildDestinationType(AdapterData source) {
		IConfigurationElement[] destinationTypes = source.getConfigurationElement()
				.getChildren(AdapterHelper.EXT_POINT_ATTR_ADAPTER);
		// check if target element is build
		if(source.getChildrenList().isEmpty())
		{
			for (IConfigurationElement target : destinationTypes) {
				AdapterData adapterData = null;
				adapterData = new AdapterData(target, AdapterElementType.DESTINATION_TYPE);
				adapterData.setParent(source);
				destinationTypeToAdapterDataMap.put(adapterData.destinationType(), adapterData);
				checkTargetIsSource(adapterData);
				source.getChildrenList().add(adapterData);
			}	
		}
	}

	public void checkTargetIsSource(AdapterData destinationAdapterData) {
		Optional<IConfigurationElement> config = findIfTargetIsSourceType(destinationAdapterData.getConfigurationElement());
		if( config.isPresent())
		{
			AdapterData source = getSourceType(config.get());
			destinationAdapterData.setHasSourceType(source);
			if(source.getChildrenList().isEmpty())
			{
				buildDestinationType(source);
			}
			source.getChildrenList().forEach( ad ->{
				if (!destinationAdapterData.getChildrenList().contains(ad)){
					destinationAdapterData.getChildrenList().add(ad);
				}
			});
		}
		
	}
	
	
	public List<AdapterData> revertSourceToType(){
		return sourceTypeToAdapterDataMap.values().stream().flatMap(AdapterData::sourceToType)
			.collect(Collectors.toList());
	}
	
	
	
	public void clear() {
		sourceTypeToAdapterDataMap.clear();
		destinationTypeToAdapterDataMap.clear();
	}
}
