package org.art_core.dev.cinder.controller;

import java.util.TimerTask;
import org.art_core.dev.cinder.CinderLog;

public class CheckFile extends TimerTask {
	private String sFilename;
	private MainController parent;

	public CheckFile(MainController mc, String sFile) {
		this.parent = mc;
		this.sFilename = sFile;
	}
	
	@Override
	public void run() {
		CinderLog.logInfo("running CheckFile on " + sFilename);
		/*parent.clearAll();
		parent.insertFromFile(sFilename, MainController.FILE_LOCAL);
		parent.hideMarkersAll();
		parent.showMarkersAll();*/
	}

}
