package org.art_core.dev.cinder.model;

public enum ItemStatus {
	NEW("NEW"),
	DONE("DONE"),
	WONTFIX("WONTFIX");
	
	private final String sName;
	
	ItemStatus(String sName) {
		this.sName = sName;
	}
	
	public String toString() {
		return this.sName;
	}
}
