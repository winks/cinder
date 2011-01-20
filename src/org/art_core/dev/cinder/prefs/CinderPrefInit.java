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
		store.setDefault(CinderPrefPage.P_INTEGER + "_xml_url_1_time", "5");
		store.setDefault(CinderPrefPage.P_BOOLEAN + "_xml_url_1_check", false);
		
		store.setDefault(CinderPrefPage.P_INTEGER + "_xml_url_2_time", "5");
		store.setDefault(CinderPrefPage.P_BOOLEAN + "_xml_url_2_check", false);
		
		store.setDefault(CinderPrefPage.P_INTEGER + "_xml_url_3_time", "5");
		store.setDefault(CinderPrefPage.P_BOOLEAN + "_xml_url_3_check", false);
		
		store.setDefault(CinderPrefPage.P_INTEGER + "_xml_file_1_time", "5");
		store.setDefault(CinderPrefPage.P_BOOLEAN + "_xml_file_1_check", false);
		
		store.setDefault(CinderPrefPage.P_INTEGER + "_xml_file_2_time", "5");
		store.setDefault(CinderPrefPage.P_BOOLEAN + "_xml_file_2_check", false);
		
		store.setDefault(CinderPrefPage.P_INTEGER + "_xml_file_3_time", "5");
		store.setDefault(CinderPrefPage.P_BOOLEAN + "_xml_file_3_check", false);
	}

}
