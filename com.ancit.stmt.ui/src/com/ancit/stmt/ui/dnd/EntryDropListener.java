package com.ancit.stmt.ui.dnd;

import java.beans.Statement;
import java.util.Iterator;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import com.ancit.stmt.model.Entry;
import com.ancit.stmt.model.Group;
import com.ancit.stmt.model.StatementManager;
import com.ancit.stmt.ui.views.StatementView;


public class EntryDropListener extends ViewerDropAdapter  {

	private Viewer viewer;

	public EntryDropListener(Viewer viewer) {
		super(viewer);
		// TODO Auto-generated constructor stub
		this.viewer = viewer;
	}

	public boolean performDrop(Object data) {
		ISelection selection = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getSelectionService().getSelection();
		
		Object[] entries = ((IStructuredSelection)selection).toArray();
		for (Object object : entries) {
			Entry firstElement = (Entry)object;
			System.out.println(firstElement.getDescription());
			
			Group group = (Group)getCurrentTarget();
			boolean result = group.addEntry(firstElement);
			if(result) {
				
				firstElement.setGroup(group);					
				
				for (Group grp : StatementManager.getInstance().getStatement().getGroup()) {
					int x=0,y=0;
					boolean flag = false;
					if(grp.getEntries().size()>0){
						y=0;
						flag = false;
						for (Entry entr : grp.getEntries()) {
							
							if(entr.getSno()==firstElement.getSno()&&!grp.getName().equals(group.getName())){								
								flag = true;
								y = x;
							}
							x++;
						}
						if(flag){
							grp.getEntries().remove(y);
						}
					}
				}
				
				viewer.refresh();
				
			} else {
				MessageDialog.openError(Display.getDefault().getActiveShell(), "Duplicate Entry", "Entry Already Exists");
			}
			
		}
		return true;
		
	}

	public boolean validateDrop(Object target, int operation,
			TransferData transferType) {
		return true;
	}


}
