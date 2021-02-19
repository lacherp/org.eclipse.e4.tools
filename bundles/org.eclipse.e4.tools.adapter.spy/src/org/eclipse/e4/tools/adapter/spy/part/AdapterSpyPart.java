package org.eclipse.e4.tools.adapter.spy.part;

import java.util.LinkedList;
import java.util.List;

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
import org.eclipse.e4.tools.adapter.spy.tools.AdapterHelper;
import org.eclipse.e4.tools.adapter.spy.viewer.AdapterContentProvider;
import org.eclipse.e4.tools.adapter.spy.viewer.AdapterFilter;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
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
	
	AdapterFilter adapterFilter;
	
	
	@Inject
	public AdapterSpyPart(IEclipseContext context) {
		// wrap eclipse adapter
		AdapterHelper.wrapperEclipseAdapter();
		adapterFilter = ContextInjectionFactory.make(AdapterFilter.class, context);
	}
	
	@PostConstruct
	public void createControls(Composite parent, IExtensionRegistry extensionRegistry ) {
		
		parent.setLayout(new GridLayout(1, false));
		createFilterZone(parent);
		
		
		SashForm sashForm = new SashForm(parent, SWT.VERTICAL | SWT.V_SCROLL | SWT.H_SCROLL);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		
		IConfigurationElement[] extp = extensionRegistry.getConfigurationElementsFor(AdapterHelper.EXT_POINT_ID);

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
		bundleTvc.getColumn().setText("Bundle contributor name");
		bundleTvc.getColumn().setWidth(500);
		bundleTvc.setLabelProvider(adapterContentProvider);
		
		TreeViewerColumn adapterFactoryClassTvc = new TreeViewerColumn(adapterTreeViewer, SWT.NONE);
		adapterFactoryClassTvc.getColumn().setText("Adapter factory class");
		adapterFactoryClassTvc.getColumn().setWidth(700);
		adapterFactoryClassTvc.setLabelProvider(adapterContentProvider);
		
		// update treeViewer
		context.set(NAMED_UPDATE_TREE, extp);
		
	}
	
	
	private void createFilterZone(Composite parent) {
		final Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(3, false));
		Text filterText = new Text(comp, SWT.SEARCH | SWT.ICON_SEARCH | SWT.ICON_CANCEL);
		GridDataFactory.fillDefaults().hint(200, SWT.DEFAULT).applyTo(filterText);
		filterText.setMessage("Search contributor");
		filterText.setToolTipText("Find contributor name with plugin id");
		
		filterText.addModifyListener( (e) -> {
			
				if(filterText.getText().isEmpty()) {
					context.set(AdapterFilter.UPDATE_CTX_FILTER, "");
					
				}else {
					context.set(AdapterFilter.UPDATE_CTX_FILTER,filterText.getText());
				}
				adapterTreeViewer.refresh(true);
				
		});
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
		uisync.syncExec(()->{
			if( adapterTreeViewer != null)
			{
				adapterTreeViewer.setInput(result);	
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
	}
	
	
}
