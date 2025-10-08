/* SPDX-FileCopyrightText: 2025 (c) Joachim MARIE <moonstroke+github@live.fr>
 * SPDX-License-Identifier: MIT */
package io.github.moonstroke.xencha;

/**
 * This enum lists the different statuses in which a test execution can end.
 */
public enum TestStatus {

	/**
	 * The test ran in its entirety and ended successfully.
	 */
	SUCCESS,
	/**
	 * The test ran in entirety but ended in failure.
	 */
	FAILURE,
	/**
	 * The test ended unexpectedly during its execution.
	 */
	ERROR,
}
