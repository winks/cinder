package org.art_core.dev.cinder.views;

import org.art_core.dev.cinder.CinderLog;
import org.art_core.dev.cinder.CinderTools;
import org.art_core.dev.cinder.model.ItemManager;
import org.art_core.dev.cinder.model.PropertiesItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.*;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.*;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;

public class JFInputView extends ViewPart {

	private final String JAVAEDITORID = "org.eclipse.jdt.ui.CompilationUnitEditor";
	private final String[] colNames = { "", "Name", "Status", "Line", "Offset" };
	private final boolean TOGGLE_OFF = false;
	private final boolean TOGGLE_ON = true;

	private TableViewer viewer;
	private TableColumn tCol, nCol, sCol, lineCol, offCol;

	private Action aRemoveMarkersGlobal;
	private Action aSetMarkersGlobal;
	private Action aSelect;

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
		executeMarkerToggle(TOGGLE_OFF);
		executeMarkerToggle(TOGGLE_ON);
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

		final JFContentProvider cpNew = new JFContentProvider();
		cpNew.insertExampleValues();

		viewer.setContentProvider(cpNew);
		viewer.setLabelProvider(new JFLabelProvider());
		viewer.setSorter(new JFSorter());
		viewer.setInput(ItemManager.getManager());
	}

	/**
	 * Discover the item selected in the TableViwer
	 * @return {@link PropertiesItem}
	 */
	private PropertiesItem getSelectedItem() {
		final ISelection selection = viewer.getSelection();
		PropertiesItem pItem = (PropertiesItem) ((IStructuredSelection) selection)
				.getFirstElement();
		return pItem;
	}

	/**
	 * Executes the creation and deletion of Markers.
	 * @param bEnable
	 */
	private void executeMarkerToggle(boolean bEnable) {
		final JFContentProvider cpMine = (JFContentProvider) viewer
				.getContentProvider();
		PropertiesItem pItem = this.getSelectedItem();

		if (pItem == null) {
			if (bEnable == TOGGLE_ON) {
				cpMine.setMarkersGlobal();
			} else {
				cpMine.removeMarkersGlobal();
			}
		} else {
			// TODO
			if (bEnable== TOGGLE_ON) {
				cpMine.setMarkersSingle(pItem);
			} else {
				cpMine.removeMarkersSingle(pItem);	
			}
		}
	}

	/**
	 * Executes the text selection.
	 */
	private void executeSelect() {
		// select the clicked item from the view
		final ISelection selection = viewer.getSelection();
		PropertiesItem pItem = (PropertiesItem) ((IStructuredSelection) selection)
				.getFirstElement();
		if (pItem == null) {
			return;
		}

		final String sMyLoc = pItem.getLocation();
		final IFile res = CinderTools.getResource(sMyLoc);
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
		aSelect = new Action() {
			public void run() {
				executeSelect();
			}
		};

		// delete - generic
		aRemoveMarkersGlobal = new Action() {
			public void run() {
				executeMarkerToggle(TOGGLE_OFF);
			}
		};
		// set - generic
		aSetMarkersGlobal = new Action() {
			public void run() {
				executeMarkerToggle(TOGGLE_ON);
			}
		};

		aRemoveMarkersGlobal.setText("Remove all Markers");
		aRemoveMarkersGlobal.setToolTipText("Remove all Markers");
		aRemoveMarkersGlobal.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages().getImageDescriptor(
						ISharedImages.IMG_TOOL_DELETE));

		aSetMarkersGlobal.setText("Set all Markers");
		aSetMarkersGlobal.setToolTipText("Set all Markers");
		aSetMarkersGlobal.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages().getImageDescriptor(
						ISharedImages.IMG_TOOL_NEW_WIZARD));

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
				aSelect.run();
			}
		});
	}

	private void contributeToActionBars() {
		final IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(final IMenuManager manager) {
		manager.add(aRemoveMarkersGlobal);
		manager.add(new Separator());
		manager.add(aSetMarkersGlobal);
	}

	private void fillContextMenu(final IMenuManager manager) {
		manager.add(aRemoveMarkersGlobal);
		manager.add(aSetMarkersGlobal);
		// Other plug-ins can contribute their actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(final IToolBarManager manager) {
		manager.add(aRemoveMarkersGlobal);
		manager.add(aSetMarkersGlobal);
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
