package com.eclipse_tips.markers;

import org.eclipse.ui.views.markers.MarkerField;
import org.eclipse.ui.views.markers.MarkerItem;

public class ProjectField extends MarkerField {

	public ProjectField() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getValue(MarkerItem item) {
		return item.getMarker().getResource().getProject().getName();
	}

}
