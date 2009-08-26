package org.art_core.dev.cinder.model;

//
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Platform;


public class PropertiesItem implements IItem {
	private ItemType type;
	private String name;
	private String location;
	private String message;
	private int line;
	private int offset;
	public IFile rFile;
	
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
	
	public PropertiesItem(final String name, final String loc, final int line, final int offset) {
		this(name, loc, DEFAULT_TYPE, line, offset);
	}
	
	/**
	 * Full constructor
	 * @param name
	 * @param loc
	 * @param ty
	 * @param line
	 * @param offset
	 */
	public PropertiesItem(String name, String loc, ItemType type, int line, int offset) {
		this.name = name;
		this.location = loc;
		this.type = type;
		this.line = line;
		this.offset = offset;
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
	
	public String getMessage() {
		return this.message;
	}
	
	public void setMessage (String msg) {
		this.message = msg;
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
}
