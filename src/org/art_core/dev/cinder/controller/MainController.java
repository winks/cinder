package org.art_core.dev.cinder.controller;

import java.util.ArrayList;
import java.util.Collection;

import org.art_core.dev.cinder.CinderLog;
import org.art_core.dev.cinder.CinderTools;
import org.art_core.dev.cinder.input.PropertiesInputReader;
import org.art_core.dev.cinder.input.XmlInputReader;
import org.art_core.dev.cinder.model.IItem;
import org.art_core.dev.cinder.model.PropertiesItem;
import org.art_core.dev.cinder.model.ItemManager;
import org.art_core.dev.cinder.model.ItemType;
import org.art_core.dev.cinder.views.JFInputView;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.ITextEditor;

public class MainController {
	private ItemManager manager;
	private JFInputView cView;
	public static final int FILE_LOCAL = 0;
	public static final int FILE_REMOTE = 1;
	public static final int FILE_WORKSPACE = 2;
	private static final String JAVAEDITORID = "org.eclipse.jdt.ui.CompilationUnitEditor";
	
	public MainController(JFInputView view) {
		this.cView = view;
		//this.viewer = cView.getViewer();
		this.manager = ItemManager.getManager();
	}
	/**
	 * Shows Markers for findings.
	 */
	public void showMarkersAll() {
		String sMyLoc;
		IFile res;
		IItem pItem;
		IMarker marker;
		
		for (Object oItem : manager.getItems()) {
			pItem = (IItem) oItem;
			sMyLoc = pItem.getLocation();
			res = CinderTools.getResource(sMyLoc);
			ItemType type = pItem.getType();

			try {
				switch (type.getPostion()) {
				case 13:
					marker = res.createMarker(IMarker.PROBLEM);
					marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
					break;
				case 12:
					marker = res.createMarker(IMarker.PROBLEM);
					marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
					break;
				case 11:
					marker = res.createMarker(IMarker.PROBLEM);
					marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_INFO);
					break;
				default:
					marker = res.createMarker(IMarker.TEXT);
					marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_INFO);
					break;
				}
				
				marker.setAttribute(IMarker.MESSAGE, pItem.getName() + "("
						+ pItem.getMessage() + "): " + pItem.getLine());
				// marker.setAttribute(IMarker.CHAR_START, 50);
				// marker.setAttribute(IMarker.CHAR_END, 70);
				marker.setAttribute(IMarker.LINE_NUMBER, pItem.getLine());
				
				marker.setAttribute("key", pItem.getName());
				marker.setAttribute("violation", pItem.getMessage());
				
				CinderLog.logDebug("JFIV:MARKER:" + marker.getType());
				CinderLog.logDebug("JFIV:MARKER:"
						+ marker.getAttribute(IMarker.LINE_NUMBER, 666));
			} catch (Exception e) {
				CinderLog.logErrorInfo("showMarkersAll", e);
			}
		}
	}

	/**
	 * Hides Markers for findings.
	 */
	public void hideMarkersAll() {
		String sMyLoc;
		IFile res;
		IItem pItem;

		for (Object oItem : manager.getItems()) {
			pItem = (IItem) oItem;
			sMyLoc = pItem.getLocation();
			res = CinderTools.getResource(sMyLoc);

			try {
				res.deleteMarkers(null, true, 2);
			} catch (Exception e1) {
				CinderLog.logErrorInfo("hideMarkersAll", e1);
			}
		}
	}

	/**
	 * Hides Markers from a single File.
	 * 
	 * @param pItem
	 */
	public void hideMarkersSelected(final IItem pItem) {
		// find the correct editor window, based on the name
		final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		final IProject[] projects = root.getProjects();
		IFile res = null;
		String sProjName = "";
		for (int i = 0; i < projects.length; i++) {
			sProjName = projects[i].getName();
			CinderLog.logDebug("JFIV_D:DBG: " + sProjName);
			res = (IFile) root
					.findMember(sProjName + "/" + pItem.getLocation());
			if (res == null) {
				CinderLog.logDebug("JFIV_D_notfound:NULL");
				continue;
			} else {
				CinderLog.logDebug("JFIV_D____found:" + res.toString());
				break;
			}
		}
		CinderLog.logDebug("JFIV_D_start2");

		try {
			res.deleteMarkers(null, true, 2);
		} catch (CoreException e) {
			CinderLog.logError(e);
		}
	}

	/**
	 * Shows markers for selected items.
	 * @param pItem
	 */
	public void showMarkersSelected(final IItem pItem) {
		this.showMarkersAll();
	}

	/**
	 * Removes all items from the manager.
	 */
	public void clearAll() {
		manager.reset();
		cView.getViewer().refresh();
	}
	
	/**
	 * Removes selected items from the manager.
	 */
	public void clearSelected() {
		
	}
	
	/**
	 * Shows a selection in the editor.
	 */
	public void select() {
		// select the clicked item from the view
		final ISelection selection = cView.getViewer().getSelection();
		final IItem pItem = (IItem) ((IStructuredSelection) selection)
				.getFirstElement();
		if (pItem == null) {
			return;
		}

		final IFile res = CinderTools.getResource(pItem.getLocation());
		AbstractTextEditor editor = null;
		FileEditorInput fileInput;

		try {
			fileInput = new FileEditorInput(res);
			editor = (AbstractTextEditor) PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage().openEditor(
							fileInput, JAVAEDITORID);
			// convert line numbers to offset numbers (eclipse internal)
			final IEditorInput input = editor.getEditorInput();
			final IDocument doc = ((ITextEditor) editor).getDocumentProvider()
					.getDocument(input);

			int iLineOffset = -1;
			int iLineLength = -1;
			int iOff = pItem.getOffset();
			int iLen = 5;

			iLineOffset = doc.getLineOffset(pItem.getLine() - 1);
			iLineLength = doc.getLineLength(pItem.getLine() - 1);
			CinderLog.logDebug("JFIV:LineOff:" + iLineOffset + " LineLen: "
					+ iLineLength);
			if (iLineOffset >= 0) {
				iOff += iLineOffset;
				CinderLog.logDebug("JFIV:getLine:" + pItem.getLine() + " iOff: "
						+ iOff);
				final StringBuilder sbX = new StringBuilder();
				sbX.append(doc.get(iOff, 3));
				CinderLog.logDebug("JFIV:numLines:" + doc.getNumberOfLines()
						+ " t: " + sbX.toString());
				if (iLineLength >= 0) {
					iLen = iLineLength;

					// optional stripping of leading whitespace
					int iCounter = 0;
					String test = "";
					for (int i = 0; i <= iLineLength; i++) {
						test = doc.get(iLineOffset + i, 1);
						if (" ".equals(test) || "\t".equals(test)) {
							iCounter++;
						} else {
							break;
						}
					}
					iOff += iCounter;
					iLen -= iCounter;
					CinderLog.logDebug("JFIV:++:" + iCounter);
				}
			}
			// avoid to select the line break at the end
			iLen -= 1;
			final TextSelection sel = new TextSelection(iOff, iLen);
			editor.getSelectionProvider().setSelection(sel);
		} catch (PartInitException e1) {
			CinderLog.logError(e1);
		} catch (Exception e) {
			CinderLog.logError(e);
		}
	}
	
	/**
	 * Inserts dummy values.
	 */
	public void insertDummyValues() {
		final String sKey = "CinderDummy";

		Collection<IItem> dummy = new ArrayList<IItem>();

		// this a bogus list for debugging
		dummy.add(new PropertiesItem(sKey, "WORKBENCH_FOLDER", ItemType.WORKBENCH_FOLDER));
		dummy.add(new PropertiesItem(sKey, "WORKBENCH_PROJECT", ItemType.WORKBENCH_PROJECT));
		dummy.add(new PropertiesItem(sKey, "JAVA_CLASS", ItemType.JAVA_CLASS));
		dummy.add(new PropertiesItem(sKey, "JAVA_CLASS_FILE", ItemType.JAVA_CLASS_FILE));
		dummy.add(new PropertiesItem(sKey, "JAVA_COMP_UNIT", ItemType.JAVA_COMP_UNIT));
		dummy.add(new PropertiesItem(sKey, "JAVA_INTERFACE", ItemType.JAVA_INTERFACE));
		dummy.add(new PropertiesItem(sKey, "JAVA_PACKAGE", ItemType.JAVA_PACKAGE));
		dummy.add(new PropertiesItem(sKey, "JAVA_PACKAGE_ROOT", ItemType.JAVA_PACKAGE_ROOT));
		dummy.add(new PropertiesItem(sKey, "JAVA_PROJECT", ItemType.JAVA_PROJECT));
		dummy.add(new PropertiesItem(sKey, "TASK_INFO", ItemType.TASK_INFO));
		dummy.add(new PropertiesItem(sKey, "TASK_WARN", ItemType.TASK_WARN));
		dummy.add(new PropertiesItem(sKey, "TASK_ERROR", ItemType.TASK_ERROR));
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
		cView.getViewer().refresh();
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
		cView.getViewer().refresh();
	}

	/**
	 * Inserts findings from a file
	 * 
	 * @param sFile
	 */
	public void insertFromFile(final String sFile, final int iType) {
		try {
			CinderLog.logDebug("JFCP_IFF:" + sFile);
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
			CinderLog.logDebug("JFCP_IFF:" + coll.size());

			for (IItem item : coll) {
				manager.add(item);
			}
			cView.getViewer().refresh();
		} catch (Exception e) {
			CinderLog.logError(e);
		}
	}
}
