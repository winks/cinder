package org.art_core.dev.cinder.views;

import org.art_core.dev.cinder.model.IItem;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

class JFLabelProvider extends LabelProvider	implements ITableLabelProvider {
	
	public String getColumnText(Object obj, int index) {
		switch(index) {
			case 0: // type column
				return "";
			case 1: // name column
				return ((IItem) obj).getName();
			case 2: // location column
				return ((IItem) obj).getLocation();
			case 3: // line number column
				return String.valueOf(((IItem) obj).getLine());
			case 4: // offset column
				return String.valueOf(((IItem) obj).getOffset());
			default:
				return "";
		}
	}
	public Image getColumnImage(Object obj, int index) {
		if ((index == 0) && (obj instanceof IItem)) {
			return ((IItem) obj).getType().getImage();
		}
		return null;
	}
	public Image getImage(Object obj) {
		return PlatformUI.getWorkbench().
				getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
	}
}
