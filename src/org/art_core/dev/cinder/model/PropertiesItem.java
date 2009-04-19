package org.art_core.dev.cinder.model;

import java.util.Hashtable;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Platform;


public class PropertiesItem implements IItem {
	private Hashtable htProp;
	private ItemType type;
	private IResource resource;
	private String name;
	
	public PropertiesItem(String name) {
		this.name = name;
		this.type = ItemType.UNKNOWN;
	}
	
	public PropertiesItem(Hashtable ht) {
		htProp = ht;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void setName(String newName) {
		
	}
	
	@Override
	public String getLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ItemType getType() {
		return type;
	}

	// For now, this is how we suppress a warning that we cannot fix
	// See Bugzilla #163093 and Bugzilla #149805 comment #14
	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		return getAdapterDelegate(adapter);
	}

	private Object getAdapterDelegate(Class<?> adapter) {
		if (adapter.isInstance(resource)) {
			return resource;
		}
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}
}
