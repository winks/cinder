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
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.text.AbstractDocument;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.*;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;

public class JFInputView extends ViewPart {
	//private static final String MONKEY_VIEW_ID = "org.art_core.dev.cinder.views.JFMonkeyView";
	
	private final String JAVAEDITORID="org.eclipse.jdt.ui.CompilationUnitEditor";
	private final String[] colNames = {"", "Name", "Status", "Line", "Offset"};
	
	private TableViewer viewer;
	private TableColumn tCol, nCol, sCol, lineCol, offCol;
	
	private Action action1;
	private Action action2;
	private Action doubleClickAction;

	/**
	 * The constructor.
	 */
	public JFInputView() {
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		createTableViewer(parent);
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
	}

	/**
	 * Creates the TableViewer
	 * @param parent
	 */
	private void createTableViewer(Composite parent) {
		viewer = new TableViewer(
			parent, 
			SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION
		);
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
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				JFInputView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}
	
	/**
	 * Adds actions to a double click
	 */
	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(action1);
		manager.add(new Separator());
		manager.add(action2);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(action1);
		manager.add(action2);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(action1);
		manager.add(action2);
	}

	/**
	 * Initialize the actions needed
	 */
	private void makeActions() {
		// action1 - generic
		action1 = new Action() {
			public void run() {
				showMessage("Action 1 executed", "Action Title");
			}
		};
		action1.setText("Action 1");
		action1.setToolTipText("Action 1 tooltip");
		action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		
		// action2 - generic
		action2 = new Action() {
			public void run() {
				showMessage("Action 2 executed", "Action Title");
			}
		};
		action2.setText("Action 2");
		action2.setToolTipText("Action 2 tooltip");
		action2.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		
		// doubleclick - generic
		doubleClickAction = new Action() {
			public void run() {
				// select the clicked item from the view
				ISelection selection = viewer.getSelection();
				PropertiesItem pi = (PropertiesItem)((IStructuredSelection)selection).getFirstElement();
				if (pi == null)  {
					return;
				}
				//String msg = "Double-click detected on "+pi.toString()+"\n\n";
				//showMessage(msg, "Action Title");
				
				AbstractTextEditor editor = null;
				
				// hardcoded project name - for now
				String projectName = "HelloWorldASD";
				String newLoc = projectName + "/" + pi.getLocation();
				try {
					// find the correct editor window, based on the name
					IFile res = (IFile)ResourcesPlugin.getWorkspace().getRoot().findMember(newLoc);
					CinderLog.logInfo("JFIV:"+res.toString());
					FileEditorInput fileinput = new FileEditorInput(res);
					editor = (AbstractTextEditor)PlatformUI.getWorkbench().getActiveWorkbenchWindow().
						getActivePage().openEditor(fileinput, JAVAEDITORID);
				} catch (Exception e) {
					//resourceMessage("AV_W_FILENOTFOUND", projectName+pi.getLocation());
					CinderLog.logInfo("JFIV:E:"+newLoc);
					return;
				}
				if(editor != null){
					// convert line numbers to offset numbers (eclipse internal)
					IEditorInput input = editor.getEditorInput();
					IDocument doc = ((ITextEditor)editor).getDocumentProvider().getDocument(input);
					int iLineOffset = -1;
					int iLineLength = -1;
					int iOff = pi.getOffset();
					int iLen = 5;
					try {
						iLineOffset = doc.getLineOffset(pi.getLine());
						iLineLength = doc.getLineLength(pi.getLine());
						CinderLog.logInfo("JFIV:LineOff:"+iLineOffset+" LineLen: "+iLineLength);
						if (iLineOffset >= 0) {
							iOff += iLineOffset;
							if (iLineLength >= 0) {
								iLen = iLineLength;
								
								// optional stripping of leading whitespace
								int iCounter = 0;
								String test = "";
								for(int i = 0; i <= iLineLength; i++) {
									test = doc.get(iLineOffset+i, 1);
									if (test.equals(" ") || test.equals("\t")) {
										iCounter++;
									} else {
										break;
									}
								}
								iOff += iCounter;
								iLen -= iCounter;
								CinderLog.logInfo("JFIV:++:"+iCounter);
							}
						}
					} catch (Exception e) {
						CinderLog.logInfo("JFIV:E:"+e.getMessage());
					}
					
					// avoid to select the linebreak at the end
					iLen -= 1;
					TextSelection sel = new TextSelection(iOff, iLen);
					editor.getSelectionProvider().setSelection(sel);
				}
			}
		};
	}

	/**
	 * Show a popup message
	 * @param message
	 * @param title
	 */
	private void showMessage(String message, String title) {
		MessageDialog.openInformation(
			viewer.getControl().getShell(),
			title,
			message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}
