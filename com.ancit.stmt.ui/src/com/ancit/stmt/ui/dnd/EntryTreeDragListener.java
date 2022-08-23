package com.ancit.stmt.ui.dnd;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;

import com.ancit.stmt.model.Entry;

public class EntryTreeDragListener implements DragSourceListener {

	private TreeViewer viewer;
	private Entry ele;
	
	
	public EntryTreeDragListener(TreeViewer treeViewer) {
		this.viewer = treeViewer;
	}

	@Override
	public void dragStart(DragSourceEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dragSetData(DragSourceEvent event) {
		IStructuredSelection sel = (IStructuredSelection)viewer.getSelection();
		
		ele = (Entry) sel.getFirstElement();
		
		if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
		      event.data = ele.getDescription(); 
		}
	}

	@Override
	public void dragFinished(DragSourceEvent event) {
		

	}

}
