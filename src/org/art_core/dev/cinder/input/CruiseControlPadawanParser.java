package org.art_core.dev.cinder.input;

import java.util.Collection;

import org.art_core.dev.cinder.CinderLog;
import org.art_core.dev.cinder.model.IItem;
import org.art_core.dev.cinder.model.ItemSource;
import org.art_core.dev.cinder.model.PropertiesItem;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * XML Parser for CruiseControl Padawan data
 * @author Florian Anderiasch
 *
 */
public class CruiseControlPadawanParser {
	private NodeList fileNodes;
	private final Collection<IItem> items;
	
	public CruiseControlPadawanParser(NodeList nodes, Collection<IItem> items) {
		this.fileNodes = nodes;
		this.items = items;
	}
	
	public void parse() {
		PropertiesItem pItem;
		Element fileElement, error;
		String eSeverity, eMessage, ePattern, sTargetFileName;
		int eLine, eColumn;
		NodeList errorNodes;
		
		// handle all <file name=""> sections
		for (int fIndex = 0; fIndex < fileNodes.getLength(); fIndex++) {
			// ignore whitespace
			final int iNodeType = fileNodes.item(fIndex).getNodeType();
			if (iNodeType == Node.ELEMENT_NODE) {
				fileElement = (Element) fileNodes.item(fIndex);
				sTargetFileName = fileElement.getAttribute("name");
				CinderLog.logDebug("CCPaP::read:" + sTargetFileName);
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
						CinderLog.logDebug("XIR:" + eLine + ":" + eColumn + ":" + eSeverity);

						pItem = new PropertiesItem(ePattern,
									sTargetFileName, 
									PropertiesItem.chooseType(eSeverity), 
									eLine, 
									eColumn);
						pItem.setMessage(eMessage);
						pItem.setSource(ItemSource.PADAWAN);
						items.add(pItem);
					}
				}
			}
		}
	}
}
