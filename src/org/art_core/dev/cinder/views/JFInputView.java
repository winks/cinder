package org.art_core.dev.cinder.views;

import org.art_core.dev.cinder.CinderLog;
import org.art_core.dev.cinder.model.ItemManager;
import org.art_core.dev.cinder.model.PropertiesItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.*;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.*;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;

public class JFInputView extends ViewPart {
	// private static final String MONKEY_VIEW_ID =
	// "org.art_core.dev.cinder.views.JFMonkeyView";

	private final String JAVAEDITORID = "org.eclipse.jdt.ui.CompilationUnitEditor";
	private final String[] colNames = { "", "Name", "Status", "Line", "Offset" };

	private TableViewer viewer;
	private TableColumn tCol, nCol, sCol, lineCol, offCol;

	private Action deleteAction;
	private Action markAction;
	private Action doubleClickAction;

	/**
	 * The constructor.
	 */
	public JFInputView() {
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(final Composite parent) {
		createTableViewer(parent);
		createActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
		executeDelete();
		executeMark();
	}

	/**
	 * Creates the TableViewer
	 * 
	 * @param parent
	 */
	private void createTableViewer(final Composite parent) {
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION);
		final Table table = viewer.getTable();

		// icon column
		tCol = new TableColumn(table, SWT.LEFT);
		tCol.setText(colNames[0]);
		tCol.setWidth(20);

		// name column
		nCol = new TableColumn(table, SWT.LEFT);
		nCol.setText(colNames[1]);
		nCol.setWidth(150);

		// status column
		sCol = new TableColumn(table, SWT.LEFT);
		sCol.setText(colNames[2]);
		sCol.setWidth(150);

		// line number column
		lineCol = new TableColumn(table, SWT.LEFT);
		lineCol.setText(colNames[3]);
		lineCol.setWidth(50);

		// offset column
		offCol = new TableColumn(table, SWT.LEFT);
		offCol.setText(colNames[4]);
		offCol.setWidth(50);

		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		viewer.setContentProvider(new JFContentProvider());
		viewer.setLabelProvider(new JFLabelProvider());
		viewer.setSorter(new JFSorter());
		viewer.setInput(ItemManager.getManager());
	}

	/**
	 * Adds actions to the context menu
	 */
	private void hookContextMenu() {
		final MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(final IMenuManager manager) {
				JFInputView.this.fillContextMenu(manager);
			}
		});
		final Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	/**
	 * Adds actions to a double click
	 */
	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(final DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}

	private void contributeToActionBars() {
		final IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(final IMenuManager manager) {
		manager.add(deleteAction);
		manager.add(new Separator());
		manager.add(markAction);
	}

	private void fillContextMenu(final IMenuManager manager) {
		manager.add(deleteAction);
		manager.add(markAction);
		// Other plug-ins can contribute their actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(final IToolBarManager manager) {
		manager.add(deleteAction);
		manager.add(markAction);
	}

	private void executeMark() {
		final JFContentProvider cpMine = (JFContentProvider) viewer
				.getContentProvider();
		// final PropertiesItem[] items = (PropertiesItem[])
		// cpMine.getElements(null);
		final Object[] items = cpMine.getElements(null);
		String sMyLoc;
		IFile res;
		PropertiesItem pItem;

		for (Object oItem : items) {
			pItem = (PropertiesItem) oItem;
			sMyLoc = pItem.getLocation();
			res = getResource(sMyLoc);

			try {
				IMarker marker = res.createMarker(IMarker.TASK);
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

	private IFile getResource(final String sFile) {
		IFile res = null;
		final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		final IProject[] projects = root.getProjects();

		try {
			CinderLog.logInfo("JFIV_GR_start");
			String sProjName = "";
			for (int i = 0; i < projects.length; i++) {
				sProjName = projects[i].getName();
				CinderLog.logInfo("JFIV_GR:DBG: " + sProjName);
				res = (IFile) root.findMember(sProjName + "/" + sFile);
				if (res == null) {
					CinderLog.logInfo("JFIV_GR_notfound:NULL");
					continue;
				} else {
					CinderLog.logInfo("JFIV_GR___found:" + res.toString());
					break;
				}
			}
		} catch (Exception e) {
			CinderLog.logError("JFIV_GR:E:" + sFile, e);
		}
		return res;
	}

	private void executeSelect() {
		// select the clicked item from the view
		final ISelection selection = viewer.getSelection();
		PropertiesItem pItem = (PropertiesItem) ((IStructuredSelection) selection)
				.getFirstElement();
		if (pItem == null) {
			return;
		}

		final String sMyLoc = pItem.getLocation();
		final IFile res = getResource(sMyLoc);
		AbstractTextEditor editor = null;
		FileEditorInput fileInput;

		try {
			if (null == res) {
				throw new Exception("resource not found");
			}
			fileInput = new FileEditorInput(res);
			editor = (AbstractTextEditor) PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage().openEditor(
							fileInput, JAVAEDITORID);
		} catch (PartInitException e1) {
			CinderLog.logError(e1);
		} catch (Exception e) {
			CinderLog.logError(e);
			;
		}

		if (editor != null) {
			// convert line numbers to offset numbers (eclipse internal)
			IEditorInput input = editor.getEditorInput();
			IDocument doc = ((ITextEditor) editor).getDocumentProvider()
					.getDocument(input);
			int iLineOffset = -1;
			int iLineLength = -1;
			int iOff = pItem.getOffset();
			int iLen = 5;
			try {
				iLineOffset = doc.getLineOffset(pItem.getLine() - 1);
				iLineLength = doc.getLineLength(pItem.getLine() - 1);
				CinderLog.logInfo("JFIV:LineOff:" + iLineOffset + " LineLen: "
						+ iLineLength);
				if (iLineOffset >= 0) {
					iOff += iLineOffset;
					CinderLog.logInfo("JFIV:getLine:" + pItem.getLine()
							+ " iOff: " + iOff);
					StringBuilder x = new StringBuilder();
					x.append(doc.get(iOff, 3));
					CinderLog.logInfo("JFIV:numLines:" + doc.getNumberOfLines()
							+ " t: " + x.toString());
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
						CinderLog.logInfo("JFIV:++:" + iCounter);
					}
				}
			} catch (Exception e) {
				CinderLog.logInfo("JFIV:E:" + e.getMessage());
			}

			// avoid to select the line break at the end
			iLen -= 1;
			TextSelection sel = new TextSelection(iOff, iLen);
			editor.getSelectionProvider().setSelection(sel);
		}
	}

	/**
	 * Initialize the actions needed
	 */
	private void createActions() {
		// double click - generic
		doubleClickAction = new Action() {
			public void run() {
				executeSelect();
			}
		};

		// delete - generic
		deleteAction = new Action() {
			public void run() {
				executeDelete();
			}
		};
		// set - generic
		markAction = new Action() {
			public void run() {
				executeMark();
			}
		};

		deleteAction.setText("Delete Markers");
		deleteAction.setToolTipText("Delete all Marksers");
		deleteAction.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages().getImageDescriptor(
						ISharedImages.IMG_TOOL_DELETE));

		markAction.setText("Set Markers");
		markAction.setToolTipText("Set All Markers");
		markAction.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages().getImageDescriptor(
						ISharedImages.IMG_TOOL_NEW_WIZARD));

	}

	private void executeDelete() {
		// select the clicked item from the view
		final ISelection selection = viewer.getSelection();
		PropertiesItem pItem = (PropertiesItem) ((IStructuredSelection) selection)
				.getFirstElement();
		if (pItem == null) {
			return;
		}

		final String sMyLoc = pItem.getLocation();
		// find the correct editor window, based on the name
		final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		final IProject[] projects = root.getProjects();
		IFile res = null;
		String sProjName = "";
		for (int i = 0; i < projects.length; i++) {
			sProjName = projects[i].getName();
			CinderLog.logInfo("JFIV_D:DBG: " + sProjName);
			res = (IFile) root.findMember(sProjName + "/" + sMyLoc);
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

	/**
	 * Show a popup message
	 * 
	 * @param message
	 * @param title
	 */
	private void showMessage(String message, String title) {
		MessageDialog.openInformation(viewer.getControl().getShell(), title,
				message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}
