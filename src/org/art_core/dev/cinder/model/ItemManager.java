package org.art_core.dev.cinder.model;

import java.util.ArrayList;
import java.util.Collection; //import java.util.Iterator;
import java.util.List;

public final class ItemManager {
	// Singleton
	private static ItemManager manager = new ItemManager();
	private Collection<IItem> items;
	private final List<ItemManagerListener> listeners = new ArrayList<ItemManagerListener>();

	private ItemManager() {
		this.reset();
	}

	public static ItemManager getManager() {
		return manager;
	}

	public IItem[] getItems() {
		return items.toArray(new IItem[items.size()]);
	}

	/**
	 * Resets to an empty state.
	 */
	public void reset() {
		this.items = new ArrayList<IItem>();
	}

	public void add(final IItem iiNewItem) {
		items.add(iiNewItem);
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

	/*
	 * private void fireItemsChanged(IItem[] itemsAdded, IItem[] itemsRemoved) {
	 * ItemManagerEvent event = new ItemManagerEvent(this, itemsAdded,
	 * itemsRemoved); for (Iterator<ItemManagerListener> iter =
	 * listeners.iterator(); iter.hasNext();) iter.next().itemsChanged(event); }
	 */
}
