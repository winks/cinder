package org.art_core.dev.cinder.model;

import java.util.HashMap;
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
	
	ItemSource getSource();
	
	HashMap<String, String> getDetails();
	
	ItemType getType();
	
	void setStatus(ItemStatus status);

	IItem[] NONE = new IItem[] {};
}
