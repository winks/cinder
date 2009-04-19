package org.art_core.dev.cinder.model;

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public abstract class ItemType implements Comparable<ItemType> {
	private static final ISharedImages PLATFORM_IMAGES =
		PlatformUI.getWorkbench().getSharedImages();
	
	private final String id;
	private final String printName;
	private final int ordinal;
	
	private ItemType(String id, String name, int position) {
		this.id = id;
		this.printName = name;
		this.ordinal = position;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return printName;
	}
	
	public abstract Image getImage();
	public abstract IItem newItem(Object obj);
	public abstract IItem loadItem(String info);
	
	public int compareTo(ItemType other) {
		return this.ordinal - other.ordinal;
	}
	
	public static final ItemType UNKNOWN 
		= new ItemType("Unknown", "Unknown", 0) 
	{
		public Image getImage() {
			return null;
		}
		public IItem newItem(Object obj) {
			return null;
		}
		public IItem loadItem(String info) {
			return null;
		}
	};
	
	public static final ItemType WORKBENCH_FILE 
		= new ItemType("WBFile", "Workbench File", 1) 
	{
		public Image getImage() {
			return PLATFORM_IMAGES
				.getImage(org.eclipse.ui.ISharedImages.IMG_OBJ_FILE);
		}

		public IItem newItem(Object obj) {
			if (!(obj instanceof IFile))
				return null;
			return null;
		}

		public IItem loadItem(String info) {
			return null;
		}
	};
	
	private static final ItemType[] TYPES = { UNKNOWN, WORKBENCH_FILE, };
  
	public static ItemType[] getTypes() {
		return TYPES;
	}
}
