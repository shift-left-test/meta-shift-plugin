/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.utils.xml;

import java.util.ArrayList;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Represents the list of tags.
 *
 * @author Sung Gon Kim
 */
public class TagList extends ArrayList<Tag> {

  /**
   * Represents the UUID of the class.
   */
  private static final long serialVersionUID = 8964488735187383673L;

  /**
   * Default constructor.
   */
  public TagList() {
    super();
  }

  /**
   * Constructs the list with nodes.
   *
   * @param nodes to add
   */
  public TagList(final NodeList nodes) {
    for (int i = 0; i < nodes.getLength(); i++) {
      Node node = nodes.item(i);
      if (node.getNodeType() == Node.ELEMENT_NODE) {
        super.add(new Tag(node));
      }
    }
  }

  /**
   * Returns the first tag object of the list.
   *
   * @return first tag object
   */
  public Tag first() {
    return super.isEmpty() ? new Tag() : super.get(0);
  }

  /**
   * Returns the last tag object of the list.
   *
   * @return last tag object
   */
  public Tag last() {
    return super.isEmpty() ? new Tag() : super.get(size() - 1);
  }
}
