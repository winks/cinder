package org.art_core.dev.cinder.model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * An Item class for Duplicates as found by CPD
 * @author Florian Anderiasch
 *
 */
public class DuplicateItem extends AbstractCinderItem implements IItem {
	private ArrayList<HashMap<String, Object>> sAlternatives;
	
	public DuplicateItem(String sName, String sFile, int iLine) {
		super();
		this.name = sName;
		this.location = sFile;
		this.line = iLine;
		this.type = ItemType.TASK_INFO;
		this.sAlternatives = new ArrayList<HashMap<String, Object>>();
	}
	
	public void setSource(ItemSource src) {
		this.source = src;
	}
	
	public void addAlternative(String sFile, int iLine) {
		HashMap<String, Object> x = new HashMap<String, Object>();
		x.put("file", sFile);
		x.put("line", iLine);
		this.sAlternatives.add(x);
		System.out.println("d");
		//
	}
	
	public String getAlternatives() {
		int iLen = sAlternatives.size();
		String sAlt = "Dup: " + iLen + ": ";
		
		try {
			for (HashMap<String, Object> alt : sAlternatives) {
				sAlt = sAlt 
				+ (String)alt.get("file") + " : " + alt.get("line").toString() + " || ";
			}
		} catch (NullPointerException e) {
			
		}
		return sAlt;
	}
	
	public String toString() {
		String s = super.toString();
		s.concat(this.getAlternatives());
		return s;
	}

}
