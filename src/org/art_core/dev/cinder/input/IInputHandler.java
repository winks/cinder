package org.art_core.dev.cinder.input;

import java.util.Collection;

import org.art_core.dev.cinder.model.IItem;

public interface IInputHandler {
	
	public boolean isReadable();
	
	public void readFile(String fileName);
	
	public Collection<IItem> getItems();
	
}
