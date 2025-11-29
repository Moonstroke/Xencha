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


	public OutputComparator(boolean ignoreWhitespaceNodes) {
		this.ignoreWhitespaceNodes = ignoreWhitespaceNodes;
	}

	public boolean areEqual(Source expectedOutput, Result obtainedOutput) {
		Node expectedNode = toDOMSource(expectedOutput).getNode();
		Node obtainedNode = toDOMResult(obtainedOutput).getNode();
		Node child1 = expectedNode.getFirstChild();
		Node child2 = obtainedNode.getFirstChild();
		while (child1 != null && child2 != null) {
			if (!child1.isEqualNode(child2)) {
				return false;
			}
			child1 = child1.getNextSibling();
			child2 = child2.getNextSibling();
		}
		if (child1 != child2) {
			return false;
		}
		return true;
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
}
