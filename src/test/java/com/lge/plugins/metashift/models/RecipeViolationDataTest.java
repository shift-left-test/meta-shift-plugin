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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.sf.json.JSONObject;
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

  @Test
  public void testInitData() {
    assertEquals("A-X-X", origin.getRecipe());
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
    assertEquals(origin.hashCode(), same.hashCode());
    assertNotEquals(origin.hashCode(),
        new MinorRecipeViolationData("A-X-X", "a.file", 1, "error_rule", "error_info", "error")
            .hashCode());
    assertNotEquals(origin.hashCode(),
        new InfoRecipeViolationData("A-X-X", "a.file", 1, "error_rule", "error_info", "error")
            .hashCode());
    assertNotEquals(origin.hashCode(),
        new MajorRecipeViolationData("B-X-X", "a.file", 1, "error_rule", "error_info", "error")
            .hashCode());
    assertNotEquals(origin.hashCode(),
        new MajorRecipeViolationData("A-X-X", "b.file", 1, "error_rule", "error_info", "error")
            .hashCode());
    assertNotEquals(origin.hashCode(),
        new MajorRecipeViolationData("A-X-X", "a.file", 2, "error_rule", "error_info", "error")
            .hashCode());
    assertNotEquals(origin.hashCode(),
        new MajorRecipeViolationData("A-X-X", "a.file", 1, "warn_rule", "error_info", "error")
            .hashCode());
    assertEquals(origin.hashCode(),
        new MajorRecipeViolationData("A-X-X", "a.file", 1, "error_rule", "warn_info", "error")
            .hashCode());
    assertEquals(origin.hashCode(),
        new MajorRecipeViolationData("A-X-X", "a.file", 1, "error_rule", "error_info", "warn")
            .hashCode());
  }

  @Test
  public void testComparable() {
    List<RecipeViolationData> expected = new ArrayList<>();
    expected.add(new MajorRecipeViolationData("A-X-X", "a.file", 1, "rule1", "info", "error"));
    expected.add(new MajorRecipeViolationData("A-X-X", "a.file", 1, "rule2", "info", "error"));
    expected.add(new MajorRecipeViolationData("A-X-X", "a.file", 2, "rule2", "info", "error"));
    expected.add(new MajorRecipeViolationData("A-X-X", "b.file", 2, "rule2", "info", "error"));
    expected.add(new MajorRecipeViolationData("B-X-X", "b.file", 2, "rule2", "info", "error"));

    List<RecipeViolationData> actual = new ArrayList<>();
    actual.add(new MajorRecipeViolationData("B-X-X", "b.file", 2, "rule2", "info", "error"));
    actual.add(new MajorRecipeViolationData("A-X-X", "b.file", 2, "rule2", "info", "error"));
    actual.add(new MajorRecipeViolationData("A-X-X", "a.file", 2, "rule2", "info", "error"));
    actual.add(new MajorRecipeViolationData("A-X-X", "a.file", 1, "rule2", "info", "error"));
    actual.add(new MajorRecipeViolationData("A-X-X", "a.file", 1, "rule1", "info", "error"));

    Collections.sort(actual);
    assertEquals(expected, actual);
  }

  @Test
  public void testToJsonObject() {
    JSONObject object = origin.toJsonObject();
    assertEquals("A-X-X", object.getString("recipe"));
    assertEquals("a.file", object.getString("file"));
    assertEquals(1, object.getLong("line"));
    assertEquals("error_rule", object.getString("rule"));
    assertEquals("error_info", object.getString("description"));
    assertEquals("error", object.getString("severity"));
    assertEquals("MAJOR", object.getString("level"));
  }
}
