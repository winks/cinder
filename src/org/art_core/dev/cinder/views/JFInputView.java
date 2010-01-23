package org.art_core.dev.cinder.views;

import org.art_core.dev.cinder.CinderLog;
import org.art_core.dev.cinder.CinderPlugin;
import org.art_core.dev.cinder.controller.MainController;
import org.art_core.dev.cinder.model.ItemManager;
import org.art_core.dev.cinder.model.PropertiesItem;
import org.art_core.dev.cinder.prefs.CinderPrefTools;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;


public class JFInputView extends ViewPart {

	private final String[] colNames = { "", "Name", "Location", "Line", "Offset", "Status", "Changed" };
	private static final boolean TOGGLE_OFF = false;
	private static final boolean TOGGLE_ON = true;

	private TableViewer viewer;
	private MainController cControl;
	private IPreferenceStore ipsPref = CinderPlugin.getDefault()
			.getPreferenceStore();

	private Action aSetMarkersGlobal;
	private Action aRemoveMarkersGlobal;
	private Action aSetMarkersSingle;
	private Action aRemoveMarkersSingle;
	private Action aSelect;
	private Action aOpenUrl;
	private Action aOpenFile;
	private Action aShowDummy;
	private Action aClear;

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(final Composite parent) {
		createTableViewer(parent);
		cControl = new MainController(this);
		createActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
		cControl.insertDummyValues();
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
		createColumn(viewer);
		
		viewer.setContentProvider(new JFContentProvider());
		viewer.setLabelProvider(new JFLabelProvider());
		viewer.setSorter(new JFSorter());
		viewer.setInput(ItemManager.getManager());
	}

	private void createColumn(TableViewer viewer) {
		final Table table = viewer.getTable();
		TableColumn tCol, nCol, locCol, lineCol, offCol, statCol, tsCol;

		// icon column
		tCol = new TableColumn(table, SWT.LEFT);
		tCol.setText(colNames[0]);
		tCol.setWidth(20);

		// name column
		nCol = new TableColumn(table, SWT.LEFT);
		nCol.setText(colNames[1]);
		nCol.setWidth(200);

		// location column
		locCol = new TableColumn(table, SWT.LEFT);
		locCol.setText(colNames[2]);
		locCol.setWidth(200);

		// line number column
		lineCol = new TableColumn(table, SWT.LEFT);
		lineCol.setText(colNames[3]);
		lineCol.setWidth(50);

		// offset column
		offCol = new TableColumn(table, SWT.LEFT);
		offCol.setText(colNames[4]);
		offCol.setWidth(50);
		
		// timestamp column
		statCol = new TableColumn(table, SWT.LEFT);
		statCol.setText(colNames[5]);
		statCol.setWidth(100);
		
		// status column
		tsCol = new TableColumn(table, SWT.LEFT);
		tsCol.setText(colNames[6]);
		tsCol.setWidth(120);

		table.setHeaderVisible(true);
		table.setLinesVisible(true);
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
				cControl.setMarkersGlobal();
			} else {
				cControl.removeMarkersGlobal();
			}
		} else {
			// TODO
			if (bEnable == TOGGLE_ON) {
				cControl.setMarkersSingle(pItem);
			} else {
				cControl.removeMarkersSingle(pItem);
			}
		}
	}

	/**
	 * Executes showing the dummy entries.
	 */
	private void executeShowDummy() {
		cControl.insertDummyValues();
	}
	
	/**
	 * Executes clearing entries.
	 */
	private void executeClear() {
		cControl.clear();
	}

	/**
	 * Executes opening a file.
	 */
	private void executeOpenFile() {
		String sPrefKey = CinderPrefTools.P_STRING + "_xml_file";
		String sPrefPath = ipsPref.getString(sPrefKey);
		CinderLog.logInfo("JFIV_eOF:" + sPrefPath);
		final String sFile = getOpenFile(sPrefPath);
		if (sFile.length() > 0) {
			try {
				cControl.insertFromFile(sFile, MainController.FILE_LOCAL);
				ipsPref.setValue(sPrefKey, sFile);
				executeMarkerToggle(TOGGLE_ON);
			} catch (Exception e) {
				CinderLog.logError(e);
			}
		}
	}

	/**
	 * Executes opening an URL.
	 */
	private void executeOpenUrl() {
		String sPrefKey = CinderPrefTools.P_STRING + "_xml_url";
		String sPrefPath = ipsPref.getString(sPrefKey);
		CinderLog.logInfo("JFIV_eOU:" + sPrefPath);
		final String sFile = getOpenUrl(sPrefPath);
		if (sFile.length() > 0) {
			try {
				cControl.insertFromFile(sFile, MainController.FILE_REMOTE);
				ipsPref.setValue(sPrefKey, sFile);
				executeMarkerToggle(TOGGLE_ON);
			} catch (Exception e) {
				CinderLog.logError(e);
			}

		}
	}

	/**
	 * Executes the text selection.
	 */
	private void executeSelect() {
		cControl.select();
	}
	
	/**
	 * Opens a file select dialog for user input of a file name.
	 * 
	 * @return String the filename
	 */
	private String getOpenFile(final String sFile) {
		String sResult = "";
		try {
			final Display display = Display.getCurrent();
			final Shell shell = new Shell(display);
			final FileDialog dlg = new FileDialog(shell);
			dlg.setFileName(sFile);
			sResult = dlg.open();
			CinderLog.logInfo("JF_OF:" + sResult);
		} catch (Exception e) {
			CinderLog.logError(e);
		}
		return sResult;
	}

	/**
	 * Opens a dialog for user input of an URL.
	 * 
	 * @param sPre
	 *            the preselected URL
	 * @return the URL
	 */
	private String getOpenUrl(final String sPre) {
		String sResult = "";
		String dialogTitle = "Read XML from URL";
		String dialogMessage = "Please enter the URL of the XML file to open:";

		final Display display = Display.getCurrent();
		final Shell shell = new Shell(display);
		final InputDialog dlg = new InputDialog(shell, dialogTitle,
				dialogMessage, sPre, null);
		dlg.open();
		sResult = dlg.getValue();

		return sResult;
	}

	/**
	 * Initialize the actions needed
	 */
	private void createActions() {
		
		// selecting an item
		aSelect = new Action() {
			public void run() {
				executeSelect();
			}
		};

		// removing all markers
		aRemoveMarkersGlobal = new Action() {
			public void run() {
				executeMarkerToggle(TOGGLE_OFF);
			}
		};
		
		// removing markers
		aRemoveMarkersSingle = new Action() {
			public void run() {
				executeMarkerToggle(TOGGLE_OFF);
			}
		};
		
		// setting all markers
		aSetMarkersGlobal = new Action() {
			public void run() {
				executeMarkerToggle(TOGGLE_ON);
			}
		};
		
		// setting markers
		aSetMarkersSingle = new Action() {
			public void run() {
				executeMarkerToggle(TOGGLE_ON);
			}
		};

		// opening a file
		aOpenFile = new Action() {
			public void run() {
				executeOpenFile();
			}
		};

		// opening an URL
		aOpenUrl = new Action() {
			public void run() {
				executeOpenUrl();
			}
		};

		// show dummy entries
		aShowDummy = new Action() {
			public void run() {
				executeShowDummy();
			}
		};
		
		// clear entries
		aClear = new Action() {
			public void run() {
				executeMarkerToggle(TOGGLE_OFF);
				executeClear();
				
			}
		};

		ImageDescriptor idRemove = PlatformUI.getWorkbench()
			.getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_COPY_DISABLED);
		ImageDescriptor idAdd = PlatformUI.getWorkbench()
			.getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_COPY);
		ImageDescriptor idOpenFile = PlatformUI.getWorkbench()
			.getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_FOLDER);
		ImageDescriptor idOpenUrl = PlatformUI.getWorkbench()
			.getSharedImages().getImageDescriptor(org.eclipse.ui.ide.IDE.SharedImages.IMG_OBJS_BKMRK_TSK);
		ImageDescriptor idDummy = PlatformUI.getWorkbench()
			.getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_ELEMENT);
		ImageDescriptor idClear = PlatformUI.getWorkbench()
			.getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_DELETE);
		
		String sRemoveAll = "Remove all Markers";
		String sRemoveOne = "Remove Markers";
		String sAddAll    = "Set all Markers";
		String sAddOne    = "Set Markers";
		String sOpenFile  = "Open File";
		String sOpenUrl   = "Open URL";
		String sDummy     = "Show Dummy";
		String sClear     = "Clear entries";
		
		aRemoveMarkersGlobal.setText(sRemoveAll);
		aRemoveMarkersGlobal.setToolTipText(sRemoveAll);
		aRemoveMarkersGlobal.setImageDescriptor(idRemove);
		
		aRemoveMarkersSingle.setText(sRemoveOne);
		aRemoveMarkersSingle.setToolTipText(sRemoveOne);
		aRemoveMarkersSingle.setImageDescriptor(idRemove);

		aSetMarkersGlobal.setText(sAddAll);
		aSetMarkersGlobal.setToolTipText(sAddAll);
		aSetMarkersGlobal.setImageDescriptor(idAdd);
		
		aSetMarkersSingle.setText(sAddOne);
		aSetMarkersSingle.setToolTipText(sAddOne);
		aSetMarkersSingle.setImageDescriptor(idAdd);

		aOpenFile.setText(sOpenFile);
		aOpenFile.setToolTipText(sOpenFile);
		aOpenFile.setImageDescriptor(idOpenFile);

		aOpenUrl.setText(sOpenUrl);
		aOpenUrl.setToolTipText(sOpenUrl);
		aOpenUrl.setImageDescriptor(idOpenUrl);

		aShowDummy.setText(sDummy);
		aShowDummy.setToolTipText(sDummy);
		aShowDummy.setImageDescriptor(idDummy);
		
		aClear.setText(sClear);
		aClear.setToolTipText(sClear);
		aClear.setImageDescriptor(idClear);
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
	 * Adds action to action bars.
	 */
	private void contributeToActionBars() {
		final IActionBars bars = getViewSite().getActionBars();

		IMenuManager mmMenu;
		IToolBarManager mmBar;

		// add to Local Menu
		mmMenu = bars.getMenuManager();
		mmMenu.add(aRemoveMarkersGlobal);
		mmMenu.add(new Separator());
		mmMenu.add(aSetMarkersGlobal);
		mmMenu.add(new Separator());
		mmMenu.add(aClear);

		// add to Local Tool Bar of the View
		mmBar = bars.getToolBarManager();
		mmBar.add(aShowDummy);
		mmBar.add(aOpenFile);
		mmBar.add(aOpenUrl);
		mmBar.add(aRemoveMarkersGlobal);
		mmBar.add(aSetMarkersGlobal);
		mmBar.add(aClear);

		bars.updateActionBars();
	}

	public TableViewer getViewer() {
		return viewer;
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

	private void fillContextMenu(final IMenuManager manager) {
		manager.add(aRemoveMarkersGlobal);
		manager.add(aSetMarkersGlobal);
		manager.add(aClear);
		// Other plug-ins can contribute their actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}
