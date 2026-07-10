/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.ui.build;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import net.sf.json.JSONArray;
import org.junit.Test;

/**
 * Unit tests for the AttentionView class.
 */
public class AttentionViewTest {

  private static final String ROWS = "["
      + "{'name':'good','unitTests':{'available':true,'ratio':1.0,'qualified':true}},"
      + "{'name':'worst','unitTests':{'available':true,'ratio':0.1,'qualified':false},"
      + "'mutationTests':{'available':true,'ratio':0.3,'qualified':false}},"
      + "{'name':'bad','statementCoverage':{'available':true,'ratio':0.5,'qualified':false}},"
      + "{'name':'na','unitTests':{'available':false}}"
      + "]";

  @Test
  public void testCollectsFailedRecipesOrderedByWorstRatio() {
    AttentionView view = AttentionView.of(JSONArray.fromObject(ROWS), 5);
    assertEquals(2, view.getFailedCount());
    assertEquals(2, view.getRecipes().size());
    assertEquals("worst", view.getRecipes().get(0).getName());
    assertEquals("bad", view.getRecipes().get(1).getName());
  }

  @Test
  public void testChipsNameFailedMetricsWithPercent() {
    AttentionView view = AttentionView.of(JSONArray.fromObject(ROWS), 5);
    AttentionView.Entry worst = view.getRecipes().get(0);
    assertEquals(2, worst.getChips().size());
    assertEquals("Unit Tests", worst.getChips().get(0).getName());
    assertEquals("10%", worst.getChips().get(0).getRatioText());
    assertEquals("Mutation Tests", worst.getChips().get(1).getName());
  }

  @Test
  public void testLimitsEntriesAndKeepsTotalCount() {
    AttentionView view = AttentionView.of(JSONArray.fromObject(ROWS), 1);
    assertEquals(2, view.getFailedCount());
    assertEquals(1, view.getRecipes().size());
    assertTrue(view.isTruncated());
  }

  @Test
  public void testAllQualifiedYieldsEmptyView() {
    AttentionView view = AttentionView.of(JSONArray.fromObject(
        "[{'name':'good','unitTests':{'available':true,'ratio':1.0,'qualified':true}}]"), 5);
    assertEquals(0, view.getFailedCount());
    assertTrue(view.getRecipes().isEmpty());
  }
}
