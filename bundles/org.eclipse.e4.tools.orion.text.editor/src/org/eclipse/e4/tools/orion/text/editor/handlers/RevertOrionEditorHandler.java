package org.eclipse.e4.tools.orion.text.editor.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.e4.tools.orion.text.editor.OrionEditor;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.ISaveablePart;

public class RevertOrionEditorHandler extends AbstractHandler implements
		IPropertyListener {

	private OrionEditor editor;

	public RevertOrionEditorHandler(OrionEditor editor) {
		this.editor = editor;
		setBaseEnabled(editor.isDirty());
		editor.addPropertyListener(this);
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		editor.revert();
		return null;
	}

	@Override
	public void propertyChanged(Object source, int propId) {
		if (editor.equals(source) && propId == ISaveablePart.PROP_DIRTY) {
			setBaseEnabled(editor.isDirty());
		}
	}
}
