package org.art_core.dev.cinder.views;

import org.art_core.dev.cinder.CinderLog;
import org.art_core.dev.cinder.CinderTools;
import org.art_core.dev.cinder.model.ItemManager;
import org.art_core.dev.cinder.model.PropertiesItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
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
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;

public class JFInputView extends ViewPart {

	private static final String JAVAEDITORID = "org.eclipse.jdt.ui.CompilationUnitEditor";
	private final String[] colNames = { "", "Name", "Status", "Line", "Offset" };
	private static final boolean TOGGLE_OFF = false;
	private static final boolean TOGGLE_ON = true;

	private TableViewer viewer;
	private JFContentProvider cpDefault;

	private Action aRemoveMarkersGlobal;
	private Action aSetMarkersGlobal;
	private Action aSelect;
	private Action aOpenFile;
	private Action aOpenUrl;

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
		TableColumn tCol, nCol, sCol, lineCol, offCol;

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

		cpDefault = new JFContentProvider();
		cpDefault.insertExampleValues();

		viewer.setContentProvider(cpDefault);
		viewer.setLabelProvider(new JFLabelProvider());
		viewer.setSorter(new JFSorter());
		viewer.setInput(ItemManager.getManager());
	}

	/**
	 * Discover the item selected in the TableViwer
	 * 
	 * @return {@link PropertiesItem}
	 */
	private PropertiesItem getSelectedItem() {
		final ISelection selection = viewer.getSelection();
		return (PropertiesItem) ((IStructuredSelection) selection)
				.getFirstElement();
	}

	/**
	 * Executes the creation and deletion of Markers.
	 * 
	 * @param bEnable
	 */
	private void executeMarkerToggle(final boolean bEnable) {
		final PropertiesItem pItem = this.getSelectedItem();

		if (pItem == null) {
			if (bEnable == TOGGLE_ON) {
				cpDefault.setMarkersGlobal();
			} else {
				cpDefault.removeMarkersGlobal();
			}
		} else {
			// TODO
			if (bEnable == TOGGLE_ON) {
				cpDefault.setMarkersSingle(pItem);
			} else {
				cpDefault.removeMarkersSingle(pItem);
			}
		}
	}

	private void executeOpenFile() {
		final String sFile = getOpenFile();
		if (sFile.length() > 0) {
			cpDefault.insertFromFile(sFile, JFContentProvider.FILE_LOCAL);
		}
	}

	private void executeOpenUrl() {
		final String sFile = getOpenUrl();
		if (sFile.length() > 0) {
			try {
				cpDefault.insertFromFile(sFile, JFContentProvider.FILE_REMOTE);
			} catch (Exception e) {
				CinderLog.logError(e);
			}

		}
	}

	private String getOpenFile() {
		String sResult = "";
		try {
			final Display display = Display.getCurrent();
			final Shell shell = new Shell(display);
			final FileDialog dlg = new FileDialog(shell);
			sResult = dlg.open();
			CinderLog.logInfo("JF_OF:" + sResult);
		} catch (Exception e) {
			CinderLog.logError(e);
		}
		return sResult;
	}

	private String getOpenUrl() {
		String sResult = "";
		String dialogTitle = "Read XML from URL";
		String dialogMessage = "Please enter the URL of the XML file to open:";

		final Display display = Display.getCurrent();
		final Shell shell = new Shell(display);
		final InputDialog dlg = new InputDialog(shell, dialogTitle,
				dialogMessage, "", null);
		dlg.open();
		sResult = dlg.getValue();

		return sResult;
	}

	/**
	 * Executes the text selection.
	 */
	private void executeSelect() {
		// select the clicked item from the view
		final ISelection selection = viewer.getSelection();
		final PropertiesItem pItem = (PropertiesItem) ((IStructuredSelection) selection)
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
			CinderLog.logInfo("JFIV:LineOff:" + iLineOffset + " LineLen: "
					+ iLineLength);
			if (iLineOffset >= 0) {
				iOff += iLineOffset;
				CinderLog.logInfo("JFIV:getLine:" + pItem.getLine() + " iOff: "
						+ iOff);
				final StringBuilder sbX = new StringBuilder();
				sbX.append(doc.get(iOff, 3));
				CinderLog.logInfo("JFIV:numLines:" + doc.getNumberOfLines()
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
					CinderLog.logInfo("JFIV:++:" + iCounter);
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

		aOpenFile = new Action() {
			public void run() {
				executeOpenFile();
			}
		};

		aOpenUrl = new Action() {
			public void run() {
				executeOpenUrl();
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

		aOpenFile.setText("Open File");
		aOpenFile.setToolTipText("Open File");
		aOpenFile.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages().getImageDescriptor(
						ISharedImages.IMG_OBJ_FOLDER));

		aOpenUrl.setText("Open URL");
		aOpenUrl.setToolTipText("Open URL");
		aOpenUrl
				.setImageDescriptor(PlatformUI
						.getWorkbench()
						.getSharedImages()
						.getImageDescriptor(
								org.eclipse.ui.ide.IDE.SharedImages.IMG_OBJS_BKMRK_TSK));
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
		manager.add(aOpenFile);
		manager.add(aOpenUrl);
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
