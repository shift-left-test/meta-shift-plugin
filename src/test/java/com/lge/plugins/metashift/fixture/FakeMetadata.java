/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.fixture;

import com.lge.plugins.metashift.utils.JsonUtils;
import java.io.File;
import java.io.IOException;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;

/**
 * FakeMetadata class.
 *
 * @author Sung Gon Kim
 */
public class FakeMetadata implements FakeReport {

  private final FakeRecipe recipe;

  public FakeMetadata(final FakeRecipe recipe) {
    this.recipe = recipe;
  }

  @Override
  public void toFile(File directory) throws IOException {
    JSONObject object = new JSONObject();
    object.put("S", recipe.getSourcePath().getAbsolutePath());
    File report = FileUtils.getFile(directory, recipe.getName(), "metadata.json");
    JsonUtils.saveAs(object, report);
  }
}
