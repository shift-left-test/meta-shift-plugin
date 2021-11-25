/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.fixture;

import com.lge.plugins.metashift.utils.JsonUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;

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
      signatures.add(FakeRandom.nextString());
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

    File report = FileUtils.getFile(directory, recipe.getName(), "checkcache", "caches.json");
    JsonUtils.saveAs(object, report);
  }
}
