package com.ancit.stmt.ui.perspective;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class StatementReconciliationPerspective implements IPerspectiveFactory {

	@Override
	public void createInitialLayout(IPageLayout layout) {
		// TODO Auto-generated method stub
		layout.setFixed(true);
		layout.setEditorAreaVisible(false);
		String editorArea = layout.getEditorArea();

		// layout.addView("com.ancit.stmt.ui.views.LedgerView",
		// IPageLayout.LEFT, 0.3f, IPageLayout.ID_EDITOR_AREA);
		layout.addView("com.ancit.stmt.ui.views.StatementView",
				IPageLayout.RIGHT, 0.29f, editorArea);
		layout.addView("com.ancit.stmt.ui.views.TallyStatementView", IPageLayout.BOTTOM, 0.5f, "com.ancit.stmt.ui.views.StatementView");

	}

}
