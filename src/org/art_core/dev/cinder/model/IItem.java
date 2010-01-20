package org.art_core.dev.cinder.model;

import org.eclipse.core.runtime.IAdaptable;

/*
 * Interface for Items in Properties.
 * 
 */
public interface IItem extends IAdaptable {
	String getName();

	String getLocation();

	int getLine();

	int getOffset();
	
	int getLastChanged();
	
	ItemStatus getStatus();

	ItemType getType();

	IItem[] NONE = new IItem[] {};
}
