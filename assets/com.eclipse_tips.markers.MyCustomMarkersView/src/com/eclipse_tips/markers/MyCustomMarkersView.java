package com.eclipse_tips.markers;

import org.eclipse.ui.views.markers.MarkerSupportView;

public class MyCustomMarkersView extends MarkerSupportView  {
	public MyCustomMarkersView() {
		super("com.eclipse-tips.markers.myCustomMarkerGenerator"); 
	}
}