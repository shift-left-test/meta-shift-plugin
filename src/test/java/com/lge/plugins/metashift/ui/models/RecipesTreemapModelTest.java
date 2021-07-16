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
import static org.junit.Assert.assertTrue;

import com.lge.plugins.metashift.metrics.Metrics;
import com.lge.plugins.metashift.models.Configuration;
import java.util.Arrays;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.junit.Test;

public class RecipesTreemapModelTest {

  @SafeVarargs
  private final <T> JSONArray newJsonArray(T... values) {
    JSONArray objects = new JSONArray();
    objects.addAll(Arrays.asList(values));
    return objects;
  }

  private JSONObject newJsonObject(JSONArray value) {
    JSONObject object = new JSONObject();
    object.put("path", "");
    object.put("link", "");
    object.put("name", "");
    object.put("target", "_self");
    object.put("value", value);
    object.put("qualifiedMap", new JSONObject());
    return object;
  }

  @Test
  public void testInitData() {
    RecipesTreemapModel model = new RecipesTreemapModel();

    JSONArray expected = new JSONArray();
    expected.add(newJsonObject(newJsonArray(0, 0)));
    expected.add(newJsonObject(newJsonArray(0, 100)));
    assertEquals(expected, JSONArray.fromObject(model.getSeries()));
  }

  @Test
  public void testAdd() {
    RecipesTreemapModel model = new RecipesTreemapModel();

    Metrics metrics = new Metrics(new Configuration());
    model.add("test", metrics);

    long[] values = {0, 0};

    assertTrue(model.getSeries().stream()
        .anyMatch(o -> "test".equals(o.getName()) && Arrays.equals(o.getValue(), values)));
  }
}
