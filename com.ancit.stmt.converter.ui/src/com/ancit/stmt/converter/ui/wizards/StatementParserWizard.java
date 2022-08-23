package com.ancit.stmt.converter.ui.wizards;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

import com.ancit.stmt.loader.parser.IStatementParser;
import com.ancit.stmt.model.StatementManager;

public class StatementParserWizard extends Wizard {

	public StatementParserWizard() {
		setWindowTitle("Statement Organizer");
	}

	@Override
	public void addPages() {
		addPage(new StatementParserWizardPage());
	}

	@Override
	public boolean performFinish() {
		StatementParserWizardPage page = (StatementParserWizardPage)getPage("wizardPage");
		IStatementParser statementParser = page.getStatementParser();
		String filePath = page.getStatementPath();
		
		StatementManager.getInstance().loadStatement(statementParser, filePath);
		
		return true;
	}

}
