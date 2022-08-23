package com.ancit.stmt.ui.dialogs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import com.ancit.stmt.model.Group;
import com.ancit.stmt.model.Rule;
import com.ancit.stmt.model.RuleList;
import com.ancit.stmt.model.StatementManager;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ListDialog;

public class NewRuleDialog extends Dialog {
	private static class ViewerLabelProvider extends LabelProvider {
		public Image getImage(Object element) {
			return super.getImage(element);
		}

		public String getText(Object element) {
			if (element instanceof Rule) {
				Rule rule = (Rule) element;
				return rule.getName();
			}
			return super.getText(element);
		}
	}

	private static class ContentProvider implements IStructuredContentProvider {
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof RuleList) {
				RuleList ruleList = (RuleList) inputElement;
				if (ruleList.getRules() != null) {
					return ruleList.getRules().toArray();
				}
			}
			return new Object[0];
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}

	private Text text;
	private Text text_1;
	private Text text_2;
	private StatementManager statementManager;
	private ComboViewer comboViewer;
	private Rule rule;

	public NewRuleDialog(Shell parentShell) {
		super(parentShell);
		setBlockOnOpen(false);
		setShellStyle(SWT.CLOSE | SWT.RESIZE);
	}
	
	public void setRuleToShow(Rule newRule) {
		this.rule = newRule;
		// TODO Auto-generated method stub
		
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(4, false));
		

		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
				false));
		lblNewLabel.setText("Rule Name");

		comboViewer = new ComboViewer(container, SWT.NONE);
		Combo combo = comboViewer.getCombo();
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		comboViewer.setLabelProvider(new ViewerLabelProvider());
		comboViewer.setContentProvider(new ContentProvider());
		statementManager = StatementManager.getInstance();
		comboViewer.setInput(statementManager.getRuleList());

		comboViewer
				.addSelectionChangedListener(new ISelectionChangedListener() {

					public void selectionChanged(SelectionChangedEvent event) {
						IStructuredSelection selection = (IStructuredSelection) event
								.getSelection();
						Object firstElement = selection.getFirstElement();
						if (firstElement instanceof Rule) {
							rule = (Rule) firstElement;
							if (rule.getDescription() != null)
								text.setText(rule.getDescription());
							if (rule.getCategoryName() != null)
								text_1.setText(rule.getCategoryName());
							if (rule.getMatchString() != null)
								text_2.setText(rule.getMatchString());
						}
					}
				});

		Button addButton = new Button(container, SWT.NONE);
		addButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		addButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				InputDialog iDialog = new InputDialog(Display.getDefault()
						.getActiveShell(), "Create Rule", "Enter rule name",
						"cheque Rule", null);
				
				if (iDialog.open() == IDialogConstants.OK_ID) {
					rule = new Rule();
					rule.setName(iDialog.getValue());
					text.setText("");
					text_1.setText("");
					text_2.setText("");
					statementManager.getRuleList().getRules().add(rule);
				}
				comboViewer.refresh();
			}
		});
		addButton.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ADD));

		Button deleteButton = new Button(container, SWT.NONE);
		deleteButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		deleteButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection iSelection = (IStructuredSelection) comboViewer
						.getSelection();
				Object firstElement = iSelection.getFirstElement();
				if (firstElement instanceof Rule) {
					Rule rule = (Rule) firstElement;
					statementManager.getRuleList().getRules().remove(rule);
					text.setText("");
					text_1.setText("");
					text_2.setText("");
					comboViewer.refresh();
				}
			}
		});
		deleteButton.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_ETOOL_DELETE));

		Label lblRuleDescription = new Label(container, SWT.NONE);
		lblRuleDescription.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				false, false));
		lblRuleDescription.setText("Rule Description");

		text = new Text(container, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		Label lblCategoryName = new Label(container, SWT.NONE);
		lblCategoryName.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				false, false));
		lblCategoryName.setText("Category Name");

		text_1 = new Text(container, SWT.BORDER);
		text_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		Button btnCategoryname = new Button(container, SWT.NONE);
		btnCategoryname.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ListDialog lDialog = new ListDialog(Display.getDefault().getActiveShell());
				lDialog.setContentProvider(ArrayContentProvider.getInstance());
				lDialog.setLabelProvider(new LabelProvider(){
					@Override
					public String getText(Object element) {
						if (element instanceof Group) {
							Group group = (Group) element;
							return group.getName();
						}
						return super.getText(element);
					}
				});
				
				lDialog.setInput(StatementManager.getInstance().getStatement().getGroup());
				if(IDialogConstants.OK_ID == lDialog.open()) {
					Object[] result = lDialog.getResult();
					Group group = (Group) result[0];
					text_1.setText(group.getName());
				}
				
				
			}
		});
		btnCategoryname.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_ELCL_SYNCED));
		new Label(container, SWT.NONE);

		Label lblMatchString = new Label(container, SWT.NONE);
		lblMatchString.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
				false));
		lblMatchString.setText("Match String");

		text_2 = new Text(container, SWT.BORDER);
		text_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		// TODO Auto-generated method stubd
		
		if(rule != null) {
			comboViewer.setSelection(new StructuredSelection(rule));
		}
		

		return container;
	}

	@Override
	protected void okPressed() {
		// TODO Auto-generated method stub
		if (rule != null) {
			rule.setCategoryName(text_1.getText());
			rule.setDescription(text.getText());
			rule.setMatchString(text_2.getText());
		}
		super.okPressed();
	}

	public Rule getRule() {
		return rule;
	}
	protected Point getInitialSize() {
		return new Point(417, 235);
	}
}
