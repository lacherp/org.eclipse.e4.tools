package org.eclipse.e4.tools.emf.editor3x.compat;

import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.contributions.IContributionFactory;
import org.eclipse.e4.tools.emf.editor3x.E4WorkbenchModelEditor;
import org.eclipse.e4.ui.css.swt.theme.IThemeEngine;
import org.eclipse.e4.ui.css.swt.theme.IThemeManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

public class E4CompatEditorPart extends EditorPart implements IExecutableExtension {
	private Object instance;
	private String classUri;
	
	//TODO can we create a context as a top-level service?
	private IEclipseContext context;
	
	public E4CompatEditorPart(String classUri) {
		this.classUri = classUri;
	}
	
	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);
		setInput(input);
		
		IEclipseContext parentContext = (IEclipseContext) getSite().getService(IEclipseContext.class);
		context = parentContext.createChild("EditPart('"+getPartName()+"')"); //$NON-NLS-1$
		context.declareModifiable(IEditorInput.class);
		context.declareModifiable(EditorPart.class);
		context.set(EditorPart.class,this);
		context.set(IEditorInput.class, input);
		
		ISelectionProvider s = new SelectionProviderImpl();
		context.set(ISelectionProvider.class, s);
		site.setSelectionProvider(s);
	}

	@Override
	public boolean isDirty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setBackgroundMode(SWT.INHERIT_DEFAULT);

		FillLayout layout = new FillLayout();
		layout.marginWidth = 10;
		layout.marginHeight = 10;
		comp.setLayout(layout);
		
		context.set(Composite.class.getName(), comp);
		IContributionFactory factory = (IContributionFactory) context.get(IContributionFactory.class);
		instance = factory.create(classUri, context);
		
		Bundle b = FrameworkUtil.getBundle(E4WorkbenchModelEditor.class);
		if( b != null ) {
			ServiceReference ref = b.getBundleContext().getServiceReference(IThemeManager.class.getName());
			if( ref != null ) {
				IThemeManager mgr = (IThemeManager) b.getBundleContext().getService(ref);
				IThemeEngine engine = mgr.getEngineForDisplay(parent.getDisplay());
				engine.applyStyles(parent, true);
			}			
		}
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		context.dispose();
		context = null;
		super.dispose();
	}
	
	private class SelectionProviderImpl implements ISelectionProvider {
		private ISelection currentSelection = StructuredSelection.EMPTY;
		
		private ListenerList listeners = new ListenerList();
		
		public void setSelection(ISelection selection) {
			currentSelection = selection;
			SelectionChangedEvent evt = new SelectionChangedEvent(this, selection);
			
			for( Object l : listeners.getListeners() ) {
				((ISelectionChangedListener)l).selectionChanged(evt);
			}
		}
				
		public void removeSelectionChangedListener(
				ISelectionChangedListener listener) {
			listeners.remove(listener);
		}
		
		public ISelection getSelection() {
			return currentSelection;
		}
		
		public void addSelectionChangedListener(ISelectionChangedListener listener) {
			listeners.add(listener);
		}
	}
}