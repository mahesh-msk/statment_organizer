package com.ancit.stmt.ui.filters;

import java.util.List;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.ancit.stmt.model.EStatement;
import com.ancit.stmt.model.Entry;
import com.ancit.stmt.model.StatementManager;

public class DebitFilter extends ViewerFilter {

	public DebitFilter() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		// TODO Auto-generated method stub

		if (element instanceof Entry) {
			Entry entry = (Entry) element;
			if (entry.getType()=='D') {
				return true;
			} else {
				return false;
			}
			
		}
		return false;
	}

}
