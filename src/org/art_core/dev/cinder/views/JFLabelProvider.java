package org.art_core.dev.cinder.views;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.art_core.dev.cinder.model.IItem;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

class JFLabelProvider extends LabelProvider implements ITableLabelProvider {
	private static final String sPattern = "yyyy-MM-dd H:m:s";
	
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
		case 5: // timestamp column
			int ts = ((IItem) obj).getLastChanged();
			Date dTS = new Date (ts*1000L);
			SimpleDateFormat sdf = new SimpleDateFormat(sPattern);
			sResult = "";
			try {
				sResult = sdf.format(dTS);
			} catch (NullPointerException e) {
				// TODO: handle exception
			}
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
		Image imResult = null;
		if ((index == 0) && (obj instanceof IItem)) {
			imResult = ((IItem) obj).getType().getImage();
		}
		return imResult;
	}

	public Image getImage(final Object obj) {
		return PlatformUI.getWorkbench().getSharedImages().getImage(
				ISharedImages.IMG_OBJ_ELEMENT);
	}
}
