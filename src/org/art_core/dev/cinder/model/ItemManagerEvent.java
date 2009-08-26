package org.art_core.dev.cinder.model;

import java.util.EventObject;

public class ItemManagerEvent extends EventObject  {
	private static final long serialVersionUID = 3697053173951102953L;

	private final IItem[] added;
	private final IItem[] removed;

	public ItemManagerEvent(
			final ItemManager source,
			final IItem[] itemsAdded, 
			final IItem[] itemsRemoved
	) {
		super(source);
		added = itemsAdded.clone();
		removed = itemsRemoved.clone();
	}

	public IItem[] getItemsAdded() {
		return added.clone();
	}

	public IItem[] getItemsRemoved() {
		return removed.clone();
	}
}
