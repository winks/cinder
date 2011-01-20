package org.art_core.dev.cinder.prefs;

import org.eclipse.jface.preference.*;
import org.eclipse.osgi.framework.internal.protocol.MultiplexingURLStreamHandler;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.art_core.dev.cinder.CinderLog;
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

	public static final String P_PATH = "pathPreference";
	public static final String P_BOOLEAN = "booleanPreference";
	public static final String P_CHOICE = "choicePreference";
	public static final String P_STRING = "stringPreference";
	public static final String P_COLOR = "colorPreference";
	public static final String P_INTEGER = "integerPreference";
	public static final String DESCRIPTION = "General settings for Cinder:";
	public static final String MESSAGE = "Cinder Preferences";
	
	public static final int STRING_FIELD = 1;
	public static final int FILE_FIELD = 2;
	
	public CinderPrefPage() {
		super(GRID);
		setPreferenceStore(CinderPlugin.getDefault().getPreferenceStore());
		setDescription(CinderPrefPage.DESCRIPTION);
		setMessage(CinderPrefPage.MESSAGE);
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	public void createFieldEditors() {
		addImportSource(STRING_FIELD, "xml_url", 1);
		addImportSource(STRING_FIELD, "xml_url", 2);
		addImportSource(FILE_FIELD, "xml_file", 1);
		addImportSource(FILE_FIELD, "xml_file", 2);
		
		addField(new BooleanFieldEditor(CinderPrefPage.P_BOOLEAN + "_show_debug", 
				"Show debug messages", getFieldEditorParent()));
	}

	private void addImportSource(int iMode, String sIdentifier, int iNumber) {
		String sNameString = CinderPrefPage.P_STRING + "_" + sIdentifier + "_" + iNumber;
		String sNameInt = CinderPrefPage.P_INTEGER + "_" + sIdentifier + "_" + iNumber;
		String sNameBool = CinderPrefPage.P_BOOLEAN + "_" + sIdentifier + "_" + iNumber;
		
		if (iMode == FILE_FIELD) {
			addField(new FileFieldEditor(sNameString,
				"XML File", getFieldEditorParent()));
		} else {
			addField(new StringFieldEditor(sNameString,
					"URL to XML", getFieldEditorParent()));
		}
		addField(new BooleanFieldEditor(sNameBool + "_check", 
				"Check periodically", getFieldEditorParent()));
		IntegerFieldEditor ifeUrl = new IntegerFieldEditor(sNameInt + "_time", 
				"Interval in minutes:", getFieldEditorParent());
		ifeUrl.setTextLimit(3);
		ifeUrl.setValidRange(1, 999);
		addField(ifeUrl);
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