package de.fraunhofer.iosb.tc_lib_encodingrulestester;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class HandleInteractionClass {
	/**
	 * 
	 * @param theSelectedNode the Xerces node at this level
	 * @return true means error
	 */
	private boolean decodeInteractionClass(Node theSelectedNode) {
		String textPointer = null;

		if (theSelectedNode.getNodeType() == Node.ELEMENT_NODE) {
			textPointer = theSelectedNode.getNodeName();
		}
		for (Node child = theSelectedNode.getFirstChild(); child != null; child = child.getNextSibling()) {
			if (child.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			// ---------------------------------------------------------------------------
			try {
				if (child.getNodeName().equals("name")) {
					if (((Element) child).getFirstChild() != null) {
						textPointer = ((Element) child).getFirstChild().getNodeValue();
						System.out.println("Name: " + textPointer);
					}
					continue;
				}
				if (child.getNodeName().equals("parameter")) {
					System.out.println("Got parameter: " + child.getNodeName());
					decodeAttribute(child);
					continue;
				}
				if (child.getNodeName().equals("interactionClass")) {
					System.out.println("Got interactionClass: " + child.getNodeName());
					decodeInteractionClass(child);
					continue;
				}
			} catch (Exception e) {
				System.out.println("HandleInteractionClass.decodeInteractionClass: " + e);
			}
		}
		return false;
	}

	/**
	 * 
	 * @param theSelectedNode the Xerces node at this level
	 * @return true means error
	 */
	private boolean decodeAttribute(Node theSelectedNode) {
		String textPointer = null;

		for (Node child = theSelectedNode.getFirstChild(); child != null; child = child.getNextSibling()) {
			if (child.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			// ---------------------------------------------------------------------------
			try {
				if (child.getNodeName().equals("name")) {
					if (((Element) child).getFirstChild() != null) {
						textPointer = ((Element) child).getFirstChild().getNodeValue();
						System.out.println("Name: " + textPointer);
					}
					continue;
				}
				if (child.getNodeName().equals("dataType")) {
					if (((Element) child).getFirstChild() != null) {
						textPointer = ((Element) child).getFirstChild().getNodeValue();
						System.out.println("dataType: " + textPointer);
					}
					continue;
				}
			} catch (Exception e) {
				System.out.println("HandleInteractionClass.decodeAttribute: " + e);
			}
		}
		return false;
	}

	/**
	 * 
	 * @param theSelectedNode the Xerces node at this level
	 * @return true means error
	 */
	boolean decode(Node theSelectedNode) {
		String textPointer = null;

		if (theSelectedNode.getNodeType() == Node.ELEMENT_NODE) {
			textPointer = theSelectedNode.getNodeName();
			System.out.println("decode: " + textPointer);
		}
		for (Node child = theSelectedNode.getFirstChild(); child != null; child = child.getNextSibling()) {
			if (child.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			if (child.getNodeName().equals("interactionClass")) {
				if (decodeInteractionClass(child)) {
					return true;
				}
			}
		}
		return false;
	}
}
