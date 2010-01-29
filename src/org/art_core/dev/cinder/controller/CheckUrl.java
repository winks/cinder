package org.art_core.dev.cinder.controller;

import java.util.TimerTask;
import org.art_core.dev.cinder.CinderLog;

public class CheckUrl extends TimerTask {
	private MainController parent;
	private String sFilename;

	public CheckUrl(MainController mc, String sFile) {
		this.parent = mc;
		this.sFilename = sFile;
	}
	

	@Override
	public void run() {
		CinderLog.logInfo("running CheckUrl on " + sFilename);
		/*parent.clearAll();
		parent.insertFromFile(sFilename, MainController.FILE_REMOTE);
		parent.hideMarkersAll();
		parent.showMarkersAll();*/
	}

}
