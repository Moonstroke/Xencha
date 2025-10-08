/* SPDX-FileCopyrightText: 2025 (c) Joachim MARIE <moonstroke+github@live.fr>
 * SPDX-License-Identifier: MIT */
package io.github.moonstroke.xencha;

/**
 * This class is the entry point of the tool, when executed from a command line.
 */
public class Main {

	/**
	 * Entry method.
	 *
	 * @param args A list of XML test descriptors
	 */
	public static void main(String[] args) {
		try {
			TestRunner testRunner = TestRunner.forPaths(args);
			// TODO
		} catch (Exception e) {
			System.err.println(e);
			System.exit(1);
		}
	}
}
