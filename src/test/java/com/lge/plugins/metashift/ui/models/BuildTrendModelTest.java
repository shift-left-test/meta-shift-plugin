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

package com.lge.plugins.metashift.ui.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.lge.plugins.metashift.metrics.Metrics;
import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.PremirrorCacheData;
import com.lge.plugins.metashift.models.Recipe;
import java.util.Arrays;
import java.util.Collections;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.junit.Test;

/**
 * Unit test for the BuildTrendModel class.
 */
public class BuildTrendModelTest {

  @SafeVarargs
  private final <T> JSONArray newJsonArray(T... values) {
    JSONArray objects = new JSONArray();
    objects.addAll(Arrays.asList(values));
    return objects;
  }

  private JSONObject newJsonObject(JSONArray data, String name, int axis) {
    JSONObject object = new JSONObject();
    object.put("data", data);
    object.put("name", name);
    object.put("type", "line");
    object.put("yAxisIndex", axis);
    return object;
  }

  @Test
  public void testInitData() {
    BuildTrendModel model = new BuildTrendModel(0);

    assertEquals(Arrays.asList("PremirrorCache", "SharedStateCache", "RecipeViolation",
        "Comment", "CodeViolation", "Complexity", "Duplication",
        "Test", "StatementCoverage", "BranchCoverage", "Mutation"),
        model.getLegend());

    assertEquals(Collections.emptyList(), model.getBuilds());

    JSONArray array = new JSONArray();
    array.add(newJsonObject(newJsonArray(), "PremirrorCache", 0));
    array.add(newJsonObject(newJsonArray(), "SharedStateCache", 0));
    array.add(newJsonObject(newJsonArray(), "RecipeViolation", 1));
    array.add(newJsonObject(newJsonArray(), "Comment", 0));
    array.add(newJsonObject(newJsonArray(), "CodeViolation", 1));
    array.add(newJsonObject(newJsonArray(), "Complexity", 0));
    array.add(newJsonObject(newJsonArray(), "Duplication", 0));
    array.add(newJsonObject(newJsonArray(), "Test", 0));
    array.add(newJsonObject(newJsonArray(), "StatementCoverage", 0));
    array.add(newJsonObject(newJsonArray(), "BranchCoverage", 0));
    array.add(newJsonObject(newJsonArray(), "Mutation", 0));

    assertEquals(array, JSONArray.fromObject(model.getSeries()));
  }

  @Test
  public void testAddData() {
    BuildTrendModel model = new BuildTrendModel(5);

    Configuration config = new Configuration();
    Metrics metrics = new Metrics(config);
    Recipe recipe = new Recipe("A-1.0.0-r0");
    recipe.add(new PremirrorCacheData("A-1.0.0-r0", "X", false));
    metrics.parse(recipe);

    model.addData("#1", metrics);
    assertEquals(Collections.singletonList("#1"), model.getBuilds());

    JSONObject expected1 = newJsonObject(newJsonArray(0), "PremirrorCache", 0);
    assertTrue(JSONArray.fromObject(model.getSeries()).contains(expected1));

    model.addData("#2", metrics);
    assertEquals(Arrays.asList("#2", "#1"), model.getBuilds());

    JSONObject expected2 = newJsonObject(newJsonArray(0, 0), "PremirrorCache", 0);
    assertTrue(JSONArray.fromObject(model.getSeries()).contains(expected2));
  }

  @Test
  public void testMaxBuildCount() {
    BuildTrendModel model = new BuildTrendModel(3);

    Configuration config = new Configuration();
    Metrics metrics = new Metrics(config);
    Recipe recipe = new Recipe("A-1.0.0-r0");
    recipe.add(new PremirrorCacheData("A-1.0.0-r0", "X", false));
    metrics.parse(recipe);

    assertTrue(model.addData("#1", metrics));
    assertTrue(model.addData("#1", metrics));
    assertTrue(model.addData("#1", metrics));
    assertFalse(model.addData("#1", metrics));
  }
}
