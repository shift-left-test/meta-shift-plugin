/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.utils.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

/**
 * Unit tests for the Tag class.
 *
 * @author Sung Gon Kim
 */
public class TagTest {

  private final Tag tag = new Tag();

  @Test
  public void testEmptyTag() {
    assertEquals(0, tag.getChildNodes("X").size());
    assertEquals(0, tag.getChildNodes().size());
    assertEquals("", tag.getAttribute("X"));
    assertEquals("O", tag.getAttribute("X", "O"));
    assertFalse(tag.hasAttribute("X"));
    assertFalse(tag.hasChildNodes());
    assertEquals("", tag.getTagName());
    assertEquals("", tag.getTextContent());
  }
}
