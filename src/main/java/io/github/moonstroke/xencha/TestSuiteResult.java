/* SPDX-FileCopyrightText: 2025 (c) Joachim MARIE <moonstroke+github@live.fr>
 * SPDX-License-Identifier: MIT */
package io.github.moonstroke.xencha;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * This class summarizes the result of the execution of a full test suite (comprising multiple test cases).
 */
public class TestSuiteResult {

	private final String name;
	private final Collection<TestResult> testResults = new ArrayList<>();


	/**
	 * Construct a result object for the test suite of specified name
	 *
	 * @param name The name of the test suite
	 */
	public TestSuiteResult(String name) {
		this.name = Objects.requireNonNull(name);
	}

	/**
	 * Retrieve the name of the test suite.
	 *
	 * @return The name of the test suite
	 */
	public String getName() {
		return name;
	}

	/**
	 * Retrieve the results of the execution of this suite's test cases.
	 *
	 * @return the test case execution results
	 */
	public Collection<TestResult> getTestResults() {
		return testResults;
	}
}
