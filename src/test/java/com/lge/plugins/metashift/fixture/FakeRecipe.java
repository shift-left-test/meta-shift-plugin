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

  public FakeRecipe(File sourcePath, String prefix) {
    recipe = String.format("%s-1.0.0-r0", prefix + FakeRandom.nextString());
    this.sourcePath = sourcePath;
    premirrorFound = 0;
    premirrorMissed = 0;
    sharedStateFound = 0;
    sharedStateMissed = 0;
    sources = new ArrayList<>();
    scripts = new ArrayList<>();
  }

  public FakeRecipe(File sourcePath) {
    this(sourcePath, "");
  }

  public String getRecipe() {
    return recipe;
  }

  public File getSourcePath() {
    return new File(sourcePath, getRecipe());
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
