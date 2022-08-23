package com.ancit.stmt.ui.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;

public class CreateGroup extends TitleAreaDialog {
	private Text txtGroupname;

	String grpname;
	String grpcat;
	
	
	private Button btnIncome;

	private Button btnExpense;

	private Button btnBankcashyourMoney;
	
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public CreateGroup(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle("Create New Group");
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(2, false));
		new Label(container, SWT.NONE);
		
		Label lblGroupName = new Label(container, SWT.NONE);
		lblGroupName.setText("Group Name:");
		
		txtGroupname = new Text(container, SWT.BORDER);
		GridData gd_txtGroupname = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_txtGroupname.widthHint = 406;
		txtGroupname.setLayoutData(gd_txtGroupname);
		
		Label lblCategory = new Label(container, SWT.NONE);
		lblCategory.setText("Category:");
		
		btnIncome = new Button(container, SWT.RADIO);
		btnIncome.setText("Income");
		new Label(container, SWT.NONE);
		
		btnExpense = new Button(container, SWT.RADIO);
		btnExpense.setText("Expense");
		new Label(container, SWT.NONE);
		
		btnBankcashyourMoney = new Button(container, SWT.RADIO);
		btnBankcashyourMoney.setText("Bank/Cash (Your Money)");

		return container;
	}

	public String getGrpname() {
		return grpname;
	}

	public String getGrpcat() {
		return grpcat;
	}


	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	@Override
	protected void okPressed() {
		grpname = txtGroupname.getText();
		if(btnIncome.getSelection()){
			grpcat = "Income";
		}else if(btnExpense.getSelection()){
			grpcat = "Expense";
		}else if(btnBankcashyourMoney.getSelection()){
			grpcat = "Bank";
		}

		super.okPressed();
	}
	
	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(450, 300);
	}

}
