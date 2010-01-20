package org.art_core.dev.cinder.views;

import java.util.ArrayList;
import java.util.Collection;

import org.art_core.dev.cinder.CinderLog;
import org.art_core.dev.cinder.CinderTools;
import org.art_core.dev.cinder.input.PropertiesInputReader;
import org.art_core.dev.cinder.input.XmlInputReader;
import org.art_core.dev.cinder.model.IItem;
import org.art_core.dev.cinder.model.ItemManager;
import org.art_core.dev.cinder.model.ItemManagerEvent;
import org.art_core.dev.cinder.model.ItemManagerListener;
import org.art_core.dev.cinder.model.ItemType;
import org.art_core.dev.cinder.model.PropertiesItem;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

/*
 * The content provider class is responsible for
 * providing objects to the view. It can wrap
 * existing objects in adapters or simply return
 * objects as-is. These objects may be sensitive
 * to the current input of the view, or ignore
 * it and always show the same content 
 * (like Task List, for example).
 */

public class JFContentProvider implements IStructuredContentProvider,
		ItemManagerListener {
	private TableViewer viewer;
	private ItemManager manager;
	public static final int FILE_LOCAL = 0;
	public static final int FILE_REMOTE = 1;
	public static final int FILE_WORKSPACE = 2;

	public JFContentProvider() {
		manager = ItemManager.getManager();
	}

	/**
	 * Creates Markers for findings.
	 */
	public void setMarkersGlobal() {
		String sMyLoc;
		IFile res;
		PropertiesItem pItem;
		IMarker marker;

		for (Object oItem : this.getElements(this)) {
			pItem = (PropertiesItem) oItem;
			sMyLoc = pItem.getLocation();
			res = CinderTools.getResource(sMyLoc);

			try {
				marker = res.createMarker(IMarker.TASK);
				marker.setAttribute(IMarker.MESSAGE, pItem.getName() + "("
						+ pItem.getMessage() + "): " + pItem.getLine());
				// marker.setAttribute(IMarker.CHAR_START, 50);
				// marker.setAttribute(IMarker.CHAR_END, 70);
				marker.setAttribute(IMarker.LINE_NUMBER, pItem.getLine());
				marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
				marker.setAttribute("key", pItem.getName());
				marker.setAttribute("violation", pItem.getMessage());
				CinderLog.logInfo("JFIV:MARKER:" + marker.getType());
				CinderLog.logInfo("JFIV:MARKER:"
						+ marker.getAttribute(IMarker.LINE_NUMBER, 666));
			} catch (Exception e) {
				CinderLog.logError(e);
			}
		}
	}

	/**
	 * Removes Markers for findings.
	 */
	public void removeMarkersGlobal() {
		String sMyLoc;
		IFile res;
		PropertiesItem pItem;

		for (Object oItem : this.getElements(this)) {
			pItem = (PropertiesItem) oItem;
			sMyLoc = pItem.getLocation();
			res = CinderTools.getResource(sMyLoc);

			try {
				res.deleteMarkers(null, true, 2);
			} catch (CoreException e) {
				CinderLog.logError(e);
			} catch (Exception e1) {
				CinderLog.logError(e1);
			}
		}
	}

	/**
	 * Removes Markers from a single File.
	 * 
	 * @param pItem
	 */
	public void removeMarkersSingle(final PropertiesItem pItem) {
		// find the correct editor window, based on the name
		final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		final IProject[] projects = root.getProjects();
		IFile res = null;
		String sProjName = "";
		for (int i = 0; i < projects.length; i++) {
			sProjName = projects[i].getName();
			CinderLog.logInfo("JFIV_D:DBG: " + sProjName);
			res = (IFile) root
					.findMember(sProjName + "/" + pItem.getLocation());
			if (res == null) {
				CinderLog.logInfo("JFIV_D_notfound:NULL");
				continue;
			} else {
				CinderLog.logInfo("JFIV_D____found:" + res.toString());
				break;
			}
		}
		CinderLog.logInfo("JFIV_D_start2");

		try {
			res.deleteMarkers(null, true, 2);
		} catch (CoreException e) {
			CinderLog.logError(e);
		}
	}

	public void setMarkersSingle(final PropertiesItem pItem) {
		this.setMarkersGlobal();
	}

	public void clear() {
		manager.reset();
		this.viewer.refresh();
	}
	
	public void insertDummyValues() {
		final String sKey = "CinderDummy";

		Collection<IItem> dummy = new ArrayList<IItem>();

		// this a bogus list for debugging
		dummy.add(new PropertiesItem(sKey));
		dummy.add(new PropertiesItem(sKey, "bar"));
		dummy.add(new PropertiesItem(sKey, "WORKBENCH_FOLDER",
				ItemType.WORKBENCH_FOLDER));
		dummy.add(new PropertiesItem(sKey, "WORKBENCH_PROJECT",
				ItemType.WORKBENCH_PROJECT));
		dummy.add(new PropertiesItem(sKey, "JAVA_CLASS", ItemType.JAVA_CLASS));
		dummy.add(new PropertiesItem(sKey, "JAVA_CLASS_FILE",
				ItemType.JAVA_CLASS_FILE));
		dummy.add(new PropertiesItem(sKey, "JAVA_COMP_UNIT",
				ItemType.JAVA_COMP_UNIT));
		dummy.add(new PropertiesItem(sKey, "JAVA_INTERFACE",
				ItemType.JAVA_INTERFACE));
		dummy.add(new PropertiesItem(sKey, "JAVA_PACKAGE",
				ItemType.JAVA_PACKAGE));
		dummy.add(new PropertiesItem(sKey, "JAVA_PACKAGE_ROOT",
				ItemType.JAVA_PACKAGE_ROOT));
		dummy.add(new PropertiesItem(sKey, "JAVA_PROJECT",
				ItemType.JAVA_PROJECT));
		// end bogus list
		for (IItem item : dummy) {
			manager.add(item);
		}

		// read from properties file
		final PropertiesInputReader pir = new PropertiesInputReader();
		pir.readFromWorkspaceFile("cinder.properties");
		Collection<IItem> coll = pir.getItems();
		for (IItem item : coll) {
			manager.add(item);
		}
		this.viewer.refresh();
	}

	/**
	 * Inserts example values.
	 */
	public void insertExampleValues() {
		// read from XML file
		final XmlInputReader xir = new XmlInputReader();
		xir.readFromWorkspaceFile("cinder.xml");
		Collection<IItem> coll = xir.getItems();
		for (IItem item : coll) {
			manager.add(item);
		}
	}

	/**
	 * Inserts findings from a file
	 * 
	 * @param sFile
	 */
	public void insertFromFile(final String sFile, final int iType) {
		try {
			CinderLog.logInfo("JFCP_IFF:" + sFile);
			final XmlInputReader xir = new XmlInputReader();

			switch (iType) {
			case FILE_LOCAL:
				xir.readFromLocalFile(sFile);
				break;
			case FILE_WORKSPACE:
				xir.readFromWorkspaceFile(sFile);
				break;
			default:
				xir.readFromUri(sFile);
				break;
			}

			final Collection<IItem> coll = xir.getItems();
			CinderLog.logInfo("JFCP_IFF:" + coll.size());

			for (IItem item : coll) {
				manager.add(item);
			}
			this.viewer.refresh();
		} catch (Exception e) {
			CinderLog.logError(e);
		}
	}

	@Override
	public void inputChanged(final Viewer vViewer, final Object oldInput,
			final Object newInput) {
		this.viewer = (TableViewer) vViewer;
		if (manager != null) {
			manager.removeItemManagerListener(this);
		}
		manager = (ItemManager) newInput;
		if (manager != null) {
			manager.addItemManagerListener(this);
		}
		this.viewer.refresh();
	}

	/**
	 * Disposes of.
	 */
	@Override
	public void dispose() {
	}

	/**
	 * Returns all elements of the manager.
	 */
	@Override
	public Object[] getElements(final Object parent) {
		return manager.getItems();
	}

	@Override
	public void itemsChanged(final ItemManagerEvent event) {
		viewer.getTable().setRedraw(false);
		try {
			viewer.remove(event.getItemsRemoved());
			viewer.add(event.getItemsAdded());
		} finally {
			viewer.refresh();
			viewer.getTable().setRedraw(true);
			viewer.refresh();
		}
	}
}