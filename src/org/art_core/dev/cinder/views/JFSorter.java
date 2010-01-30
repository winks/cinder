package org.art_core.dev.cinder.views;

import org.art_core.dev.cinder.model.IItem;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

/**
 * A custom sorter for a content provider.
 */
public class JFSorter extends ViewerSorter {
	private int propertyIndex;
	private int direction;
	//private static int ASC = 0;
	private static int DESC = 1;
	
	/**
	 * Constructor.
	 */
	public JFSorter() {
		this.propertyIndex = 0;
		direction = DESC;
	}
	
	/**
	 * Set column to be sorted.
	 * @param col
	 */
	public void setColumn(int col) {
		if (col == this.propertyIndex) {
			direction = 1 - direction;
		} else {
			this.propertyIndex = col;
			direction = DESC;
		}
	}
	
	/**
	 * Compare to determine sort order.
	 */
	public int compare(Viewer viewer, Object o1, Object o2) {
		IItem item1 = (IItem) o1;
		IItem item2 = (IItem) o2;
		
		int ret = 0;
		
		switch (propertyIndex) {
		case 0:
			ret = item1.getType().compareTo(item2.getType());
			break;
		case 1:
			ret = item1.getName().compareTo(item2.getName());
			break;
		case 2:
			ret = item1.getMessage().compareTo(item2.getMessage());
			break;
		case 3:
			ret = item1.getLocation().compareTo(item2.getLocation());
			break;
		case 4:
			ret = item1.getLine() - item2.getLine();
			break;
		case 5:
			ret = item1.getOffset() - item2.getOffset();
			break;
		case 6:
			ret = item1.getStatus().compareTo(item2.getStatus());
			break;
		case 7:
			ret = item1.getTimestamp() - item2.getTimestamp();
			break;
		default:
			ret = 0;
			break;
		}
		
		if (direction == DESC) {
			ret = -ret;
		}
		return ret;
	}
}