package com.ancit.stmt.ui.action;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

import com.ancit.stmt.model.StatementManager;

public class ConnectToTally implements IWorkbenchWindowActionDelegate {

	@Override
	public void run(IAction action) {
		// TODO Auto-generated method stub
		InputDialog iDialog = new InputDialog(Display.getDefault()
				.getActiveShell(), "Tally Server",
				"Enter Tally Server URL", "server:9000", null);
		if (iDialog.open() == IDialogConstants.OK_ID) {
			StatementManager.getInstance().setTallyServer(
					iDialog.getValue());
			StatementManager.getInstance().getCompanies();

		}
		ISharedImages sharedImages = PlatformUI.getWorkbench()
				.getSharedImages();
		ImageDescriptor exportImage = sharedImages
				.getImageDescriptor(ISharedImages.IMG_ELCL_SYNCED);
		action.setImageDescriptor(exportImage);
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(IWorkbenchWindow window) {
		// TODO Auto-generated method stub

	}

}
