package org.art_core.dev.cinder.model;

import java.util.HashMap;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Platform;

/**
 * An abstract item class for every need.
 * @author Florian Anderiasch
 *
 */
public abstract class AbstractCinderItem implements IItem {
	protected ItemType type;
	protected ItemStatus status;
	protected ItemSource source;
	protected String name;
	protected String location;
	protected String message;
	protected int line;
	protected int offset;
	protected int timestamp;
	protected HashMap<String, String> details;
	protected IResource resource;
	
	public static final ItemType DEFAULT_TYPE = ItemType.JAVA_PACKAGE;
	public static final ItemSource DEFAULT_SOURCE = ItemSource.UNKNOWN;
	public static final int DEFAULT_LINE = 0;
	public static final int DEFAULT_OFFSET = 0;
	
	public AbstractCinderItem() {
		this.name = "Abstract";
		this.location = "bogus";
		this.line = DEFAULT_LINE;
		this.offset = DEFAULT_OFFSET;
		this.type = DEFAULT_TYPE;
		this.setSource(DEFAULT_SOURCE);
		this.setStatus(ItemStatus.NEW);
		this.details = new HashMap<String, String>();
		this.setTimestamp();
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
	
	@Override
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

	@Override
	public ItemSource getSource() {
		return this.source;
	}
	
	@Override
	public HashMap<String, String> getDetails() {
		return this.details;
	}
	
	public void setMessage(String msg) {
		this.message = msg;
	}
	
	public void setSource(ItemSource src) {
		this.source = src;
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
			itemtype = DEFAULT_TYPE;
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
		str.append(sSep);
		str.append(this.source.toString());
		return str.toString();
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
