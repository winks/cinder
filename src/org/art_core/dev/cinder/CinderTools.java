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
			itemtype = ItemType.JAVA_INTERFACE;
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
			CinderLog.logInfo("CT_GR_start");
			
			for (int i = 0; i < projects.length; i++) {
				sProjName = projects[i].getName();
				CinderLog.logInfo("CT_GR:DBG: " + sProjName);
				res = (IFile) root.findMember(sProjName + "/" + sFile);
				if (res == null) {
					CinderLog.logInfo("CT_GR_notfound:NULL");
					continue;
				} else {
					CinderLog.logInfo("CT_GR___found:" + res.toString());
					break;
				}
			}
		} catch (Exception e) {
			CinderLog.logError("CT_GR:E:" + sFile, e);
		}
		return res;
	}
}
