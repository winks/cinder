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
	private final String sFilename;
	private final Collection<IItem> items = new ArrayList<IItem>();
	
	public XmlInputReader(final String file) {
		final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		final String sPath = root.getLocation().toString();
		sFilename = sPath + "/" + file;

		CinderLog.logInfo("XIR:path:" + sPath + "_" + sFilename);
	}

	@Override
	public boolean isReadable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void readFile() {		
		final File fXml = new File(sFilename);
		CinderLog.logInfo("XIR:len:"+fXml.length());
		final DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
		try {
			final DocumentBuilder builder	= fac.newDocumentBuilder();
			Document doc = builder.parse(fXml);

			PropertiesItem xItem;
			NodeList fileNodes, errorNodes;
			Integer eLine, eColumn;
			String eSeverity, eMessage, ePattern, sTargetFileName;			
			Element fileElement, error;
			
			fileNodes = doc.getChildNodes().item(0).getChildNodes();
			// handle all <file name=""> sections
			for (int fIndex = 0; fIndex < fileNodes.getLength(); fIndex++) {
				// ignore whitespace
				if (fileNodes.item(fIndex).getNodeType() == Node.ELEMENT_NODE) {
					fileElement = (Element)fileNodes.item(fIndex);
					sTargetFileName = fileElement.getAttribute("name");
					CinderLog.logInfo("XIR::read:" + sTargetFileName);
					errorNodes = fileElement.getChildNodes();
					// handle all <error line="" column="" severity="" message="" pattern=""> sections
					for (int eIndex = 0; eIndex < errorNodes.getLength(); eIndex++) {
						// ignore whitespace
						if (errorNodes.item(eIndex).getNodeType() == Node.ELEMENT_NODE) {
							error = (Element)errorNodes.item(eIndex);
							
							eLine = Integer.valueOf(error.getAttribute("line"));
							eColumn = Integer.valueOf(error.getAttribute("column"));
							eSeverity = error.getAttribute("severity");
							eMessage = error.getAttribute("message");
							ePattern = error.getAttribute("pattern");
							
							xItem = new PropertiesItem(ePattern, sTargetFileName,
									CinderTools.chooseType(eSeverity),
									eLine.intValue(), eColumn.intValue());
							xItem.setMessage(eMessage);
							items.add(xItem);
						}
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			CinderLog.logError(e);
		}

	}
	
	@Override
	public Collection<IItem> getItems() {
		return this.items;
	}
	
	@Override
	public String getFilename() {
		return this.sFilename;
	}

}
