package com.ancit.stmt.ui.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.SharedImages;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.themes.IColorFactory;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.util.Rotation;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.jfree.experimental.swt.*;

import com.ancit.stmt.model.Activator;
import com.ancit.stmt.model.Entry;
import com.ancit.stmt.model.Group;
import com.ancit.stmt.model.GroupTypeEnum;
import com.ancit.stmt.model.StatementManager;

import org.eclipse.swt.SWT;

public class GraphView extends ViewPart  {

	private Action refreshAction;
	private JFreeChart chart;
	private PieDataset dataset;
	private ChartComposite cht;
	private Action expensesAction;
	private Action incomeAction;

	private int flag;
	private Action incExpAction;
	
	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public GraphView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		
		cht = new ChartComposite(parent, SWT.NONE, chart, true);

		createAction();
		initializetoolbar();
	}

	private void initializetoolbar() {
		IToolBarManager toolbarManager = getViewSite().getActionBars()
				.getToolBarManager();
		toolbarManager.add(refreshAction);
		toolbarManager.add(incomeAction);
		toolbarManager.add(expensesAction);
		toolbarManager.add(incExpAction);
	}

	private void createAction() {
		refreshAction = new Action("REF") {
			@Override
			public void run() {
				if(getFlag()==1){
					dataset = createExpenseDataset();
					chart = createChart(dataset);
					chart.fireChartChanged();
					cht.setChart(chart);
					cht.forceRedraw();
					setFlag(1);
				}else if(getFlag()==2){
					dataset = createIncomeDataset();
					chart = createChart(dataset);
					chart.fireChartChanged();
					cht.setChart(chart);
					cht.forceRedraw();
					setFlag(2);
				}else if(getFlag()==3){
					dataset = createIncExpDataset();
					chart = createChart(dataset);
					chart.fireChartChanged();
					cht.setChart(chart);
					cht.forceRedraw();
					setFlag(3);
				}
			}
		};
		
		refreshAction.setImageDescriptor(com.ancit.stmt.ui.Activator.imageDescriptorFromPlugin("com.ancit.stmt.ui", "icons/refresh.jpg"));
		
		expensesAction = new Action("EXP") {
			@Override
			public void run() {
				dataset = createExpenseDataset();
				chart = createChart(dataset);
				chart.fireChartChanged();
				cht.setChart(chart);
				cht.forceRedraw();
				setFlag(1);
			}			
		};
		
		expensesAction.setImageDescriptor(com.ancit.stmt.ui.Activator.imageDescriptorFromPlugin("com.ancit.stmt.ui", "icons/coins.jpg"));
		
		
		incomeAction = new Action("INC") {
			@Override
			public void run() {
				dataset = createIncomeDataset();
				chart = createChart(dataset);
				chart.fireChartChanged();
				cht.setChart(chart);
				cht.forceRedraw();
				setFlag(2);
			}			
		};
		incomeAction.setImageDescriptor(com.ancit.stmt.ui.Activator.imageDescriptorFromPlugin("com.ancit.stmt.ui", "icons/points.png"));
		
		incExpAction = new Action("DIF") {
			@Override
			public void run() {
				dataset = createIncExpDataset();
				chart = createChart(dataset);
				chart.fireChartChanged();
				cht.setChart(chart);
				cht.forceRedraw();
				setFlag(3);
			}		
		};
		
		incExpAction.setImageDescriptor(com.ancit.stmt.ui.Activator.imageDescriptorFromPlugin("com.ancit.stmt.ui", "icons/diff.jpg"));
	}
	
	private PieDataset createIncomeDataset() {
		DefaultPieDataset result = new DefaultPieDataset();
		List<Group> grps = StatementManager.getInstance().getStatement().getGroup();
		
		ArrayList<Group> incgrps = new ArrayList<Group>();
		
		for (Group group : grps) {
			if(group.getGroupType()==GroupTypeEnum.INCOME){
				incgrps.add(group);
			}
		}
		
		String[] grpnames = new String[incgrps.size()];
		Double[] totamt = new Double[incgrps.size()];
		
		for (int i = 0; i < incgrps.size(); i++) {
			double tot = 0.0;
			List<Entry> ent = incgrps.get(i).getEntries();
			
			for (int j = 0; j < ent.size(); j++) {
				if(ent.get(j).getType()=='C'){
					tot += ent.get(j).getAmount();
				}else if(ent.get(j).getType()=='D'){
					tot -= ent.get(j).getAmount();
				}
			}
			grpnames[i] = incgrps.get(i).getName();
			totamt[i] = tot;
		} 
		
		for (int i = 0; i < incgrps.size(); i++) {
			if(incgrps.get(i).getEntries().size()>0){
				System.out.println(grpnames[i]+":"+ totamt[i]);
				result.setValue(grpnames[i], totamt[i]);
			}
		}
		
		return result;
	}
	
	private PieDataset createExpenseDataset() {
		DefaultPieDataset result = new DefaultPieDataset();
		List<Group> grps = StatementManager.getInstance().getStatement().getGroup();
		
		ArrayList<Group> expgrps = new ArrayList<Group>();
		
		for (Group group : grps) {
			if(group.getGroupType()==GroupTypeEnum.EXPENSE){
				expgrps.add(group);
			}
		}
		
		String[] grpnames = new String[expgrps.size()];
		Double[] totamt = new Double[expgrps.size()];
		
		for (int i = 0; i < expgrps.size(); i++) {
			double tot = 0.0;
			List<Entry> ent = expgrps.get(i).getEntries();
			
			for (int j = 0; j < ent.size(); j++) {
				if(ent.get(j).getType()=='C'){
					tot -= ent.get(j).getAmount();
				}else if(ent.get(j).getType()=='D'){
					tot += ent.get(j).getAmount();
				}
			}
			grpnames[i] = expgrps.get(i).getName();
			totamt[i] = tot;
		} 
		
		for (int i = 0; i < expgrps.size(); i++) {
			if(expgrps.get(i).getEntries().size()>0){
				System.out.println(grpnames[i]+":"+ totamt[i]);
				result.setValue(grpnames[i], totamt[i]);
			}
		}
		
		return result;
	}
	
	private PieDataset createIncExpDataset() {
		DefaultPieDataset result = new DefaultPieDataset();
		List<Group> grps = StatementManager.getInstance().getStatement().getGroup();
		
		ArrayList<Group> incgrps = new ArrayList<Group>();
		ArrayList<Group> expgrps = new ArrayList<Group>();
		
		for (Group group : grps) {
			if(group.getGroupType()==GroupTypeEnum.INCOME){
				incgrps.add(group);
			}else if(group.getGroupType()==GroupTypeEnum.EXPENSE){
				expgrps.add(group);
			}
		}
		
		Double totIncAmt = 0.0;
		Double totExpAmt = 0.0;
		
		for (int i = 0; i < incgrps.size(); i++) {
			double tot = 0.0;
			List<Entry> ent = incgrps.get(i).getEntries();
			
			for (int j = 0; j < ent.size(); j++) {
				if(ent.get(j).getType()=='C'){
					tot += ent.get(j).getAmount();
				}else if(ent.get(j).getType()=='D'){
					tot -= ent.get(j).getAmount();
				}
			}
			totIncAmt+= tot;
		} 
		
		for (int i = 0; i < expgrps.size(); i++) {
			double tot = 0.0;
			List<Entry> ent = expgrps.get(i).getEntries();
			
			for (int j = 0; j < ent.size(); j++) {
				if(ent.get(j).getType()=='C'){
					tot -= ent.get(j).getAmount();
				}else if(ent.get(j).getType()=='D'){
					tot += ent.get(j).getAmount();
				}
			}
			totExpAmt += tot;
		}
		
		result.setValue("Total Income", totIncAmt);
		result.setValue("Total Expense", totExpAmt);
		
		return result;
	}	
	
	private JFreeChart createChart(PieDataset dataset) {
		
		JFreeChart chart = ChartFactory.createPieChart("", dataset, true, true, false);
		
		PiePlot plot = (PiePlot) chart.getPlot();
		
		plot.setStartAngle(290);
		plot.setDirection(Rotation.CLOCKWISE);
		plot.setForegroundAlpha(0.5f);
		
		return chart;
	}
	
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}
}
