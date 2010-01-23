package org.art_core.dev.cinder;

import org.art_core.dev.cinder.model.ItemType;
import org.art_core.dev.cinder.model.PropertiesItem;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;

public final class CinderTools {
	private CinderTools() {
	}

	public static ItemType chooseType(final String type) {
		ItemType itemtype;

		if ("warning".equals(type)) {
			itemtype = ItemType.TASK_WARN;
		} else if ("error".equals(type)) {
			itemtype = ItemType.TASK_ERROR;
		} else if ("info".equals(type)) {
			itemtype = ItemType.TASK_INFO;
		} else {
			itemtype = PropertiesItem.DEFAULT_TYPE;
		}

		return itemtype;
	}

	public static IFile getResource(final String sFile) {
		IFile res = null;
		final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		final IProject[] projects = root.getProjects();
		String sProjName = "";

		try {
			CinderLog.logDebug("CT_GR_start");
			
			for (int i = 0; i < projects.length; i++) {
				sProjName = projects[i].getName();
				CinderLog.logDebug("CT_GR:DBG: " + sProjName);
				res = (IFile) root.findMember(sProjName + "/" + sFile);
				if (res == null) {
					CinderLog.logDebug("CT_GR_notfound:NULL");
					continue;
				} else {
					CinderLog.logDebug("CT_GR___found:" + res.toString());
					break;
				}
			}
		} catch (Exception e) {
			CinderLog.logErrorInfo("CT_GR:E:" + sFile, e);
		}
		return res;
	}
}
