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

package com.lge.plugins.metashift.fixture;

import com.lge.plugins.metashift.utils.JsonUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;

/**
 * FakeCacheReport class.
 *
 * @author Sung Gon Kim
 */
public class FakeCacheReport implements FakeReport {

  private final FakeRecipe recipe;

  public FakeCacheReport(final FakeRecipe recipe) {
    this.recipe = recipe;
  }

  private JSONObject createSummaryObject(long found, long missed) {
    JSONObject object = new JSONObject();
    object.put("Wanted", found + missed);
    object.put("Found", found);
    object.put("Missed", missed);
    return object;
  }

  private List<String> createCacheList(long size) {
    List<String> signatures = new ArrayList<>();
    for (long i = 0; i < size; i++) {
      String signature = String.format("%s:%s",
          RandomStringUtils.randomAlphabetic(20),
          RandomStringUtils.randomAlphabetic(20));
      signatures.add(signature);
    }
    return signatures;
  }

  @Override
  public void toFile(File directory) throws IOException {
    JSONObject sharedState = new JSONObject();
    sharedState.put("Summary",
        createSummaryObject(recipe.getSharedStateFound(), recipe.getSharedStateMissed()));
    sharedState.put("Found", createCacheList(recipe.getSharedStateFound()));
    sharedState.put("Missed", createCacheList(recipe.getSharedStateMissed()));

    JSONObject premirror = new JSONObject();
    premirror.put("Summary",
        createSummaryObject(recipe.getPremirrorFound(), recipe.getPremirrorMissed()));
    premirror.put("Found", createCacheList(recipe.getPremirrorFound()));
    premirror.put("Missed", createCacheList(recipe.getPremirrorMissed()));

    JSONObject object = new JSONObject();
    object.put("Shared State", sharedState);
    object.put("Premirror", premirror);

    File report = FileUtils.getFile(directory, recipe.getRecipe(), "checkcache", "caches.json");
    JsonUtils.saveAs(object, report);
  }
}
