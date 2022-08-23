package com.ancit.stmt.ui.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.ancit.stmt.model.Entry;
import com.ancit.stmt.model.Group;
import com.ancit.stmt.model.StatementManager;
import org.eclipse.swt.widgets.Text;

public class EntrySplitDialog extends Dialog {
	private class TableLabelProvider extends LabelProvider implements ITableLabelProvider {
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}
		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof Entry) {
				Entry entry = (Entry) element;
				switch (columnIndex) {
				case 0:
					return Integer.toString(entry.getSno());
				case 1:
					return entry.getGroup() != null ? entry.getGroup().getName() : "";
				case 2:
					return Double.toString(entry.getAmount());
				default:
					break;
				}
			}
			return element.toString();
		}
	}
	private Table table;
	private Entry mainEntry;
	private ArrayList<Entry> subEntries;
	private Text text;
	private Text text_1;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public EntrySplitDialog(Shell parentShell, Entry mainEntry) {
		super(parentShell);
		this.mainEntry = mainEntry;
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout gridLayout = (GridLayout) container.getLayout();
		gridLayout.numColumns = 2;
		
		Composite composite_1 = new Composite(container, SWT.NONE);
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		composite_1.setLayout(new GridLayout(2, false));
		
		Label lblTotalAmountTo = new Label(composite_1, SWT.NONE);
		GridData gd_lblTotalAmountTo = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblTotalAmountTo.widthHint = 135;
		lblTotalAmountTo.setLayoutData(gd_lblTotalAmountTo);
		lblTotalAmountTo.setText("Total Amount to Split:");
		
		text = new Text(composite_1, SWT.BORDER);
		text.setText(Double.toString(mainEntry.getAmount()));
		GridData gd_text = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_text.widthHint = 121;
		text.setLayoutData(gd_text);
		new Label(container, SWT.NONE);
	
		
		final TableViewer tableViewer = new TableViewer(container, SWT.BORDER | SWT.FULL_SELECTION);
		tableViewer.setUseHashlookup(true);
		tableViewer.setColumnProperties(new String[] {"s no", "ledger", "amount"});
		tableViewer.setContentProvider(ArrayContentProvider.getInstance());
		table = tableViewer.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		TableColumn tblclmnSlNo = new TableColumn(table, SWT.NONE);
		tblclmnSlNo.setWidth(50);
		tblclmnSlNo.setText("S No");
		
		TableColumn tblclmnLedger = new TableColumn(table, SWT.NONE);
		tblclmnLedger.setWidth(173);
		tblclmnLedger.setText("Ledger");
		
		TableColumn tblclmnAmount = new TableColumn(table, SWT.NONE);
		tblclmnAmount.setWidth(195);
		tblclmnAmount.setText("Amount");
		
		CellEditor[] cellEditors = new CellEditor[3];
		List<Group> groups = StatementManager.getInstance().getStatement().getGroup();
		final List<String> groupNames = new ArrayList<String>();
		for (Group group : groups) {
			groupNames.add(group.getName());
		}
		cellEditors[0] = new TextCellEditor(table);
		cellEditors[1] = new ComboBoxCellEditor(table, groupNames.toArray(new String[groupNames.size()]));
		cellEditors[2] = new TextCellEditor(table);
		tableViewer.setCellEditors(cellEditors);
		
		tableViewer.setCellModifier(new ICellModifier() {
			
			@Override
			public void modify(Object element, String property, Object value) {
				Entry entry = ((TableItem)element).getData() != null ? (Entry)((TableItem)element).getData():null;
				if(entry != null) {
				if(property.equals("s no"))	{
					if(value.toString().isEmpty()) {
						return;
					}
					entry.setSno(Integer.parseInt(value.toString()));
					
				}else if (property.equals("ledger")) {
					List<Group> groupList = StatementManager.getInstance()
							.getStatement().getGroup();
					for (Group group : groupList) {
						if (groupNames.indexOf(group.getName())== Integer.parseInt(value.toString())) {
							entry.setGroup(group);
						}
					}
				} else if (property.equals("amount")) {
					if(value.toString().isEmpty()) {
						return;
					}
					entry.setAmount(Double.parseDouble(value.toString()));
				}
				}
				updateButtons();
				tableViewer.refresh();
			}
			
			@Override
			public Object getValue(Object element, String property) {
				Entry entry = (Entry)element;
				if(property.equals("s no")){
					return Integer.toString(entry.getSno());
				}else if(property.equals("ledger")) {
					if(entry.getGroup() == null) {
						return 1;
					}
					int position = groupNames.indexOf(entry.getGroup().getName());
					return position > -1 ? position : 1;
				} else if (property.equals("amount")) {
					return Double.toString(entry.getAmount());
				}
				return null;
			}
			
			@Override
			public boolean canModify(Object element, String property) {
				// TODO Auto-generated method stub
				return true;
			}
		});
		tableViewer.setLabelProvider(new TableLabelProvider());
		
		Composite composite = new Composite(container, SWT.NONE);
		composite.setLayout(new RowLayout(SWT.VERTICAL));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
		
		Button btnAdd = new Button(composite, SWT.NONE);
		btnAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Entry entry = new Entry();
				
				entry.setChequeNo(mainEntry.getChequeNo());
				entry.setDescription(mainEntry.getDescription());
				entry.setDate(mainEntry.getDate());
				entry.setType(mainEntry.getType());
				((ArrayList)tableViewer.getInput()).add(entry);
				tableViewer.refresh();
				updateButtons();
			}
		});
		btnAdd.setText("+");
		
		Button btnX = new Button(composite, SWT.NONE);
		btnX.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Object selectedEntry = ((IStructuredSelection)tableViewer.getSelection()).getFirstElement();
				((ArrayList)tableViewer.getInput()).remove(selectedEntry);
				tableViewer.refresh();
				updateButtons();
			}
		});
		btnX.setText("X");
		
		Composite composite_2 = new Composite(container, SWT.NONE);
		composite_2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		composite_2.setLayout(new GridLayout(2, false));
		
		Label lblRemainingAmountTo = new Label(composite_2, SWT.NONE);
		lblRemainingAmountTo.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblRemainingAmountTo.setText("Remaining Amount to Assign:");
		
		text_1 = new Text(composite_2, SWT.BORDER);
		
		GridData gd_text_1 = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_text_1.widthHint = 112;
		text_1.setLayoutData(gd_text_1);
		new Label(container, SWT.NONE);
		
		subEntries = new ArrayList<Entry>();
		tableViewer.setInput(subEntries);
		
		return container;
	}

	protected void updateButtons() {
		float sum = 0;
		float balance=0;
		for (Entry entry : subEntries) {
			sum += entry.getAmount();
			balance=(float) (mainEntry.getAmount()-sum);
			text_1.setText(Float.toString(balance));
		}
		if(sum == mainEntry.getAmount()) {
			getButton(OK).setEnabled(true);
		}
		
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true).setEnabled(false);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(450, 300);
	}
	
	public ArrayList<Entry> getSubEntries() {
		return subEntries;
	}
	
	@Override
	protected void okPressed() {

		for (Entry entry : subEntries) {
			entry.getGroup().getEntries().add(entry);			
		}
		super.okPressed();
	}
}
