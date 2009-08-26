package org.art_core.dev.cinder.model;

import java.util.ArrayList;
import java.util.Collection;
//import java.util.Iterator;
import java.util.List;

import org.art_core.dev.cinder.input.PropertiesInputReader;
import org.art_core.dev.cinder.input.XmlInputReader;

public final class ItemManager {
	// Singleton
	private static ItemManager manager = new ItemManager();
	private Collection<IItem> items;
	private List<ItemManagerListener> listeners =
        new ArrayList<ItemManagerListener>();
	
	private ItemManager() {}
	
	public static ItemManager getManager() {
		return manager;
	}
	
	public IItem[] getItems() {
		if (items == null) {
			items = new ArrayList<IItem>();
			
			String sKey = "abc";
			// this a bogus list for debugging
			items.add(new PropertiesItem(sKey));
			items.add(new PropertiesItem(sKey, "bar"));
			items.add(new PropertiesItem(sKey, "WORKBENCH_FOLDER", ItemType.WORKBENCH_FOLDER));
			items.add(new PropertiesItem(sKey, "WORKBENCH_PROJECT", ItemType.WORKBENCH_PROJECT));
			items.add(new PropertiesItem(sKey, "JAVA_CLASS", ItemType.JAVA_CLASS));
			items.add(new PropertiesItem(sKey, "JAVA_CLASS_FILE", ItemType.JAVA_CLASS_FILE));
			items.add(new PropertiesItem(sKey, "JAVA_COMP_UNIT", ItemType.JAVA_COMP_UNIT));
			items.add(new PropertiesItem(sKey, "JAVA_INTERFACE", ItemType.JAVA_INTERFACE));
			items.add(new PropertiesItem(sKey, "JAVA_PACKAGE", ItemType.JAVA_PACKAGE));
			items.add(new PropertiesItem(sKey, "JAVA_PACKAGE_ROOT", ItemType.JAVA_PACKAGE_ROOT));
			items.add(new PropertiesItem(sKey, "JAVA_PROJECT", ItemType.JAVA_PROJECT));
			// end bogus list
			
			// read from properties file
			final PropertiesInputReader pir = new PropertiesInputReader("cinder.properties");
			pir.readFile();
			Collection<IItem> coll = pir.getItems();
			for(IItem item : coll) {
				items.add(item);
			}
			
			// read from XML file
			final XmlInputReader xir = new XmlInputReader("cinder.xml");
			xir.readFile();
			coll = xir.getItems();
			for(IItem item : coll) {
				items.add(item);
			}
		}
		return items.toArray(new IItem[items.size()]);
	}
	
	// /////////////////////////////////////////////////////////////////////////
	//
	// Event Handling
	//
	// /////////////////////////////////////////////////////////////////////////
	
	public void addItemManagerListener(final ItemManagerListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}
	
	public void removeItemManagerListener(final ItemManagerListener listener) {
		listeners.remove(listener);
	}
	
	/*private void fireItemsChanged(IItem[] itemsAdded, IItem[] itemsRemoved) {
		ItemManagerEvent event = new ItemManagerEvent(this, itemsAdded, itemsRemoved);
		for (Iterator<ItemManagerListener> iter = listeners.iterator(); iter.hasNext();)
			iter.next().itemsChanged(event);
	}*/
}
