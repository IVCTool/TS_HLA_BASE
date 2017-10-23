package de.fraunhofer.iosb.tc_lib_encodingrulestester;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.fraunhofer.iosb.ivct.HlaDataTypes;

public class DataTreeBuilder {
	HlaDataTypes hlaDataTypes;
	
	public DataTreeBuilder(final HlaDataTypes hlaDataTypes) {
		this.hlaDataTypes = hlaDataTypes;
	}
	/**
	 * 
	 * @param theSelectedNode the Xerces node at this level
	 * @return true means error
	 */
	public boolean buildData(Node theSelectedNode) {
		HandleObjectClass handleObjectClass = new HandleObjectClass();
		HandleInteractionClass handleInteractionClass = new HandleInteractionClass();
		HandleDataTypes handleDataTypes = new HandleDataTypes();
		for (Node child = theSelectedNode.getFirstChild(); child != null; child = child.getNextSibling()) {
			try {
				if (child.getNodeType() != Node.ELEMENT_NODE) {
					continue;
				}
				// ---------------------------------------------------------------------------
				if (child.getNodeName().equals("objects")) {
					System.out.println("Got objects!");
					if (((Element) child).getFirstChild() != null) {
						handleObjectClass.decode(child);
					}
					continue;
				}
				// ---------------------------------------------------------------------------
				if (child.getNodeName().equals("interactions")) {
					System.out.println("Got interactions!");
					if (((Element) child).getFirstChild() != null) {
						handleInteractionClass.decode(child);
					}
					continue;
				}
				// ---------------------------------------------------------------------------
				if (child.getNodeName().equals("tags")) {
					System.out.println("Got tags!");
					if (((Element) child).getFirstChild() != null) {
						// handleTags.decode(child);
					}
					continue;
				}
				// ---------------------------------------------------------------------------
				if (child.getNodeName().equals("dataTypes")) {
					System.out.println("Got dataTypes!");
					if (((Element) child).getFirstChild() != null) {
						handleDataTypes.decode(child, hlaDataTypes);
					}
					continue;
				}
				// ---------------------------------------------------------------------------
				if (child.getNodeName().equals("notes")) {
					System.out.println("Got notes!");
					if (((Element) child).getFirstChild() != null) {
						// handleNotes.decode(child);
					}
					continue;
				}
			} catch (Exception e) {
				System.out.println("DataTreeBuilder.buildData: " + e);
			}
		}
		return false;
	}
}
