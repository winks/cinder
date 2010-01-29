package org.art_core.dev.cinder.prefs;

import org.art_core.dev.cinder.CinderPlugin;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Class used to initialize default preference values.
 */
public class CinderPrefInit extends AbstractPreferenceInitializer {
	
	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#
	 * initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = CinderPlugin.getDefault().getPreferenceStore();
		store.setDefault(CinderPrefPage.P_INTEGER + "_xml_url_time", "5");
		store.setDefault(CinderPrefPage.P_INTEGER + "_xml_file_time", "5");
	}

}
