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
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.lge.plugins.metashift.metrics.Metrics;
import com.lge.plugins.metashift.models.Configuration;

import net.sf.json.JSONArray;
import org.junit.Test;

import hudson.scheduler.Hash;

public class RecipesTreemapModelTest {

  @Test
  public void testInitData() {
    RecipesTreemapModel model = new RecipesTreemapModel();

    Map [] expectedSeries = {
      new HashMap<String, Object>() {{
        put("path", "");
        put("link", "");
        put("name", "");
        put("target", "_self");
        put("value", new int [] {0, 0});
        put("qualifiedMap", new HashMap<String, Boolean>());
      }},
      new HashMap<String, Object>() {{
        put("path", "");
        put("link", "");
        put("name", "");
        put("target", "_self");
        put("value", new int [] {0, 100});
        put("qualifiedMap", new HashMap<String, Boolean>());
      }}
    };

    assertEquals(JSONArray.fromObject(expectedSeries),
        JSONArray.fromObject(model.getSeries()));
  }

  @Test
  public void testAdd() {
    RecipesTreemapModel model = new RecipesTreemapModel();

    Metrics metrics = new Metrics(new Configuration());
    model.add("test", metrics);
    System.out.println(JSONArray.fromObject(model.getSeries()));
    long[] values = {0, 0};

    assertNotNull(model.getSeries().stream().filter(o ->
        "test".equals(o.getName()) && Arrays.equals(o.getValue(), values))
        .findAny().orElse(null));
  }
}
