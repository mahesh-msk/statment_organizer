package com.ancit.stmt.ui.views;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.ancit.stmt.model.Group;

public class CategoriesWithEntriesFilter extends ViewerFilter {

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (element instanceof Group) {
			Group group = (Group) element;
			if(group.getEntries().size() == 0) {
				return false;
			}
		}
		return true;
	}

}
