package com.ancit.stmt.ui.filters;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.ancit.stmt.model.Entry;

public class GroupFilter extends ViewerFilter {

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		// TODO Auto-generated method stub
		if (element instanceof Entry) {
			Entry entry = (Entry) element;
			if (entry.getGroup() == null) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

}
