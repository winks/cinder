package org.art_core.dev.cinder;

import java.util.ListResourceBundle;

public class CinderResource extends ListResourceBundle {

	@Override
	protected Object[][] getContents() {
		return contents;
	}
	
	static Object[][] contents = {
		{ "TEXT_SET_STATUS",
			"Set status to"
		},
		{ "DIALOG_READ_XML_URL_TITLE",
			"Read XML from URL"
		},
		{ "DIALOG_READ_XML_URL_MESSAGE",
			"Please enter the URL of the XML file to open:"
		}
		
	};

}
