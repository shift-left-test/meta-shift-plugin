/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.fixture;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * FakeRecipe class.
 *
 * @author Sung Gon Kim
 */
public class FakeRecipe implements FakeReport {

  private final String recipe;
  private final File sourcePath;
  private long premirrorFound;
  private long premirrorMissed;
  private long sharedStateFound;
  private long sharedStateMissed;
  private final List<FakeSource> sources;
  private final List<FakeScript> scripts;

  public FakeRecipe(File sourcePath, String name) {
    recipe = name;
    this.sourcePath = sourcePath;
    premirrorFound = 0;
    premirrorMissed = 0;
    sharedStateFound = 0;
    sharedStateMissed = 0;
    sources = new ArrayList<>();
    scripts = new ArrayList<>();
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

  public long getPremirrorFound() {
    return premirrorFound;
  }

  public long getPremirrorMissed() {
    return premirrorMissed;
  }

  public FakeRecipe setPremirror(long found, long missed) {
    premirrorFound = found;
    premirrorMissed = missed;
    return this;
  }

  public long getSharedStateFound() {
    return sharedStateFound;
  }

  public long getSharedStateMissed() {
    return sharedStateMissed;
  }

  public FakeRecipe setSharedState(long found, long missed) {
    sharedStateFound = found;
    sharedStateMissed = missed;
    return this;
  }

  public List<FakeSource> getSources() {
    return sources;
  }

  public FakeRecipe add(FakeSource source) {
    sources.add(source.setRecipe(this));
    return this;
  }

  public List<FakeScript> getScripts() {
    return scripts;
  }

  public FakeRecipe add(FakeScript script) {
    scripts.add(script.setRecipe(this));
    return this;
  }

  @Override
  public void toFile(File directory) throws IOException {
    for (FakeSource source : sources) {
      source.toFile();
    }
    for (FakeScript script : scripts) {
      script.toFile();
    }
    new FakeMetadata(this).toFile(directory);
  }
}
