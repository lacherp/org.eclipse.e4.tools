package org.eclipse.e4.tools.adapter.spy.part;

import java.util.LinkedList;
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
import org.eclipse.swt.widgets.Tree;

@SuppressWarnings("restriction")
public class AdapterSpyPart {
	
	private TreeViewer adapterTreeViewer;
	
	private AdapterContentProvider adapterContentProvider;
	
	private static final String NAMED_UPDATE_TREE ="udpateTree";
	
	@Inject
	UISynchronize uisync;
	
	@Inject
	IEclipseContext context;
	
	@Inject 
	AdapterRepository adapterRepo;
	
	AdapterFilter adapterFilter;

	private Button reduceSourceType;
	
	
	@Inject
	public AdapterSpyPart(IEclipseContext context) {
		// wrap eclipse adapter
		AdapterHelper.wrapperEclipseAdapter();
		adapterFilter = ContextInjectionFactory.make(AdapterFilter.class, context);
		
	}
	
	@PostConstruct
	public void createControls(Composite parent, IExtensionRegistry extensionRegistry ) {
		
		parent.setLayout(new GridLayout(1, false));
		createSearchFilterZone(parent);
		
		SashForm sashForm = new SashForm(parent, SWT.VERTICAL | SWT.V_SCROLL | SWT.H_SCROLL);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		AdapterHelper.getServicesContext().set(AdapterRepository.class, adapterRepo);
		adapterRepo.clear();
		IConfigurationElement[] extp = adapterRepo.getAdapters();

		// Adapter TreeViewer 
		adapterTreeViewer = new TreeViewer(sashForm);
		adapterContentProvider = 	ContextInjectionFactory.make(AdapterContentProvider.class, context);
		adapterTreeViewer.setContentProvider(adapterContentProvider);
		adapterTreeViewer.setLabelProvider(adapterContentProvider);
		adapterTreeViewer.setFilters(adapterFilter);
		
		// define columns
		final Tree cTree = adapterTreeViewer.getTree();
		cTree.setHeaderVisible(true);
		cTree.setLinesVisible(true);
		cTree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		TreeViewerColumn bundleTvc = new TreeViewerColumn(adapterTreeViewer, SWT.NONE);
		bundleTvc.getColumn().setText("Source Type");
		bundleTvc.getColumn().setWidth(500);
		bundleTvc.setLabelProvider(adapterContentProvider);
		
		TreeViewerColumn adapterFactoryClassTvc = new TreeViewerColumn(adapterTreeViewer, SWT.NONE);
		adapterFactoryClassTvc.getColumn().setText("AdapterFactory");
		adapterFactoryClassTvc.getColumn().setWidth(700);
		adapterFactoryClassTvc.setLabelProvider(adapterContentProvider);
		
		// update treeViewer
		context.set(NAMED_UPDATE_TREE, extp);
		
	}
	
	
	private void createSearchFilterZone(Composite parent) {
		final Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(3, false));
		Text filterText = new Text(comp, SWT.SEARCH | SWT.ICON_SEARCH | SWT.ICON_CANCEL);
		GridDataFactory.fillDefaults().hint(250, SWT.DEFAULT).applyTo(filterText);
		filterText.setMessage("Search data");
		filterText.setToolTipText("Find contributor name with plugin id");
		
		filterText.addModifyListener( e -> {
				FilterData fdata = getFilterData();
				if(filterText.getText().isEmpty()) {
					fdata.setTxtSeachFilter("");
				}else {
					fdata.setTxtSeachFilter(filterText.getText());
				}
				context.set(AdapterFilter.UPDATE_CTX_FILTER,fdata);
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
				context.set(AdapterFilter.UPDATE_CTX_FILTER,fdata);
				adapterTreeViewer.refresh(true);
			}
		});
		
		reduceSourceType = new Button(comp, SWT.CHECK);
		reduceSourceType.setText("Reduce source type");
		reduceSourceType.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				context.set(NAMED_UPDATE_TREE,null);
				adapterRepo.clear();
				context.set(NAMED_UPDATE_TREE,adapterRepo.getAdapters());
				FilterData fdata = getFilterData();
				context.set(AdapterFilter.UPDATE_CTX_FILTER,fdata);
				adapterTreeViewer.refresh(true);
			}
		});
	}

	private FilterData getFilterData() {
		if(context.get(AdapterFilter.UPDATE_CTX_FILTER) == null) {
			return new FilterData();
		}
		return new FilterData((FilterData) context.get(AdapterFilter.UPDATE_CTX_FILTER)) ;
	}
	
	@Inject
	@Optional
	private void updateAdapterTree(@Named(NAMED_UPDATE_TREE) IConfigurationElement [] configElement, Adapter adapter)
	{
		if(configElement == null)
		{
			return;
		}
		List<AdapterData> result = new LinkedList<>();
		for( IConfigurationElement elem:configElement)
		{
			AdapterData adata = adapter.adapt(elem, AdapterData.class);
			result.add(adata);
		}
		// reduce source Type
		List<AdapterData> reduceresult = result;
		if( reduceSourceType.getSelection()) {
			
			Map<String, List<AdapterData>> resultmap = result.stream().collect(Collectors.groupingBy(AdapterData::sourceType));
			reduceresult.clear();
			resultmap.forEach((k,v)->{
				AdapterData firstElem = v.get(0);
				reduceresult.add(firstElem);
				for(int idx=1;idx<v.size();idx++) {
					firstElem.getChildrenList().addAll(v.get(idx).getChildrenList());
				}
			});
		}
		
		uisync.syncExec(()->{
			if( adapterTreeViewer != null)
			{
				adapterTreeViewer.setInput(reduceresult);
				context.set(NAMED_UPDATE_TREE,null);
			}
		});
		
	}
	
	
	@PreDestroy
	public void dispose()
	{
		adapterTreeViewer = null;
		if(adapterContentProvider != null)
		{
			ContextInjectionFactory.uninject(adapterContentProvider, context);
		}
		if(adapterFilter != null)
		{
			ContextInjectionFactory.uninject(adapterFilter, context);
		}
		AdapterHelper.restoreOriginalEclipseAdapter();
		context.set(AdapterFilter.UPDATE_CTX_FILTER,null);
		adapterRepo.clear();
	}
	
	
}
