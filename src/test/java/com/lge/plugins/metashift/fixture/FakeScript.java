/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.fixture;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.apache.commons.io.FileUtils;

/**
 * FakeScript class.
 *
 * @author Sung Gon Kim
 */
public class FakeScript implements FakeFile {

  private static final String SOURCE_LINE = "HELLO WORLD";

  private FakeRecipe recipe;
  private final String filename;
  private final long lines;
  private long majorIssues;
  private long minorIssues;
  private long infoIssues;

  public FakeScript(long lines) {
    this(null, lines);
  }

  public FakeScript(FakeRecipe recipe, long lines) {
    this(recipe, lines, 0, 0, 0);
  }

  public FakeScript(long lines, long majorIssues, long minorIssues, long infoIssues) {
    this(null, lines, majorIssues, minorIssues, infoIssues);
  }

  public FakeScript(FakeRecipe recipe, long lines, long majorIssues, long minorIssues,
      long infoIssues) {
    this.recipe = recipe;
    this.filename = FakeRandom.nextString() + ".bb";
    this.lines = lines;
    this.majorIssues = majorIssues;
    this.minorIssues = minorIssues;
    this.infoIssues = infoIssues;
  }

  public String getFilename() {
    return filename;
  }

  public String getAbsolutePath() {
    return getFile().getAbsolutePath();
  }

  public File getFile() {
    Objects.requireNonNull(recipe);
    return new File(recipe.getSourcePath(), getFilename());
  }

  public long getLines() {
    return lines;
  }

  public long getMajorIssues() {
    return majorIssues;
  }

  public long getMinorIssues() {
    return minorIssues;
  }

  public long getInfoIssues() {
    return infoIssues;
  }

  public FakeScript setRecipe(FakeRecipe recipe) {
    this.recipe = recipe;
    return this;
  }

  public FakeScript setIssues(long majorIssues, long minorIssues, long infoIssues) {
    this.majorIssues = majorIssues;
    this.minorIssues = minorIssues;
    this.infoIssues = infoIssues;
    return this;
  }

  @Override
  public void toFile() throws IOException {
    List<String> lines = new ArrayList<>();
    for (long i = 0; i < getLines(); i++) {
      lines.add(SOURCE_LINE);
    }
    File file = getFile();
    FileUtils.forceMkdirParent(file);
    FileUtils.writeLines(file, lines);
  }
}
