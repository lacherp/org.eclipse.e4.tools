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
package org.eclipse.e4.tools.adapter.spy.part;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.adapter.Adapter;
import org.eclipse.e4.tools.adapter.spy.model.AdapterData;
import org.eclipse.e4.tools.adapter.spy.model.AdapterRepository;
import org.eclipse.e4.tools.adapter.spy.tools.AdapterHelper;
import org.eclipse.e4.tools.adapter.spy.viewer.AdapterContentProvider;
import org.eclipse.e4.tools.adapter.spy.viewer.AdapterFilter;
import org.eclipse.e4.tools.adapter.spy.viewer.FilterData;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;

@SuppressWarnings("restriction")
public class AdapterSpyPart {

	private TreeViewer adapterTreeViewer;

	private AdapterContentProvider adapterContentProvider;

	private static final String NAMED_UPDATE_TREE_SOURCE_TO_DESTINATION = "updateTreeSourceToDestination";
	private static final String NAMED_UPDATE_TREE_DESTINATION_TO_SOURCE = "updateTreeDestinationToSource";

	@Inject
	UISynchronize uisync;

	@Inject
	IEclipseContext context;

	@Inject
	AdapterRepository adapterRepo;

	AdapterFilter adapterFilter;

	private Button reduceType;
	
	boolean sourceToDestination = true;

	private TreeViewerColumn sourceOrDestinationTvc;

	@Inject
	public AdapterSpyPart(IEclipseContext context) {
		// wrap eclipse adapter
		AdapterHelper.wrapperEclipseAdapter();
		adapterFilter = ContextInjectionFactory.make(AdapterFilter.class, context);
		context.set(ImageRegistry.class,AdapterHelper.getImageRegistry(this));
	}

	@PostConstruct
	public void createControls(Composite parent, IExtensionRegistry extensionRegistry,ImageRegistry imgr) {

		parent.setLayout(new GridLayout(1, false));
		createToolBarZone(parent,imgr);

		SashForm sashForm = new SashForm(parent, SWT.VERTICAL | SWT.V_SCROLL | SWT.H_SCROLL);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		AdapterHelper.getServicesContext().set(AdapterRepository.class, adapterRepo);
		adapterRepo.clear();
		IConfigurationElement[] extp = adapterRepo.getAdapters();

		// Adapter TreeViewer
		adapterTreeViewer = new TreeViewer(sashForm);
		adapterContentProvider = ContextInjectionFactory.make(AdapterContentProvider.class, context);
		adapterTreeViewer.setContentProvider(adapterContentProvider);
		adapterTreeViewer.setLabelProvider(adapterContentProvider);
		adapterTreeViewer.setFilters(adapterFilter);

		// define columns
		final Tree cTree = adapterTreeViewer.getTree();
		cTree.setHeaderVisible(true);
		cTree.setLinesVisible(true);
		cTree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		sourceOrDestinationTvc = new TreeViewerColumn(adapterTreeViewer, SWT.NONE);
		sourceOrDestinationTvc.getColumn().setText("Source Type");
		sourceOrDestinationTvc.getColumn().setWidth(500);
		sourceOrDestinationTvc.setLabelProvider(adapterContentProvider);

		TreeViewerColumn adapterFactoryClassTvc = new TreeViewerColumn(adapterTreeViewer, SWT.NONE);
		adapterFactoryClassTvc.getColumn().setText("AdapterFactory");
		adapterFactoryClassTvc.getColumn().setWidth(700);
		adapterFactoryClassTvc.setLabelProvider(adapterContentProvider);

		// update treeViewer
		context.set(NAMED_UPDATE_TREE_SOURCE_TO_DESTINATION, extp);

	}

	private void createToolBarZone(Composite parent, ImageRegistry imgr) {
		final Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(4, false));
		
		Text filterText = new Text(comp, SWT.SEARCH | SWT.ICON_SEARCH | SWT.ICON_CANCEL);
		GridDataFactory.fillDefaults().hint(250, SWT.DEFAULT).applyTo(filterText);
		filterText.setMessage("Search data");
		filterText.setToolTipText("Find any element in tree");

		filterText.addModifyListener(e -> {
			FilterData fdata = getFilterData();
			if (filterText.getText().isEmpty()) {
				fdata.setTxtSeachFilter("");
			} else {
				fdata.setTxtSeachFilter(filterText.getText());
			}
			context.set(AdapterFilter.UPDATE_CTX_FILTER, fdata);
			adapterTreeViewer.refresh(true);

		});

		Button showPackageFilter = new Button(comp, SWT.CHECK);
		showPackageFilter.setText("Show package");

		showPackageFilter.setToolTipText("Show source type with packages name");
		showPackageFilter.setEnabled(true);
		showPackageFilter.setSelection(true);
		showPackageFilter.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FilterData fdata = getFilterData();
				fdata.setShowPackage(!fdata.getShowPackage());
				context.set(AdapterFilter.UPDATE_CTX_FILTER, fdata);
				adapterTreeViewer.refresh(true);
			}
		});

		reduceType = new Button(comp, SWT.CHECK);
		reduceType.setText("Reduce type");
		reduceType.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FilterData fdata = getFilterData();
				if(sourceToDestination) {
					context.set(NAMED_UPDATE_TREE_SOURCE_TO_DESTINATION, null);
					adapterRepo.clear();
					context.set(NAMED_UPDATE_TREE_SOURCE_TO_DESTINATION, adapterRepo.getAdapters());
					context.set(AdapterFilter.UPDATE_CTX_FILTER, fdata);
				}else {
					
					context.set(NAMED_UPDATE_TREE_DESTINATION_TO_SOURCE,adapterRepo.revertSourceToType());
					context.set(AdapterFilter.UPDATE_CTX_FILTER, fdata);
				}
				adapterTreeViewer.refresh(true);
			
			}
		});
	
		ToolBar toolBar = new ToolBar(comp, SWT.NONE);
		ToolItem toolItem = new ToolItem(toolBar, SWT.CHECK);
		toolItem.setImage(imgr.get(AdapterHelper.DESTINATION_TYPE_IMG_KEY));
		toolItem.setToolTipText("Toggle to destination type");
		// sourceToType event
		toolItem.addSelectionListener(new SelectionAdapter() {
		
			@Override
			public void widgetSelected(SelectionEvent event) {
				Object source = event.getSource();
				if (source instanceof ToolItem) {
					FilterData fdata = getFilterData();
					sourceToDestination = !sourceToDestination;
					String tooltiptext = sourceToDestination ? "Toggle to destination type" : "Toggle to source type";
					String imageKey = sourceToDestination ? AdapterHelper.DESTINATION_TYPE_IMG_KEY:AdapterHelper.SOURCE_TYPE_IMG_KEY;
					toolItem.setToolTipText(tooltiptext);
					toolItem.setImage(imgr.get(imageKey));
					
					if(sourceToDestination) {
						sourceOrDestinationTvc.getColumn().setText("Source Type");
						context.set(NAMED_UPDATE_TREE_SOURCE_TO_DESTINATION, adapterRepo.getAdapters());
					}else {
						sourceOrDestinationTvc.getColumn().setText("Destination Type");
						context.set(NAMED_UPDATE_TREE_DESTINATION_TO_SOURCE,adapterRepo.revertSourceToType());	
					}
					fdata.setSourceToDestination(sourceToDestination);
					context.set(AdapterFilter.UPDATE_CTX_FILTER, fdata);
				}
				
			}
		});
		uisync.asyncExec(()-> comp.pack());
		
	}

	private FilterData getFilterData() {
		if (context.get(AdapterFilter.UPDATE_CTX_FILTER) == null) {
			return new FilterData();
		}
		return new FilterData((FilterData) context.get(AdapterFilter.UPDATE_CTX_FILTER));
	}

	@Inject
	@Optional
	private void updateAdapterTreeViewerSourceToType(@Named(NAMED_UPDATE_TREE_SOURCE_TO_DESTINATION) IConfigurationElement[] configElement, Adapter adapter) {
		if (configElement == null) {
			return;
		}
		List<AdapterData> result = new ArrayList<>();
		for (IConfigurationElement elem : configElement) {
			AdapterData adata = adapter.adapt(elem, AdapterData.class);
			result.add(adata);
		}
		// reduce source Type
		List<AdapterData> reduceresult = reduceType(result);
		refreshAdapterTree(NAMED_UPDATE_TREE_SOURCE_TO_DESTINATION, reduceresult);
	}

	
	@Inject
	@Optional
	private void udpateAdapterTreeViewTypeToSource(@Named(NAMED_UPDATE_TREE_DESTINATION_TO_SOURCE) List<AdapterData> adapaters) {
		List<AdapterData> result=reduceType(adapaters);
		refreshAdapterTree(NAMED_UPDATE_TREE_SOURCE_TO_DESTINATION, result);
	}
	
	
	private List<AdapterData> reduceType(List<AdapterData> originalList){
		List<AdapterData> reduceresult = originalList;
		if (reduceType.getSelection()) {

			Map<String, List<AdapterData>> resultmap = groupBy(originalList);
			
			reduceresult.clear();
			resultmap.forEach((k, v) -> {
				AdapterData firstElem = v.get(0);
				reduceresult.add(firstElem);
				for (int idx = 1; idx < v.size(); idx++) {
					firstElem.getChildrenList().addAll(v.get(idx).getChildrenList());
				}
			});
		}
		
		
		return reduceresult;
	}
	
	private Map<String, List<AdapterData>> groupBy(List<AdapterData> originalList) {
		Map<String, List<AdapterData>> result=null;
		if( sourceToDestination) {
			result = originalList.stream()
					.collect(Collectors.groupingBy(AdapterData::sourceType));
		}else {
			result = originalList.stream()
					.collect(Collectors.groupingBy(AdapterData::destinationType));
		}
		return result;
	}
	
	@PreDestroy
	public void dispose() {
		adapterTreeViewer = null;
		if (adapterContentProvider != null) {
			ContextInjectionFactory.uninject(adapterContentProvider, context);
		}
		if (adapterFilter != null) {
			ContextInjectionFactory.uninject(adapterFilter, context);
		}
		AdapterHelper.restoreOriginalEclipseAdapter();
		context.set(AdapterFilter.UPDATE_CTX_FILTER, null);
		adapterRepo.clear();
	}
	
	
	private void refreshAdapterTree(String namedContext,List<AdapterData> result) {
		uisync.syncExec(() -> {
			if (adapterTreeViewer != null) {
				adapterTreeViewer.setInput(result);
				context.set(namedContext, null);
			}
		});
	}


	


}
