/* SPDX-FileCopyrightText: 2025 (c) Joachim MARIE <moonstroke+github@live.fr>
 * SPDX-License-Identifier: MIT */
package io.github.moonstroke.xencha;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import io.github.moonstroke.xencha.model.InlineSource;
import io.github.moonstroke.xencha.model.Source;
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
			errorResult.setGlobalStatus(TestStatus.ERROR);
			return errorResult;
		}
		return runTestSuite(testSuite);
	}

	private TestSuite parseTestSuiteFromPath(Path path, Unmarshaller unmarshaller) throws IOException, JAXBException {
		try (InputStream inputStream = Files.newInputStream(path)) {
			@SuppressWarnings("unchecked")
			JAXBElement<TestSuite> root = (JAXBElement<TestSuite>) unmarshaller.unmarshal(inputStream);
			return root.getValue();
		}
	}

	private TestSuiteResult runTestSuite(TestSuite testSuite) {
		TestSuiteResult result = new TestSuiteResult(testSuite.getName());
		try {
			javax.xml.transform.Source testSource = getTestSource(testSuite.getSource());
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer sourceStylesheet = factory.newTransformer(testSource);
			// TODO
		} catch (IOException | IllegalStateException | TransformerConfigurationException e) {
			result.setGlobalStatus(TestStatus.ERROR);
			return result;
		}
		return result;
	}

	private javax.xml.transform.Source getTestSource(Source testSource) throws IOException {
		javax.xml.transform.Source src;
		if (testSource.getPath() == null) {
			/* Node.getOwnerDocument conveniently returns a standalone document object, not the descriptor's */
			src = new DOMSource(getInlineXslRoot(testSource.getInline()).getOwnerDocument());
		} else {
			src = new StreamSource(Files.newInputStream(Path.of(testSource.getPath())));
		}
		return src;
	}

	/* Retrieve the root element of the inline source and ensure that it is a valid XSL root element (stylesheet or
	 * transform) */
	protected Element getInlineXslRoot(InlineSource inlineSource) {
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
}
