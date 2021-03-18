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
 * Unit tests for the RecipeViolationData class
 *
 * @author Sung Gon Kim
 */
public class RecipeViolationDataTest {
  private RecipeViolationData origin = new RecipeViolationData("A", "a.file", 1, "error_rule", "error_info", "error");
  private RecipeViolationData same = new RecipeViolationData("A", "a.file", 1, "warning_rule", "warning_info", "warning");

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
    assertNotEquals(origin, new RecipeViolationData("B", "a.file", 1, "error_rule", "error_info", "error"));
    assertNotEquals(origin, new RecipeViolationData("A", "b.file", 1, "error_rule", "error_info", "error"));
    assertNotEquals(origin, new RecipeViolationData("A", "a.file", 2, "error_rule", "error_info", "error"));
    assertEquals(origin, new RecipeViolationData("A", "a.file", 1, "warning_rule", "error_info", "error"));
    assertEquals(origin, new RecipeViolationData("A", "a.file", 1, "error_rule", "warning_info", "error"));
    assertEquals(origin, new RecipeViolationData("A", "a.file", 1, "error_rule", "error_info", "warning"));
  }

  @Test
  public void testHashCode() throws Exception {
    assertEquals(origin.hashCode(), same.hashCode());
    assertNotEquals(origin.hashCode(), new RecipeViolationData("B", "a.file", 1, "error_rule", "error_info", "error").hashCode());
    assertNotEquals(origin.hashCode(), new RecipeViolationData("A", "b.file", 1, "error_rule", "error_info", "error").hashCode());
    assertNotEquals(origin.hashCode(), new RecipeViolationData("A", "a.file", 2, "error_rule", "error_info", "error").hashCode());
    assertEquals(origin.hashCode(), new RecipeViolationData("A", "a.file", 1, "warning_rule", "error_info", "error").hashCode());
    assertEquals(origin.hashCode(), new RecipeViolationData("A", "a.file", 1, "error_rule", "warning_info", "error").hashCode());
    assertEquals(origin.hashCode(), new RecipeViolationData("A", "a.file", 1, "error_rule", "error_info", "warning").hashCode());
  }

  @Test
  public void testComparable() throws Exception {
    List<RecipeViolationData> expected = new ArrayList<>();
    expected.add(new RecipeViolationData("A", "a.file", 1, "rule1", "rule1_info", "error"));
    expected.add(new RecipeViolationData("A", "a.file", 2, "rule1", "rule1_info", "error"));
    expected.add(new RecipeViolationData("A", "b.file", 1, "rule1", "rule1_info", "error"));
    expected.add(new RecipeViolationData("B", "a.file", 1, "rule1", "rule1_info", "error"));

    List<RecipeViolationData> actual = new ArrayList<>();
    actual.addAll(expected);
    Collections.shuffle(actual);
    Collections.sort(actual);

    assertEquals(expected, actual);
  }
}
