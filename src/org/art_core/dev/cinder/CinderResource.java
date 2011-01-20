package org.art_core.dev.cinder;

import java.util.ListResourceBundle;

import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * Localization for Cinder
 * @author Florian Anderiasch
 *
 */
public class CinderResource extends ListResourceBundle {

	@Override
	protected Object[][] getContents() {
		return contents;
	}
	
	static Object[][] contents = {
		{ "COLNAMES",
			  "", "Name", "Message", "Location", "Line", "Offset", "Status", "Changed" 
		},
		{ "TEXT_SET_STATUS",
			"Set Status to"
		},
		{ "DIALOG_READ_XML_FILE_TITLE",
			"Read XML from File"
		},
		{ "DIALOG_READ_XML_URL_TITLE",
			"Read XML from URL"
		},
		{ "DIALOG_READ_XML_URL_MESSAGE",
			"Please enter the URL of the XML file to open:"
		},
		{ "GENERAL_SETTINGS",
			"General settings for Cinder:"
		},
		{ "PREFERENCES",
			"Cinder Preferences"
		},
		{ "SHOW_DEBUG",
			"Show debug messages"
		},
		{ "XML_FILE",
			"XML File"
		},
		{ "XML_URL",
			"URL to XML"
		},
		{ "CHECK_PERIODICALLY",
			"Check periodically"
		},
		{ "INTERVAL_IN_MINUTES",
			"Interval in minutes:"
		},
		{ "TEXT_HIDE_ALL_MARKERS",
			"Hide all Markers"
		},
		{ "IMAGE_HIDE_ALL_MARKERS",
			PlatformUI.getWorkbench()
			.getSharedImages().getImageDescriptor(ISharedImages.IMG_ETOOL_CLEAR)
		},
		{ "TEXT_HIDE_SEL_MARKERS",
			"Hide Markers"
		},
		{ "IMAGE_HIDE_SEL_MARKERS",
			PlatformUI.getWorkbench()
			.getSharedImages().getImageDescriptor(ISharedImages.IMG_ETOOL_CLEAR)
		},
		{ "TEXT_SHOW_ALL_MARKERS",
			"Show all Markers"
		},
		{ "IMAGE_SHOW_ALL_MARKERS",
			PlatformUI.getWorkbench()
			.getSharedImages().getImageDescriptor(org.eclipse.ui.ide.IDE.SharedImages.IMG_OBJS_TASK_TSK)
		},
		{ "TEXT_SHOW_SEL_MARKERS",
			"Show Markers"
		},
		{ "IMAGE_SHOW_SEL_MARKERS",
			PlatformUI.getWorkbench()
			.getSharedImages().getImageDescriptor(org.eclipse.ui.ide.IDE.SharedImages.IMG_OBJS_TASK_TSK)
		},
		{ "TEXT_OPEN_FILE",
			"Open File"
		},
		{ "IMAGE_OPEN_FILE",
			PlatformUI.getWorkbench()
			.getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_FOLDER)
		},
		{ "TEXT_OPEN_URL",
			"Open URL"
		},
		{ "IMAGE_OPEN_URL",
			PlatformUI.getWorkbench()
			.getSharedImages().getImageDescriptor(org.eclipse.ui.ide.IDE.SharedImages.IMG_OBJS_BKMRK_TSK)
		},
		{ "TEXT_SHOW_DUMMY",
			"Show Dummy"
		},
		{ "IMAGE_SHOW_DUMMY",
			PlatformUI.getWorkbench()
			.getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_ELEMENT)
		},
		{ "TEXT_CLEAR_ALL",
			"Clear all entries"
		},
		{ "IMAGE_CLEAR_ALL",
			PlatformUI.getWorkbench()
			.getSharedImages().getImageDescriptor(ISharedImages.IMG_ELCL_REMOVE)
		},
		{ "TEXT_CLEAR_SEL",
			"Clear entries"
		},
		{ "IMAGE_CLEAR_SEL",
			PlatformUI.getWorkbench()
			.getSharedImages().getImageDescriptor(ISharedImages.IMG_ELCL_REMOVE)
		},
	};

}
