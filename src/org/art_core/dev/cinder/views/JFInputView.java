package org.art_core.dev.cinder.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;

import org.art_core.dev.cinder.CinderLog;
import org.art_core.dev.cinder.CinderPlugin;
import org.art_core.dev.cinder.controller.MainController;
import org.art_core.dev.cinder.model.ItemManager;
import org.art_core.dev.cinder.model.IItem;
import org.art_core.dev.cinder.model.ItemStatus;
import org.art_core.dev.cinder.prefs.CinderPrefPage;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
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

/**
 * Main view for cinder.
 * @author Florian Anderiasch
 *
 */
public class JFInputView extends ViewPart {

	private final String[] colNames = { "", "Name", "Message", "Location", "Line", "Offset", "Status", "Changed" };
	private final int[] colSizes = {20, 150, 200, 200, 50, 50, 100, 120 };
	private ResourceBundle cRes = ResourceBundle.getBundle("org.art_core.dev.cinder.CinderResource");
	private static final boolean TOGGLE_OFF = false;
	private static final boolean TOGGLE_ON = true;

	private TableViewer viewer;
	private JFSorter sorter;
	private MainController cControl;
	private IPreferenceStore ipsPref = CinderPlugin.getDefault()
			.getPreferenceStore();

	private Action aShowMarkersAll;
	private Action aHideMarkersAll;
	private Action aShowMarkersSelected;
	private Action aHideMarkersSelected;
	private Action aSelect;
	private Action aOpenUrl;
	private Action aOpenFile;
	private Action aShowDummy;
	private Action aClearAll;
	private Action aClearSelected;
	private Action[] aStatusActions;

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
		executeMarkerToggleAll(TOGGLE_OFF);
		executeMarkerToggleAll(TOGGLE_ON);
	}

	/**
	 * Creates the TableViewer
	 * 
	 * @param parent
	 */
	private void createTableViewer(final Composite parent) {
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION);
		createColumns(viewer);
		
		viewer.setContentProvider(new JFContentProvider());
		viewer.setLabelProvider(new JFLabelProvider());
		sorter = new JFSorter();
		viewer.setSorter(sorter);
		viewer.setInput(ItemManager.getManager());
	}

	/**
	 * Create columns in the TableViewer.
	 * @param viewer
	 */
	private void createColumns(final TableViewer viewer) {
		final Table table = viewer.getTable();
		
		for (int i = 0; i < colSizes.length; i++) {
			final int index = i;
			final TableColumn col = new TableColumn(table, SWT.LEFT);
			col.setText(colNames[i]);
			col.setWidth(colSizes[i]);
			
			col.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					sorter.setColumn(index);
					int dir = table.getSortDirection();
					if (table.getSortColumn() == col) {
						dir = dir == SWT.UP ? SWT.DOWN : SWT.UP;
					} else {
						dir = SWT.DOWN;
					}
					table.setSortDirection(dir);
					table.setSortColumn(col);
					viewer.refresh();
				}
			});
		}
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
	}

	/**
	 * Discover the item selected in the TableViwer
	 * 
	 * @return {@link IItem}
	 */
	@SuppressWarnings("unchecked")
	private List<IItem> getSelectedItems() {
		CinderLog.logDebug("getSel_start");
		final ISelection selection = viewer.getSelection();
		List<Object> foo = ((IStructuredSelection) selection).toList();
		List<IItem> list = new ArrayList<IItem>();
		list.addAll((Collection<? extends IItem>) foo);
		CinderLog.logDebug("getSel_end");
		return list;
	}

	/**
	 * Executes the creation and deletion of Markers.
	 * 
	 * @param bEnable
	 */
	private void executeMarkerToggleAll(final boolean bEnable) {
		if (bEnable == TOGGLE_ON) {
			cControl.showMarkersAll();
		} else {
			cControl.hideMarkersAll();
		}
	}
	
	/**
	 * Executes the creation and deletion of Markers.
	 * 
	 * @param bEnable
	 */
	private void executeMarkerToggleSelected(final boolean bEnable) {
		if (bEnable == TOGGLE_ON) {
			for (IItem pItem: this.getSelectedItems()) {
				cControl.showMarkersSelected(pItem);
			}
		} else {
			for (IItem pItem: this.getSelectedItems()) {
				cControl.hideMarkersSelected(pItem);
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
	 * Executes clearing all entries.
	 */
	private void executeClearAll() {
		cControl.clearAll();
	}
	
	/**
	 * Executes clearing selected entries.
	 */
	private void executeClearSelected() {
		for (IItem pItem: this.getSelectedItems()) {
			cControl.clearSelected(pItem);
		}
	}
	
	/**
	 * Sets the status for selected items.
	 * @param status
	 */
	private void executeSetStatus(ItemStatus status) {
		for (IItem pItem: this.getSelectedItems()) {
			cControl.setStatus(pItem, status);
		}
		this.getViewer().refresh();
	}

	/**
	 * Executes opening a file.
	 */
	private void executeOpenFile() {
		String sPrefKey = CinderPrefPage.P_STRING + "_xml_file";
		String sPrefPath = ipsPref.getString(sPrefKey);
		CinderLog.logDebug("JFIV_eOF:" + sPrefPath);
		final String sFile = getOpenFile(sPrefPath);
		if (sFile != null && sFile.length() > 0) {
			try {
				cControl.insertFromFile(sFile, MainController.FILE_LOCAL);
				ipsPref.setValue(sPrefKey, sFile);
				executeMarkerToggleAll(TOGGLE_ON);
			} catch (Exception e) {
				CinderLog.logError(e);
			}
		}
	}

	/**
	 * Executes opening an URL.
	 */
	private void executeOpenUrl() {
		String sPrefKey = CinderPrefPage.P_STRING + "_xml_url";
		String sPrefPath = ipsPref.getString(sPrefKey);
		CinderLog.logDebug("JFIV_eOU:" + sPrefPath);
		final String sFile = getOpenUrl(sPrefPath);
		if (sFile != null && sFile.length() > 0) {
			try {
				cControl.insertFromFile(sFile, MainController.FILE_REMOTE);
				ipsPref.setValue(sPrefKey, sFile);
				executeMarkerToggleAll(TOGGLE_ON);
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
			Display display = Display.getCurrent();
			if (display == null) {
				display = Display.getDefault();
			}
			final Shell shell = new Shell(display);
			final FileDialog dlg = new FileDialog(shell);
			dlg.setText(cRes.getString("DIALOG_READ_XML_FILE_TITLE"));
			dlg.setFileName(sFile);
			sResult = dlg.open();
			CinderLog.logDebug("JF_OF:" + sResult);
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
		String dialogTitle = cRes.getString( "DIALOG_READ_XML_URL_TITLE");
		String dialogMessage = cRes.getString( "DIALOG_READ_XML_URL_MESSAGE");

		Display display = Display.getCurrent();
		if (display == null) {
			display = Display.getDefault();
		}
		final Shell shell = new Shell(display);
		final InputDialog dlg = new InputDialog(shell, dialogTitle,
				dialogMessage, sPre, null);

		int result = dlg.open();
		if (result == org.eclipse.jface.window.Window.OK) {
			sResult = dlg.getValue();
		}

		return sResult;
	}

	/**
	 * Initialize the actions needed
	 */
	private void createActions() {
		
		// select an item
		aSelect = new Action() {
			public void run() {
				executeSelect();
			}
		};

		// hide all markers
		aHideMarkersAll = new Action() {
			public void run() {
				executeMarkerToggleAll(TOGGLE_OFF);
			}
		};
		aHideMarkersAll.setText(cRes.getString("TEXT_HIDE_ALL_MARKERS"));
		aHideMarkersAll.setToolTipText(cRes.getString("TEXT_HIDE_ALL_MARKERS"));
		aHideMarkersAll.setImageDescriptor((ImageDescriptor) cRes.getObject("IMAGE_HIDE_ALL_MARKERS"));
		
		// hide selected markers
		aHideMarkersSelected = new Action() {
			public void run() {
				executeMarkerToggleSelected(TOGGLE_OFF);
			}
		};
		aHideMarkersSelected.setText(cRes.getString("TEXT_HIDE_SEL_MARKERS"));
		aHideMarkersSelected.setToolTipText(cRes.getString("TEXT_HIDE_SEL_MARKERS"));
		aHideMarkersSelected.setImageDescriptor((ImageDescriptor) cRes.getObject("IMAGE_HIDE_SEL_MARKERS"));
		
		// show all markers
		aShowMarkersAll = new Action() {
			public void run() {
				executeMarkerToggleAll(TOGGLE_ON);
			}
		};
		aShowMarkersAll.setText(cRes.getString("TEXT_SHOW_ALL_MARKERS"));
		aShowMarkersAll.setToolTipText(cRes.getString("TEXT_SHOW_ALL_MARKERS"));
		aShowMarkersAll.setImageDescriptor((ImageDescriptor) cRes.getObject("IMAGE_SHOW_ALL_MARKERS"));
		
		// show selected markers
		aShowMarkersSelected = new Action() {
			public void run() {
				executeMarkerToggleSelected(TOGGLE_ON);
			}
		};
		aShowMarkersSelected.setText(cRes.getString("TEXT_SHOW_SEL_MARKERS"));
		aShowMarkersSelected.setToolTipText(cRes.getString("TEXT_SHOW_SEL_MARKERS"));
		aShowMarkersSelected.setImageDescriptor((ImageDescriptor) cRes.getObject("IMAGE_SHOW_SEL_MARKERS"));

		// open a file
		aOpenFile = new Action() {
			public void run() {
				executeOpenFile();
			}
		};
		aOpenFile.setText(cRes.getString("TEXT_OPEN_FILE"));
		aOpenFile.setToolTipText(cRes.getString("TEXT_OPEN_FILE"));
		aOpenFile.setImageDescriptor((ImageDescriptor) cRes.getObject("IMAGE_OPEN_FILE"));

		

		// open an URL
		aOpenUrl = new Action() {
			public void run() {
				executeOpenUrl();
			}
		};
		aOpenUrl.setText(cRes.getString("TEXT_OPEN_URL"));
		aOpenUrl.setToolTipText(cRes.getString("TEXT_OPEN_URL"));
		aOpenUrl.setImageDescriptor((ImageDescriptor) cRes.getObject("IMAGE_OPEN_URL"));

		// show dummy entries
		aShowDummy = new Action() {
			public void run() {
				executeShowDummy();
			}
		};
		aShowDummy.setText(cRes.getString("TEXT_SHOW_DUMMY"));
		aShowDummy.setToolTipText(cRes.getString("TEXT_SHOW_DUMMY"));
		aShowDummy.setImageDescriptor((ImageDescriptor) cRes.getObject("IMAGE_SHOW_DUMMY"));
		
		// clear all entries
		aClearAll = new Action() {
			public void run() {
				executeMarkerToggleAll(TOGGLE_OFF);
				executeClearAll();
			}
		};
		aClearAll.setText(cRes.getString("TEXT_CLEAR_ALL"));
		aClearAll.setToolTipText(cRes.getString("TEXT_CLEAR_ALL"));
		aClearAll.setImageDescriptor((ImageDescriptor) cRes.getObject("IMAGE_CLEAR_ALL"));
		
		// clear some entries
		aClearSelected = new Action() {
			public void run() {
				executeMarkerToggleSelected(TOGGLE_OFF);
				executeClearSelected();
			}
		};
		aClearSelected.setText(cRes.getString("TEXT_CLEAR_SEL"));
		aClearSelected.setToolTipText(cRes.getString("TEXT_CLEAR_SEL"));
		aClearSelected.setImageDescriptor((ImageDescriptor) cRes.getObject("IMAGE_CLEAR_SEL"));
		
		// set status to XXX
		int iLen = ItemStatus.values().length;
		aStatusActions = new Action[iLen];
		for (final ItemStatus x: ItemStatus.values()) {
			iLen--;
			aStatusActions[iLen] = new Action() {
				public void run() {
					executeSetStatus(x);
				}
			};
			aStatusActions[iLen].setText(x.name());
			aStatusActions[iLen].setToolTipText(x.name());
		}
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

		IMenuManager mmMenu = bars.getMenuManager();
		IToolBarManager mmBar = bars.getToolBarManager();

		// add to Local Menu, mirroring the context menu
		fillContextMenu(mmMenu);
		mmMenu.add(aShowDummy);

		// add to Local Tool Bar of the View
		mmBar.add(aOpenFile);
		mmBar.add(aOpenUrl);
		mmBar.add(aShowMarkersAll);
		mmBar.add(aHideMarkersAll);
		mmBar.add(aClearAll);

		bars.updateActionBars();
	}
	
	/**
	 * Add content to a menu manager, for example the context menu
	 * @param manager
	 */
	private void fillContextMenu(final IMenuManager manager) {
		MenuManager subMenu = new MenuManager(cRes.getString("TEXT_SET_STATUS"));
		
		for (Action a: aStatusActions) {
			subMenu.add(a);
		}
		
		manager.add(aShowMarkersSelected);
		manager.add(aHideMarkersSelected);
		manager.add(new Separator());
		manager.add(subMenu);
		manager.add(new Separator());
		manager.add(aClearSelected);
		
		// Other plug-ins can contribute their actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
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

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}