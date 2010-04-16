package org.art_core.dev.cinder.input;

import java.util.Collection;

import org.art_core.dev.cinder.CinderLog;
import org.art_core.dev.cinder.model.DuplicateItem;
import org.art_core.dev.cinder.model.IItem;
import org.art_core.dev.cinder.model.ItemSource;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * XML Parser for CruiseControl CPD data
 * @author Florian Anderiasch
 *
 */
public class CruiseControlCpdParser {
	private NodeList fileNodes;
	private final Collection<IItem> items;
	
	public CruiseControlCpdParser(NodeList nodes, Collection<IItem> items) {
		this.fileNodes = nodes;
		this.items = items;
	}
	
	public void parse() {
		DuplicateItem[] dItems;
		Element fileElement, error;
		String eSeverity, eMessage, ePattern, sTargetFileName;
		int eLine, eColumn, eLen;
		NodeList errorNodes;
		//HashMap<String, Object>[] sAlternatives;
		
		// handle all <duplication lines="" tokens=""> sections
		for (int fIndex = 0; fIndex < fileNodes.getLength(); fIndex++) {
			// ignore whitespace
			final int iNodeType = fileNodes.item(fIndex).getNodeType();
			if (iNodeType == Node.ELEMENT_NODE) {
				fileElement = (Element) fileNodes.item(fIndex);
				errorNodes = fileElement.getElementsByTagName("file");
				eLen = errorNodes.getLength();
				dItems = new DuplicateItem[eLen];
				
				// handle all <file path="" line=""> sections
				for (int eIndex = 0; eIndex < eLen; eIndex++) {
					// ignore whitespace
					if (errorNodes.item(eIndex).getNodeType() == Node.ELEMENT_NODE) {
						error = (Element) errorNodes.item(eIndex);

						eLine = Integer.valueOf(error.getAttribute("line"));
						sTargetFileName = error.getAttribute("path");
						
						CinderLog.logDebug("CCCpdP:" + sTargetFileName + ":" + eLine);

						dItems[eIndex] = new DuplicateItem("CPD_Finding", sTargetFileName, eLine);
						dItems[eIndex].setSource(ItemSource.CPD);
						
					}
				}
				CinderLog.logDebug("CCCpdP:_foo: " + eLen);
				for (int eIndex = 0; eIndex < eLen; eIndex++) {
					for (int eIndex2 = 0; eIndex2 < eLen; eIndex2++) {
						if (eIndex2 != eIndex) {
							sTargetFileName = dItems[eIndex2].getLocation();
							eLine = dItems[eIndex2].getLine();
							dItems[eIndex].addAlternative(sTargetFileName, eLine);
							CinderLog.logDebug("CCCpdP:_adding alt: " + sTargetFileName + ":" + eLine);
						}
					}
					dItems[eIndex].setMessage(dItems[eIndex].getAlternatives());
					items.add(dItems[eIndex]);
				}
				
			}
		}
	}
}
