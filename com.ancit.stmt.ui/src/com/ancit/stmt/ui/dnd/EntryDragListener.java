package com.ancit.stmt.ui.dnd;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;

import com.ancit.stmt.model.Entry;


public final class EntryDragListener implements DragSourceListener{
	
	private TableViewer viewer;

	public EntryDragListener(TableViewer viewer) {
		this.viewer = viewer;
		// TODO Auto-generated constructor stub
	}

	public void dragStart(DragSourceEvent event) {
		// TODO Auto-generated method stub
		
	}

	public void dragSetData(DragSourceEvent event) {
		// Here you do the convertion to the type which is expected.
	    IStructuredSelection selection = (IStructuredSelection) viewer
	    .getSelection();
	    Entry firstElement = (Entry) selection.getFirstElement();
	    
	    if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
	      event.data = firstElement.getDescription(); 
	    }
		
	}

	public void dragFinished(DragSourceEvent event) {
		viewer.refresh();
		
	}
	
}