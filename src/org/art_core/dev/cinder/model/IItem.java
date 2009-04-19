package org.art_core.dev.cinder.model;

import org.eclipse.core.runtime.IAdaptable;

public interface IItem 
	extends IAdaptable
{	
	String getName();
	void setName(String newName);
	String getLocation();
	
	ItemType getType();
	
	
	static IItem[] NONE = new IItem[] {};
}
