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
 * FakeSource class.
 *
 * @author Sung Gon Kim
 */
public class FakeSource implements FakeFile {

  public static final String SOURCE_LINE = "HELLO WORLD";

  private FakeRecipe recipe;
  private final String filename;
  private final long totalLines;
  private final long codeLines;
  private final long commentLines;
  private final long duplicatedLines;
  private final long classes;
  private final long functions;
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
  private int complexityTolerance;
  private long complexityExceeded;
  private long complexityNormal;
  private long majorViolations;
  private long minorViolations;
  private long infoViolations;

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
    this.codeLines = codeLines;
    this.commentLines = commentLines;
    this.duplicatedLines = duplicatedLines;
    this.classes = 0;
    this.functions = 0;
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
    complexityTolerance = 0;
    complexityExceeded = 0;
    complexityNormal = 0;
    majorViolations = 0;
    minorViolations = 0;
    infoViolations = 0;
  }

  public String getFilename() {
    return filename;
  }

  public File getFile() {
    Objects.requireNonNull(recipe);
    return new File(recipe.getSourcePath(), getFilename());
  }

  public long getTotalLines() {
    return totalLines;
  }

  public long getCodeLines() {
    return codeLines;
  }

  public long getCommentLines() {
    return commentLines;
  }

  public long getDuplicatedLines() {
    return duplicatedLines;
  }

  public long getClasses() {
    return classes;
  }

  public long getFunctions() {
    return functions;
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

  public int getComplexityTolerance() {
    return complexityTolerance;
  }

  public long getComplexityExceeded() {
    return complexityExceeded;
  }

  public long getComplexityNormal() {
    return complexityNormal;
  }

  public long getMajorViolations() {
    return majorViolations;
  }

  public long getMinorViolations() {
    return minorViolations;
  }

  public long getInfoViolations() {
    return infoViolations;
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

  public FakeSource setComplexity(int tolerance, long exceeded, long normal) {
    complexityTolerance = tolerance;
    complexityExceeded = exceeded;
    complexityNormal = normal;
    return this;
  }

  public FakeSource setCodeViolations(long major, long minor, long info) {
    majorViolations = major;
    minorViolations = minor;
    infoViolations = info;
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
