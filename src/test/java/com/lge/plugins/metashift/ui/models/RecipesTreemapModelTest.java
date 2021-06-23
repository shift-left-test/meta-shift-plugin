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

import org.junit.Test;

import net.sf.json.JSONArray;

public class RecipesTreemapModelTest {
  @Test
  public void testInitData() {
    RecipesTreemapModel model = new RecipesTreemapModel();

    assertEquals(JSONArray.fromObject(
      "[{\"path\":\"\",\"link\":\"\",\"name\":\"\",\"value\":[0,0]},"
      + "{\"path\":\"\",\"link\":\"\",\"name\":\"\",\"value\":[0,100]}]"),
      JSONArray.fromObject(model.getSeries()));
  }

  @Test
  public void testAdd() {
    RecipesTreemapModel model = new RecipesTreemapModel();

    model.add("test", "testpath", 1, 2);
    System.out.println(JSONArray.fromObject(model.getSeries()));
    int [] values = {1, 2};

    assertNotNull(model.getSeries().stream().filter(o -> o.getName() == "test" 
        && o.getPath() == "testpath" && Arrays.equals(o.getValue(), values))
        .findAny().orElse(null));
  }
}
