/* SPDX-FileCopyrightText: 2025 (c) Joachim MARIE <moonstroke+github@live.fr>
 * SPDX-License-Identifier: MIT */
package io.github.moonstroke.xencha;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;

import io.github.moonstroke.xencha.model.TestSuite;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

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
		Collection<Path> paths = new ArrayList<Path>(rawPaths.length);
		for (String rawPath : rawPaths) {
			Path path = Path.of(rawPath);
			if (!Files.isRegularFile(path)) {
				throw new FileNotFoundException(rawPath);
			}
			paths.add(path);
		}
		return new TestRunner(paths);
	}

	/**
	 * Run the tests for which this runner was configured.
	 *
	 * @return The results of the execution of the tests
	 */
	public Collection<TestSuiteResult> runTests() {
		Unmarshaller testSuiteUnmarshaller;
		try {
			JAXBContext context = JAXBContext.newInstance(TestSuite.class);
			testSuiteUnmarshaller = context.createUnmarshaller();
		} catch (JAXBException e) {
			throw new IllegalStateException(e);
		}
		Collection<TestSuiteResult> testSuiteResults = new ArrayList<>(paths.size());
		for (Path path : paths) {
			TestSuite testSuite;
			try {
				testSuite = parseTestSuiteFromPath(path, testSuiteUnmarshaller);
			} catch (IOException | JAXBException e) {
				TestSuiteResult errorResult = new TestSuiteResult(path.toString());
				errorResult.setGlobalStatus(TestStatus.ERROR);
				testSuiteResults.add(errorResult);
				continue;
			}
			TestSuiteResult result = runTestSuite(testSuite);
			testSuiteResults.add(result);
		}
		return testSuiteResults;
	}

	private TestSuite parseTestSuiteFromPath(Path path, Unmarshaller unmarshaller) throws IOException, JAXBException {
		try (InputStream inputStream = Files.newInputStream(path)) {
			@SuppressWarnings("unchecked")
			JAXBElement<TestSuite> root = (JAXBElement<TestSuite>) unmarshaller.unmarshal(inputStream);
			return root.getValue();
		}
	}

	private TestSuiteResult runTestSuite(TestSuite testSuite) {
		throw new UnsupportedOperationException("Not implemented"); // TODO
	}
}
