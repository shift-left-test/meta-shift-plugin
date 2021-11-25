/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

/**
 * Unit tests for the RecipeViolationData class.
 *
 * @author Sung Gon Kim
 */
public class RecipeViolationDataTest {

  private final RecipeViolationData origin = new MajorRecipeViolationData("A-X-X", "a.file",
      1, "error_rule", "error_info", "error");
  private final RecipeViolationData same = new MajorRecipeViolationData("A-X-X", "a.file",
      1, "error_rule", "error_info2", "error2");

  private void assertHashEquals(Object expected, Object actual) {
    assertEquals(expected.hashCode(), actual.hashCode());
  }

  private void assertHashNotEquals(Object expected, Object actual) {
    assertNotEquals(expected.hashCode(), actual.hashCode());
  }

  @Test
  public void testInitData() {
    assertEquals("A-X-X", origin.getName());
    assertEquals("a.file", origin.getFile());
    assertEquals(1, origin.getLine());
    assertEquals("error_rule", origin.getRule());
    assertEquals("error_info", origin.getDescription());
    assertEquals("error", origin.getSeverity());
    assertEquals("MAJOR", origin.getLevel());
  }

  @Test
  public void testEquality() {
    assertNotEquals(origin, null);
    assertNotEquals(origin, new Object());
    assertEquals(origin, origin);
    assertEquals(origin, same);
    assertNotEquals(origin,
        new MinorRecipeViolationData("A-X-X", "a.file", 1, "error_rule", "error_info", "error"));
    assertNotEquals(origin,
        new InfoRecipeViolationData("A-X-X", "a.file", 1, "error_rule", "error_info", "error"));
    assertNotEquals(origin,
        new MajorRecipeViolationData("B-X-X", "a.file", 1, "error_rule", "error_info", "error"));
    assertNotEquals(origin,
        new MajorRecipeViolationData("A-X-X", "b.file", 1, "error_rule", "error_info", "error"));
    assertNotEquals(origin,
        new MajorRecipeViolationData("A-X-X", "a.file", 2, "error_rule", "error_info", "error"));
    assertNotEquals(origin,
        new MajorRecipeViolationData("A-X-X", "a.file", 1, "warn_rule", "error_info", "error"));
    assertEquals(origin,
        new MajorRecipeViolationData("A-X-X", "a.file", 1, "error_rule", "warn_info", "error"));
    assertEquals(origin,
        new MajorRecipeViolationData("A-X-X", "a.file", 1, "error_rule", "error_info", "warn"));
  }

  @Test
  public void testHashCode() {
    assertHashEquals(origin, same);
    assertHashNotEquals(origin,
        new MinorRecipeViolationData("A-X-X", "a.file", 1, "error_rule", "error_info", "error"));
    assertHashNotEquals(origin,
        new InfoRecipeViolationData("A-X-X", "a.file", 1, "error_rule", "error_info", "error"));
    assertHashNotEquals(origin,
        new MajorRecipeViolationData("B-X-X", "a.file", 1, "error_rule", "error_info", "error"));
    assertHashNotEquals(origin,
        new MajorRecipeViolationData("A-X-X", "b.file", 1, "error_rule", "error_info", "error"));
    assertHashNotEquals(origin,
        new MajorRecipeViolationData("A-X-X", "a.file", 2, "error_rule", "error_info", "error"));
    assertHashNotEquals(origin,
        new MajorRecipeViolationData("A-X-X", "a.file", 1, "warn_rule", "error_info", "error"));
    assertHashEquals(origin,
        new MajorRecipeViolationData("A-X-X", "a.file", 1, "error_rule", "warn_info", "error"));
    assertHashEquals(origin,
        new MajorRecipeViolationData("A-X-X", "a.file", 1, "error_rule", "error_info", "warn"));
  }
}
