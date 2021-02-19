package org.eclipse.e4.tools.adapter.spy.viewer;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.tools.adapter.spy.model.AdapterData;
import org.eclipse.e4.tools.adapter.spy.model.AdapterElementType;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * Adapter Tree viewer filter
 * @author pascal
 *
 */
public class AdapterFilter extends ViewerFilter {

	public static final String UPDATE_CTX_FILTER ="updateCtxfilter";
	
	private String  txtSeachFilter;
	
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (txtSeachFilter != null && !txtSeachFilter.isEmpty() &&  element instanceof AdapterData) {
			checkFilteredplugin((AdapterData) element);
		}
		if(txtSeachFilter != null && txtSeachFilter.isEmpty())
		{
			((AdapterData)element).setVisibilityFilter(true);
			((AdapterData)element).propagateVisibility();
		}
		return ((AdapterData)element).isVisibilityFilter();
	}
	
	@Inject
	@Optional
	public void updateTextSearchFilter(@Named(UPDATE_CTX_FILTER) String txtSeachFilter) {
		if( txtSeachFilter == null )
		{
			return;
		}
		this.txtSeachFilter = txtSeachFilter;
	}

	private void checkFilteredplugin(AdapterData adapterData)
	{
		if( adapterData.getAdapterElementType().equals(AdapterElementType.PLUGIN))
		{
			AtomicBoolean bfound = new AtomicBoolean(false);
			adapterData.textSearch(txtSeachFilter, bfound);
			adapterData.setVisibilityFilter(bfound.get());
			adapterData.propagateVisibility();
		}
	}
	
}
