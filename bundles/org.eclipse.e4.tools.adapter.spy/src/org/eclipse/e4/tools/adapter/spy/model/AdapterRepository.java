package org.eclipse.e4.tools.adapter.spy.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.tools.adapter.spy.tools.AdapterHelper;

@Creatable
@Singleton
public class AdapterRepository {

	@Inject
	IExtensionRegistry extensionRegistry;

	Map<String, AdapterData> sourceTypeToAdapterDataMap = new HashMap<>();

	Map<String, AdapterData> destinationTypeToAdapterDataMap = new HashMap<>();

	public AdapterRepository() {
	}

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
				String destType = target.getAttribute(AdapterHelper.EXT_POINT_ATTR_TYPE);
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
	
	
	
	
	public void clear() {
		sourceTypeToAdapterDataMap.clear();
		destinationTypeToAdapterDataMap.clear();
	}
}
