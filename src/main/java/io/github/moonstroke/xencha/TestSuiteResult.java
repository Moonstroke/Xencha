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
	private TestStatus globalStatus;
	private final Collection<TestResult> testResults = new ArrayList<>();


	/**
	 * Construct a result object for the test suite of specified name
	 *
	 * @param name The name of the test suite
	 */
	public TestSuiteResult(String name) {
		this.name = Objects.requireNonNull(name);
		this.globalStatus = TestStatus.SUCCESS;
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
	 * Assign the given status to this test suite execution.
	 *
	 * @param status The test suite execution status
	 */
	public void setGlobalStatus(TestStatus status) {
		this.globalStatus = Objects.requireNonNull(status);
	}

	/**
	 * Retrieve the global status of the execution of the test suite.
	 *
	 * This value is {@link TestStatus#SUCCESS} if the test suite was initialized without error and all tests cases
	 * ended in success.
	 *
	 * @return the global status of the suite's execution
	 */
	public TestStatus getGlobalStatus() {
		return globalStatus;
	}

	/**
	 * Add the result of the execution of a test case from the suite.
	 *
	 * @param testResult The test result to add
	 */
	public void addTestResult(TestResult testResult) {
		testResults.add(testResult);
		if (testResult.getStatus() != TestStatus.SUCCESS && globalStatus == TestStatus.SUCCESS) {
			globalStatus = testResult.getStatus();
		}
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
