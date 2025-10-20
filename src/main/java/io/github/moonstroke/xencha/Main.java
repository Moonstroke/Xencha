/* SPDX-FileCopyrightText: 2025 (c) Joachim MARIE <moonstroke+github@live.fr>
 * SPDX-License-Identifier: MIT */
package io.github.moonstroke.xencha;

import java.io.PrintStream;
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
		if (args.length == 0) {
			System.err.println("At least one XML descriptor path expected");
			System.exit(1);
		}
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
			TestStatus suiteStatus = result.getStatus();
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
		printTestResultData(System.out, result.getName(), result.getStatus(), result.getDetails());
		for (TestResult testResult : result.getTestResults()) {
			System.out.print("\t- ");
			printTestResultData(System.out, testResult.getName(), testResult.getStatus(), testResult.getDetails());
		}
	}

	private static void printTestResultData(PrintStream pw, String name, TestStatus status, String details) {
		pw.print(name + ": " + status);
		if (details == null) {
			pw.println();
		} else {
			pw.println(" (" + details + ")");
		}
	}

	private static void logRunStatus(TestStatus runStatus, Map<TestStatus, Integer> statusCounts) {
		System.out.println("Status: " + runStatus + " " + statusCounts); // TODO improve display
	}
}
