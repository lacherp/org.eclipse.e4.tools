package org.eclipse.e4.tools.adapter.spy.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.e4.tools.adapter.spy.tools.AdapterHelper;
import org.eclipse.emf.ecore.util.EcoreUtil;


/**
 * Adapter data model Object
 * This class is used to store ConfigarationElement elements
 * which use an adapter
 * @author pascal
 *
 */
public class AdapterData {

	IConfigurationElement configElem;
	AdapterData parent;
	List<AdapterData> children = new ArrayList<>();
	AdapterElementType elemType;
	boolean visibilityFilter = true;
	final String uid;
	
	public AdapterData(IConfigurationElement elem, AdapterElementType elemType) {
		this.elemType = elemType;
		configElem = elem;
		uid=EcoreUtil.generateUUID();
	}

	public void buildchilds() {
		AdapterData adapter = new AdapterData(configElem, AdapterElementType.FROM_TYPE);
		adapter.setParent(this);
		children.add(adapter);

		IConfigurationElement[] childs = configElem.getChildren("adapter");
		for (IConfigurationElement child : childs) {
			AdapterData adaptdata = new AdapterData(child, AdapterElementType.TO_TYPE);
			adaptdata.setParent(adapter);
			adapter.getChildrenList().add(adaptdata);
		}
	}

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
		if (parent == null) {
			bfound.set(getAdaterClass().contains(txtSearch));
		}
		children.forEach(d -> {
			d.textSearch(txtSearch, bfound);
		});

	}

	public String getPluginName() {
		return configElem.getContributor().getName();
	}

	public String fromType() {
		return configElem.getAttribute(AdapterHelper.EXT_POINT_ATTR_ADAPTABLE_TYPE);
	}

	public String getAdaterClass() {
		return configElem.getAttribute(AdapterHelper.EXT_POINT_ATTR_CLASS);
	}

	public String toType() {
		return configElem.getAttribute(AdapterHelper.EXT_POINT_ATTR_TYPE);
	}

	public boolean hasChildren() {
		return !children.isEmpty();
	}

	public List<AdapterData> getChildrenList() {
		return this.children;
	}

	public AdapterData[] getChildren() {
		if (!children.isEmpty()) {
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

	public String getText(int colIndex) {
		if (colIndex == 0) {
			return toString();
		}
		if (parent == null) {
			return getAdaterClass();
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

	@Override
	public String toString() {
		String result = "";
		switch (elemType) {
		case FROM_TYPE:
			result = fromType();
			break;
		case PLUGIN:
			result = getPluginName();
			break;
		case TO_TYPE:
			result = toType();
			break;
		default:
			throw new IllegalArgumentException("Unexpected value: " + elemType);
		}
		return result;
	}

}
