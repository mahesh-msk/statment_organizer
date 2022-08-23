package com.ancit.stmt.ui.views;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.SWT;

import com.ancit.stmt.model.EStatement;
import com.ancit.stmt.model.Entry;
import com.ancit.stmt.model.Group;
import com.ancit.stmt.model.GroupTypeEnum;
import com.ancit.stmt.model.StatementManager;
import com.ancit.stmt.ui.dialogs.BankSelectionDialog;
import com.ancit.stmt.ui.dialogs.CreateGroup;
import com.ancit.stmt.ui.dnd.EntryDragListener;
import com.ancit.stmt.ui.dnd.EntryDropListener;
import com.ancit.stmt.ui.dnd.EntryTreeDragListener;
import com.ancit.stmt.ui.views.providers.LedgerLabelProvider;

import org.eclipse.swt.widgets.Label;

public class UserLedgerView extends ViewPart {
	
	private static class TreeContentProvider implements ITreeContentProvider {
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		public void dispose() {
		}

		public Object[] getElements(Object inputElement) {
			return getChildren(inputElement);
		}

		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof Group) {
				Group group = (Group) parentElement;
				return group.getEntries().toArray();
			} else if (parentElement instanceof EStatement) {
				EStatement eStatement = (EStatement) parentElement;
				return eStatement.getGroup().toArray();
			}
			return new Object[0];
		}

		public Object getParent(Object element) {
			return null;
		}

		public boolean hasChildren(Object element) {
			return getChildren(element).length > 0;
		}
	}

	public static final String ID = "com.ancit.stmt.ui.views.UserLedgerView";
	
	private TreeViewer treeViewer;
	private EStatement statement;
	private Action loadFromEStatement;
	private Action saveToEStatement;
	private Action deleteAction;
	private Action createAction;
	private Action filterFilledCategories;

	private Action deleteGroupAction;

	private Action exportToExcel;

	private CategoriesWithEntriesFilter categoriesWithEntriesFilter;

	public UserLedgerView() {
		statement = StatementManager.getInstance().getStatement();
	}

	@Override
	public void createPartControl(Composite parent) {
		
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		{
			treeViewer = new TreeViewer(container, SWT.BORDER | SWT.MULTI);
			Tree tree = treeViewer.getTree();
			tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,1));
			treeViewer.setContentProvider(new TreeContentProvider());
			ILabelDecorator decorator = PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator();
			treeViewer.setLabelProvider(new LedgerLabelProvider());
			
		}

		int operations = DND.DROP_COPY | DND.DROP_MOVE;
		Transfer[] transferTypes = new Transfer[] { TextTransfer.getInstance() };
		treeViewer.addDropSupport(operations, transferTypes,
				new EntryDropListener(treeViewer));		
		treeViewer.setInput(StatementManager.getInstance().getStatement());	
		
		
		int oper = DND.DROP_COPY| DND.DROP_MOVE;
	    Transfer[] transTypes = new Transfer[]{TextTransfer.getInstance()};
	    treeViewer.addDragSupport(oper, transTypes, new EntryTreeDragListener(treeViewer));
		treeViewer.setSorter(new ViewerSorter(){
			
			@Override
			public int category(Object element) {
				
				if(element instanceof Group){
					Group grp = (Group)element;
					GroupTypeEnum groupType = grp.getGroupType();
					if (groupType != null) {
						if (groupType.equals(GroupTypeEnum.INCOME)) {
							return 1;
						} else if (groupType.equals(GroupTypeEnum.EXPENSE)) {
							return 2;
						} else if (groupType.equals(GroupTypeEnum.BANK)) {
							return 3;
						}
					}
				}
				return super.category(element);
			}
			
		});
		getSite().setSelectionProvider(treeViewer);
		
		categoriesWithEntriesFilter =  new CategoriesWithEntriesFilter();
		
		createActions();
		initializeToolBar();
		initializeMenu();
		hookContextMenu();
	}

	private void initializeMenu() {
		IMenuManager menuManager = getViewSite().getActionBars()
				.getMenuManager();		
	}

	private void hookContextMenu() {
		MenuManager manager = new MenuManager();
		manager.setRemoveAllWhenShown(true);
		
		manager.addMenuListener(new IMenuListener() {
			
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				
				Object selectedEntry = ((TreeSelection) treeViewer
						.getSelection()).getFirstElement();
				if (selectedEntry instanceof Entry) {
					manager.add(deleteAction);
				}else if(selectedEntry instanceof Group){
					Group grp = (Group)selectedEntry;
					manager.add(createAction);
					if(grp.getEntries().size()==0){						
						manager.add(deleteGroupAction);						
					}					
				}else{
					manager.add(createAction);
				}
			}
		});
		
		Menu menu = manager.createContextMenu(treeViewer.getTree());
		treeViewer.getTree().setMenu(menu);
	}

	private void initializeToolBar() {
		IToolBarManager toolbarManager = getViewSite().getActionBars()
				.getToolBarManager();
		toolbarManager.add(loadFromEStatement);
		toolbarManager.add(saveToEStatement);	
		toolbarManager.add(filterFilledCategories);
		toolbarManager.add(exportToExcel);
	}

	private void createActions() {
		// Create the actions
				{
					
					ISharedImages sharedImages = PlatformUI.getWorkbench()
							.getSharedImages();
					ImageDescriptor exportImage = sharedImages
							.getImageDescriptor(ISharedImages.IMG_ELCL_SYNCED);
					
					loadFromEStatement = new Action("Load from EStatement") {
						@Override
						public void run() {
							FileDialog fDialog = new FileDialog(getSite().getShell(),
									SWT.OPEN);
							String filePath = fDialog.open();
							if (filePath.trim().length() > 0) {
								StatementManager.getInstance().loadEStatement(filePath);
								treeViewer.setInput(StatementManager.getInstance().getStatement());
							}
						}
					};
					loadFromEStatement.setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_UP));

					saveToEStatement = new Action("Save EStatement") {
						@Override
						public void run() {
							FileDialog fDialog = new FileDialog(getSite().getShell(),
									SWT.SAVE);
							String filePath = fDialog.open();
							if (filePath.trim().length() > 0) {
								StatementManager.getInstance().saveStatement(filePath);
							}
						}
					};
					saveToEStatement.setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_ETOOL_SAVE_EDIT));
					
					deleteAction = new Action("Delete Entry", sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE)) {
						@Override
						public void run() {
							Object selectedEntry = ((TreeSelection)treeViewer.getSelection()).getFirstElement();
							if (selectedEntry instanceof Entry) {
								Entry entry = (Entry) selectedEntry;
								Group group = entry.getGroup();
								group.getEntries().remove(entry);
								entry.setGroup(null);
								treeViewer.refresh(group);
							}
						}
					};
					
					createAction = new Action("Create Group", sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_NEW_WIZARD)) {
						@Override
						public void run() {
							
							CreateGroup grpdia = new CreateGroup(getSite().getShell());
							grpdia.create();
							
													
							if (grpdia.open() == Window.OK) {
								
								boolean chk = true;
								
								List<Group> grps = StatementManager.getInstance().getStatement().getGroup();
								
								for (Group group : grps) {
									if(group.getName().equalsIgnoreCase(grpdia.getGrpname())){
										chk = false;
									}
								}
								
								if(chk && !grpdia.getGrpname().equals("")){
									Group grp = new Group();
									grp.setName(grpdia.getGrpname());
									
									if(grpdia.getGrpcat().equals("Income")){
										grp.setGroupType(GroupTypeEnum.INCOME);
									}else if(grpdia.getGrpcat().equals("Expense")){
										grp.setGroupType(GroupTypeEnum.EXPENSE);
									} else if(grpdia.getGrpcat().equals("Bank")){
										grp.setGroupType(GroupTypeEnum.BANK);
										grp.setBank(true);
										StatementManager.getInstance().getStatement().getBankGroups().add(grp);
									}
									
																	
									StatementManager.getInstance().getStatement().getGroup().add(grp);
									treeViewer.setInput(StatementManager.getInstance().getStatement());
								}else if(grpdia.getGrpname().equals("")){
									MessageDialog
									.openError(PlatformUI.getWorkbench()
									.getActiveWorkbenchWindow()
									.getShell(), "Error",
									"Group Name cannot be empty...!!!");
								}else if(!chk){
									MessageDialog
									.openError(PlatformUI.getWorkbench()
									.getActiveWorkbenchWindow()
									.getShell(), "Error",
									"Group Name already Exist...!!!");
								}
							}
						}
					};
					
					deleteGroupAction = new Action("Delete Group", sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE)) {
						@Override
						public void run() {
							Object selectedGroup = ((TreeSelection)treeViewer.getSelection()).getFirstElement();
							if (selectedGroup instanceof Group) {
								Group grp  = (Group) selectedGroup;								
								
								if (MessageDialog
										.openConfirm(PlatformUI.getWorkbench()
												.getActiveWorkbenchWindow().getShell(),
												"Delete Group",
												"Do you really want to delete this group...???")) {
									StatementManager.getInstance().getStatement().getGroup().remove(grp);								
									treeViewer.setInput(StatementManager.getInstance().getStatement());
								}								
							}
						}
					};
					
					exportToExcel = new Action("Export To Excel", com.ancit.stmt.ui.Activator.imageDescriptorFromPlugin("com.ancit.stmt.ui", "icons/excel.jpg")) {
						@Override
						public void run() {
							
							HSSFWorkbook workbook = new HSSFWorkbook();
							int inc=0,exp=0,bank=0;
							
							List<Group> grps = StatementManager.getInstance().getStatement().getGroup();
							
							List<Group> incgrp = new ArrayList<Group>();
							List<Group> expgrp = new ArrayList<Group>();
							List<Group> bankgrp = new ArrayList<Group>();							
							
							for (Group group : grps) {
								if(group.getGroupType()==GroupTypeEnum.INCOME){
									inc++;
									incgrp.add(group);
								}else if(group.getGroupType()==GroupTypeEnum.EXPENSE){
									exp++;
									expgrp.add(group);
								}else if(group.getGroupType()==GroupTypeEnum.BANK){
									bank++;
									bankgrp.add(group);
								}								
							}
							
							
							Double[] totinc = new Double[incgrp.size()];
							Double[] totexp = new Double[expgrp.size()];
							Double[] totbank = new Double[bankgrp.size()];
							Double totIncAmt = 0.0;
							Double totExpAmt = 0.0;
							Double totBankAmt = 0.0;
							
							for (int i = 0; i < incgrp.size(); i++) {
								double tot = 0.0;
								List<Entry> ent = incgrp.get(i).getEntries();
								
								for (int j = 0; j < ent.size(); j++) {
									if(ent.get(j).getType()=='C'){
										tot += ent.get(j).getAmount();
									}else if(ent.get(j).getType()=='D'){
										tot -= ent.get(j).getAmount();
									}
								}
								totinc[i] = tot;
								totIncAmt+= tot;
							} 
							
							for (int i = 0; i < expgrp.size(); i++) {
								double tot = 0.0;
								List<Entry> ent = expgrp.get(i).getEntries();
								
								for (int j = 0; j < ent.size(); j++) {
									if(ent.get(j).getType()=='C'){
										tot -= ent.get(j).getAmount();
									}else if(ent.get(j).getType()=='D'){
										tot += ent.get(j).getAmount();
									}
								}
								totexp[i]=tot;
								totExpAmt += tot;
							}
							
							for (int i = 0; i < bankgrp.size(); i++) {
								double tot = 0.0;
								List<Entry> ent = bankgrp.get(i).getEntries();
								
								for (int j = 0; j < ent.size(); j++) {
									tot += ent.get(j).getAmount();
								}
								totbank[i]=tot;
								totBankAmt += tot;
							}
							
							HSSFSheet summSheet = workbook.createSheet("Summary");							
							
							HSSFFont fnt = workbook.createFont();
							fnt.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
							fnt.setColor(HSSFColor.BLUE.index);
							fnt.setFontHeightInPoints((short)30);
							HSSFCellStyle stle = workbook.createCellStyle();
							stle.setFont(fnt);							
							int rowcnt = 0;
							
							Row row = summSheet.createRow(rowcnt);rowcnt++;
							Cell col = row.createCell(0);
							col.setCellValue("REPORT-SUMMARY");
							col.setCellStyle(stle);
							
							HSSFFont ft = workbook.createFont();
							ft.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
							HSSFCellStyle sle = workbook.createCellStyle();
							sle.setFont(ft);
							
							row = summSheet.createRow(rowcnt);rowcnt++;
							row = summSheet.createRow(rowcnt);rowcnt++;
							if(!totIncAmt.equals(0.0)){
								row = summSheet.createRow(rowcnt);rowcnt++;
								col = row.createCell(1);
								col.setCellValue("INCOME:");
								col.setCellStyle(sle);
								row = summSheet.createRow(rowcnt);rowcnt++;
								for (int i = 0; i < totinc.length; i++) {
									row = summSheet.createRow(rowcnt);rowcnt++;
									col = row.createCell(2);
									col.setCellValue(incgrp.get(i).getName());									
									col = row.createCell(3);
									col.setCellValue(totinc[i]);
								}
								row = summSheet.createRow(rowcnt);rowcnt++;
								col = row.createCell(2);
								col.setCellValue("TOTAL:");
								col.setCellStyle(sle);
								col = row.createCell(3);
								col.setCellValue(totIncAmt);
							}							

							row = summSheet.createRow(rowcnt);rowcnt++;
							
							if(!totExpAmt.equals(0.0)){
								row = summSheet.createRow(rowcnt);rowcnt++;
								col = row.createCell(1);
								col.setCellValue("EXPENSE:");
								col.setCellStyle(sle);
								row = summSheet.createRow(rowcnt);rowcnt++;
								for (int i = 0; i < totexp.length; i++) {
									row = summSheet.createRow(rowcnt);rowcnt++;
									col = row.createCell(2);
									col.setCellValue(expgrp.get(i).getName());									
									col = row.createCell(3);
									col.setCellValue(totexp[i]);
								}
								row = summSheet.createRow(rowcnt);rowcnt++;
								col = row.createCell(2);
								col.setCellValue("TOTAL:");
								col.setCellStyle(sle);
								col = row.createCell(3);
								col.setCellValue(totExpAmt);
							}

							row = summSheet.createRow(rowcnt);rowcnt++;
							
							if(!totBankAmt.equals(0.0)){
								row = summSheet.createRow(rowcnt);rowcnt++;
								col = row.createCell(1);
								col.setCellValue("BANK/CASH:");
								col.setCellStyle(sle);
								row = summSheet.createRow(rowcnt);rowcnt++;
								for (int i = 0; i < totbank.length; i++) {
									row = summSheet.createRow(rowcnt);rowcnt++;
									col = row.createCell(2);
									col.setCellValue(bankgrp.get(i).getName());									
									col = row.createCell(3);
									col.setCellValue(totbank[i]);
								}
								row = summSheet.createRow(rowcnt);rowcnt++;
								col = row.createCell(2);
								col.setCellValue("BANK/CASH:");
								col.setCellStyle(sle);
								col = row.createCell(3);
								col.setCellValue(totBankAmt);
							}
							
							if(inc>0){
								HSSFSheet incSheet = workbook.createSheet("Income");
								
								HSSFFont font = workbook.createFont();
								font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
								HSSFCellStyle style = workbook.createCellStyle();
								style.setFont(font);								
								int rwcnt = 0,cnt=0;
								
								for (Group group : incgrp) {
								
									if(group.getEntries().size()>0){
										Row rw = incSheet.createRow(rwcnt);rwcnt++;
										Cell cl = rw.createCell(0);
										cl.setCellValue("Group: "+group.getName());
										cl.setCellStyle(style);
										
										rw = incSheet.createRow(rwcnt);rwcnt++;
										
										cl = rw.createCell(0);
										cl.setCellValue("SNo");
										cl.setCellStyle(style);
										
										cl = rw.createCell(1);
										cl.setCellValue("Date");
										cl.setCellStyle(style);
										
										cl = rw.createCell(2);
										cl.setCellValue("ChequeNo");
										cl.setCellStyle(style);
										
										cl = rw.createCell(3);
										cl.setCellValue("Type");
										cl.setCellStyle(style);
										
										cl = rw.createCell(4);
										cl.setCellValue("Amount");
										cl.setCellStyle(style);
										
										cl = rw.createCell(5);
										cl.setCellValue("Description");
										cl.setCellStyle(style);
										
										List<Entry> ent = group.getEntries();
										
										for (Entry entry : ent) {
											rw = incSheet.createRow(rwcnt);rwcnt++;
											
											cl = rw.createCell(0);
											cl.setCellValue(entry.getSno());
											
											cl = rw.createCell(1);
											cl.setCellValue(new SimpleDateFormat("dd-MM-yyyy").format(entry.getDate()));
											
											cl = rw.createCell(2);
											cl.setCellValue(entry.getChequeNo());
											
											if(entry.getType()=='C'){
												cl = rw.createCell(3);
												cl.setCellValue("C");
											}else if(entry.getType()=='D'){
												cl = rw.createCell(3);
												cl.setCellValue("D");
											} 
											
											cl = rw.createCell(4);
											cl.setCellValue(entry.getAmount());
											
											cl = rw.createCell(5);
											cl.setCellValue(entry.getDescription());
										}
										rw = incSheet.createRow(rwcnt);rwcnt++;
										rw = incSheet.createRow(rwcnt);rwcnt++;
										cl = rw.createCell(3);
										cl.setCellValue("Total");
										cl.setCellStyle(style);
										cl = rw.createCell(4);
										cl.setCellValue(totinc[cnt]);cnt++;
										rw = incSheet.createRow(rwcnt);rwcnt++;
									}
								}
								Row rw = incSheet.createRow(rwcnt);rwcnt++;
								rw = incSheet.createRow(rwcnt);rwcnt++;
								rw = incSheet.createRow(rwcnt);rwcnt++;
								Cell cl = rw.createCell(0);
								cl.setCellValue("Total Income: ");
								cl.setCellStyle(style);
								cl = rw.createCell(1);
								cl.setCellValue(totIncAmt);
								rw = incSheet.createRow(rwcnt);rwcnt++;
							}
							
							if (exp > 0) {
								HSSFSheet expSheet = workbook.createSheet("Expense");
								
								HSSFFont font = workbook.createFont();
								font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
								HSSFCellStyle style = workbook.createCellStyle();
								style.setFont(font);								
								int rwcnt = 0,cnt=0;
								
								for (Group group : expgrp) {
								
									if(group.getEntries().size()>0){
										Row rw = expSheet.createRow(rwcnt);rwcnt++;
										Cell cl = rw.createCell(0);
										cl.setCellValue("Group: "+group.getName());
										cl.setCellStyle(style);
										
										rw = expSheet.createRow(rwcnt);rwcnt++;
										
										cl = rw.createCell(0);
										cl.setCellValue("SNo");
										cl.setCellStyle(style);
										
										cl = rw.createCell(1);
										cl.setCellValue("Date");
										cl.setCellStyle(style);
										
										cl = rw.createCell(2);
										cl.setCellValue("ChequeNo");
										cl.setCellStyle(style);
										
										cl = rw.createCell(3);
										cl.setCellValue("Type");
										cl.setCellStyle(style);
										
										cl = rw.createCell(4);
										cl.setCellValue("Amount");
										cl.setCellStyle(style);
										
										cl = rw.createCell(5);
										cl.setCellValue("Description");
										cl.setCellStyle(style);
										
										List<Entry> ent = group.getEntries();
										
										for (Entry entry : ent) {
											rw = expSheet.createRow(rwcnt);rwcnt++;
											
											cl = rw.createCell(0);
											cl.setCellValue(entry.getSno());
											
											cl = rw.createCell(1);
											cl.setCellValue(new SimpleDateFormat("dd-MM-yyyy").format(entry.getDate()));
											
											cl = rw.createCell(2);
											cl.setCellValue(entry.getChequeNo());
											
											if(entry.getType()=='C'){
												cl = rw.createCell(3);
												cl.setCellValue("C");
											}else if(entry.getType()=='D'){
												cl = rw.createCell(3);
												cl.setCellValue("D");
											} 
											
											cl = rw.createCell(4);
											cl.setCellValue(entry.getAmount());
											
											cl = rw.createCell(5);
											cl.setCellValue(entry.getDescription());
										}
										rw = expSheet.createRow(rwcnt);rwcnt++;
										rw = expSheet.createRow(rwcnt);rwcnt++;
										cl = rw.createCell(3);
										cl.setCellValue("Total");
										cl.setCellStyle(style);
										cl = rw.createCell(4);
										cl.setCellValue(totexp[cnt]);cnt++;
										rw = expSheet.createRow(rwcnt);rwcnt++;																			
									}
								}
								Row rw = expSheet.createRow(rwcnt);rwcnt++;
								rw = expSheet.createRow(rwcnt);rwcnt++;
								rw = expSheet.createRow(rwcnt);rwcnt++;
								Cell cl = rw.createCell(0);
								cl.setCellValue("Total Expense: ");
								cl.setCellStyle(style);
								cl = rw.createCell(1);
								cl.setCellValue(totExpAmt);
								rw = expSheet.createRow(rwcnt);rwcnt++;
							}
							
							if (bank > 0) {

								HSSFSheet bankSheet = workbook.createSheet("BankCash");
								
								HSSFFont font = workbook.createFont();
								font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
								HSSFCellStyle style = workbook.createCellStyle();
								style.setFont(font);								
								int rwcnt = 0,cnt=0;
								
								for (Group group : bankgrp) {
								
									if(group.getEntries().size()>0){
										Row rw = bankSheet.createRow(rwcnt);rwcnt++;
										Cell cl = rw.createCell(0);
										cl.setCellValue("Group: "+group.getName());
										cl.setCellStyle(style);
										
										rw = bankSheet.createRow(rwcnt);rwcnt++;
										
										cl = rw.createCell(0);
										cl.setCellValue("SNo");
										cl.setCellStyle(style);
										
										cl = rw.createCell(1);
										cl.setCellValue("Date");
										cl.setCellStyle(style);
										
										cl = rw.createCell(2);
										cl.setCellValue("ChequeNo");
										cl.setCellStyle(style);
										
										cl = rw.createCell(3);
										cl.setCellValue("Type");
										cl.setCellStyle(style);
										
										cl = rw.createCell(4);
										cl.setCellValue("Amount");
										cl.setCellStyle(style);
										
										cl = rw.createCell(5);
										cl.setCellValue("Description");
										cl.setCellStyle(style);
										
										List<Entry> ent = group.getEntries();
										
										for (Entry entry : ent) {
											rw = bankSheet.createRow(rwcnt);rwcnt++;
											
											cl = rw.createCell(0);
											cl.setCellValue(entry.getSno());
											
											cl = rw.createCell(1);
											cl.setCellValue(new SimpleDateFormat("dd-MM-yyyy").format(entry.getDate()));
											
											cl = rw.createCell(2);
											cl.setCellValue(entry.getChequeNo());
											
											if(entry.getType()=='C'){
												cl = rw.createCell(3);
												cl.setCellValue("C");
											}else if(entry.getType()=='D'){
												cl = rw.createCell(3);
												cl.setCellValue("D");
											} 
											
											cl = rw.createCell(4);
											cl.setCellValue(entry.getAmount());
											
											cl = rw.createCell(5);
											cl.setCellValue(entry.getDescription());
										}
										rw = bankSheet.createRow(rwcnt);rwcnt++;
										rw = bankSheet.createRow(rwcnt);rwcnt++;
										cl = rw.createCell(3);
										cl.setCellValue("Total");
										cl.setCellStyle(style);
										cl = rw.createCell(4);
										cl.setCellValue(totbank[cnt]);cnt++;
										rw = bankSheet.createRow(rwcnt);rwcnt++;
									}
								}
								Row rw = bankSheet.createRow(rwcnt);rwcnt++;
								rw = bankSheet.createRow(rwcnt);rwcnt++;
								rw = bankSheet.createRow(rwcnt);rwcnt++;
								Cell cl = rw.createCell(0);
								cl.setCellValue("Total Bank/Cash: ");
								cl.setCellStyle(style);
								cl = rw.createCell(1);
								cl.setCellValue(totBankAmt);
								rw = bankSheet.createRow(rwcnt);rwcnt++;
							
							}							
							
							try {
								FileDialog fDialog = new FileDialog(getSite().getShell(),
										SWT.SAVE);
								String filePath = fDialog.open();
								if (filePath.trim().length() > 0) {
									FileOutputStream out = new FileOutputStream(new File(filePath+".xls"));
								    workbook.write(out);
								    out.close();
								}								
							     
							} catch (FileNotFoundException e) {
							    e.printStackTrace();
							} catch (IOException e) {
							    e.printStackTrace();
							}
							
							MessageDialog.openInformation(PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow()
							.getShell(), "Export",
							"Ledger succesfully exported to excel...!!!");
							
						}
					}; 
					
					filterFilledCategories = new Action("Show Categories with Entries", SWT.TOGGLE) {
						@Override
						public void run() {
							if(filterFilledCategories.isChecked()) {
								treeViewer.addFilter(categoriesWithEntriesFilter);
							} else {
								treeViewer.removeFilter(categoriesWithEntriesFilter);
							}
						}
					};
					filterFilledCategories.setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_ELCL_COLLAPSEALL));
				}
	}

	@Override
	public void setFocus() {
		

	}
	public TreeViewer getTreeViewer() {
		return treeViewer;
	}
}
