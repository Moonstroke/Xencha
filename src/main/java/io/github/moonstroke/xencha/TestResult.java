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


	/**
	 * Construct the result of the execution of a specified test
	 * 
	 * @param name   The name of the test that was executed
	 * @param status The status of the execution of the test
	 */
	public TestResult(String name, TestStatus status) {
		this.name = Objects.requireNonNull(name);
		this.status = Objects.requireNonNull(status);
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
}
