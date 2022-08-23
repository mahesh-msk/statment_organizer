package com.ancit.stmt.ui.views.providers;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import com.ancit.stmt.model.Entry;
import com.ancit.stmt.ui.Activator;
import com.ancit.stmt.ui.views.StatementView;

public class TableLabelProvider extends LabelProvider implements ITableLabelProvider, ITableColorProvider {
	
	/**
	 * @param statementView
	 */
	public TableLabelProvider() {
	
	}
	public Image getColumnImage(Object element, int columnIndex) {
		Entry entry = (Entry)element;
		switch (columnIndex) {
		case 0:
			if(Character.toString(entry.getType()).equalsIgnoreCase("c")) {
				return Activator.getDefault().getImage("save_money.png");
			} else {
				return Activator.getDefault().getImage("money.png");
			}

		default:
			break;
		}
		return null;
	}
	public String getColumnText(Object element, int columnIndex) {
		Entry entry = (Entry)element;
		String result = "";
		switch(columnIndex) {
		case 0:
			result = Integer.toString(entry.getSno());
			break;
		case 1:
			result = new SimpleDateFormat("dd-MM-yyyy").format(entry.getDate());
			break;
		case 2:
			result = entry.getChequeNo();
			break;
		case 3:
			result = Character.toString(entry.getType());
			break;
		case 4:
			DecimalFormat df = new DecimalFormat("#.##");
			result = df.format(entry.getAmount());
			break;
		case 5:
			result = entry.getDescription();
			break;
		default:
		}
		return result;
	}
	public Color getForeground(Object element, int columnIndex) {
		// TODO Auto-generated method stub
		return null;
	}
	public Color getBackground(Object element, int columnIndex) {
		if (element instanceof Entry) {
			Entry entry = (Entry) element;
			if(entry.getGroup() != null) {
				return Display.getDefault().getSystemColor(SWT.COLOR_GREEN);
			}
		}
		return null;
	}
}