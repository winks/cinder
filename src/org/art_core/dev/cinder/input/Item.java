package org.art_core.dev.cinder.input;

import org.eclipse.core.runtime.IAdaptable;

public interface Item /*extends IAdaptable*/ {
	
	public String getName();
	void setName(String newName);
	String getLocation();

	public String getDefinition();
	public String getFormatted();
	public String getOrigin();	
	public String getSource();
}
