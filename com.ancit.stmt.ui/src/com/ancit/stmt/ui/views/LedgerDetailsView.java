package com.ancit.stmt.ui.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import com.ancit.stmt.model.Entry;
import com.ancit.stmt.model.Group;
import com.ancit.stmt.model.StatementManager;
import com.ancit.stmt.ui.dialogs.BankSelectionDialog;
import com.ancit.stmt.ui.dialogs.NewVoucherDialog;
import com.ancit.stmt.ui.views.providers.ContentProvider;
import com.ancit.stmt.ui.views.providers.TableLabelProvider;

public class LedgerDetailsView extends ViewPart implements ISelectionListener {

	public static final String ID = "com.ancit.stmt.ui.views.LedgerDetailsView"; //$NON-NLS-1$
	private Table table;
	private TableViewer tableViewer;
	private Action toTallyAction;
	private Action addVoucherAction;
	private Action exportToTallyAction;
	private Action entryDeleteAction;
	private Action editVoucherAction;

	public LedgerDetailsView() {
	}

	/**
	 * Create contents of the view part.
	 * 
	 * @param parent
	 */
	public void createPartControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		{
			tableViewer = new TableViewer(container, SWT.BORDER
					| SWT.FULL_SELECTION);
			table = tableViewer.getTable();
			table.setLinesVisible(true);
			table.setHeaderVisible(true);
			table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
					1));
			{
				TableColumn tblclmnSno = new TableColumn(table, SWT.NONE);
				tblclmnSno.setWidth(100);
				tblclmnSno.setText("SNo");

				TableColumn tblclmnDate = new TableColumn(table, SWT.NONE);
				tblclmnDate.setWidth(100);
				tblclmnDate.setText("Date");

				TableColumn tblclmnChequeNo = new TableColumn(table, SWT.NONE);
				tblclmnChequeNo.setWidth(100);
				tblclmnChequeNo.setText("Cheque No");

				TableColumn tblclmnType = new TableColumn(table, SWT.NONE);
				tblclmnType.setWidth(100);
				tblclmnType.setText("Type");

				TableColumn tblclmnAmount = new TableColumn(table, SWT.NONE);
				tblclmnAmount.setWidth(100);
				tblclmnAmount.setText("Amount");

				TableColumn tblclmnDescription = new TableColumn(table,
						SWT.NONE);
				tblclmnDescription.setWidth(100);
				tblclmnDescription.setText("Description");
			}
			tableViewer.setLabelProvider(new TableLabelProvider());
			tableViewer.setContentProvider(new ContentProvider());

			getSite().getPage().addSelectionListener(this);
		}

		createActions();
		initializeToolBar();
		initializeMenu();
		hookContextMenu();
	}

	private void hookContextMenu() {
		// TODO Auto-generated method stub
		MenuManager manager = new MenuManager();
		manager.setRemoveAllWhenShown(true);

		manager.addMenuListener(new IMenuListener() {

			@Override
			public void menuAboutToShow(
					org.eclipse.jface.action.IMenuManager manager) {
				Object selectedEntry = ((IStructuredSelection) tableViewer
						.getSelection()).getFirstElement();
				if (selectedEntry instanceof Entry) {
					manager.add(exportToTallyAction);
					manager.add(entryDeleteAction);
					manager.add(editVoucherAction);
				}
			}
		});

		Menu menu = manager.createContextMenu(tableViewer.getTable());
		table.setMenu(menu);

	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
		{
			toTallyAction = new Action("Export to Tally") {
				@Override
				public void run() {
					Group group = (Group) tableViewer.getInput();
					List<Group> listGroup = new ArrayList<Group>();
					listGroup.add(group);
					BankSelectionDialog dialog = new BankSelectionDialog(
							getSite().getShell());
					int result = dialog.open();
					if (IDialogConstants.OK_ID == result) {
						Group bankGroup = dialog.getGroup();
						String tallyServerUrl = StatementManager.getInstance()
								.getTallyServerUrl();
						if (tallyServerUrl == null) {
							InputDialog iDialog = new InputDialog(Display
									.getDefault().getActiveShell(),
									"Tally Server", "Enter Tally Server URL",
									"server:9000", null);
							if (iDialog.open() == IDialogConstants.OK_ID) {
								StatementManager.getInstance().setTallyServer(
										iDialog.getValue());
								StatementManager.getInstance()
										.exportGroupToTally(bankGroup,
												listGroup);
							}
						} else {
							StatementManager.getInstance().exportGroupToTally(
									bankGroup, listGroup);
						}

					}

				}
			};
			ImageDescriptor exportImage = PlatformUI.getWorkbench()
					.getSharedImages()
					.getImageDescriptor(ISharedImages.IMG_TOOL_FORWARD);
			toTallyAction.setImageDescriptor(exportImage);
		}
		{
			addVoucherAction = new Action("Add Voucher") {
				@Override
				public void run() {
					NewVoucherDialog vDialog = new NewVoucherDialog(Display
							.getDefault().getActiveShell(), null);
					if (vDialog.open() == IDialogConstants.OK_ID) {
						Entry entry = null;
						entry = vDialog.getEntry();
						Group group = (Group) tableViewer.getInput();
						group.addEntry(entry);
						entry.setGroup(group);
						entry.setSno(group.getEntries().size());
						tableViewer.refresh();
					}
				}
			};
			ImageDescriptor addImage = PlatformUI.getWorkbench()
					.getSharedImages()
					.getImageDescriptor(ISharedImages.IMG_OBJ_ADD);
			addVoucherAction.setImageDescriptor(addImage);
		}
		{
			exportToTallyAction = new Action("Export to Tally") {
				@Override
				public void run() {
					Entry entry = (Entry) ((IStructuredSelection) tableViewer
							.getSelection()).getFirstElement();
					Group group = (Group) tableViewer.getInput();
					BankSelectionDialog dialog = new BankSelectionDialog(
							getSite().getShell());
					int result = dialog.open();
					if (IDialogConstants.OK_ID == result) {
						Group bankGroup = dialog.getGroup();
						String tallyServerUrl = StatementManager.getInstance()
								.getTallyServerUrl();
						if (tallyServerUrl == null) {
							InputDialog iDialog = new InputDialog(Display
									.getDefault().getActiveShell(),
									"Tally Server", "Enter Tally Server URL",
									"server:9000", null);
							if (iDialog.open() == IDialogConstants.OK_ID) {
								StatementManager.getInstance().setTallyServer(
										iDialog.getValue());
								StatementManager.getInstance()
										.exportEntryToTally(bankGroup,
												entry,group);
							}
						} else {
							StatementManager.getInstance().exportEntryToTally(
									bankGroup, entry,group);
						}

					}

				}
			};
			ImageDescriptor exportImage = PlatformUI.getWorkbench()
					.getSharedImages()
					.getImageDescriptor(ISharedImages.IMG_TOOL_FORWARD);
			exportToTallyAction.setImageDescriptor(exportImage);
		}
		{
			entryDeleteAction = new Action("Delete Entry") {
				@Override
				public void run() {
					Object selectedEntry = ((IStructuredSelection) tableViewer.getSelection()).getFirstElement();
					if (selectedEntry instanceof Entry) {
						Entry entry = (Entry) selectedEntry;
						Group group = entry.getGroup();
						group.getEntries().remove(entry);
						entry.setGroup(null);
						tableViewer.refresh(group);
					}
				}
			};
			ImageDescriptor addImage = PlatformUI.getWorkbench()
					.getSharedImages()
					.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE);
			entryDeleteAction.setImageDescriptor(addImage);
		}
		{
			editVoucherAction = new Action("Edit Voucher") {
				private NewVoucherDialog vDialog;

				@Override
				public void run() {
					Object selectedEntry = ((IStructuredSelection) tableViewer.getSelection()).getFirstElement();
					if (selectedEntry instanceof Entry) {
						Entry entry = (Entry)selectedEntry; 
						vDialog = new NewVoucherDialog(Display
								.getDefault().getActiveShell(),entry);
						if (vDialog.open() == IDialogConstants.OK_ID) {
							
							Entry entry1 = null;
							entry1 = vDialog.getEntry();
							Group group = (Group) tableViewer.getInput();
							entry1.setGroup(group);
							
							tableViewer.refresh();
						}
					}
				}
			};
			ImageDescriptor addImage = PlatformUI.getWorkbench()
					.getSharedImages()
					.getImageDescriptor(ISharedImages.IMG_OBJ_FILE);
			editVoucherAction.setImageDescriptor(addImage);
		}
	}

	/**
	 * Initialize the toolbar.
	 */
	private void initializeToolBar() {
		IToolBarManager toolbarManager = getViewSite().getActionBars()
				.getToolBarManager();
		toolbarManager.add(addVoucherAction);
		toolbarManager.add(toTallyAction);
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

	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		IStructuredSelection sSelection = (IStructuredSelection) selection;
		if (sSelection.getFirstElement() instanceof Group) {
			Group group = (Group) sSelection.getFirstElement();
			tableViewer.setInput(group);
		}

	}

}
