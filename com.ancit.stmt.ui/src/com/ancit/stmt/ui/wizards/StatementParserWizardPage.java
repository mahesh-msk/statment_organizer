package com.ancit.stmt.ui.wizards;

import java.util.HashMap;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;

import com.ancit.stmt.loader.parser.IStatementParser;

public class StatementParserWizardPage extends WizardPage {
	private Text txtStatementpath;
	private HashMap<String, IStatementParser> statementParserMap = new HashMap<String, IStatementParser>();
	private ComboViewer comboViewer;

	/**
	 * Create the wizard.
	 */
	public StatementParserWizardPage() {
		super("wizardPage");
		setTitle("Wizard Page title");
		setDescription("Wizard Page description");
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		container.setLayout(new GridLayout(3, false));
		
		Label lblBankName = new Label(container, SWT.NONE);
		lblBankName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblBankName.setText("Bank Name");
		
		comboViewer = new ComboViewer(container, SWT.NONE);
		Combo combo = comboViewer.getCombo();
		GridData gd_combo = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		gd_combo.widthHint = 480;
		combo.setLayoutData(gd_combo);
		
		
		try {
			IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
			IConfigurationElement[] configurationElementsFor = extensionRegistry.getConfigurationElementsFor("com.ancit.stmt.loader.parser");
			
			for (IConfigurationElement iConfigurationElement : configurationElementsFor) {
				String bankName = iConfigurationElement.getAttribute("bank_name");
				combo.add(bankName);
				IStatementParser statementParser = (IStatementParser)iConfigurationElement.createExecutableExtension("class");
				statementParserMap.put(bankName, statementParser);
			}
		} catch (InvalidRegistryObjectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		Label lblStatementPath = new Label(container, SWT.NONE);
		lblStatementPath.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblStatementPath.setText("Statement Path");
		
		txtStatementpath = new Text(container, SWT.BORDER);
		txtStatementpath.setText("statementPath");
		txtStatementpath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button button = new Button(container, SWT.NONE);
		button.setText("...");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fDialog = new FileDialog(Display.getDefault().getActiveShell());
				txtStatementpath.setText(fDialog.open());
			}
		});
	}
	
	public IStatementParser getStatementParser() {
		return statementParserMap.get(comboViewer.getCombo().getText());
	}
	
	public String getStatementPath() {
		return txtStatementpath.getText();
	}

}
