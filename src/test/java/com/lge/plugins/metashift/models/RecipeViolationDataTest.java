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

  @Test
  public void testToJsonObject() {
    JSONObject object = origin.toJsonObject();
    assertEquals("A-X-X", object.getString("name"));
    assertEquals("a.file", object.getString("file"));
    assertEquals(1, object.getLong("line"));
    assertEquals("error_rule", object.getString("rule"));
    assertEquals("error_info", object.getString("description"));
    assertEquals("error", object.getString("severity"));
    assertEquals("MAJOR", object.getString("level"));
  }
}
