package org.art_core.dev.cinder.model;

import org.eclipse.core.runtime.IAdaptable;

/*
 * Interface for Items in Properties.
 * 
 */
public interface IItem extends IAdaptable {
	String getName();

	String getLocation();
	
	String getMessage();

	int getLine();

	int getOffset();
	
	int getTimestamp();
	
	ItemStatus getStatus();
	
	void setStatus(ItemStatus status);

	ItemType getType();

	IItem[] NONE = new IItem[] {};
}
