package org.art_core.dev.cinder.input;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.art_core.dev.cinder.CinderLog;
import org.art_core.dev.cinder.CinderTools;
import org.art_core.dev.cinder.model.IItem;
import org.art_core.dev.cinder.model.PropertiesItem;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlInputReader implements IInputHandler {
	private String sFilename = null;
	private final Collection<IItem> items = new ArrayList<IItem>();

	/**
	 * Reads an XML file from an URI.
	 * 
	 * @param String
	 *            the URI
	 */
	@Override
	public void readFromUri(final String sUri) {
		this.readFromFile(sUri, true);
	}

	/**
	 * Reads an XML file from the local file system.
	 * 
	 * @param String
	 *            the location of the file
	 */
	@Override
	public void readFromLocalFile(final String sFile) {
		this.readFromFile(sFile, false);
	}

	/**
	 * Reads an XML file from the workspace.
	 * 
	 * @param String
	 *            the file name
	 */
	@Override
	public void readFromWorkspaceFile(final String sWorkspaceFile) {
		final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		final String sPath = root.getLocation().toString();
		String sFilename;
		sFilename = sPath + "/" + sWorkspaceFile;
		CinderLog.logDebug("XIR:RFW:" + sPath + "_" + sWorkspaceFile);

		this.readFromFile(sFilename, false);
	}

	/**
	 * Reads an XML file.
	 * 
	 * @param sFile
	 *            the filename
	 * @param bRemote
	 *            Whether the file is given via URI
	 */
	protected void readFromFile(final String sFile, final boolean bRemote) {
		this.sFilename = sFile;
		File fXml = null;
		Document doc = null;

		final DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();

		try {
			final DocumentBuilder builder = fac.newDocumentBuilder();
			if (bRemote) {
				doc = builder.parse(sFile);
				CinderLog.logDebug("XIR:RFF_R:" + sFile);
			} else {
				fXml = new File(sFile);
				doc = builder.parse(fXml);
				CinderLog.logDebug("XIR:RFF_L:" + sFile + "_" + fXml.length());
			}

		} catch (Exception e) {
			CinderLog.logError(e);
		}
		this.parseDocument(doc);
	}

	/**
	 * Parses a Document.
	 * 
	 * @param doc
	 */
	protected void parseDocument(final Document doc) {
		try {
			PropertiesItem xItem;
			NodeList fileNodes, errorNodes;
			Integer eLine, eColumn;
			String eSeverity, eMessage, ePattern, sTargetFileName;
			Element fileElement, error;

			fileNodes = doc.getChildNodes().item(0).getChildNodes();
			// handle all <file name=""> sections
			for (int fIndex = 0; fIndex < fileNodes.getLength(); fIndex++) {
				// ignore whitespace
				final int iNodeType = fileNodes.item(fIndex).getNodeType();
				if (iNodeType == Node.ELEMENT_NODE) {
					fileElement = (Element) fileNodes.item(fIndex);
					sTargetFileName = fileElement.getAttribute("name");
					CinderLog.logDebug("XIR::read:" + sTargetFileName);
					errorNodes = fileElement.getChildNodes();
					// handle all <error line="" column="" severity=""
					// message="" pattern=""> sections
					for (int eIndex = 0; eIndex < errorNodes.getLength(); eIndex++) {
						// ignore whitespace
						if (errorNodes.item(eIndex).getNodeType() == Node.ELEMENT_NODE) {
							error = (Element) errorNodes.item(eIndex);

							eLine = Integer.valueOf(error.getAttribute("line"));
							eColumn = Integer.valueOf(error
									.getAttribute("column"));
							eSeverity = error.getAttribute("severity");
							eMessage = error.getAttribute("message");
							ePattern = error.getAttribute("pattern");
							CinderLog.logDebug("XIR:" + eLine + ":" + eColumn
									+ ":" + eSeverity);

							xItem = new PropertiesItem(ePattern,
									sTargetFileName, CinderTools
											.chooseType(eSeverity), eLine
											.intValue(), eColumn.intValue());
							xItem.setMessage(eMessage);
							items.add(xItem);
						}
					}
				}
			}
		} catch (Exception e) {
			CinderLog.logError(e);
		}
	}

	@Override
	public boolean isReadable() {
		return false;
	}

	/**
	 * Shows a list of the items read from XML.
	 * 
	 * @return Collection<IItem> the list
	 */
	@Override
	public Collection<IItem> getItems() {
		return this.items;
	}

	/**
	 * Shows the filename that was last accessed.
	 * 
	 * @return String the file name or URI
	 */
	@Override
	public String getFilename() {
		return this.sFilename;
	}

}
