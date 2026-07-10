/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.ui.tables;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Unit tests for {@link TableHtml}.
 */
public class TableHtmlTest {

  @Test
  public void testEscape() {
    assertEquals("&lt;a&gt;&amp;&quot;", TableHtml.escape("<a>&\""));
    assertEquals("", TableHtml.escape(null));
  }

  @Test
  public void testAnchor() {
    assertEquals("<a href=\"file?name=a.c\">a.c</a>", TableHtml.anchor("file?name=a.c", "a.c"));
  }

  @Test
  public void testProgressBar() {
    assertTrue(TableHtml.progressBar(0.5, true).contains("width:50%"));
    assertTrue(TableHtml.progressBar(0.5, true).contains("50%"));
    assertTrue(TableHtml.progressBar(0.5, true).contains("bg-success bg-opacity-25"));
    assertTrue(TableHtml.progressBar(0.5, false).contains("bg-danger bg-opacity-25"));
    assertTrue(TableHtml.progressBar(0.5, true, "50% (1/2)").contains("50% (1/2)"));
  }

  @Test
  public void testQualifiedIcon() {
    assertTrue(TableHtml.qualifiedIcon(true).contains("text-success"));
    assertFalse(TableHtml.qualifiedIcon(false).contains("text-success"));
  }
}
