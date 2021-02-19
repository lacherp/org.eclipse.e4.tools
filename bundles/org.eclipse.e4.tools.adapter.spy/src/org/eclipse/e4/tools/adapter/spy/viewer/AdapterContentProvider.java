package org.eclipse.e4.tools.adapter.spy.viewer;

import java.util.LinkedList;

import org.eclipse.e4.tools.adapter.spy.model.AdapterData;
import org.eclipse.e4.tools.adapter.spy.model.AdapterElementType;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * This provider is used to display available plugins
 * which contribute to adapters.exsd extension point  
 * @author pascal
 *
 */
public class AdapterContentProvider extends ColumnLabelProvider implements ITreeContentProvider {

	// Image keys constants
	private static  final String BUNDLE_IMG_KEY ="icons/osgi.png";
	private static  final String FROM_TYPE_IMG_KEY ="icons/from_type.png";
	private static  final String TO_TYPE_IMG_KEY ="icons/to_type.png";
	
	private ImageRegistry imgReg;
	
	private int columnIndex;
	/**
	 * Ctor
	 */
	public AdapterContentProvider(){
		initImageRegistry();
	}

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
			if( elemType.equals(AdapterElementType.PLUGIN))
			{
				return imgReg.get(BUNDLE_IMG_KEY);
			}
			if( elemType.equals(AdapterElementType.FROM_TYPE))
			{
				return imgReg.get(FROM_TYPE_IMG_KEY);
			}
			if( elemType.equals(AdapterElementType.TO_TYPE))
			{
				return imgReg.get(TO_TYPE_IMG_KEY);
			}	
		}
		return super.getImage(element);
	}
	
	private void initImageRegistry() {
		Bundle b = FrameworkUtil.getBundle(this.getClass());
		imgReg = new ImageRegistry();
		imgReg.put(BUNDLE_IMG_KEY, ImageDescriptor.createFromURL(b.getEntry(BUNDLE_IMG_KEY)));
		imgReg.put(FROM_TYPE_IMG_KEY, ImageDescriptor.createFromURL(b.getEntry(FROM_TYPE_IMG_KEY)));
		imgReg.put(TO_TYPE_IMG_KEY, ImageDescriptor.createFromURL(b.getEntry(TO_TYPE_IMG_KEY)));
	}

	
	
}
