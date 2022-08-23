package com.ancit.stmt.converter.ui.views;

import java.io.File;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.SharedImages;
import org.eclipse.ui.part.ViewPart;

import com.ancit.stmt.converter.ui.dialogs.StatementExportDialog;
import com.ancit.stmt.converter.ui.wizards.StatementParserWizard;
import com.ancit.stmt.model.StatementManager;
import org.eclipse.jface.action.Action;

public class StatementView extends ViewPart {
	public static final String ID = "com.ancit.stmt.ui.views.StatementView"; //$NON-NLS-1$
	private Table table;
	private TableViewer tableViewer;
	private File file;
	private Action loadAction;
	private Action exportAction;


	public StatementView() {
	}

	/**
	 * Create contents of the view part.
	 * 
	 * @param parent
	 */

	public void createPartControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(1, false));


		tableViewer = new TableViewer(container, SWT.BORDER
				| SWT.FULL_SELECTION | SWT.MULTI);
		table = tableViewer.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		TableColumn tblclmnSno = new TableColumn(table, SWT.NONE);
		tblclmnSno.setWidth(40);
		tblclmnSno.setText("SNo");

		TableColumn tblclmnDate = new TableColumn(table, SWT.NONE);
		tblclmnDate.setWidth(100);
		tblclmnDate.setText("Date");

		TableColumn tblclmnChequeNo = new TableColumn(table, SWT.NONE);
		tblclmnChequeNo.setWidth(100);
		tblclmnChequeNo.setText("Cheque No");

		TableColumn tblclmnAmount = new TableColumn(table, SWT.NONE);
		tblclmnAmount.setWidth(70);
		tblclmnAmount.setText("Amount");

		TableColumn tblclmnDescription = new TableColumn(table, SWT.NONE);
		tblclmnDescription.setWidth(600);
		tblclmnDescription.setText("Description");

		tableViewer.setLabelProvider(new TableLabelProvider());
		tableViewer.setContentProvider(new ContentProvider());
		

		
		getSite().setSelectionProvider(tableViewer);
		createActions();
		initializeToolBar();
		initializeMenu();
		hookContextMenu();

		//Responsible for Loading the Rules
		loadRuleFile();

	}

	private void loadRuleFile() {
		System.out.println(Platform.getInstallLocation().getURL().getPath());
		file = new File(Platform.getInstallLocation().getURL().getPath()
				+ "rule.rl");
		if(file.exists()) {
			StatementManager.getInstance().loadRuleList(
					file.getAbsolutePath());
		}
	}

	private void hookContextMenu() {
		  MenuManager mgr = new MenuManager();
		  mgr.setRemoveAllWhenShown(true);
		  mgr.addMenuListener(new IMenuListener() {

		   @Override
		   public void menuAboutToShow(
		     org.eclipse.jface.action.IMenuManager manager) {
			   
		   }

		  });

		  Menu menu = mgr.createContextMenu(tableViewer.getTable());
		  table.setMenu(menu);
		 }

	/**
	 * Create the actions.
	 */
	private void createActions() {
		
		ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
		
		{
			loadAction = new Action("Load") {
				@Override
				public void run() {
					StatementParserWizard statementParserWizard = new StatementParserWizard();
					WizardDialog dialog = new WizardDialog(Display.getDefault()
							.getActiveShell(), statementParserWizard);
					if (IDialogConstants.OK_ID == dialog.open()) {
						tableViewer.setInput(StatementManager.getInstance()
								.getStatement());
					}
				}
			};
			
			loadAction.setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_OBJ_ADD));
		}
		{
			exportAction = new Action("Export") {
				
				@Override
				public void run() {
					StatementExportDialog dialog = new StatementExportDialog(Display.getDefault().getActiveShell());
					dialog.open();
				}
				
			};
			exportAction.setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_ETOOL_SAVE_EDIT));
		}
	}

	/**
	 * Initialize the toolbar.
	 */
	private void initializeToolBar() {
		IToolBarManager toolbarManager = getViewSite().getActionBars()
				.getToolBarManager();
		toolbarManager.add(loadAction);
		toolbarManager.add(exportAction);
	}

	/**
	 * Initialize the menu.
	 */
	private void initializeMenu() {
		IMenuManager menuManager = getViewSite().getActionBars()
				.getMenuManager();
	}

	public void setFocus() {
		// Set the focus
	}
}
