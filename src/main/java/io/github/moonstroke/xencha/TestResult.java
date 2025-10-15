/* SPDX-FileCopyrightText: 2025 (c) Joachim MARIE <moonstroke+github@live.fr>
 * SPDX-License-Identifier: MIT */
package io.github.moonstroke.xencha;

import java.util.Objects;

/**
 * This class represents the result of the execution of a test.
 */
public class TestResult {

	private final String name;
	private final TestStatus status;
	private final String details;


	/**
	 * Construct the result of the execution of a specified test
	 * 
	 * @param name    The name of the test that was executed
	 * @param status  The status of the execution of the test
	 * @param details A string providing details regarding the test status (only relevant when the test is not
	 *                successful; may be {@code null})
	 */
	public TestResult(String name, TestStatus status, String details) {
		this.name = Objects.requireNonNull(name);
		this.status = Objects.requireNonNull(status);
		this.details = details;
	}


	/**
	 * Retrieve the name of the test that was executed
	 * 
	 * @return The test name
	 */
	public String getName() {
		return name;
	}


	/**
	 * Retrieve the status of the execution of the test.
	 * 
	 * @return The test execution status
	 */
	public TestStatus getStatus() {
		return status;
	}

	/**
	 * Retrieve the details message of this test execution.
	 * 
	 * @return the test execution details, or {@code null}
	 */
	public String getDetails() {
		return details;
	}
}
