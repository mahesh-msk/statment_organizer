package com.ancit.stmt.ui.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;

import com.ancit.stmt.model.EStatement;
import com.ancit.stmt.model.Entry;
import com.ancit.stmt.model.Group;
import com.ancit.stmt.model.StatementManager;
import com.ancit.stmt.ui.Activator;
import com.ancit.stmt.ui.dnd.EntryDragListener;
import com.ancit.stmt.ui.filters.CreditFilter;
import com.ancit.stmt.ui.filters.DebitFilter;
import com.ancit.stmt.ui.filters.GroupFilter;
import com.ancit.stmt.ui.views.providers.ContentProvider;
import com.ancit.stmt.ui.views.providers.TableLabelProvider;

public class TallyStatementView extends ViewPart {

	private static class ViewerLabelProvider extends LabelProvider {
		public Image getImage(Object element) {
			return super.getImage(element);
		}
		public String getText(Object element) {
			if (element instanceof Group) {
				Group group = (Group) element;
				return group.getName();
			}
			return super.getText(element);
		}
	}
	public TallyStatementView() {
		// TODO Auto-generated constructor stub
	}

	public static final String ID = "com.ancit.stmt.ui.views.TallyStatementView"; //$NON-NLS-1$
	private Table table;
	private TableViewer tableViewer;
	private Action splitEntryAction;
	private Action debitFilterAction;
	private DebitFilter debitFilter;
	private Action creditFilterAction;
	private CreditFilter creditFilter;
	private Action nonGroupFilterAction;
	private GroupFilter groupFilter;
	private Action connectToTallyAction;
	private ComboViewer comboViewer;
	private ComboViewer comboViewer_1;

	/**
	 * Create contents of the view part.
	 * 
	 * @param parent
	 */
	public void createPartControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(1, false));

		Composite composite = new Composite(container, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		
		Label lblGroups = new Label(composite, SWT.NONE);
		lblGroups.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblGroups.setText("Groups");
		
		comboViewer_1 = new ComboViewer(composite, SWT.NONE);
		Combo combo_1 = comboViewer_1.getCombo();
		combo_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		comboViewer_1.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer_1.setLabelProvider(new ViewerLabelProvider());
		comboViewer_1.setInput(StatementManager.getInstance().getStatement().getBankGroups());

		// comboViewer.setInput(StatementManager.getInstance().getCompanies());
		comboViewer_1
				.addSelectionChangedListener(new ISelectionChangedListener() {

					public void selectionChanged(SelectionChangedEvent event) {
						IStructuredSelection selection = (IStructuredSelection) event
								.getSelection();
						Object firstElement = selection.getFirstElement();
						if (firstElement instanceof Group) {
							Group group = (Group) firstElement;
							tableViewer.setInput(group);
						}
					}
				});
	
		

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
		tblclmnDate.setWidth(78);
		tblclmnDate.setText("Date");
		
		TableColumn tblclmnBankDate = new TableColumn(table, SWT.NONE);
		tblclmnBankDate.setWidth(100);
		tblclmnBankDate.setText("Bank Date");

		TableColumn tblclmnChequeNo = new TableColumn(table, SWT.NONE);
		tblclmnChequeNo.setWidth(100);
		tblclmnChequeNo.setText("Cheque No");

		TableColumn tblclmnType = new TableColumn(table, SWT.NONE);
		tblclmnType.setWidth(40);
		tblclmnType.setText("Type");

		TableColumn tblclmnAmount = new TableColumn(table, SWT.NONE);
		tblclmnAmount.setWidth(70);
		tblclmnAmount.setText("Amount");

		TableColumn tblclmnDescription = new TableColumn(table, SWT.NONE);
		tblclmnDescription.setWidth(600);
		tblclmnDescription.setText("Description");

		tableViewer.setLabelProvider(new TableLabelProvider());
		tableViewer.setContentProvider(new ContentProvider());
		debitFilter = new DebitFilter();
		creditFilter = new CreditFilter();
		groupFilter = new GroupFilter();

		int operations = DND.DROP_COPY | DND.DROP_MOVE;
		Transfer[] transferTypes = new Transfer[] { TextTransfer.getInstance() };
		tableViewer.addDragSupport(operations, transferTypes,
				new EntryDragListener(tableViewer));
		getSite().setSelectionProvider(tableViewer);
		createActions();
		initializeToolBar();
		initializeMenu();
		hookContextMenu();
	}

	private void hookContextMenu() {
		MenuManager mgr = new MenuManager();
		mgr.setRemoveAllWhenShown(true);
		mgr.addMenuListener(new IMenuListener() {

			@Override
			public void menuAboutToShow(
					org.eclipse.jface.action.IMenuManager manager) {
				Object selectedEntry = ((IStructuredSelection) tableViewer
						.getSelection()).getFirstElement();
				if (selectedEntry instanceof Entry) {
					manager.add(splitEntryAction);
					manager.add(debitFilterAction);
					manager.add(creditFilterAction);
				}
			}

		});

		Menu menu = mgr.createContextMenu(tableViewer.getTable());
		table.setMenu(menu);

	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		
		debitFilterAction = new Action("Debit", SWT.TOGGLE) {
			@Override
			public void run() {
				if (debitFilterAction.isChecked()) {
					tableViewer.addFilter(debitFilter);
					tableViewer.removeFilter(creditFilter);
					creditFilterAction.setChecked(false);
				} else {
					tableViewer.removeFilter(debitFilter);
				}

			}
		};
		creditFilterAction = new Action("Credit", SWT.TOGGLE) {
			@Override
			public void run() {
				if (creditFilterAction.isChecked()) {
					tableViewer.addFilter(creditFilter);
					tableViewer.removeFilter(debitFilter);
					debitFilterAction.setChecked(false);
				} else {
					tableViewer.removeFilter(creditFilter);
				}

			}
		};

		nonGroupFilterAction = new Action("Nongroup Statements", SWT.TOGGLE) {
			@Override
			public void run() {
				if (nonGroupFilterAction.isChecked()) {
					tableViewer.addFilter(groupFilter);
					tableViewer.removeFilter(debitFilter);
					debitFilterAction.setChecked(false);
					tableViewer.removeFilter(creditFilter);
					creditFilterAction.setChecked(false);
				} else {
					tableViewer.removeFilter(groupFilter);
				}

			}
		};
		
	
		ImageDescriptor imageDescriptorFromPlugin = Activator
				.imageDescriptorFromPlugin("com.ancit.stmt.ui",
						"icons/shape_ungroup.png");
		nonGroupFilterAction.setImageDescriptor(imageDescriptorFromPlugin);
	
	}

	/**
	 * Initialize the toolbar.
	 */
	private void initializeToolBar() {
		IToolBarManager toolbarManager = getViewSite().getActionBars()
				.getToolBarManager();
		toolbarManager.add(nonGroupFilterAction);
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
