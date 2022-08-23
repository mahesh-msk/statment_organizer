package com.ancit.stmt.converter.ui.dialogs;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;

import com.ancit.stmt.exporter.extnpt.IStatementExporter;
import com.ancit.stmt.loader.parser.IStatementParser;
import com.ancit.stmt.model.StatementManager;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class StatementExportDialog extends TitleAreaDialog {
	private Text txtFilepath;
	private HashMap<String, IStatementExporter> statementExporterMap = new HashMap<String, IStatementExporter>();
	private Combo cmbType;
	

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public StatementExportDialog(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setMessage("Export the Statement to desired format");
		setTitle("Statement Export");
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(3, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label lblTypeToExport = new Label(container, SWT.NONE);
		lblTypeToExport.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblTypeToExport.setText("Type to Export");
		
		cmbType = new Combo(container, SWT.NONE);
		cmbType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(container, SWT.NONE);
		
		IConfigurationElement[] configurationElements = Platform.getExtensionRegistry().getConfigurationElementsFor("com.ancit.stmt.exporter.exporttype");
		for (IConfigurationElement iConfigurationElement : configurationElements) {
			String exportType = iConfigurationElement.getAttribute("type");
			cmbType.add(exportType);
			
			IStatementExporter statementParser;
			try {
				statementParser = (IStatementExporter)iConfigurationElement.createExecutableExtension("class");
				statementExporterMap.put(exportType, statementParser);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		
		Label lblFilePath = new Label(container, SWT.NONE);
		lblFilePath.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblFilePath.setText("File Path");
		
		txtFilepath = new Text(container, SWT.BORDER);
		txtFilepath.setText("filePath");
		txtFilepath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button btnBrowse = new Button(container, SWT.NONE);
		btnBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fDialog = new FileDialog(Display.getDefault().getActiveShell(), SWT.SAVE);
				String text = cmbType.getText();
				fDialog.setFilterExtensions(new String[]{"*."+text.toLowerCase()});
				txtFilepath.setText(fDialog.open());
			}
		});
		btnBrowse.setText("...");

		return area;
	}
	
	@Override
	protected void okPressed() {
		IStatementExporter iStatementExporter = statementExporterMap.get(cmbType.getText());
		String export = iStatementExporter.export(StatementManager.getInstance().getStatement());
		
		System.out.println(cmbType.getText());
		if(txtFilepath.getText().contentEquals("filePath"))
		{
			
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Error", "Select Export Type & File Type");
		}
		else
		{
			if(cmbType.getText().isEmpty())
			{
				MessageDialog.openError(Display.getDefault().getActiveShell(), "Error", "Select Export Type & File Type");
			}
			else
			{
		try {
			System.out.println("hi2");
			
			Writer w = new OutputStreamWriter(
					   new FileOutputStream(txtFilepath.getText()), "UTF-8");
			//FileWriter fileWriter = new FileWriter(txtFilepath.getText());
			System.out.println("w"+w);
			w.append(export);
			w.flush();
			w.close();
//			fileWriter.append(export);
//			fileWriter.flush();
//			fileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		}
		
		
		super.okPressed();
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(450, 300);
	}

}
