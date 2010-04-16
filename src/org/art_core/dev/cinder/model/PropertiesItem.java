package org.art_core.dev.cinder.model;

import java.util.HashMap;

public class PropertiesItem extends AbstractCinderItem implements IItem {
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
	public PropertiesItem(String name, String loc, ItemType type, int line, int offset) {
		super();
		this.name = name;
		this.location = loc;
		this.type = type;
		this.line = line;
		this.offset = offset;
		this.details = new HashMap<String, String>();
		this.setTimestamp();
		this.setStatus(ItemStatus.NEW);
		this.setSource(ItemSource.UNKNOWN);
	}
	
	public String getDetail(String sKey) {
		return this.details.get(sKey);
	}
	
	
	public void setDetail(String sKey, String sVal) {
		this.details.put(sKey, sVal);
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
