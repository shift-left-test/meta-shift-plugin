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
 * FakeSource class.
 *
 * @author Sung Gon Kim
 */
public class FakeSource implements FakeFile {

  public static final String SOURCE_LINE = "HELLO WORLD";

  private FakeRecipe recipe;
  private final String filename;
  private final long totalLines;
  private long testPassed;
  private long testFailed;
  private long testError;
  private long testSkipped;
  private long mutationTestKilled;
  private long mutationTestSurvived;
  private long mutationTestSkipped;
  private long statementCoverageCovered;
  private long statementCoverageMissed;
  private long branchCoverageCovered;
  private long branchCoverageMissed;

  public FakeSource(String name, long totalLines, long codeLines, long commentLines,
      long duplicatedLines) {
    this(null, name, totalLines, codeLines, commentLines, duplicatedLines);
  }

  public FakeSource(long totalLines, long codeLines, long commentLines, long duplicatedLines) {
    this(null, FakeRandom.nextString(), totalLines, codeLines, commentLines, duplicatedLines);
  }

  public FakeSource(FakeRecipe recipe, long totalLines, long codeLines, long commentLines,
      long duplicatedLines) {
    this(recipe, FakeRandom.nextString(), totalLines, codeLines, commentLines, duplicatedLines);
  }

  public FakeSource(FakeRecipe recipe, String name, long totalLines, long codeLines,
      long commentLines, long duplicatedLines) {
    this.recipe = recipe;
    this.filename = name;
    this.totalLines = totalLines;
    testPassed = 0;
    testFailed = 0;
    testError = 0;
    testSkipped = 0;
    mutationTestKilled = 0;
    mutationTestSurvived = 0;
    mutationTestSkipped = 0;
    statementCoverageCovered = 0;
    statementCoverageMissed = 0;
    branchCoverageCovered = 0;
    branchCoverageMissed = 0;
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

  public long getTotalLines() {
    return totalLines;
  }

  public long getTestPassed() {
    return testPassed;
  }

  public long getTestFailed() {
    return testFailed;
  }

  public long getTestError() {
    return testError;
  }

  public long getTestSkipped() {
    return testSkipped;
  }

  public long getMutationTestKilled() {
    return mutationTestKilled;
  }

  public long getMutationTestSurvived() {
    return mutationTestSurvived;
  }

  public long getMutationTestSkipped() {
    return mutationTestSkipped;
  }

  public long getStatementCoverageCovered() {
    return statementCoverageCovered;
  }

  public long getStatementCoverageMissed() {
    return statementCoverageMissed;
  }

  public long getBranchCoverageCovered() {
    return branchCoverageCovered;
  }

  public long getBranchCoverageMissed() {
    return branchCoverageMissed;
  }

  public FakeSource setRecipe(FakeRecipe recipe) {
    this.recipe = recipe;
    return this;
  }

  public FakeSource setTests(long passed, long failed, long error, long skipped) {
    testPassed = passed;
    testFailed = failed;
    testError = error;
    testSkipped = skipped;
    return this;
  }

  public FakeSource setMutationTests(long killed, long survived, long skipped) {
    mutationTestKilled = killed;
    mutationTestSurvived = survived;
    mutationTestSkipped = skipped;
    return this;
  }

  public FakeSource setStatementCoverage(long covered, long missed) {
    statementCoverageCovered = covered;
    statementCoverageMissed = missed;
    return this;
  }

  public FakeSource setBranchCoverage(long covered, long missed) {
    branchCoverageCovered = covered;
    branchCoverageMissed = missed;
    return this;
  }

  @Override
  public void toFile() throws IOException {
    List<String> lines = new ArrayList<>();
    for (long i = 0; i < getTotalLines(); i++) {
      lines.add(SOURCE_LINE);
    }
    File file = getFile();
    FileUtils.forceMkdirParent(file);
    FileUtils.writeLines(file, lines);
  }
}
