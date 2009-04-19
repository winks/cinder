package org.art_core.dev.cinder.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

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
			items.add(new PropertiesItem("foo"));
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
