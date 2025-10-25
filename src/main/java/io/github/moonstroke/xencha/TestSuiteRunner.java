/* SPDX-FileCopyrightText: 2025 (c) Joachim MARIE <moonstroke+github@live.fr>
 * SPDX-License-Identifier: MIT */
package io.github.moonstroke.xencha;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import io.github.moonstroke.xencha.model.Case;
import io.github.moonstroke.xencha.model.InlineSource;
import io.github.moonstroke.xencha.model.TestSuite;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

/**
 * This class handles the execution of a single test suite.
 */
public class TestSuiteRunner {

	/**
	 * Run the test suite described inthe file of given path.
	 *
	 * @param testSuitePath The path to the test suite descriptor
	 *
	 * @return The result if the execution of the test suite
	 */
	public TestSuiteResult runSuite(Path testSuitePath) {
		TestSuite testSuite;
		try {
			testSuite = parseTestSuiteFromPath(testSuitePath, TestSuiteUnmarshaller.INSTANCE);
		} catch (IOException | JAXBException e) {
			TestSuiteResult errorResult = new TestSuiteResult(testSuitePath.toString());
			errorResult.setStatus(TestStatus.ERROR);
			errorResult.setDetails(e.toString());
			return errorResult;
		}
		return runTestSuite(testSuitePath.getParent(), testSuite);
	}

	private TestSuite parseTestSuiteFromPath(Path path, Unmarshaller unmarshaller) throws IOException, JAXBException {
		try (InputStream inputStream = Files.newInputStream(path)) {
			@SuppressWarnings("unchecked")
			JAXBElement<TestSuite> root = (JAXBElement<TestSuite>) unmarshaller.unmarshal(inputStream);
			return root.getValue();
		}
	}

	private TestSuiteResult runTestSuite(Path rootPath, TestSuite testSuite) {
		TestSuiteResult result = new TestSuiteResult(testSuite.getName());
		try {
			Source testSource = getTestSource(rootPath, testSuite.getSource());
			Transformer sourceStylesheet = TestSuiteTransformerFactory.INSTANCE.newTransformer(testSource);
			for (Case c : testSuite.getCases().getCase()) {
				TestResult caseResult = runTestCase(rootPath, sourceStylesheet, c);
				result.addTestResult(caseResult);
			}
		} catch (IOException | IllegalStateException | TransformerConfigurationException e) {
			result.setStatus(TestStatus.ERROR);
			result.setDetails(e.toString());
			return result;
		}
		return result;
	}

	private Source getTestSource(Path rootPath, io.github.moonstroke.xencha.model.Source testSource) throws IOException {
		Source src;
		if (testSource.getPath() == null) {
			/* Node.getOwnerDocument conveniently returns a standalone document object, not the descriptor's */
			src = new DOMSource(getInlineXslRoot(testSource.getInline()).getOwnerDocument());
		} else {
			src = new StreamSource(Files.newInputStream(rootPath.resolve(testSource.getPath())));
		}
		return src;
	}

	/* Retrieve the root element of the inline source and ensure that it is a valid XSL root element (stylesheet or
	 * transform) */
	private Element getInlineXslRoot(InlineSource inlineSource) {
		List<Object> content = inlineSource.getContent();
		if (content.isEmpty()) {
			throw new IllegalStateException("Expected XSL stylesheet or transform, got nothing");
		}
		Object root = content.get(0);
		if (root instanceof String) {
			String str = (String) root;
			if (str.isBlank()) {
				if (content.size() == 1) {
					throw new IllegalStateException("Expected XSL stylesheet or transform, got nothing");
				}
				/* Accept (and skip) a leading whitespace node */
				root = content.get(1);
			}
		}
		if (root instanceof String) {
			throw new IllegalStateException("Expected XSL stylesheet or transform, got text: \"" + root + "\"");
		}
		Element rootElement = (Element) root;
		if (!"http://www.w3.org/1999/XSL/Transform".equals(rootElement.getNamespaceURI())
		    || !rootElement.getLocalName().equals("stylesheet") && !rootElement.getLocalName().equals("transform")) {
			throw new IllegalStateException("Expected XSL stylesheet or transform, got " + rootElement.getNodeName());
		}
		return rootElement;
	}

	private TestResult runTestCase(Path rootPath, Transformer sourceStylesheet, Case c) {
		TestStatus status = TestStatus.SUCCESS;
		String details = null;
		try {
			Source input = getSource(rootPath, c.getInput());
			Result target = transform(sourceStylesheet, input);
			Source expectedOutput = getSource(rootPath, c.getExpectedOutput());
			if (!areEqual(expectedOutput, target)) {
				status = TestStatus.FAILURE;
				details = "The output of the test differs from the expected output";
			}
		} catch (RuntimeException | IOException | SAXException | TransformerException e) {
			status = TestStatus.ERROR;
			details = e.toString();
		}
		return new TestResult(c.getName(), status, details);
	}

	private Result transform(Transformer sourceStylesheet, Source input) throws TransformerException {
		Result target = new DOMResult(TestSuiteDocumentBuilder.INSTANCE.newDocument());
		sourceStylesheet.transform(input, target);
		return target;
	}

	private Source getSource(Path rootPath, io.github.moonstroke.xencha.model.Source source) throws IOException, SAXException {
		if (source.getPath() == null) {
			List<Object> content = source.getInline().getContent();
			if (content.isEmpty()) {
				return new DOMSource(/* empty source */);
			}
			Object root = content.get(0);
			if (root instanceof String) {
				String text = (String) root;
				if (text.isBlank()) {
					/* Whitespace only => discard (consider empty) */
					if (content.size() == 1) {
						return new DOMSource(/* empty source */);
					}
					root = content.get(1);
				} else {
					throw new IllegalStateException("Expected XML input, got text: \"" + text + "\"");
				}
			}
			return new DOMSource(((Element) root).getOwnerDocument());
		}
		return new DOMSource(TestSuiteDocumentBuilder.INSTANCE.parse(rootPath.resolve(source.getPath()).toString()));
	}

	private boolean areEqual(Source expectedOutput, Result obtainedOutput) {
		return toDOMSource(expectedOutput).getNode().isEqualNode(toDOMResult(obtainedOutput).getNode());
	}

	private static DOMSource toDOMSource(Source source) {
		if (source instanceof DOMSource) {
			return (DOMSource) source;
		}
		// TODO handle other subtypes
		throw new UnsupportedOperationException("Source type not handled: " + source.getClass());
	}

	private static DOMResult toDOMResult(Result result) {
		if (result instanceof DOMResult) {
			return (DOMResult) result;
		}
		// TODO handle other subtypes
		throw new UnsupportedOperationException("Result type not handled: " + result.getClass());
	}


	private static class TestSuiteUnmarshaller {

		private static final Unmarshaller INSTANCE;

		static {
			try {
				JAXBContext context = JAXBContext.newInstance(TestSuite.class);
				INSTANCE = context.createUnmarshaller();
				INSTANCE.setSchema(SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
				                                .newSchema(TestRunner.class.getResource("/test.xsd")));
			} catch (JAXBException | SAXException e) {
				throw new ExceptionInInitializerError(e);
			}
		}
	}

	private static class TestSuiteTransformerFactory {

		private static final TransformerFactory INSTANCE = TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl",
		                                                                                  null);
	}

	private static class TestSuiteDocumentBuilder {

		private static final DocumentBuilder INSTANCE;
		static {
			try {
				INSTANCE = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			} catch (ParserConfigurationException e) {
				throw new ExceptionInInitializerError(e);
			}
		}
	}
}
