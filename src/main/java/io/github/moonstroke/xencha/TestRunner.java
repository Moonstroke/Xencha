/* SPDX-FileCopyrightText: 2025 (c) Joachim MARIE <moonstroke+github@live.fr>
 * SPDX-License-Identifier: MIT */
package io.github.moonstroke.xencha;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.Collection;

/**
 * This class handles the execution of tests described in XML files whose paths it is given.
 */
public class TestRunner {

	private final Collection<Path> paths;


	private TestRunner(Collection<Path> paths) {
		this.paths = paths;
	}

	/**
	 * Construct a test runner for the specified paths.
	 *
	 * @param rawPaths The paths to XML test suites
	 *
	 * @return A test runner configured with the specified paths
	 *
	 * @throws FileNotFoundException if one of the given paths does not refer to an existing file
	 */
	public static TestRunner forPaths(String... rawPaths) throws FileNotFoundException {
		throw new UnsupportedOperationException("Not implemented"); // TODO
	}
}
