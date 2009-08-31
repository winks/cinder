package org.art_core.dev.cinder.prefs;

import org.eclipse.jface.preference.*;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.art_core.dev.cinder.CinderPlugin;

/**
 * This class represents a preference page that is contributed to the
 * Preferences dialog. By subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows us to create a page
 * that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the
 * preference store that belongs to the main plug-in class. That way,
 * preferences can be accessed directly via the preference store.
 */

public class CinderPrefPage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	public CinderPrefPage() {
		super(GRID);
		setPreferenceStore(CinderPlugin.getDefault().getPreferenceStore());
		setDescription("A demonstration of a cinder preference page");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	public void createFieldEditors() {
		addField(new StringFieldEditor(CinderPrefTools.P_STRING + "_xml_url",
				"URL to XML", getFieldEditorParent()));

		addField(new FileFieldEditor(CinderPrefTools.P_STRING + "_xml_file",
				"XML File", getFieldEditorParent()));

		/*
		 * addField(new BooleanFieldEditor(CinderPrefTools.P_BOOLEAN,
		 * "&An example of a boolean preference - cinder",
		 * getFieldEditorParent()));
		 * 
		 * addField(new RadioGroupFieldEditor(CinderPrefTools.P_CHOICE,
		 * "An example of a multiple-choice preference - cinder", 1, new
		 * String[][] { { "&Choice 1", "choice1" }, { "C&hoice 2", "choice2" }
		 * }, getFieldEditorParent()));
		 * 
		 * addField(new StringFieldEditor(CinderPrefTools.P_STRING +
		 * "_highlight", "&highlight:", getFieldEditorParent()));
		 * 
		 * addField(new StringFieldEditor(CinderPrefTools.P_STRING + "_line",
		 * "&line number:", getFieldEditorParent()));
		 * 
		 * addField(new ColorFieldEditor(CinderPrefTools.P_COLOR, "boo",
		 * getFieldEditorParent()));
		 * 
		 * addField(new DirectoryFieldEditor(CinderPrefTools.P_STRING + "_d",
		 * "xy", getFieldEditorParent()));
		 * 
		 * addField(new FileFieldEditor(CinderPrefTools.P_STRING + "lkhölbhlb",
		 * "fdsafdsfs", getFieldEditorParent()));
		 * 
		 * addField(new FontFieldEditor(CinderPrefTools.P_STRING + "dsgdsgsdg",
		 * "ödskflj", getFieldEditorParent()));
		 * 
		 * addField(new IntegerFieldEditor( CinderPrefTools.P_STRING +
		 * "_sdfsdgsdg", "<yfdfds", getFieldEditorParent()));
		 * 
		 * addField(new PathEditor(CinderPrefTools.P_STRING + "ödosghks",
		 * "ldkhn", ".", getFieldEditorParent()));
		 * 
		 * addField(new ScaleFieldEditor(CinderPrefTools.P_STRING + "fdf", "sa",
		 * getFieldEditorParent()));
		 */
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

}