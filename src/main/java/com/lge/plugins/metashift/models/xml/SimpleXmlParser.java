/*
 * MIT License
 *
 * Copyright (c) 2021 LG Electronics, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.lge.plugins.metashift.models.xml;

import com.lge.plugins.metashift.utils.LruCache;
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
   * Represents the singleton cache object.
   */
  private static final LruCache<String, Document> objects = new LruCache<>();

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
    String checksum = file.digest();
    if (!objects.containsKey(checksum)) {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      builder.setEntityResolver((publicId, systemId) -> new InputSource(new StringReader("")));
      Document document = builder.parse(file.read());
      document.getDocumentElement().normalize();
      objects.put(checksum, document);
    }
    this.document = objects.get(checksum);
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
