package org.art_core.dev.cinder.input;

import java.util.Collection;

import org.art_core.dev.cinder.model.IItem;

public interface IInputHandler {
	
	boolean isReadable();
	
	void readFile();
	
	Collection<IItem> getItems();
	
	String getFilename();
}
