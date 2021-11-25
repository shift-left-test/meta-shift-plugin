/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.utils.xml;

import static net.sf.ezmorph.test.ArrayAssertions.assertEquals;

import org.junit.Test;

/**
 * Unit tests for the TagList class.
 *
 * @author Sung Gon Kim
 */
public class TagListTest {

  private final TagList tagList = new TagList();

  @Test
  public void testEmptyTagList() {
    assertEquals(0, tagList.size());
    assertEquals("", tagList.first().getTagName());
    assertEquals("", tagList.last().getTagName());
  }
}
