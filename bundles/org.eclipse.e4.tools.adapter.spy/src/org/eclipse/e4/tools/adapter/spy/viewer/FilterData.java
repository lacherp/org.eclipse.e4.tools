package org.eclipse.e4.tools.adapter.spy.viewer;

/**
 * This class is used to store data filter in the context
 * @author pascal
 *
 */
public class FilterData {

	String txtSeachFilter;
	Boolean showPackage;
	
	/**
	 * Ctor
	 */
	public FilterData() {
		showPackage = Boolean.TRUE;
	}

	/**
	 * Copy ctor
	 * @param fdata
	 */
	public FilterData(FilterData fdata)
	{
		this.txtSeachFilter = fdata.txtSeachFilter;
		this.showPackage = fdata.showPackage;
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

}
