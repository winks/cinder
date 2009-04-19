package org.art_core.dev.cinder.model;

import java.util.Hashtable;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Platform;


public class PropertiesItem implements IItem {
	//private Hashtable<String, String> htProp;
	private ItemType type;
	private String name;
	private String location;
	private int line;
	private int offset;
	
	private IResource resource;
	
	public PropertiesItem(String name) {
		this(name, "empty", ItemType.WORKBENCH_FILE);
	}
	
	public PropertiesItem(String name, String loc) {
		this(name, loc, ItemType.WORKBENCH_FILE);
	}
	
	public PropertiesItem(String name, String loc, ItemType ty) {
		this.name = name;
		this.location = loc;
		this.type = ty;
		this.line = 2;
		this.offset = 4;
	}
	
	/*public PropertiesItem(Hashtable ht) {
		htProp = ht;
	}*/

	@Override
	public String getName() {
		return this.name;
	}
	
	@Override
	public String getLocation() {
		return this.location;
	}

	@Override
	public ItemType getType() {
		return this.type;
	}
	
	@Override
	public int getLine() {
		return this.line;
	}

	@Override
	public int getOffset() {
		return this.offset;
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
