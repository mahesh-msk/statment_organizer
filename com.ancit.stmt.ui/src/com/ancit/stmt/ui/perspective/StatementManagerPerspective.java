package com.ancit.stmt.ui.perspective;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IFolderLayout;

public class StatementManagerPerspective implements IPerspectiveFactory {

	/**
	 * Creates the initial layout for a page.
	 */
	public void createInitialLayout(IPageLayout layout) {
		layout.setFixed(true);
		layout.setEditorAreaVisible(false);
		String editorArea = layout.getEditorArea();
		addFastViews(layout);
		addViewShortcuts(layout);
		addPerspectiveShortcuts(layout);
		{
			IFolderLayout folderLayout = layout.createFolder("folder", IPageLayout.LEFT, 0.3f, IPageLayout.ID_EDITOR_AREA);
			folderLayout.addView("com.ancit.stmt.ui.views.LedgerView");
		}
		
		layout.addView("com.ancit.stmt.ui.views.GraphView", IPageLayout.BOTTOM, 0.5f, "com.ancit.stmt.ui.views.LedgerView");
		layout.addView("com.ancit.stmt.ui.views.StatementView", IPageLayout.RIGHT, 0.29f, "com.ancit.stmt.ui.views.UserLedgerView");
		layout.addView("com.ancit.stmt.ui.views.LedgerDetailsView", IPageLayout.BOTTOM,  0.5f, "com.ancit.stmt.ui.views.StatementView");
	}

	/**
	 * Add fast views to the perspective.
	 */
	private void addFastViews(IPageLayout layout) {
	}

	/**
	 * Add view shortcuts to the perspective.
	 */
	private void addViewShortcuts(IPageLayout layout) {
	}

	/**
	 * Add perspective shortcuts to the perspective.
	 */
	private void addPerspectiveShortcuts(IPageLayout layout) {
	}

}
