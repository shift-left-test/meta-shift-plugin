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
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.sf.json.JSONObject;
import org.junit.Test;

/**
 * Unit tests for the coverage data class.
 *
 * @author Sung Gon Kim
 */
public class CoverageDataTest {

  private final CoverageData origin =
      new StatementCoverageData("A-B-C", "a.file", 1, true);
  private final CoverageData same =
      new StatementCoverageData("A-B-C", "a.file", 1, false);

  @Test
  public void testInitData() {
    assertEquals("A-B-C", origin.getRecipe());
    assertEquals("a.file", origin.getFile());
    assertEquals(1, origin.getLine());
    assertEquals(0, origin.getIndex());
    assertTrue(origin.isCovered());
    assertEquals("Statement", origin.getType());
  }

  @Test
  public void testEquality() {
    assertNotEquals(origin, null);
    assertNotEquals(origin, new Object());
    assertEquals(origin, origin);
    assertEquals(origin, same);
    assertNotEquals(origin, new BranchCoverageData("A-B-C", "a.file", 1, 0, true));
    assertNotEquals(origin, new StatementCoverageData("B-B-C", "a.file", 1, true));
    assertNotEquals(origin, new StatementCoverageData("A-B-C", "b.file", 1, true));
    assertNotEquals(origin, new StatementCoverageData("A-B-C", "a.file", 2, true));
    assertEquals(origin, new StatementCoverageData("A-B-C", "a.file", 1, false));
  }

  @Test
  public void testHashCode() {
    assertEquals(origin.hashCode(), same.hashCode());
    assertNotEquals(origin.hashCode(),
        new BranchCoverageData("A-B-C", "a.file", 1, 0, true).hashCode());
    assertNotEquals(origin.hashCode(),
        new StatementCoverageData("B-B-C", "a.file", 1, true).hashCode());
    assertNotEquals(origin.hashCode(),
        new StatementCoverageData("A-B-C", "b.file", 1, true).hashCode());
    assertNotEquals(origin.hashCode(),
        new StatementCoverageData("A-B-C", "a.file", 2, true).hashCode());
    assertEquals(origin.hashCode(),
        new StatementCoverageData("A-B-C", "a.file", 1, false).hashCode());
  }

  @Test
  public void testComparable() {
    List<CoverageData> expected = new ArrayList<>();
    expected.add(new BranchCoverageData("A-B-C", "a.file", 1, 1, true));
    expected.add(new BranchCoverageData("B-B-C", "a.file", 1, 1, true));
    expected.add(new BranchCoverageData("B-B-C", "b.file", 1, 1, true));
    expected.add(new BranchCoverageData("B-B-C", "b.file", 2, 1, true));
    expected.add(new BranchCoverageData("B-B-C", "b.file", 2, 2, true));

    List<CoverageData> actual = new ArrayList<>();
    actual.add(new BranchCoverageData("B-B-C", "b.file", 2, 2, true));
    actual.add(new BranchCoverageData("B-B-C", "b.file", 2, 1, true));
    actual.add(new BranchCoverageData("B-B-C", "b.file", 1, 1, true));
    actual.add(new BranchCoverageData("B-B-C", "a.file", 1, 1, true));
    actual.add(new BranchCoverageData("A-B-C", "a.file", 1, 1, true));

    Collections.sort(actual);
    assertEquals(expected, actual);
  }

  @Test
  public void testToJsonObject() {
    JSONObject object = origin.toJsonObject();
    assertEquals("A-B-C", object.getString("recipe"));
    assertEquals("a.file", object.getString("file"));
    assertEquals(1, object.getLong("line"));
    assertTrue(object.getBoolean("covered"));
    assertEquals("Statement", object.getString("type"));
  }
}
