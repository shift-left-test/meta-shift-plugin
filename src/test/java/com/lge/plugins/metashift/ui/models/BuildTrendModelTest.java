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

  @Test
  public void testInitData() {
    BuildTrendModel model = new BuildTrendModel(0);

    assertEquals(Arrays.asList("PremirrorCache", "SharedStateCache", "RecipeViolation",
        "Comment", "CodeViolation", "Complexity", "Duplication",
        "Test", "Coverage", "Mutation"),
        model.getLegend());

    assertEquals(Collections.emptyList(), model.getBuilds());

    assertEquals(JSONArray.fromObject(
        "[{\"data\":[],\"name\":\"PremirrorCache\",\"type\":\"line\",\"yAxisIndex\":0},"
            + "{\"data\":[],\"name\":\"SharedStateCache\",\"type\":\"line\",\"yAxisIndex\":0},"
            + "{\"data\":[],\"name\":\"RecipeViolation\",\"type\":\"line\",\"yAxisIndex\":1},"
            + "{\"data\":[],\"name\":\"Comment\",\"type\":\"line\",\"yAxisIndex\":0},"
            + "{\"data\":[],\"name\":\"CodeViolation\",\"type\":\"line\",\"yAxisIndex\":1},"
            + "{\"data\":[],\"name\":\"Complexity\",\"type\":\"line\",\"yAxisIndex\":0},"
            + "{\"data\":[],\"name\":\"Duplication\",\"type\":\"line\",\"yAxisIndex\":0},"
            + "{\"data\":[],\"name\":\"Test\",\"type\":\"line\",\"yAxisIndex\":0},"
            + "{\"data\":[],\"name\":\"Coverage\",\"type\":\"line\",\"yAxisIndex\":0},"
            + "{\"data\":[],\"name\":\"Mutation\",\"type\":\"line\",\"yAxisIndex\":0}]"),
        JSONArray.fromObject(model.getSeries()));
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

    assertTrue(JSONArray.fromObject(model.getSeries()).contains(
        JSONObject.fromObject(
            "{\"data\":[0],\"name\":\"PremirrorCache\",\"type\":\"line\",\"yAxisIndex\":0}")));

    model.addData("#2", metrics);
    assertEquals(Arrays.asList("#2", "#1"), model.getBuilds());

    assertTrue(JSONArray.fromObject(model.getSeries()).contains(
        JSONObject.fromObject(
            "{\"data\":[0, 0],\"name\":\"PremirrorCache\",\"type\":\"line\",\"yAxisIndex\":0}")));
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
