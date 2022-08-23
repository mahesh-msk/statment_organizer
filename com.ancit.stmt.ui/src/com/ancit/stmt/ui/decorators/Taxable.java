package com.ancit.stmt.ui.decorators;

import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class Taxable implements ILightweightLabelDecorator {

	@Override
	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void decorate(Object element, IDecoration decoration) {
		decoration.addOverlay(AbstractUIPlugin.imageDescriptorFromPlugin("com.ancit.stmt.ui", "icons/Taxable.jpg"),IDecoration.TOP_RIGHT);
	}

}
