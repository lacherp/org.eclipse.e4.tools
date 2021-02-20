package org.eclipse.e4.tools.adapter.spy.viewer;

import java.util.LinkedList;

import javax.inject.Inject;

import org.eclipse.e4.tools.adapter.spy.model.AdapterData;
import org.eclipse.e4.tools.adapter.spy.model.AdapterElementType;
import org.eclipse.e4.tools.adapter.spy.tools.AdapterHelper;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;

/**
 * This provider is used to display available plugins
 * which contribute to adapters.exsd extension point  
 * @author pascal
 *
 */
public class AdapterContentProvider extends ColumnLabelProvider implements ITreeContentProvider {

	@Inject
	private ImageRegistry imgReg;
	
	private int columnIndex;
	

	@Override
	public void update(ViewerCell cell) {
		columnIndex = cell.getColumnIndex();
		super.update(cell);
	}
	
	@Override
	public Object[] getElements(Object inputElement) {
		
		if(inputElement instanceof LinkedList<?>) {
			return ((LinkedList<?>) inputElement).toArray();
		}
		return (Object[]) inputElement;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof AdapterData)
		{
			return ((AdapterData)parentElement).getChildren();
		}
	
		return null;
	}

	@Override
	public Object getParent(Object element) {
		if(element instanceof AdapterData)
		{
			return ((AdapterData)element).getParent();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		
		if(element instanceof AdapterData)
		{
			return ((AdapterData)element).hasChildren();
		}
		return false;
	}

	@Override
	public String getText(Object element) {
		
		if ( element instanceof AdapterData)
		{
			return ((AdapterData)element).getText(columnIndex);	
		}
		return "";
	}
	
	@Override
	public Image getImage(Object element) {
		if(columnIndex ==0) {
			AdapterElementType elemType = ((AdapterData)element).getAdapterElementType();
			if( elemType.equals(AdapterElementType.SOURCE_TYPE))
			{
				return imgReg.get(AdapterHelper.FROM_TYPE_IMG_KEY);
			}
			if( elemType.equals(AdapterElementType.DESTINATION_TYPE))
			{
				return imgReg.get(AdapterHelper.TO_TYPE_IMG_KEY);
			}	
		}
		return super.getImage(element);
	}
	
}
