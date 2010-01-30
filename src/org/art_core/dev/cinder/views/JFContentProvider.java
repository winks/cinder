package org.art_core.dev.cinder.views;

import org.art_core.dev.cinder.CinderLog;
import org.art_core.dev.cinder.model.ItemManager;
import org.art_core.dev.cinder.model.ItemManagerEvent;
import org.art_core.dev.cinder.model.ItemManagerListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
//import org.eclipse.jface.viewers.TableViewer;
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
	

	public JFContentProvider(/*JFInputView parent*/) {
		//this.viewer = parent.getViewer();
		this.manager = ItemManager.getManager();
	}

	@Override
	public void inputChanged(final Viewer vViewer, final Object oldInput,
			final Object newInput) {
		this.viewer = (TableViewer) vViewer;
		if (manager != null) {
			manager.removeListener(this);
		}
		manager = (ItemManager) newInput;
		if (manager != null) {
			manager.addListener(this);
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
		Object[] x = new Object[1];
		try {
			x = manager.getItems();
		} catch (Exception e) {
			x[0] = parent;
			CinderLog.logErrorInfo("getElements", e);
		}
		return x;
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