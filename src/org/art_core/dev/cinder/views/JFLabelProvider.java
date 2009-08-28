package org.art_core.dev.cinder.views;

import org.art_core.dev.cinder.model.IItem;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

class JFLabelProvider extends LabelProvider implements ITableLabelProvider {

	/**
	 * Returns the label image for the given column of the given element.
	 * 
	 * @param element
	 * @param index
	 * @return String
	 */
	@Override
	public String getColumnText(final Object obj, final int index) {
		String sResult;
		switch (index) {
		case 0: // type column
			sResult = "";
			break;
		case 1: // name column
			sResult = ((IItem) obj).getName();
			break;
		case 2: // location column
			sResult = ((IItem) obj).getLocation();
			break;
		case 3: // line number column
			sResult = String.valueOf(((IItem) obj).getLine());
			break;
		case 4: // offset column
			sResult = String.valueOf(((IItem) obj).getOffset());
			break;
		default:
			sResult = "";

		}
		return sResult;
	}

	/**
	 * Returns the label image for the given column of the given element.
	 * 
	 * @param element
	 * @param index
	 * @return Image or <code>null</code>
	 */
	@Override
	public Image getColumnImage(final Object obj, final int index) {
		if ((index == 0) && (obj instanceof IItem)) {
			return ((IItem) obj).getType().getImage();
		}
		return null;
	}

	public Image getImage(final Object obj) {
		return PlatformUI.getWorkbench().getSharedImages().getImage(
				ISharedImages.IMG_OBJ_ELEMENT);
	}
}
