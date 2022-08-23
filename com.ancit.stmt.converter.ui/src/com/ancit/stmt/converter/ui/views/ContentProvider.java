package com.ancit.stmt.converter.ui.views;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.ancit.stmt.model.EStatement;
import com.ancit.stmt.model.Group;

public class ContentProvider implements IStructuredContentProvider {
	public Object[] getElements(Object inputElement) {
		if(inputElement instanceof EStatement) {
			return ((EStatement)inputElement).getEntries().toArray();
		} else if (inputElement instanceof Group) {
			return ((Group)inputElement).getEntries().toArray();
				
		}
		return new Object[0];
	}
	public void dispose() {
	}
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}
}