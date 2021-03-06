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
package org.eclipse.e4.tools.adapter.spy.viewer;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.tools.adapter.spy.model.AdapterData;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;

/**
 * This provider is used to display available plugins which contribute to
 * adapters.exsd extension point
 * 
 * @author pascal
 *
 */
public class AdapterContentProvider extends ColumnLabelProvider implements ITreeContentProvider {

	@Inject
	private ImageRegistry imgReg;

	private int columnIndex;

	private Boolean sourceToDestination = true;


	@Override
	public Object[] getElements(Object inputElement) {

		if (inputElement instanceof Collection<?>) {
			return ((Collection<?>) inputElement).toArray();
		}
		return (Object[]) inputElement;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof AdapterData) {
			return (Object[]) ((AdapterData) parentElement).getChildren(sourceToDestination);
		}

		return new Object[0];
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof AdapterData) {
			return ((AdapterData) element).getParent();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {

		if (element instanceof AdapterData) {
			return ((AdapterData) element).hasChildren();
		}
		return false;
	}

	@Override
	public String getText(Object element) {

		if (element instanceof AdapterData) {
			return ((AdapterData) element).getText(columnIndex);
		}
		return "";
	}

	@Override
	public Image getImage(Object element) {
		if (columnIndex == 0) {
			if ( element instanceof AdapterData) {
				String imgname = ((AdapterData)element).getImageName(); 
				return imgname == null ? super.getImage(element): imgReg.get(imgname);
			}
			
		}
		return super.getImage(element);
	}

	@Override
	public int getToolTipStyle(Object object) {
		return SWT.SHADOW_OUT;
	}

	@Override
	public String getToolTipText(Object element) {
		if ( element instanceof AdapterData)
		{
			return ((AdapterData) element).getToolTipText(sourceToDestination, columnIndex);
		}
		return "" ;
	}

	@Inject
	@Optional
	public void updateTextSearchFilter(@Named(AdapterFilter.UPDATE_CTX_FILTER) FilterData filterData) {
		if (filterData == null) {
			return;
		}
		this.sourceToDestination = filterData.getSourceToDestination();
	}

	public void setColumnIndex(int columnIndex) {
		this.columnIndex = columnIndex;
		
	}
}
