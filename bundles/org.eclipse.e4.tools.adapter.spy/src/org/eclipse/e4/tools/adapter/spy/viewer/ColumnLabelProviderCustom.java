package org.eclipse.e4.tools.adapter.spy.viewer;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.custom.StyleRange;

public class ColumnLabelProviderCustom extends ColumnLabelProvider {

	protected StyleRange[] getToolTipStyleRanges(Object element) {
		return null;
	}

}
