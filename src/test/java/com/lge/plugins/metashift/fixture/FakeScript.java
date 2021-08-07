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
