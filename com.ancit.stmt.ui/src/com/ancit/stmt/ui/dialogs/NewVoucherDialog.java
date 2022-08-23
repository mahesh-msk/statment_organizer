package com.ancit.stmt.ui.dialogs;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.ancit.stmt.model.Entry;

public class NewVoucherDialog extends Dialog {
	private Text txtDescription;
	private Text txtAmount;
	private Text txtChequeno;
	private DateTime dateTime;
	private Combo combo;
	private Entry entry;
	private Entry entryObject;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 * @param entryObject
	 * @param entry2
	 */
	public NewVoucherDialog(Shell parentShell, Entry entryObject) {
		super(parentShell);
		this.entryObject = entryObject;
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(4, false));

		Label lblDate = new Label(container, SWT.NONE);
		lblDate.setText("Date");

		dateTime = new DateTime(container, SWT.BORDER);

		Label lblType = new Label(container, SWT.NONE);
		lblType.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
				1, 1));
		lblType.setText("Type");

		combo = new Combo(container, SWT.NONE);
		combo.setItems(new String[] { "D", "C" });
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));

		Label lblDescription = new Label(container, SWT.NONE);
		lblDescription.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblDescription.setText("Description");

		txtDescription = new Text(container, SWT.BORDER);
		txtDescription.setText("description");
		txtDescription.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 3, 1));

		Label lblAmount = new Label(container, SWT.NONE);
		lblAmount.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblAmount.setText("Amount");

		txtAmount = new Text(container, SWT.BORDER);
		txtAmount.setText("amount");
		txtAmount.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));

		Label lblChequeNo = new Label(container, SWT.NONE);
		lblChequeNo.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblChequeNo.setText("Cheque No");

		txtChequeno = new Text(container, SWT.BORDER);
		txtChequeno.setText("chequeNo");
		txtChequeno.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		if (entryObject != null) {
			txtAmount.setText(Double.toString(entryObject.getAmount()));
			int year = entryObject.getDate().getYear() - 100;
			int month = entryObject.getDate().getMonth();
			int date = entryObject.getDate().getDate();
			dateTime.setDate(Integer.parseInt("20" + year), month, date);
			txtDescription.setText(entryObject.getDescription());
			if (entryObject.getChequeNo() != null) {
				txtChequeno.setText(entryObject.getChequeNo());
			}
			combo.setText(Character.toString(entryObject.getType()));
		}

		return container;
	}

	/**
	 * Create contents of the button bar.
	 * 
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

		try {
			if (entryObject != null) {
				entryObject.setAmount(Double.parseDouble(txtAmount.getText()));
				entryObject.setDate(new SimpleDateFormat("dd-MM-yyyy")
						.parse(dateTime.getDay() + "-"
								+ (dateTime.getMonth() + 1) + "-"
								+ dateTime.getYear()));
				entryObject.setDescription(txtDescription.getText());
				entryObject.setChequeNo(txtChequeno.getText());
				entryObject.setType(combo.getText().charAt(0));
			} else {
				entry = new Entry();
				entry.setAmount(Double.parseDouble(txtAmount.getText()));
				entry.setDate(new SimpleDateFormat("dd-MM-yyyy").parse(dateTime
						.getDay()
						+ "-"
						+ (dateTime.getMonth() + 1)
						+ "-"
						+ dateTime.getYear()));
				entry.setDescription(txtDescription.getText());
				entry.setChequeNo(txtChequeno.getText());
				entry.setType(combo.getText().charAt(0));
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.okPressed();
	}

	public Entry getEntry() {
		if (entryObject != null) {
			return entryObject;
		} else {
			return entry;
		}
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(450, 194);
	}

}
