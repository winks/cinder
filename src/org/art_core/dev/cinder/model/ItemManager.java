package org.art_core.dev.cinder.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.art_core.dev.cinder.input.PropertiesInputReader;

public class ItemManager {
	private static ItemManager manager;
	private Collection<IItem> items;
	private List<ItemManagerListener> listeners =
        new ArrayList<ItemManagerListener>();
	
	private ItemManager() {
	}
	
	public static ItemManager getManager() {
		if (manager == null){
			manager = new ItemManager();
		}
		return manager;
	}
	
	public IItem[] getItems() {
		if (items == null) {
			items = new ArrayList<IItem>();
			items.add(new PropertiesItem("xyz"));
			items.add(new PropertiesItem("foo", "bar"));
			items.add(new PropertiesItem("foo", "WORKBENCH_FOLDER", ItemType.WORKBENCH_FOLDER));
			items.add(new PropertiesItem("foo", "WORKBENCH_PROJECT", ItemType.WORKBENCH_PROJECT));
			items.add(new PropertiesItem("foo", "JAVA_CLASS", ItemType.JAVA_CLASS));
			items.add(new PropertiesItem("foo", "JAVA_CLASS_FILE", ItemType.JAVA_CLASS_FILE));
			items.add(new PropertiesItem("foo", "JAVA_COMP_UNIT", ItemType.JAVA_COMP_UNIT));
			items.add(new PropertiesItem("foo", "JAVA_INTERFACE", ItemType.JAVA_INTERFACE));
			items.add(new PropertiesItem("foo", "JAVA_PACKAGE", ItemType.JAVA_PACKAGE));
			items.add(new PropertiesItem("foo", "JAVA_PACKAGE_ROOT", ItemType.JAVA_PACKAGE_ROOT));
			items.add(new PropertiesItem("foo", "JAVA_PROJECT", ItemType.JAVA_PROJECT));
			PropertiesInputReader pir = new PropertiesInputReader("cinder.properties");
			Collection<IItem> coll = pir.getItems();
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
	
	public void addItemManagerListener(ItemManagerListener listener) {
		if (!listeners.contains(listener))
			listeners.add(listener);
	}
	
	public void removeItemManagerListener(ItemManagerListener listener) {
		listeners.remove(listener);
	}
	
	private void fireItemsChanged(IItem[] itemsAdded, IItem[] itemsRemoved) {
		ItemManagerEvent event = new ItemManagerEvent(this, itemsAdded, itemsRemoved);
		for (Iterator<ItemManagerListener> iter = listeners.iterator(); iter.hasNext();)
			iter.next().itemsChanged(event);
	}
}
