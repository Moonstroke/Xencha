/* SPDX-FileCopyrightText: 2025 (c) Joachim MARIE <moonstroke+github@live.fr>
 * SPDX-License-Identifier: MIT */
package io.github.moonstroke.xencha;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;

import org.w3c.dom.Node;

/**
 * This class encapsulates the operation of comparing two output trees for equality.
 */
public class OutputComparator {

	private final boolean ignoreWhitespaceNodes;


	/**
	 * Construct a new output comparator.
	 *
	 * @param ignoreWhitespaceNodes A boolean flag indicating whether to skip whitespace-only text nodes
	 */
	public OutputComparator(boolean ignoreWhitespaceNodes) {
		this.ignoreWhitespaceNodes = ignoreWhitespaceNodes;
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
		return areEqual(expectedNode, obtainedNode);
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
}
