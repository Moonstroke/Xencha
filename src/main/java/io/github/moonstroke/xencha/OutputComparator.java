/* SPDX-FileCopyrightText: 2025 (c) Joachim MARIE <moonstroke+github@live.fr>
 * SPDX-License-Identifier: MIT */
package io.github.moonstroke.xencha;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * This class encapsulates the operation of comparing two output trees for equality.
 */
public class OutputComparator {

	private final boolean ignoreWhitespaceNodes;
	private final boolean ignoreDifferingNsPrefixes;


	/**
	 * Construct a new output comparator.
	 *
	 * @param ignoreWhitespaceNodes     Whether to skip whitespace-only text nodes
	 * @param ignoreDifferingNsPrefixes Whether to ignore differences in prefixes for a same namespace
	 */
	public OutputComparator(boolean ignoreWhitespaceNodes, boolean ignoreDifferingNsPrefixes) {
		this.ignoreWhitespaceNodes = ignoreWhitespaceNodes;
		this.ignoreDifferingNsPrefixes = ignoreDifferingNsPrefixes;
	}

	/**
	 * Compare the output trees stored in the given objects for equality.
	 *
	 * @param expectedOutput The first tree
	 * @param obtainedOutput The second tree
	 *
	 * @return {@code true} if, and only if, the two trees are recursively equal
	 */
	public boolean areEqual(Source expectedOutput, Result obtainedOutput) {
		Node expectedNode = toDOMSource(expectedOutput).getNode();
		Node obtainedNode = toDOMResult(obtainedOutput).getNode();
		if (!(expectedNode instanceof Document && obtainedNode instanceof Document)) {
			throw new IllegalArgumentException("DOM documents expected");
		}
		return areEqual(((Document) expectedNode).getDocumentElement(), ((Document) obtainedNode).getDocumentElement());
	}

	private static DOMSource toDOMSource(Source source) {
		if (source instanceof DOMSource) {
			return (DOMSource) source;
		}
		// TODO handle other subtypes
		throw new UnsupportedOperationException("Source type not handled: " + source.getClass());
	}

	private static DOMResult toDOMResult(Result result) {
		if (result instanceof DOMResult) {
			return (DOMResult) result;
		}
		// TODO handle other subtypes
		throw new UnsupportedOperationException("Result type not handled: " + result.getClass());
	}

	private boolean areEqual(Node node1, Node node2) {
		if (node1 == node2) {
			return true;
		}
		if (node1.getNodeType() != node2.getNodeType()
		    || !areEqual(node1.getNodeName(), node2.getNodeName())
		    || !areEqual(node1.getLocalName(), node2.getLocalName())
		    || !areEqual(node1.getNamespaceURI(), node2.getNamespaceURI())
		    || !areEqual(node1.getPrefix(), node2.getPrefix())
		    || !areEqual(node1.getNodeValue(), node2.getNodeValue())) {
			return false;
		}
		if (node1.hasAttributes()) {
			if (!node2.hasAttributes()) {
				return false;
			}
			NamedNodeMap attrs1 = node1.getAttributes();
			NamedNodeMap attrs2 = node2.getAttributes();
			int attrsLength = attrs1.getLength();
			if (attrs2.getLength() != attrsLength) {
				return false;
			}
			for (int i = 0; i < attrsLength; ++i) {
				Node ithAttr1 = attrs1.item(i);
				Node ithAttr2 = attrs2.item(i);
				if (!areEqual(ithAttr1.getLocalName(), ithAttr2.getLocalName())
				    || !areEqual(ithAttr1.getNamespaceURI(), ithAttr2.getNamespaceURI())
				    || !areEqual(ithAttr1.getPrefix(), ithAttr2.getPrefix())
				    || !areEqual(ithAttr1.getNodeValue(), ithAttr2.getNodeValue())) {
					return false;
				}
			}
		}
		Node child1 = node1.getFirstChild();
		Node child2 = node2.getFirstChild();
		while (child1 != null && child2 != null) {
			if (!areEqual(child1, child2)) {
				return false;
			}
			child1 = child1.getNextSibling();
			child2 = child2.getNextSibling();
		}
		return child1 == child2; /* Implicit "both null" / "both non-null" check */
	}

	private static boolean areEqual(String str1, String str2) {
		return str1 == str2 || str1 != null && str1.equals(str2);
	}
}
