package org.art_core.dev.cinder;

import org.art_core.dev.cinder.model.ItemType;
import org.art_core.dev.cinder.model.PropertiesItem;

public final class CinderTools {
	private CinderTools() {}
	public static ItemType chooseType(final String type) {
		ItemType itemtype;
		
		if ("warning".equals(type)) {
			itemtype = ItemType.JAVA_INTERFACE;
		} else {
			itemtype = PropertiesItem.DEFAULT_TYPE;
		}
		
		return itemtype;
	}
}
