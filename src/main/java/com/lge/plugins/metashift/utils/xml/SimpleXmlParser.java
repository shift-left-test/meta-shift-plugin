/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.utils.xml;

import hudson.FilePath;
import java.io.IOException;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * A simple XML parser.
 *
 * @author Sung Gon Kim
 */
public class SimpleXmlParser {

  /**
   * Represents the document object.
   */
  private final Document document;

  /**
   * Default constructor.
   */
  public SimpleXmlParser() {
    document = null;
  }

  /**
   * Default constructor.
   *
   * @param file path to the xml file
   * @throws ParserConfigurationException if failed to parse the file
   * @throws IOException                  if failed to parse the file
   * @throws SAXException                 if failed to parse the file
   * @throws InterruptedException         if an interruption occurs
   */
  public SimpleXmlParser(final FilePath file)
      throws ParserConfigurationException, IOException, SAXException, InterruptedException {
    // Parsed documents used to be held in a static LRU cache. Two builds publishing at once each
    // parse on their own thread, and that cache was mutated without synchronization: a get could
    // come back null right after a successful put, leaving this parser without a document and
    // silently dropping the recipe from the report. Sharing one org.w3c.dom tree between threads
    // is a second hazard on its own, since the DOM is not thread-safe. Parsing per instance costs
    // one read of a file whose digest we would have had to compute anyway.
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    builder.setEntityResolver((publicId, systemId) -> new InputSource(new StringReader("")));
    this.document = builder.parse(file.read());
    this.document.getDocumentElement().normalize();
  }

  /**
   * Finds all tag objects matched by the given name.
   *
   * @param name of the tag
   * @return tag objects
   */
  public TagList getChildNodes(final String name) {
    return (document == null) ? new TagList() : new TagList(document.getElementsByTagName(name));
  }
}
