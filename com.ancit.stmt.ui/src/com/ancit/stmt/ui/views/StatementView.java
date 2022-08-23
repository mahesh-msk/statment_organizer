package com.ancit.stmt.ui.views;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import com.ancit.stmt.model.Entry;
import com.ancit.stmt.model.Group;
import com.ancit.stmt.model.Rule;
import com.ancit.stmt.model.StatementManager;
import com.ancit.stmt.ui.Activator;
import com.ancit.stmt.ui.dialogs.EntrySplitDialog;
import com.ancit.stmt.ui.dialogs.NewRuleDialog;
import com.ancit.stmt.ui.dnd.EntryDragListener;
import com.ancit.stmt.ui.filters.CreditFilter;
import com.ancit.stmt.ui.filters.DebitFilter;
import com.ancit.stmt.ui.filters.GroupFilter;
import com.ancit.stmt.ui.views.providers.ContentProvider;
import com.ancit.stmt.ui.views.providers.TableLabelProvider;
import com.ancit.stmt.ui.wizards.StatementParserWizard;

public class StatementView extends ViewPart {
	public static final String ID = "com.ancit.stmt.ui.views.StatementView"; //$NON-NLS-1$
	private Text txtStmtpath;
	private Table table;
	private TableViewer tableViewer;
	private Action splitEntryAction;
	private Action debitFilterAction;
	private DebitFilter debitFilter;
	private Action creditFilterAction;
	private CreditFilter creditFilter;
	private Action nonGroupFilterAction;
	private GroupFilter groupFilter;
	private Action ruleAction;
	private File file;
	private Action applyRulesAcion;
	private Action ruleCreation;

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

		Composite composite = new Composite(container, SWT.NONE);
		composite.setLayout(new GridLayout(3, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));

		Label lblStatementPath = new Label(composite, SWT.NONE);
		lblStatementPath.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
				false, false, 1, 1));
		lblStatementPath.setText("Statement Path");

		txtStmtpath = new Text(composite, SWT.BORDER);
		txtStmtpath.setText("stmtPath");
		txtStmtpath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		txtStmtpath.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				// StatementManager.getInstance().loadStatement(txtStmtpath.getText());
				tableViewer.setInput(StatementManager.getInstance()
						.getStatement());
			}
		});

		Button btnBrowse = new Button(composite, SWT.NONE);
		btnBrowse.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				// FileDialog fDialog = new
				// FileDialog(Display.getDefault().getActiveShell());
				// txtStmtpath.setText(fDialog.open());
				StatementParserWizard statementParserWizard = new StatementParserWizard();
				WizardDialog dialog = new WizardDialog(Display.getDefault()
						.getActiveShell(), statementParserWizard);
				if (IDialogConstants.OK_ID == dialog.open()) {
					tableViewer.setInput(StatementManager.getInstance()
							.getStatement());
				}
			}
		});
		btnBrowse.setText("...");

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
		
		tableViewer.addFilter(groupFilter);

		int operations = DND.DROP_COPY | DND.DROP_MOVE;
		Transfer[] transferTypes = new Transfer[] { TextTransfer.getInstance() };
		tableViewer.addDragSupport(operations, transferTypes,
				new EntryDragListener(tableViewer));
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
		    Object selectedEntry = ((IStructuredSelection) tableViewer
		      .getSelection()).getFirstElement();
		    List<Group> groups = StatementManager.getInstance().getStatement().getGroup();
		    final List<String> groupNames = new ArrayList<String>();
		    for (Group group : groups) {
		     groupNames.add(group.getName());
		    }
		    if (selectedEntry instanceof Entry) {
		     if(groupNames.size()!=0){
		      manager.add(splitEntryAction);
		      manager.add(ruleCreation);
		     }
		     
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
		splitEntryAction = new Action("Split Entry") {
			@Override
			public void run() {
				Entry selectedElement = (Entry) ((IStructuredSelection) tableViewer
						.getSelection()).getFirstElement();
				EntrySplitDialog dialog = new EntrySplitDialog(Display
						.getDefault().getActiveShell(), selectedElement);
				dialog.open();
			}
		};
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
		nonGroupFilterAction.setChecked(true);
		ImageDescriptor imageDescriptorFromPlugin = Activator
				.imageDescriptorFromPlugin("com.ancit.stmt.ui",
						"icons/shape_ungroup.png");
		nonGroupFilterAction.setImageDescriptor(imageDescriptorFromPlugin);

		ruleAction = new Action("Rule Creation") {
			private NewRuleDialog ruleDialog;

			@Override
			public void run() {
				ruleDialog = new NewRuleDialog(Display.getDefault()
						.getActiveShell());
				if (ruleDialog.open() == IDialogConstants.OK_ID) {
					try {
						FileOutputStream fis = new FileOutputStream(file);
						ObjectOutputStream ois = new ObjectOutputStream(fis);
						ois.writeObject(StatementManager.getInstance()
								.getRuleList());
						ois.close();
						fis.close();
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		ruleAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_UP));

		applyRulesAcion = new Action("Apply Rules") {
			@Override
			public void run() {
				StatementManager.getInstance().autocategorize(StatementManager.getInstance().getRuleList());
				tableViewer.refresh();
			}
		};
		applyRulesAcion.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ETOOL_CLEAR));
		
		
		ruleCreation = new Action("Create Rule") {

			@Override
			public void run() {
				
				IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
				Entry firstElement = (Entry) selection.getFirstElement();
				
				List<Rule> rules = StatementManager.getInstance().getRuleList().getRules();
				
				Rule newRule=new Rule();
				newRule.setName("NewRule"+rules.size()+1);
				newRule.setMatchString(firstElement.getDescription());
				rules.add(newRule);
				
				NewRuleDialog rcDialog = new NewRuleDialog(Display.getDefault().getActiveShell());
				rcDialog.setRuleToShow(newRule);
				if(rcDialog.open()==IDialogConstants.OK_ID){
					try {
						FileOutputStream fis = new FileOutputStream(file);
						ObjectOutputStream ois = new ObjectOutputStream(fis);
						ois.writeObject(StatementManager.getInstance()
								.getRuleList());
						ois.close();
						fis.close();
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
	
			}
		};
	}

	/**
	 * Initialize the toolbar.
	 */
	private void initializeToolBar() {
		IToolBarManager toolbarManager = getViewSite().getActionBars()
				.getToolBarManager();
		toolbarManager.add(nonGroupFilterAction);
		toolbarManager.add(ruleAction);
		toolbarManager.add(applyRulesAcion);
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
