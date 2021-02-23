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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.e4.tools.adapter.spy.tools.AdapterHelper;


/**
 * Adapter data model Object This class is used to store ConfigarationElement
 * elements which use an adapter
 * 
 * @author pascal
 *
 */
public class AdapterData implements Comparable<AdapterData> {

	IConfigurationElement configElem;

	AdapterData parent ;
	List<AdapterData> children = new ArrayList<>();
	AdapterElementType elemType;
	boolean visibilityFilter = true;
	Boolean showPackage ;
	AdapterData hasSourceType;

	/**
	 * Ctor
	 * @param elem
	 * 	configuration element from extension
	 * @param elemType
	 * 	type of the AdapterData
	 */
	public AdapterData(IConfigurationElement elem, AdapterElementType elemType) {
		this.elemType = elemType;
		configElem = elem;
		showPackage = Boolean.TRUE;
	}
	
	/**
	 * Copy constructor
	 * @param adapterData
	 */
	public AdapterData(AdapterData adapterData) {
		this.elemType = adapterData.getAdapterElementType();
		configElem = adapterData.getConfigurationElement();
		showPackage = adapterData.showPackage;
		hasSourceType = adapterData.isHasSourceType();
		this.visibilityFilter = adapterData.isVisibilityFilter();
	}
	
	/**
	 * propagate visibility to children
	 */
	public void propagateVisibility() {
		children.forEach(d -> {
			d.setVisibilityFilter(isVisibilityFilter());
			d.propagateVisibility();
		});
	}

	public void textSearch(String txtSearch, AtomicBoolean bfound) {
		
		if (bfound.get()) {
			return;
		}
		String txt = this.toString();
		bfound.set(txt.contains(txtSearch));
		// check in adapter class
		children.forEach(d -> {
			d.textSearch(txtSearch, bfound);
		});

	}

	public String getPluginName() {
		return checkNull(configElem.getContributor().getName());
	}

	public String sourceType() {
		return checkNull(configElem.getAttribute(AdapterHelper.EXT_POINT_ATTR_ADAPTABLE_TYPE));
	}

	public String getAdaterClass() {

		return checkNull(configElem.getAttribute(AdapterHelper.EXT_POINT_ATTR_CLASS));
	}

	public String destinationType() {
		return checkNull(configElem.getAttribute(AdapterHelper.EXT_POINT_ATTR_TYPE));
	}

	public boolean hasChildren() {
		return !children.isEmpty();
	}

	public List<AdapterData> getChildrenList() {
		return this.children;
	}

	public AdapterData[] getChildren() {
		if (!children.isEmpty()) {
			Collections.sort(children);
			return children.toArray(new AdapterData[0]);
		}
		return new AdapterData[0];
	}

	public Object getParent() {
		return this.parent;
	}

	public void setParent(AdapterData parent) {
		this.parent = parent;
	}

	public AdapterElementType getAdapterElementType() {
		return this.elemType;
	}

	public IConfigurationElement getConfigurationElement() {
		return this.configElem;
	}

	/**
	 * @return the hasSourceType
	 */
	public AdapterData isHasSourceType() {
		return hasSourceType;
	}

	/**
	 * @param hasSourceType the hasSourceType to set
	 */
	public void setHasSourceType(AdapterData hasSourceType) {
		this.hasSourceType = hasSourceType;
	}

	
	/**
	 * @return the showPackage
	 */
	public boolean isShowPackage() {
		return showPackage;
	}

	/**
	 * @param showPackage the showPackage to set
	 */
	public void setShowPackage(boolean showPackage) {
		this.showPackage = showPackage;
	}

	public String getText(int colIndex) {
		if (colIndex == 0) {
			String result = "";
			switch (elemType) {
			case SOURCE_TYPE:
				result = displayPackage(sourceType());
				break;
			case DESTINATION_TYPE:
				result = displayPackage(destinationType());
				break;
			default:
				result = "";
			}
			return result;
		}
		
		if(colIndex == 1) {
			if( elemType.equals(AdapterElementType.SOURCE_TYPE )&& !getChildrenList().isEmpty()) {
				return "";
			}else if(elemType.equals(AdapterElementType.SOURCE_TYPE )&& getChildrenList().isEmpty()) {
				return getAdaterClass();
			}
			
			if( parent == null && elemType.equals(AdapterElementType.DESTINATION_TYPE)) {
			 return	"";
			}
			if(parent == null) {
				return "";
			}
			return parent.getAdaterClass();
		}
		return "";
	}

	/**
	 * @return the visibilityFilter
	 */
	public boolean isVisibilityFilter() {
		return visibilityFilter;
	}

	/**
	 * @param visibilityFilter the visibilityFilter to set
	 */
	public void setVisibilityFilter(boolean visibilityFilter) {
		this.visibilityFilter = visibilityFilter;
	}

	public Stream<AdapterData> sourceToType() {
		final ArrayList<AdapterData> result=new ArrayList<>();
		this.getChildrenList().forEach( child -> {
			AdapterData newAdapterData = new AdapterData(child);
			newAdapterData.getChildrenList().add(new AdapterData(this));
			result.add(newAdapterData);
		});
		return result.stream();
	}

	
	
	@Override
	public String toString() {
		return getPluginName() + "@" + sourceType() + "@" + destinationType() + getAdaterClass();
	}

	private String checkNull(String val) {
		return (val == null) ? "" : val;
	}
	
	private String displayPackage(String value) {
		if(Boolean.TRUE.equals(showPackage)) {
			return value;
		}
		return value.substring(value.lastIndexOf(".")+1, value.length());
	}

	@Override
	public int compareTo(AdapterData o) {
		return this.getText(0).compareTo(o.getText(0));
	}

	
}
