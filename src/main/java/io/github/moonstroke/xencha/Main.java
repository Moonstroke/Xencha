/* SPDX-FileCopyrightText: 2025 (c) Joachim MARIE <moonstroke+github@live.fr>
 * SPDX-License-Identifier: MIT */
package io.github.moonstroke.xencha;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

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
			Collection<TestSuiteResult> results = testRunner.runTests();
			logResults(results);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	private static void logResults(Collection<TestSuiteResult> results) {
		TestStatus runStatus = TestStatus.SUCCESS;
		Map<TestStatus, Integer> statusCounts = new EnumMap<>(TestStatus.class);
		for (TestSuiteResult result : results) {
			logTestSuiteResult(result);
			TestStatus suiteStatus = result.getGlobalStatus();
			if (suiteStatus == TestStatus.FAILURE && runStatus == TestStatus.SUCCESS
			    || suiteStatus == TestStatus.ERROR && runStatus != TestStatus.ERROR) {
				runStatus = suiteStatus;
			}
			/* Set to 1 if absent or increment if present */
			statusCounts.merge(suiteStatus, 1, Integer::sum);
		}
		logRunStatus(runStatus, statusCounts);
	}

	private static void logTestSuiteResult(TestSuiteResult result) {
		throw new UnsupportedOperationException("Not implemented"); // TODO
	}

	private static void logRunStatus(TestStatus runStatus, Map<TestStatus, Integer> statusCounts) {
		throw new UnsupportedOperationException("Not implemented"); // TODO
	}
}
