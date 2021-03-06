/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.utils.xml;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Represents the tag class.
 *
 * @author Sung Gon Kim
 */
public class Tag {

  /**
   * Represents the dom element.
   */
  private final Element element;

  /**
   * Default constructor.
   */
  public Tag() {
    this.element = null;
  }

  /**
   * Default constructor.
   *
   * @param node to parse
   */
  public Tag(final Node node) {
    this.element = (Element) node;
  }

  /**
   * Finds all tag objects matched by the given name.
   *
   * @param name of the tag
   * @return tag objects
   */
  public TagList getChildNodes(final String name) {
    return (element == null) ? new TagList() : new TagList(element.getElementsByTagName(name));
  }

  /**
   * Returns the child nodes.
   *
   * @return list of child nodes
   */
  public TagList getChildNodes() {
    return (element == null) ? new TagList() : new TagList(element.getChildNodes());
  }

  /**
   * Returns the attribute value of the tag.
   *
   * @param name of the attribute
   * @return attribute value
   */
  public String getAttribute(final String name) {
    return getAttribute(name, "");
  }

  /**
   * Returns the attribute value of the tag.
   *
   * @param name         of the attribute
   * @param defaultValue default value
   * @return attribute value, or default value if null or empty string
   */
  public String getAttribute(final String name, final String defaultValue) {
    if (element == null || !element.hasAttribute(name)) {
      return defaultValue;
    }
    String value = element.getAttribute(name);
    return value.isEmpty() ? defaultValue : value;
  }

  /**
   * Test if the attribute exists.
   *
   * @param name of the attribute
   * @return true if the attribute exists, false otherwise
   */
  public boolean hasAttribute(final String name) {
    return element != null && element.hasAttribute(name);
  }

  /**
   * Test if the tag has child nodes.
   *
   * @return true if the tag has child nodes, false otherwise
   */
  public boolean hasChildNodes() {
    return element != null && element.hasChildNodes();
  }

  /**
   * Returns the tag name.
   *
   * @return tag name
   */
  public String getTagName() {
    return (element == null) ? "" : element.getTagName();
  }

  /**
   * Returns the content of the tag.
   *
   * @return text content
   */
  public String getTextContent() {
    return (element == null) ? "" : element.getTextContent();
  }
}
