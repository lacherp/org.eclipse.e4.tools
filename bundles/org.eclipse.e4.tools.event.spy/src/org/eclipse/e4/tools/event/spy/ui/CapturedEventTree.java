/*******************************************************************************
 * Copyright (c) 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.tools.event.spy.ui;

import java.util.ArrayList;

import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.masterdetail.IObservableFactory;
import org.eclipse.e4.tools.event.spy.model.CapturedEvent;
import org.eclipse.e4.tools.event.spy.model.CapturedEventTreeSelection;
import org.eclipse.e4.tools.event.spy.model.IEventItem;
import org.eclipse.e4.tools.event.spy.model.ItemToFilter;
import org.eclipse.e4.tools.event.spy.util.JDTUtils;
import org.eclipse.jface.databinding.viewers.ObservableListTreeContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.jface.databinding.viewers.TreeStructureAdvisor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;


public class CapturedEventTree extends TreeViewer {

	public interface SelectionListener {
		void selectionChanged(CapturedEventTreeSelection selection);
	}

	private SelectionListener selectionListener;

	private final WritableList capturedEvents;
	 
	private final Clipboard clipboard;

	private final TreeItemCursor treeItemCursor;
	
	private final TreeItemColor treeItemColor;

	private final TreeItemFont treeItemFont;

	private final SelectedClassItem selectedClassItem;
	
	private String selectedItemText;

	public CapturedEventTree(Composite parent) {
		super(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);

		getTree().setHeaderVisible(true);
		getTree().setLinesVisible(true);
		
		TreeColumn column = new TreeColumn(getTree(), SWT.LEFT);
		column.setText(ItemToFilter.Topic.toString());
		column.setWidth(350);

		column = new TreeColumn(getTree(), SWT.LEFT);
		column.setText(ItemToFilter.Publisher.toString());
		column.setWidth(150);

		column = new TreeColumn(getTree(), SWT.LEFT);
		column.setText(ItemToFilter.ChangedElement.toString());
		column.setWidth(150);
		
		ObservableListTreeContentProvider contentProvider = new ObservableListTreeContentProvider(
				new CapturedEventsObservableFactory(), new CapturedEventsTreeStructureAdvisor());
		setContentProvider(contentProvider);

		IObservableMap[] attributes = PojoObservables.observeMaps(
				contentProvider.getKnownElements(), IEventItem.class,
				new String[] {"name", "param1", "param2"});
		setLabelProvider(new ObservableMapLabelProvider(attributes));

		capturedEvents = new WritableList(new ArrayList<CapturedEvent>(), CapturedEvent.class);
		setInput(capturedEvents);

		clipboard = new Clipboard(getTree().getDisplay());

		treeItemCursor = new TreeItemCursor(getTree().getCursor(),
				getTree().getDisplay().getSystemCursor(SWT.CURSOR_HAND));

		treeItemColor = new TreeItemColor(new Color(getTree().getDisplay(), new RGB(0, 0, 120)),
				getTree().getDisplay().getSystemColor(SWT.COLOR_WHITE),
				getTree().getDisplay().getSystemColor(SWT.COLOR_BLACK));

		Font currentFont = getTree().getFont();
		FontData currentFontData = currentFont.getFontData()[0];
		treeItemFont = new TreeItemFont(currentFont, new Font(getTree().getDisplay(),
				currentFontData.getName(), currentFontData.getHeight(), SWT.ITALIC));

		selectedClassItem = new SelectedClassItem();
		
		addTreeEventListeners();
	}
	
	private static class CapturedEventsTreeStructureAdvisor extends TreeStructureAdvisor {
		@Override
		public Boolean hasChildren(Object element) {
			if (element instanceof CapturedEvent) {
				return !((CapturedEvent) element).getParameters().isEmpty();
		 	}
			return false;
		}
	}

	private static class CapturedEventsObservableFactory implements IObservableFactory {
		public IObservable createObservable(Object target) {
			if (target instanceof IObservableList) {
				return (IObservableList) target;
			}
			if (target instanceof CapturedEvent) {
				return PojoObservables.observeList(target, "parameters");
			}
			return null;
		}
	}

	private void addTreeEventListeners() {
		getTree().addDisposeListener(new DisposeListener() {						
			public void widgetDisposed(DisposeEvent e) {
				if (clipboard != null && !clipboard.isDisposed()) {
					clipboard.dispose();
				}
				if (treeItemColor.getParamColor() != null &&
						!treeItemColor.getParamColor().isDisposed()) {
					treeItemColor.getParamColor().dispose();
				}
				if (treeItemFont.getSelectedClassNameFont() != null &&
						!treeItemFont.getSelectedClassNameFont().isDisposed()) {
					treeItemFont.getSelectedClassNameFont().dispose();
				}
			}
		});
		
		//TODO: Simplify the hit test for item
		getTree().addMouseMoveListener(new MouseMoveListener() {
			public void mouseMove(MouseEvent e) {
				Tree tree = getTree();
				clearSelectedClassItem();
				
				//we can select and finally open the class only when 'ctrl' is pressed
				if ((e.stateMask & SWT.CTRL) != SWT.CTRL) {
					return;
				}
				
				TreeItem item = getTree().getItem(new Point(e.x, e.y));
				int selectedItemIndex = getSelectedColumnIndex(item, e.x, e.y);

				if (selectedItemIndex > 0 /*we check the 2nd and 3rd column only*/ &&
						item.getParentItem() == null /*we don't check parameters at this moment*/) {
					String text = item.getText(selectedItemIndex);
					if (JDTUtils.containsClassName(text)) {
						selectedClassItem.setClassName(text);
						selectedClassItem.setColumnIndex(selectedItemIndex);
						selectedClassItem.setTreeItem(item);
						tree.setCursor(treeItemCursor.getPointerCursor());
						redrawTreeItem(item, selectedItemIndex);
					}
				}
			}
		});
		
		getTree().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				TreeItem item = getTree().getItem(new Point(e.x, e.y));
				int index = getSelectedColumnIndex(item, e.x, e.y);
				selectedItemText = index != -1? item.getText(index): null;

				if ((e.stateMask & SWT.CTRL) == SWT.CTRL && selectedClassItem.getClassName() != null) {
					selectionListener.selectionChanged(new CapturedEventTreeSelection(selectedClassItem.getClassName()));
				}
			}
		});

		getTree().addListener(SWT.EraseItem, new Listener() {
			public void handleEvent(Event event) {
				event.detail &= ~SWT.FOREGROUND;
			}
		});

		getTree().addListener(SWT.PaintItem, new Listener() {
			public void handleEvent(Event event) {
				TreeItem item = (TreeItem) event.item;
				String text = item.getText(event.index);
				int xOffset = item.getParentItem() != null? 10: 2;
				Color color = getColorForItem(item, event.detail);

				event.gc.setFont(getFontForItem(item, event.index));
				event.gc.setForeground(color);
				event.gc.drawText(text, event.x + xOffset, event.y, true);
			}
		});
		
		getTree().addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				boolean ctrlC = (e.stateMask & SWT.CTRL) == SWT.CTRL && e.keyCode == 'c';
				if(ctrlC && selectedItemText != null && selectedItemText.length() > 0) {
					clipboard.setContents(new Object[] {selectedItemText},
							new Transfer[] {TextTransfer.getInstance()});
				}
			}
			public void keyReleased(KeyEvent e) {
				clearSelectedClassItem();
			}
		});

		getTree().addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				clearSelectedClassItem();
			}
		});
	}

	private Color getColorForItem(TreeItem item, int eventDetails) {
		if ((eventDetails & SWT.SELECTED) == SWT.SELECTED) {
			return treeItemColor.getSelectedColor();
		}
		if (item.getParentItem() != null) {
			return treeItemColor.getParamColor();
		}
		return treeItemColor.getDefaultColor();
	}

	private Font getFontForItem(TreeItem item, int columnIndex) {
		if (selectedClassItem.getTreeItem() == item &&
				selectedClassItem.getColumnIndex() == columnIndex) {
			return treeItemFont.getSelectedClassNameFont();
		}
		return treeItemFont.getDefaultFont();
	}

	private void redrawTreeItem(TreeItem item, int columnIndex) {
		if (item != null) {
			Rectangle rec = item.getBounds(columnIndex);
			getTree().redraw(rec.x, rec.y, rec.width, rec.height, true);
		}
	}
	
	private int getSelectedColumnIndex(TreeItem item, int mouseX, int mouseY) {
		for (int i=0; item != null && i<getTree().getColumnCount(); i++) {
			Rectangle rec = item.getBounds(i);
			if (mouseX >= rec.x && mouseX <= rec.x + rec.width) {
				return i;
			}
		}
		return -1;
	}

	private void clearSelectedClassItem() {
		redrawTreeItem(selectedClassItem.getTreeItem(), selectedClassItem.getColumnIndex());
		selectedClassItem.clear();

		Tree tree = getTree();
		if (tree.getCursor() != treeItemCursor.getDefaultCursor()) {
			tree.setCursor(treeItemCursor.getDefaultCursor());
		}
	}

	public void addEvent(CapturedEvent event) {
		capturedEvents.add(event);
	}

	public void setSelectionListener(SelectionListener selectionListener) {
		this.selectionListener = selectionListener;
	}

	public void removeAll() {
		capturedEvents.clear();
	}
	
	private static class TreeItemColor {
		private final Color paramColor;

		private final Color selectedColor;
		
		private final Color defaultColor;

		public TreeItemColor(Color paramColor, Color selectedColor, Color defaultColor) {
			this.paramColor = paramColor;
			this.selectedColor = selectedColor;
			this.defaultColor = defaultColor;
		}

		public Color getParamColor() {
			return paramColor;
		}

		public Color getSelectedColor() {
			return selectedColor;
		}
		
		public Color getDefaultColor() {
			return defaultColor;
		}
	}

	private static class TreeItemCursor {
		private final Cursor defaultCursor;
		
		private final Cursor pointerCursor;
		
		public TreeItemCursor(Cursor defaultCursor, Cursor pointerCursor) {
			this.defaultCursor = defaultCursor;
			this.pointerCursor = pointerCursor;
		}
		
		public Cursor getDefaultCursor() {
			return defaultCursor;
		}
		
		public Cursor getPointerCursor() {
			return pointerCursor;
		}
	}
	
	private static class TreeItemFont {
		private final Font defaultFont;
		
		private final Font selectedClassNameFont;
		
		public TreeItemFont(Font defaultFont, Font selectedClassNameFont) {
			this.defaultFont = defaultFont;
			this.selectedClassNameFont = selectedClassNameFont;
		}

		public Font getDefaultFont() {
			return defaultFont;
		}
		
		public Font getSelectedClassNameFont() {
			return selectedClassNameFont;
		}
	}
	
	private static class SelectedClassItem {
		private TreeItem treeItem;
		
		private int columnIndex;
		
		private String className;
		
		public SelectedClassItem() {
			clear();
		}
		
		public void setTreeItem(TreeItem treeItem) {
			this.treeItem = treeItem;
		}

		public TreeItem getTreeItem() {
			return treeItem;
		}
		
		public void setColumnIndex(int columnIndex) {
			this.columnIndex = columnIndex;
		}
		
		public int getColumnIndex() {
			return columnIndex;
		}
		
		public void setClassName(String className) {
			this.className = className;
		}
		
		public String getClassName() {
			return className;
		}
		
		public void clear() {
			treeItem = null;
			columnIndex = -1;
			className = null;
		}
	}
}
