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

/**
 * Adapter data model Object This class is used to store ConfigarationElement
 * elements which use an adapter
 * 
 * @author pascal
 *
 */
public class AdapterData implements Comparable<AdapterData> {

	String sourceType;
	String destinationType;
	String adapterClassName;
	boolean isInterface = false;

	AdapterData parent;
	List<AdapterData> children = new ArrayList<>();
	AdapterElementType elemType;
	boolean visibilityFilter = true;
	Boolean showPackage;
	AdapterData hasSourceType;

	/**
	 * Ctor
	 * @param elemType
	 */
	public AdapterData(AdapterElementType elemType) {
		this.elemType = elemType;
		showPackage = Boolean.TRUE;
	}
	
	public AdapterData(AdapterData adapterData) {
		this.elemType = adapterData.getAdapterElementType();
		this.showPackage = Boolean.TRUE;
		this.destinationType = adapterData.getDestinationType();
		this.adapterClassName = adapterData.getAdapterClassName();
		this.sourceType = adapterData.getSourceType();
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

	/**
	 * @return the sourceType
	 */
	public String getSourceType() {
		return sourceType;
	}

	/**
	 * @param sourceType the sourceType to set
	 */
	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	/**
	 * @return the destinationType
	 */
	public String getDestinationType() {
		return destinationType;
	}

	/**
	 * @param destinationType the destinationType to set
	 */
	public void setDestinationType(String destinationType) {
		this.destinationType = destinationType;
	}

	/**
	 * @return the adapterClassName
	 */
	public String getAdapterClassName() {
		return adapterClassName;
	}

	/**
	 * @param adapterClassName the adapterClassName to set
	 */
	public void setAdapterClassName(String adapterClassName) {
		this.adapterClassName = adapterClassName;
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
			List<AdapterData> reduceresult = children;
			return reduceresult.toArray(new AdapterData[0]);
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
	 * @return the isInterface
	 */
	public boolean isInterface() {
		return isInterface;
	}

	/**
	 * @param isInterface the isInterface to set
	 */
	public void setInterface(boolean isInterface) {
		this.isInterface = isInterface;
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
				result = displayPackage(getSourceType());
				break;
			case DESTINATION_TYPE:
				result = displayPackage(getDestinationType());
				break;
			default:
				result = "";
			}
			return result;
		}

		if (colIndex == 1) {

			if (elemType.equals(AdapterElementType.DESTINATION_TYPE)) {
				return getAdapterClassName();
			}
			return "";
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

	public Stream<AdapterData> convertSourceToType() {
		final ArrayList<AdapterData> result=new ArrayList<>();
		this.getChildrenList().forEach( child -> {
			AdapterData newAdapterData = new AdapterData(child);
			AdapterData soon = new AdapterData(this);
			soon.setParent(child);
			newAdapterData.getChildrenList().add(soon);
		
			result.add(newAdapterData);
		});
		return result.stream();
	}
	
	@Override
	public String toString() {
		return getSourceType() + "@" + getDestinationType() + getAdapterClassName();
	}

	private String checkNull(String val) {
		return (val == null) ? "" : val;
	}

	private String displayPackage(String value) {
		if (Boolean.TRUE.equals(showPackage)) {
			return value;
		}
		return value.substring(value.lastIndexOf(".") + 1, value.length());
	}

	@Override
	public int compareTo(AdapterData o) {
		return this.getText(0).compareTo(o.getText(0));
	}

//		try {
//		Bundle bundle = OSGIUtils.getDefault().getBundle(getPluginName());
//		Class<?> clsss = bundle.loadClass(configElem.getAttribute(AdapterHelper.EXT_POINT_ATTR_ADAPTABLE_TYPE));
//		Class<?> clazz = Class.forName(configElem.getAttribute(AdapterHelper.EXT_POINT_ATTR_ADAPTABLE_TYPE)
////				,false,bundle.loadClass(name)getBundleContext().getClass().getClassLoader());
//		System.out.println("class is inteface :" + clsss.isInterface());
//	} catch (ClassNotFoundException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	} catch (InvalidRegistryObjectException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
	
}
