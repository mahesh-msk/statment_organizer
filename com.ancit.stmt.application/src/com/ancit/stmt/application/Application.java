package com.ancit.stmt.application;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

/**
 * This class controls all aspects of the application's execution
 */
public class Application implements IApplication {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.
	 * IApplicationContext)
	 */
	public Object start(IApplicationContext context) throws Exception {
		Display display = PlatformUI.createDisplay();
		try {

//			String licenseKey = Activator.getDefault().getPreferenceStore()
//					.getString("LICENSE-KEY");
//			if (licenseKey == null || licenseKey.isEmpty()) {
//				DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
//				Date date = new Date();
//				InputDialog inputDialog = new InputDialog(Display.getDefault()
//						.getActiveShell(), "License Key",
//						"Enter your System Mac ID:", "EF-EF-EF-EF-EF-EF", null);
//				if (inputDialog.open() == IDialogConstants.OK_ID) {
//					Activator
//							.getDefault()
//							.getPreferenceStore()
//							.setValue(
//									"LICENSE-KEY",
//									inputDialog.getValue() + "/"
//											+ dateFormat.format(date));
//				}
//			} else {
//				InetAddress ip = InetAddress.getLocalHost();
//
//				NetworkInterface network = NetworkInterface
//						.getByInetAddress(ip);
//
//				byte[] mac = network.getHardwareAddress();
//
//				StringBuilder sb = new StringBuilder();
//				for (int i = 0; i < mac.length; i++) {
//					sb.append(String.format("%02X%s", mac[i],
//							(i < mac.length - 1) ? "-" : ""));
//				}
//				
//				String[] split = licenseKey.split("/");
//				String date = split[1].toString();
//				if(sb.toString().equals(split[0])) {
//				SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
//				Date dateformat = formatter.parse(date);
//
//				DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
//				Date current = new Date();
//				String parse = dateFormat.format(current);
//				Date datefo = formatter.parse(parse);
//
//				int diffInDays = (int) ((datefo.getTime() - dateformat
//						.getTime()) / (1000 * 60 * 60 * 24));
//
//				if (diffInDays >= 30) {
//					MessageDialog.openWarning(display.getDefault()
//							.getActiveShell(), "Licence Expired",
//							"Your trial peroid has expired");
//					return IApplication.EXIT_OK;
//				} else {
//					int days = 30 - diffInDays;
//					MessageDialog.openWarning(display.getDefault()
//							.getActiveShell(), "Licence Expired",
//							"Your have only" + " " + days + " " + "left");
//				}
//				} else {
//					MessageDialog.openError(Display.getDefault().getActiveShell(), "Invalid Installation", "This Installation is intended to work on Machine with MAC ID"+licenseKey);
//					return IApplication.EXIT_OK;
//				}
//			}
			int returnCode = PlatformUI.createAndRunWorkbench(display,
					new ApplicationWorkbenchAdvisor());
			if (returnCode == PlatformUI.RETURN_RESTART)
				return IApplication.EXIT_RESTART;
			else
				return IApplication.EXIT_OK;
		} finally {
			display.dispose();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.equinox.app.IApplication#stop()
	 */
	public void stop() {
		if (!PlatformUI.isWorkbenchRunning())
			return;
		final IWorkbench workbench = PlatformUI.getWorkbench();
		final Display display = workbench.getDisplay();
		display.syncExec(new Runnable() {
			public void run() {
				if (!display.isDisposed())
					workbench.close();
			}
		});
	}
}
