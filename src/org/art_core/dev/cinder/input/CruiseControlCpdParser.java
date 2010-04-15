package org.art_core.dev.cinder.input;

import java.util.Collection;

import org.art_core.dev.cinder.CinderLog;
import org.art_core.dev.cinder.model.IItem;
import org.art_core.dev.cinder.model.PropertiesItem;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class CruiseControlCpdParser {
	private NodeList fileNodes;
	private final Collection<IItem> items;
	
	public CruiseControlCpdParser(NodeList nodes, Collection<IItem> items) {
		this.fileNodes = nodes;
		this.items = items;
	}
	
	public void parse() {
		PropertiesItem pItem;
		Element fileElement, error;
		String eSeverity, eMessage, ePattern, sTargetFileName;
		int eLine, eColumn;
		NodeList errorNodes;
		
		// handle all <duplication lines="" tokens=""> sections
		for (int fIndex = 0; fIndex < fileNodes.getLength(); fIndex++) {
			// ignore whitespace
			final int iNodeType = fileNodes.item(fIndex).getNodeType();
			if (iNodeType == Node.ELEMENT_NODE) {
				fileElement = (Element) fileNodes.item(fIndex);
				//sTargetFileName = fileElement.getAttribute("name");
				//CinderLog.logDebug("CCPaP::read:" + sTargetFileName);
				errorNodes = fileElement.getElementsByTagName("file");
				
				// handle all <file path="" line=""> sections
				for (int eIndex = 0; eIndex < errorNodes.getLength(); eIndex++) {
					// ignore whitespace
					if (errorNodes.item(eIndex).getNodeType() == Node.ELEMENT_NODE) {
						error = (Element) errorNodes.item(eIndex);

						eLine = Integer.valueOf(error.getAttribute("line"));
						ePattern = "CPD";
						sTargetFileName = error.getAttribute("path");
						CinderLog.logDebug("CCCpdP:" + sTargetFileName + ":" + eLine);

						pItem = new PropertiesItem(ePattern,
									sTargetFileName, 
									PropertiesItem.chooseType("warning"), 
									eLine, 
									0);
						pItem.setMessage("Duplicates found");
						items.add(pItem);
					}
				}
			}
		}
	}
}
