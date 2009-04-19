package org.art_core.dev.cinder.views;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IViewSite;

/*
 * The content provider class is responsible for
 * providing objects to the view. It can wrap
 * existing objects in adapters or simply return
 * objects as-is. These objects may be sensitive
 * to the current input of the view, or ignore
 * it and always show the same content 
 * (like Task List, for example).
 */
 
public class JFContentProvider implements IStructuredContentProvider {
	private static final String JFMONKEY_VIEW_ID = 
		"org.art_core.dev.cinder.views.JFMonkeyView";

	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
	}
	public void dispose() {
	}
	public Object[] getElements(Object parent) {
		if (parent instanceof IViewSite) {
			IViewSite view = (IViewSite)parent;
			String viewId = view.getId();
			
			if (viewId.equals(JFMONKEY_VIEW_ID)) {
				return new String[] { "Cheetah", "King Kong", "Donkey Kong" };
			}
		}
		return null;
	}
}