package org.eclipse.e4.tools.orion.text.editor.test;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import junit.framework.TestCase;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.e4.tools.orion.text.editor.OrionEditor;
import org.eclipse.e4.tools.orion.text.editor.handlers.RevertOrionEditorHandler;
import org.eclipse.ui.ISaveablePart;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class RevertOrionEditorHandlerTest extends TestCase {

	private RevertOrionEditorHandler handler;

	@Mock
	private OrionEditor mockEditor;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		MockitoAnnotations.initMocks(this);
		when(mockEditor.isDirty()).thenReturn(false);
		handler = new RevertOrionEditorHandler(mockEditor);
	}

	public void testExecuteCallsOrionEditorRevert() throws ExecutionException {
		handler = new RevertOrionEditorHandler(mockEditor);
		assertNull(handler.execute(null));
		verify(mockEditor).revert();
	}

	public void testIsEnabledReturnsFalseUponConstructionIfOrionEditorIsNotDirty() {
		assertFalse(handler.isEnabled());
	}

	public void testIsEnabledReturnsTrueUponConstructionIfOrionEditorIsDirty() {
		when(mockEditor.isDirty()).thenReturn(true);
		handler = new RevertOrionEditorHandler(mockEditor);
		assertTrue(handler.isEnabled());
	}

	public void testIsEnabledReturnsTrueWhenEditorBecomesDirty() {
		when(mockEditor.isDirty()).thenReturn(true);
		handler.propertyChanged(mockEditor, ISaveablePart.PROP_DIRTY);
		assertTrue(handler.isEnabled());
	}

	public void testIsEnabledReturnsFalseWhenEditorIsNotDirty() {
		when(mockEditor.isDirty()).thenReturn(false);
		handler.propertyChanged(mockEditor, ISaveablePart.PROP_DIRTY);
		assertFalse(handler.isEnabled());
	}

	public void testIsEnabledReturnsSameValueWhenPropertyChangedSourceNotEditor() {
		assertFalse(handler.isEnabled());
		handler.propertyChanged(null, ISaveablePart.PROP_DIRTY);
		assertFalse(handler.isEnabled());

	}

	public void testIsEnabledReturnsSameValueWhenPropertyChangedIdNotDirtyProperty() {
		assertFalse(handler.isEnabled());
		handler.propertyChanged(mockEditor, 0);
		assertFalse(handler.isEnabled());
	}
}
