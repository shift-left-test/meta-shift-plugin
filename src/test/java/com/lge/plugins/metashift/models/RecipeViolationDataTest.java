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

package com.lge.plugins.metashift.models;

import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;

/**
 * Unit tests for the RecipeViolationData class.
 *
 * @author Sung Gon Kim
 */
public class RecipeViolationDataTest {
  private RecipeViolationData origin = new MajorRecipeViolationData("A", "a.file", 1, "error_rule", "error_info", "error");
  private RecipeViolationData same = new MajorRecipeViolationData("A", "a.file", 1, "error_rule", "error_info2", "error2");

  @Test
  public void testInitialization() throws Exception {
    assertEquals("A", origin.getRecipe());
    assertEquals("a.file", origin.getFile());
    assertEquals(1, origin.getLine());
    assertEquals("error_rule", origin.getRule());
    assertEquals("error_info", origin.getDescription());
    assertEquals("error", origin.getSeverity());
  }

  @Test
  public void testEquality() throws Exception {
    assertEquals(origin, origin);
    assertEquals(origin, same);
    assertNotEquals(origin, new MinorRecipeViolationData("A", "a.file", 1, "error_rule", "error_info", "error"));
    assertNotEquals(origin, new InfoRecipeViolationData("A", "a.file", 1, "error_rule", "error_info", "error"));
    assertNotEquals(origin, new MajorRecipeViolationData("B", "a.file", 1, "error_rule", "error_info", "error"));
    assertNotEquals(origin, new MajorRecipeViolationData("A", "b.file", 1, "error_rule", "error_info", "error"));
    assertNotEquals(origin, new MajorRecipeViolationData("A", "a.file", 2, "error_rule", "error_info", "error"));
    assertNotEquals(origin, new MajorRecipeViolationData("A", "a.file", 1, "warn_rule", "error_info", "error"));
    assertEquals(origin, new MajorRecipeViolationData("A", "a.file", 1, "error_rule", "warn_info", "error"));
    assertEquals(origin, new MajorRecipeViolationData("A", "a.file", 1, "error_rule", "error_info", "warn"));
  }

  @Test
  public void testHashCode() throws Exception {
    assertEquals(origin.hashCode(), same.hashCode());
    assertNotEquals(origin.hashCode(), new MinorRecipeViolationData("A", "a.file", 1, "error_rule", "error_info", "error").hashCode());
    assertNotEquals(origin.hashCode(), new InfoRecipeViolationData("A", "a.file", 1, "error_rule", "error_info", "error").hashCode());
    assertNotEquals(origin.hashCode(), new MajorRecipeViolationData("B", "a.file", 1, "error_rule", "error_info", "error").hashCode());
    assertNotEquals(origin.hashCode(), new MajorRecipeViolationData("A", "b.file", 1, "error_rule", "error_info", "error").hashCode());
    assertNotEquals(origin.hashCode(), new MajorRecipeViolationData("A", "a.file", 2, "error_rule", "error_info", "error").hashCode());
    assertNotEquals(origin.hashCode(), new MajorRecipeViolationData("A", "a.file", 1, "warn_rule", "error_info", "error").hashCode());
    assertEquals(origin.hashCode(), new MajorRecipeViolationData("A", "a.file", 1, "error_rule", "warn_info", "error").hashCode());
    assertEquals(origin.hashCode(), new MajorRecipeViolationData("A", "a.file", 1, "error_rule", "error_info", "warn").hashCode());
  }

  @Test
  public void testComparable() throws Exception {
    List<RecipeViolationData> expected = new ArrayList<>();
    expected.add(new MajorRecipeViolationData("A", "a.file", 1, "rule1", "rule1_info", "error"));
    expected.add(new MajorRecipeViolationData("A", "a.file", 2, "rule1", "rule1_info", "error"));
    expected.add(new MajorRecipeViolationData("A", "b.file", 1, "rule1", "rule1_info", "error"));
    expected.add(new MajorRecipeViolationData("B", "a.file", 1, "rule1", "rule1_info", "error"));

    List<RecipeViolationData> actual = new ArrayList<>();
    actual.add(new MajorRecipeViolationData("B", "a.file", 1, "rule1", "rule1_info", "error"));
    actual.add(new MajorRecipeViolationData("A", "b.file", 1, "rule1", "rule1_info", "error"));
    actual.add(new MajorRecipeViolationData("A", "a.file", 2, "rule1", "rule1_info", "error"));
    actual.add(new MajorRecipeViolationData("A", "a.file", 1, "rule1", "rule1_info", "error"));

    Collections.sort(actual);
    assertEquals(expected, actual);
  }
}
