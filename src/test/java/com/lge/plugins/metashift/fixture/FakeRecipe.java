/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.fixture;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;

/**
 * FakeRecipe class.
 *
 * @author Sung Gon Kim
 */
public class FakeRecipe implements FakeReport {

  private final String recipe;
  private final File sourcePath;
  private final List<FakeSource> sources;

  public FakeRecipe(File sourcePath, String name) {
    recipe = name;
    this.sourcePath = sourcePath;
    sources = new ArrayList<>();
  }

  public FakeRecipe(File sourcePath) {
    this(sourcePath, String.format("%s-1.0.0-r0", FakeRandom.nextString()));
  }

  public String getName() {
    return recipe;
  }

  public File getSourcePath() {
    return new File(sourcePath, getName());
  }

  public List<FakeSource> getSources() {
    return sources;
  }

  public FakeRecipe add(FakeSource source) {
    sources.add(source.setRecipe(this));
    return this;
  }

  @Override
  public void toFile(File directory) throws IOException {
    FileUtils.forceMkdir(new File(directory, getName()));
    for (FakeSource source : sources) {
      source.toFile();
    }
    new FakeMetadata(this).toFile(directory);
  }
}
