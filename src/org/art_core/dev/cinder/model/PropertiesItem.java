package org.art_core.dev.cinder.model;

//import java.util.Hashtable;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;


public class PropertiesItem implements IItem {
	//private Hashtable<String, String> htProp;
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
	public static final int DEFAULT_OFFSET = -1;
	
	/*
	 * These are the Constructors
	 */
	public PropertiesItem(String name) {
		this(name, "empty", DEFAULT_TYPE, DEFAULT_LINE, DEFAULT_OFFSET);
	}
	
	public PropertiesItem(String name, String loc) {
		this(name, loc, DEFAULT_TYPE, DEFAULT_LINE, DEFAULT_OFFSET);
	}
	
	public PropertiesItem(String name, String loc, ItemType ty) {
		this(name, loc, ty, DEFAULT_LINE, DEFAULT_OFFSET);
	}
	
	public PropertiesItem(String name, String loc, int line) {
		this(name, loc, DEFAULT_TYPE, line, DEFAULT_OFFSET);
	}
	
	public PropertiesItem(String name, String loc, int line, int offset) {
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
	public PropertiesItem(String name, String loc, ItemType ty, int line, int offset) {
		this.name = name;
		this.location = loc;
		this.type = ty;
		this.line = line;
		this.offset = offset;
		Path p = new Path("HelloWorldASD/" + loc);
		this.rFile = ResourcesPlugin.getWorkspace().getRoot().getFile(p);
		/*try {
			IMarker marker = rFile.createMarker("warning");
			marker.setAttribute(IMarker.MESSAGE, name + ": " + line);
			marker.setAttribute(IMarker.CHAR_START, 50);
			marker.setAttribute(IMarker.CHAR_END, 70);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
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
	
	public String getMessage() {
		return this.message;
	}
	
	public void setMessage (String msg) {
		this.message = msg;
	}
	
	public String toString() {
		String s = "";
		s += this.name + ":";
		s += this.location + ":";
		s += this.line + ":";
		s += this.offset + ":";
		s += this.type.toString();
		return s;
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
