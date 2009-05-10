package org.art_core.dev.cinder;

import org.art_core.dev.cinder.model.ItemType;
import org.art_core.dev.cinder.model.PropertiesItem;

public class CinderTools {
	public static ItemType chooseType(String type) {
		ItemType itemtype;
		
		if (type.equals("warning")) {
			itemtype = ItemType.JAVA_INTERFACE;
		} else {
			itemtype = PropertiesItem.DEFAULT_TYPE;
		}
		
		return itemtype;
	}
}
