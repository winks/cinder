package org.art_core.dev.cinder.builder;

import java.io.ByteArrayInputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IMarkerResolution2;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

import org.art_core.dev.cinder.CinderLog;

/**
 * An instance of CreatePropertyKeyResolution is returned for missing property
 * key violations. If the user selects this resolution, the run() method is
 * executed, opening or activating the properties editor and appending a new
 * property key/value pair.
 */
public class CreatePropertyKeyResolution
	implements IMarkerResolution2
{
	public String getDescription() {
		return "Append a new property key/value pair" + " to the plugin.properties file";
	}

	public Image getImage() {
		return null;
	}

	public String getLabel() {
		return "Create a new property key";
	}

	public void run(final IMarker marker) {
		//Get the corresponding plugin.properties.
		final IFile file =
			marker.getResource().getParent().getFile(new Path("plugin.properties"));
		if (!file.exists()) {
			final ByteArrayInputStream stream = new ByteArrayInputStream(new byte[] {});
			try {
				file.create(stream, false, null);
			}
			catch (CoreException e) {
				CinderLog.logError(e);
				return;
			}
		}

		//Open or activate the editor.
		final IWorkbenchPage page =
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IEditorPart part;
		try {
			part = IDE.openEditor(page, file, true);
		}
		catch (PartInitException e) {
			CinderLog.logError(e);
			return;
		}

		// Get the editor’s document.
		if (!(part instanceof ITextEditor)) {
			return;
		}
		final ITextEditor editor = (ITextEditor) part;
		final IDocument doc = editor.getDocumentProvider().getDocument(new FileEditorInput(file));

		// Determine the text to be added.
		String key;
		try {
			key = (String) marker.getAttribute(PropertiesFileAuditor.KEY);
		}
		catch (CoreException e) {
			CinderLog.logError(e);
			return;
		}
		String text = key + "=Value for " + key;

		// If necessary, add a newline.
		int index = doc.getLength();
		if (index > 0) {
			char cTest;
			try {
				cTest = doc.getChar(index - 1);
			}
			catch (BadLocationException e) {
				CinderLog.logError(e);
				return;
			}
			if (cTest != '\r' || cTest != '\n') {
				text = System.getProperty("line.separator") + text;
			}
		}

		// Append the new text.
		try {
			doc.replace(index, 0, text);
		}
		catch (BadLocationException e) {
			CinderLog.logError(e);
			return;
		}

		// Select the value so the user can type.
		index += text.indexOf('=') + 1;
		editor.selectAndReveal(index, doc.getLength() - index);
	}
}
