package org.art_core.dev.cinder.controller;

import java.util.ResourceBundle;
import java.util.TimerTask;
import org.art_core.dev.cinder.CinderLog;
import org.eclipse.swt.widgets.Display;

public class CheckFilesTask extends TimerTask {
	private MainController parent;
	private String sFilename;
	private int iFileLocation;

	public CheckFilesTask(MainController mc, String sFile, int iFileLocation) {
		this.parent = mc;
		this.sFilename = sFile;
		this.iFileLocation = iFileLocation;
		
	}
	
	@Override
	public void run() {
		CinderLog.logDebug("running CheckFilesTask on " + sFilename);
		
		Display display = Display.getCurrent();
		if (display == null) {
			display = Display.getDefault();
		}
			
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				parent.insertFromFile(sFilename, iFileLocation);
				parent.hideMarkersAll();
				parent.showMarkersAll();
			}
		});		
	}

}
