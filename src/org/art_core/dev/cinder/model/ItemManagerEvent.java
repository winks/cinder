package org.art_core.dev.cinder.model;

import java.util.EventObject;

public class ItemManagerEvent extends EventObject  {
	private static final long serialVersionUID = 3697053173951102953L;

	private final IItem[] added;
	private final IItem[] removed;

	public ItemManagerEvent(
			ItemManager source,
			IItem[] itemsAdded, 
			IItem[] itemsRemoved
	) {
		super(source);
		added = itemsAdded;
		removed = itemsRemoved;
	}

	public IItem[] getItemsAdded() {
		return added;
	}

	public IItem[] getItemsRemoved() {
		return removed;
	}
}
