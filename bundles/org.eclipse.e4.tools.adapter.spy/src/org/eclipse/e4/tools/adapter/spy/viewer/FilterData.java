package org.eclipse.e4.tools.adapter.spy.viewer;

/**
 * This class is used to store data filter in the context
 * @author pascal
 *
 */
public class FilterData {

	String txtSeachFilter;
	Boolean showPackage;
	Boolean sourceToDestination;
	/**
	 * Ctor
	 */
	public FilterData() {
		showPackage = Boolean.TRUE;
		sourceToDestination = Boolean.TRUE;
	}

	/**
	 * Copy ctor
	 * @param fdata
	 */
	public FilterData(FilterData fdata)
	{
		this.txtSeachFilter = fdata.txtSeachFilter;
		this.showPackage = fdata.showPackage;
		this.sourceToDestination = fdata.sourceToDestination;
	}
	/**
	 * @return the txtSeachFilter
	 */
	public String getTxtSeachFilter() {
		return txtSeachFilter;
	}

	/**
	 * @param txtSeachFilter the txtSeachFilter to set
	 */
	public void setTxtSeachFilter(String txtSeachFilter) {
		this.txtSeachFilter = txtSeachFilter;

	}

	/**
	 * @return the showPackage
	 */
	public Boolean getShowPackage() {
		return showPackage;
	}

	/**
	 * @param showPackage the showPackage to set
	 */
	public void setShowPackage(Boolean showPackage) {
		this.showPackage = showPackage;
	}

	/**
	 * @return the sourceToDestination
	 */
	public Boolean getSourceToDestination() {
		return sourceToDestination;
	}

	/**
	 * @param sourceToDestination the sourceToDestination to set
	 */
	public void setSourceToDestination(Boolean sourceToDestination) {
		this.sourceToDestination = sourceToDestination;
	}

	
}
