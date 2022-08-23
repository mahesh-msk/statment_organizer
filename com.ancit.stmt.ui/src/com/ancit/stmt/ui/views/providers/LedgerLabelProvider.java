package com.ancit.stmt.ui.views.providers;

import java.util.List;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.ancit.stmt.model.Entry;
import com.ancit.stmt.model.Group;
import com.ancit.stmt.model.GroupTypeEnum;
import com.ancit.stmt.model.StatementManager;
import com.ancit.stmt.ui.Activator;

public class LedgerLabelProvider extends LabelProvider implements IColorProvider{
	
	@Override
	public Image getImage(Object element) {
		if (element instanceof Group) {
			Group group = (Group) element;
			if(group.isBank()) {
				return Activator.getDefault().getImage("cash.jpg");
			} else if(group.getGroupType()==GroupTypeEnum.EXPENSE){
				return Activator.getDefault().getImage("coins.jpg");
			}			
			else {
				return Activator.getDefault().getImage("points.png");
			}
			
			
		} else if (element instanceof Entry) {
			Entry entry = (Entry) element;
			if(Character.toString(entry.getType()).equalsIgnoreCase("c")) {
				return Activator.getDefault().getImage("save_money.png");
			} else {
				return Activator.getDefault().getImage("money.png");
			}
		}
		return super.getImage(element);
	}
	
	@Override
	public String getText(Object element) {
		if(element instanceof Entry){
			Entry ent = (Entry)element;
			return ent.getDescription()+"["+ent.getAmount()+"]";
		}
		
		return super.getText(element);
	}

	@Override
	public Color getForeground(Object element) {
		if (element instanceof Group) {
			Group group = (Group) element;
			if(group.getGroupType()==GroupTypeEnum.INCOME){
				List<Entry> entries = group.getEntries();
				for (Entry entry : entries) {
					if(entry.getType()=='D'){
						return Display.getDefault().getSystemColor(SWT.COLOR_DARK_RED);
					}
				}
			}else if(group.getGroupType()==GroupTypeEnum.EXPENSE){
				List<Entry> entries = group.getEntries();
				for (Entry entry : entries) {
					if(entry.getType()=='C'){
						return Display.getDefault().getSystemColor(SWT.COLOR_DARK_RED);
					}
				}
			}
		}
		return null;
	}

	@Override
	public Color getBackground(Object element) {
		// TODO Auto-generated method stub
		return null;
	}
}
