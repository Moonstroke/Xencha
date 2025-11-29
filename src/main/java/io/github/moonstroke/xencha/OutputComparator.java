/* SPDX-FileCopyrightText: 2025 (c) Joachim MARIE <moonstroke+github@live.fr>
 * SPDX-License-Identifier: MIT */
package io.github.moonstroke.xencha;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;

/**
 * This class encapsulates the operation of comparing two output trees for equality.
 */
public class OutputComparator {

	private final boolean ignoreWhitespaceNodes;


	public OutputComparator(boolean ignoreWhitespaceNodes) {
		this.ignoreWhitespaceNodes = ignoreWhitespaceNodes;
	}

	public boolean areEqual(Source expectedOutput, Result obtainedOutput) {
		return toDOMSource(expectedOutput).getNode().isEqualNode(toDOMResult(obtainedOutput).getNode());
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
