package org.art_core.dev.cinder.model;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Platform;

public class PropertiesItem implements IItem {
	private ItemType type;
	private ItemStatus status;
	private String name;
	private String location;
	private String message;
	private int line;
	private int offset;
	private int timestamp;

	private IResource resource;

	public static final ItemType DEFAULT_TYPE = ItemType.JAVA_PACKAGE;
	public static final int DEFAULT_LINE = 0;
	public static final int DEFAULT_OFFSET = 0;

	/*
	 * These are the Constructors
	 */
	public PropertiesItem(final String name) {
		this(name, "empty", DEFAULT_TYPE, DEFAULT_LINE, DEFAULT_OFFSET);
	}

	public PropertiesItem(final String name, final String loc) {
		this(name, loc, DEFAULT_TYPE, DEFAULT_LINE, DEFAULT_OFFSET);
	}

	public PropertiesItem(final String name, final String loc, final ItemType ty) {
		this(name, loc, ty, DEFAULT_LINE, DEFAULT_OFFSET);
	}

	public PropertiesItem(String name, String loc, int line) {
		this(name, loc, DEFAULT_TYPE, line, DEFAULT_OFFSET);
	}

	public PropertiesItem(final String name, final String loc, final int line,
			final int offset) {
		this(name, loc, DEFAULT_TYPE, line, offset);
	}

	/**
	 * Full constructor
	 * 
	 * @param name
	 * @param loc
	 * @param ty
	 * @param line
	 * @param offset
	 */
	public PropertiesItem(String name, String loc, ItemType type, int line,
			int offset) {
		this.name = name;
		this.location = loc;
		this.type = type;
		this.line = line;
		this.offset = offset;
		this.setTimestamp();
		this.setStatus(ItemStatus.NEW);
	}
	
	private int getCurrentTimestamp() {
		int iNow = (int) (System.currentTimeMillis()/1000L);
		return iNow;
	}
	
	public void setTimestamp() {
		this.timestamp = this.getCurrentTimestamp();
	}
	
	public void setTimestamp(int iTime) {
		this.timestamp = iTime;
	}
	
	public void setStatus(ItemStatus st) {
		this.status = st;
	}

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
	
	@Override
	public int getTimestamp() {
		return this.timestamp;
	}
	
	@Override
	public ItemStatus getStatus() {
		return this.status;
	}

	@Override
	public String getMessage() {
		return this.message;
	}

	public void setMessage(String msg) {
		this.message = msg;
	}
	
	public static ItemType chooseType(final String type) {
		ItemType itemtype;

		if ("warning".equals(type)) {
			itemtype = ItemType.TASK_WARN;
		} else if ("error".equals(type)) {
			itemtype = ItemType.TASK_ERROR;
		} else if ("info".equals(type)) {
			itemtype = ItemType.TASK_INFO;
		} else {
			itemtype = PropertiesItem.DEFAULT_TYPE;
		}

		return itemtype;
	}

	public String toString() {
		String sSep;
		sSep = ":";
		final StringBuilder str = new StringBuilder("");
		str.append(this.name);
		str.append(sSep);
		str.append(this.location);
		str.append(sSep);
		str.append(this.line);
		str.append(sSep);
		str.append(this.offset);
		str.append(sSep);
		str.append(this.type.toString());
		return str.toString();
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + line;
		result = prime * result
				+ ((location == null) ? 0 : location.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + offset;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PropertiesItem other = (PropertiesItem) obj;
		if (line != other.line)
			return false;
		if (location == null) {
			if (other.location != null)
				return false;
		} else if (!location.equals(other.location))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (offset != other.offset)
			return false;
		return true;
	}
}
