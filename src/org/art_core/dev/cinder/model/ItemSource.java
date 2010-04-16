package org.art_core.dev.cinder.model;

/**
 * ItemSource for CINDER IItems
 * @author Florian Anderiasch
 *
 */
public enum ItemSource {
	UNKNOWN("UNKNOWN"),
	CHECKSTYLE("CHECKSTYLE"),
	CPD("CPD"),
	PADAWAN("PADAWAN"),
	PHPMD("PHPMD"),
	;
	
	private final String sName;
	
	ItemSource(String sName) {
		this.sName = sName;
	}
	
	public String toString() {
		return this.sName;
	}
}
