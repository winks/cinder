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
 * XML Parser for CruiseControl PHPMD data
 * @author Florian Anderiasch
 *
 */
public class CruiseControlPMDParser {
	private NodeList fileNodes;
	private final Collection<IItem> items;
	
	public CruiseControlPMDParser(NodeList nodes, Collection<IItem> items) {
		this.fileNodes = nodes;
		this.items = items;
	}
	
	public void parse() throws Exception {
		PropertiesItem pItem;
		Element fileElement, error;
		String eSeverity, eMessage, ePattern, sTargetFileName; 
		String eExternalInfoUrl, eRuleSet, ePackage, eClass;
		int eBeginLine, eEndLine;
		NodeList violationNodes;
		
		// handle all <file name=""> sections
		for (int fIndex = 0; fIndex < fileNodes.getLength(); fIndex++) {
			// ignore whitespace
			final int iNodeType = fileNodes.item(fIndex).getNodeType();
			if (iNodeType == Node.ELEMENT_NODE) {
				fileElement = (Element) fileNodes.item(fIndex);
				sTargetFileName = fileElement.getAttribute("name");
				CinderLog.logDebug("CCPMP::read:" + sTargetFileName);
				violationNodes = fileElement.getChildNodes();
				
				// handle all <violation beginline="" endline="" priority=""
				// rule="" ruleset="" package="" class="" externalInfoUrl=""> sections
				for (int eIndex = 0; eIndex < violationNodes.getLength(); eIndex++) {
					// ignore whitespace
					if (violationNodes.item(eIndex).getNodeType() == Node.ELEMENT_NODE) {
						error = (Element) violationNodes.item(eIndex);

						eBeginLine = Integer.valueOf(error.getAttribute("beginline"));
						eEndLine = Integer.valueOf(error.getAttribute("endline"));
						ePattern = error.getAttribute("rule");
						eSeverity = error.getAttribute("priority");
						
						switch (Integer.parseInt(eSeverity)) {
						case 5:
						case 4:
							eSeverity = "error";
							break;
						case 3:
						case 2:
						case 1:
							eSeverity = "warning";
							break;

						default:
							eSeverity = "info";
							break;
						}
						
						eRuleSet = error.getAttribute("ruleset");
						ePackage = error.getAttribute("package");
						eExternalInfoUrl = error.getAttribute("externalInfoUrl");
						eClass = error.getAttribute("class");
						eMessage = error.getTextContent().trim();
						
						CinderLog.logDebug("CCPmdP:" + eMessage + "_" + eBeginLine + ":" + eEndLine + ":" + eSeverity);

						pItem = new PropertiesItem(ePattern,
									sTargetFileName, 
									PropertiesItem.chooseType(eSeverity), 
									eBeginLine, 
									eEndLine);
						pItem.setMessage(eMessage);
						pItem.setSource(ItemSource.PHPMD);
						pItem.setDetail("ruleset", eRuleSet);
						pItem.setDetail("package", ePackage);
						pItem.setDetail("class", eClass);
						pItem.setDetail("reference", eExternalInfoUrl);
						items.add(pItem);
					}
				}
			}
		}
	}
}
