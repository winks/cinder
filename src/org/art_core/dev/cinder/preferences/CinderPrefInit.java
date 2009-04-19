package org.art_core.dev.cinder.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import org.art_core.dev.cinder.CinderPlugin;

/**
 * Class used to initialize default preference values.
 */
public class CinderPrefInit extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = CinderPlugin.getDefault().getPreferenceStore();
		store.setDefault(CinderPrefTools.P_BOOLEAN, true);
		store.setDefault(CinderPrefTools.P_CHOICE, "choice2");
		store.setDefault(CinderPrefTools.P_STRING,
				"Default value");
	}

}
