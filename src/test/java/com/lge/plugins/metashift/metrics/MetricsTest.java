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

package com.lge.plugins.metashift.metrics;

import static com.lge.plugins.metashift.metrics.TestUtils.assertCounter;
import static com.lge.plugins.metashift.metrics.TestUtils.assertEvaluator;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.lge.plugins.metashift.models.BranchCoverageData;
import com.lge.plugins.metashift.models.BuildStatus;
import com.lge.plugins.metashift.models.CodeSizeData;
import com.lge.plugins.metashift.models.CommentData;
import com.lge.plugins.metashift.models.ComplexityData;
import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.DuplicationData;
import com.lge.plugins.metashift.models.ErrorTestData;
import com.lge.plugins.metashift.models.FailedTestData;
import com.lge.plugins.metashift.models.InfoCodeViolationData;
import com.lge.plugins.metashift.models.KilledMutationTestData;
import com.lge.plugins.metashift.models.MajorCodeViolationData;
import com.lge.plugins.metashift.models.MajorRecipeViolationData;
import com.lge.plugins.metashift.models.MinorCodeViolationData;
import com.lge.plugins.metashift.models.MinorRecipeViolationData;
import com.lge.plugins.metashift.models.PassedTestData;
import com.lge.plugins.metashift.models.PremirrorCacheData;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.RecipeSizeData;
import com.lge.plugins.metashift.models.Recipes;
import com.lge.plugins.metashift.models.SharedStateCacheData;
import com.lge.plugins.metashift.models.SkippedMutationTestData;
import com.lge.plugins.metashift.models.SkippedTestData;
import com.lge.plugins.metashift.models.StatementCoverageData;
import com.lge.plugins.metashift.models.SurvivedMutationTestData;
import com.lge.plugins.metashift.utils.TemporaryFileUtils;
import hudson.FilePath;
import java.io.File;
import java.io.IOException;
import net.sf.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Unit tests for the Metrics class.
 *
 * @author Sung Gon Kim
 */
public class MetricsTest {

  @Rule
  public final TemporaryFolder folder = new TemporaryFolder();
  private TemporaryFileUtils utils;
  private StringBuilder builder;
  private Metrics metrics;
  private Recipe recipe;
  private Recipes recipes;

  @Before
  public void setUp() {
    utils = new TemporaryFileUtils(folder);
    builder = new StringBuilder();
    Configuration configuration = new Configuration();
    configuration.setPremirrorCacheThreshold(50);
    configuration.setSharedStateCacheThreshold(50);
    configuration.setRecipeViolationThreshold(0.5);
    configuration.setCommentThreshold(50);
    configuration.setCodeViolationThreshold(0.5);
    configuration.setComplexityLevel(5);
    configuration.setComplexityThreshold(50);
    configuration.setDuplicationThreshold(50);
    configuration.setTestThreshold(50);
    configuration.setStatementCoverageThreshold(50);
    configuration.setBranchCoverageThreshold(50);
    configuration.setMutationTestThreshold(50);
    metrics = new Metrics(configuration);
    recipe = new Recipe("A-1.0.0-r0");
    recipes = new Recipes();
    recipes.add(recipe);
  }

  @Test
  public void testInitialState() {
    assertEvaluator(metrics.getPremirrorCache(), false, false);
    assertEvaluator(metrics.getSharedStateCache(), false, false);
    assertEvaluator(metrics.getCodeSize(), false, false);
    assertEvaluator(metrics.getCodeViolations(), false, false);
    assertEvaluator(metrics.getComments(), false, false);
    assertEvaluator(metrics.getComplexity(), false, false);
    assertEvaluator(metrics.getStatementCoverage(), false, false);
    assertEvaluator(metrics.getBranchCoverage(), false, false);
    assertEvaluator(metrics.getDuplications(), false, false);
    assertEvaluator(metrics.getMutationTest(), false, false);
    assertEvaluator(metrics.getRecipeViolations(), false, false);
    assertEvaluator(metrics.getTest(), false, false);
    assertCounter(metrics, 0, 0, 0.0);
  }

  @Test
  public void testParseEmptyRecipes() {
    metrics.parse(recipes);
    assertEvaluator(metrics.getPremirrorCache(), false, false);
    assertEvaluator(metrics.getSharedStateCache(), false, false);
    assertEvaluator(metrics.getCodeSize(), false, false);
    assertEvaluator(metrics.getCodeViolations(), false, false);
    assertEvaluator(metrics.getComments(), false, false);
    assertEvaluator(metrics.getComplexity(), false, false);
    assertEvaluator(metrics.getStatementCoverage(), false, false);
    assertEvaluator(metrics.getBranchCoverage(), false, false);
    assertEvaluator(metrics.getDuplications(), false, false);
    assertEvaluator(metrics.getMutationTest(), false, false);
    assertEvaluator(metrics.getRecipeViolations(), false, false);
    assertEvaluator(metrics.getTest(), false, false);
    assertCounter(metrics, 0, 0, 0.0);
  }

  @Test
  public void testParseWithUnqualifiedPremirrorCacheData() {
    recipe.add(new PremirrorCacheData("A-1.0.0-r0", "X", false));
    recipe.add(new PremirrorCacheData("B-1.0.0-r0", "X", false));
    recipe.add(new PremirrorCacheData("C-1.0.0-r0", "X", true));
    metrics.parse(recipes);
    assertEvaluator(metrics.getPremirrorCache(), true, false);
    assertCounter(metrics.getPremirrorCache(), 3, 1, 0.3);
  }

  @Test
  public void testParseWithQualifiedPremirrorCacheData() {
    recipe.add(new PremirrorCacheData("A-1.0.0-r0", "X", false));
    recipe.add(new PremirrorCacheData("B-1.0.0-r0", "X", true));
    metrics.parse(recipes);
    assertEvaluator(metrics.getPremirrorCache(), true, true);
    assertCounter(metrics.getPremirrorCache(), 2, 1, 0.5);
  }

  @Test
  public void testParseWithUnqualifiedSharedStateCacheData() {
    recipe.add(new SharedStateCacheData("A-1.0.0-r0", "do_X", false));
    recipe.add(new SharedStateCacheData("B-1.0.0-r0", "do_X", false));
    recipe.add(new SharedStateCacheData("C-1.0.0-r0", "do_X", true));
    metrics.parse(recipes);
    assertEvaluator(metrics.getSharedStateCache(), true, false);
    assertCounter(metrics.getSharedStateCache(), 3, 1, 0.3);
  }

  @Test
  public void testParseWithQualifiedSharedStateCacheData() {
    recipe.add(new SharedStateCacheData("A-1.0.0-r0", "do_X", false));
    recipe.add(new SharedStateCacheData("B-1.0.0-r0", "do_X", true));
    metrics.parse(recipes);
    assertEvaluator(metrics.getSharedStateCache(), true, true);
    assertCounter(metrics.getSharedStateCache(), 2, 1, 0.5);
  }

  @Test
  public void testParseWithUnqualifiedCodeViolationData() {
    recipe.add(new CodeSizeData("A-1.0.0-r0", "a.file", 1, 1, 1));
    recipe.add(
        new MajorCodeViolationData("A-1.0.0-r0", "a.file", 1, 2, "rule", "m", "d", "E", "t"));
    metrics.parse(recipes);
    assertEvaluator(metrics.getCodeViolations(), true, false);
    assertCounter(metrics.getCodeViolations(), 1, 1, 1.0);
  }

  @Test
  public void testParseWithQualifiedCodeViolationData() {
    recipe.add(new CodeSizeData("A-1.0.0-r0", "a.file", 10, 1, 1));
    recipe.add(
        new MajorCodeViolationData("A-1.0.0-r0", "a.file", 1, 2, "rule", "m", "d", "E", "t"));
    recipe.add(
        new MinorCodeViolationData("A-1.0.0-r0", "b.file", 1, 2, "rule", "m", "d", "E", "t"));
    recipe.add(
        new InfoCodeViolationData("A-1.0.0-r0", "c.file", 1, 2, "rule", "m", "d", "E", "t"));
    metrics.parse(recipes);
    assertEvaluator(metrics.getCodeViolations(), true, true);
    assertCounter(metrics.getCodeViolations(), 10, 3, 0.3);
  }

  @Test
  public void testParseWithUnqualifiedCommentData() {
    recipe.add(new CommentData("A-1.0.0-r0", "a.file", 10, 5));
    recipe.add(new CommentData("B-1.0.0-r0", "b.file", 10, 0));
    metrics.parse(recipes);
    assertEvaluator(metrics.getComments(), true, false);
    assertCounter(metrics.getComments(), 20, 5, 0.25);
  }

  @Test
  public void testParseWithQualifiedCommentData() {
    recipe.add(new CommentData("A-1.0.0-r0", "a.file", 10, 5));
    recipe.add(new CommentData("B-1.0.0-r0", "b.file", 20, 15));
    metrics.parse(recipes);
    assertEvaluator(metrics.getComments(), true, true);
    assertCounter(metrics.getComments(), 30, 20, 0.66);
  }

  @Test
  public void testParseWithUnqualifiedComplexityData() {
    recipe.add(new ComplexityData("A-1.0.0-r0", "a.file", "f()", 5, 10, 5));
    recipe.add(new ComplexityData("B-1.0.0-r0", "a.file", "f()", 5, 10, 5));
    metrics.parse(recipes);
    assertEvaluator(metrics.getComplexity(), true, false);
    assertCounter(metrics.getComplexity(), 2, 2, 1.0);
  }

  @Test
  public void testParseWithQualifiedComplexityData() {
    recipe.add(new ComplexityData("A-1.0.0-r0", "a.file", "f()", 5, 10, 5));
    recipe.add(new ComplexityData("B-1.0.0-r0", "a.file", "f()", 5, 10, 0));
    metrics.parse(recipes);
    assertEvaluator(metrics.getComplexity(), true, true);
    assertCounter(metrics.getComplexity(), 2, 1, 0.5);
  }

  @Test
  public void testParseWithUnqualifiedStatementCoverageData() {
    recipe.add(new PassedTestData("A-B-C", "test", "test", ""));
    recipe.add(new StatementCoverageData("A-B-C", "a.file", 1, true));
    recipe.add(new StatementCoverageData("A-B-C", "b.file", 1, false));
    recipe.add(new StatementCoverageData("A-B-C", "c.file", 1, false));
    metrics.parse(recipes);
    assertEvaluator(metrics.getStatementCoverage(), true, false);
    assertCounter(metrics.getStatementCoverage(), 3, 1, 0.3);
  }

  @Test
  public void testParseWithQualifiedStatementCoverageData() {
    recipe.add(new PassedTestData("A-B-C", "test", "test", ""));
    recipe.add(new StatementCoverageData("A-B-C", "a.file", 1, true));
    recipe.add(new StatementCoverageData("A-B-C", "b.file", 1, false));
    recipe.add(new StatementCoverageData("A-B-C", "c.file", 1, true));
    metrics.parse(recipes);
    assertEvaluator(metrics.getStatementCoverage(), true, true);
    assertCounter(metrics.getStatementCoverage(), 3, 2, 0.6);
  }

  @Test
  public void testParseWithUnqualifiedBranchCoverageData() {
    recipe.add(new PassedTestData("A-B-C", "test", "test", ""));
    recipe.add(new BranchCoverageData("A-B-C", "a.file", 1, 1, true));
    recipe.add(new BranchCoverageData("A-B-C", "b.file", 1, 2, false));
    recipe.add(new BranchCoverageData("A-B-C", "c.file", 1, 3, false));
    metrics.parse(recipes);
    assertEvaluator(metrics.getBranchCoverage(), true, false);
    assertCounter(metrics.getBranchCoverage(), 3, 1, 0.3);
  }

  @Test
  public void testParseWithQualifiedBranchCoverageData() {
    recipe.add(new PassedTestData("A-B-C", "test", "test", ""));
    recipe.add(new BranchCoverageData("A-B-C", "a.file", 1, 1, true));
    recipe.add(new BranchCoverageData("A-B-C", "b.file", 1, 2, false));
    recipe.add(new BranchCoverageData("A-B-C", "c.file", 1, 3, true));
    metrics.parse(recipes);
    assertEvaluator(metrics.getBranchCoverage(), true, true);
    assertCounter(metrics.getBranchCoverage(), 3, 2, 0.6);
  }

  @Test
  public void testParseWithUnqualifiedDuplicationData() {
    recipe.add(new DuplicationData("A-1.0.0-r0", "a.file", 5, 0));
    recipe.add(new DuplicationData("A-1.0.0-r0", "b.file", 10, 10));
    metrics.parse(recipes);
    assertEvaluator(metrics.getDuplications(), true, false);
    assertCounter(metrics.getDuplications(), 15, 10, 0.6);
  }

  @Test
  public void testParseWithQualifiedDuplicationData() {
    recipe.add(new DuplicationData("A-1.0.0-r0", "a.file", 20, 0));
    recipe.add(new DuplicationData("A-1.0.0-r0", "b.file", 10, 10));
    metrics.parse(recipes);
    assertEvaluator(metrics.getDuplications(), true, true);
    assertCounter(metrics.getDuplications(), 30, 10, 0.3);
  }

  @Test
  public void testParseWithUnqualifiedMutationTestData() {
    recipe.add(new SkippedMutationTestData("A-1.0.0-r0", "a.file", "C", "f()", 1, "AOR", "TC"));
    recipe.add(new SurvivedMutationTestData("A-1.0.0-r0", "b.file", "C", "f()", 1, "AOR", "TC"));
    recipe.add(new KilledMutationTestData("A-1.0.0-r0", "c.file", "C", "f()", 1, "AOR", "TC"));
    metrics.parse(recipes);
    assertEvaluator(metrics.getMutationTest(), true, false);
    assertCounter(metrics.getMutationTest(), 3, 1, 0.3);
  }

  @Test
  public void testParseWithQualifiedMutationTestData() {
    recipe.add(new SkippedMutationTestData("A-1.0.0-r0", "a.file", "C", "f()", 1, "AOR", "TC"));
    recipe.add(new SurvivedMutationTestData("A-1.0.0-r0", "b.file", "C", "f()", 1, "AOR", "TC"));
    recipe.add(new KilledMutationTestData("A-1.0.0-r0", "c.file", "C", "f()", 1, "AOR", "TC"));
    recipe.add(new KilledMutationTestData("A-1.0.0-r0", "d.file", "C", "f()", 1, "AOR", "TC"));
    metrics.parse(recipes);
    assertEvaluator(metrics.getMutationTest(), true, true);
    assertCounter(metrics.getMutationTest(), 4, 2, 0.5);
  }

  @Test
  public void testParseWithUnqualifiedRecipeViolationData() {
    recipe.add(new RecipeSizeData("A-1.0.0-r0", "a.file", 5));
    recipe.add(new MajorRecipeViolationData("A-1.0.0-r0", "a.file", 1, "major", "major", "major"));
    recipe.add(new MinorRecipeViolationData("A-1.0.0-r0", "a.file", 1, "minor", "minor", "minor"));
    recipe.add(new MajorRecipeViolationData("A-1.0.0-r0", "b.file", 1, "major", "major", "major"));
    metrics.parse(recipes);
    assertEvaluator(metrics.getRecipeViolations(), true, false);
    assertCounter(metrics.getRecipeViolations(), 5, 3, 0.6);
  }

  @Test
  public void testParseWithQualifiedRecipeViolationData() {
    recipe.add(new RecipeSizeData("A-1.0.0-r0", "a.file", 5));
    recipe.add(new MajorRecipeViolationData("A-1.0.0-r0", "a.file", 1, "major", "major", "major"));
    recipe.add(new MinorRecipeViolationData("A-1.0.0-r0", "a.file", 1, "minor", "minor", "minor"));
    metrics.parse(recipes);
    assertEvaluator(metrics.getRecipeViolations(), true, true);
    assertCounter(metrics.getRecipeViolations(), 5, 2, 0.4);
  }

  @Test
  public void testParseWithSizeData() {
    recipe.add(new CodeSizeData("A-1.0.0-r0", "a.file", 3, 2, 1));
    recipe.add(new CodeSizeData("A-1.0.0-r0", "b.file", 6, 4, 2));
    metrics.parse(recipes);
    assertEquals(1, metrics.getCodeSize().getRecipes());
    assertEquals(2, metrics.getCodeSize().getFiles());
    assertEquals(9, metrics.getCodeSize().getLines());
    assertEquals(6, metrics.getCodeSize().getFunctions());
    assertEquals(3, metrics.getCodeSize().getClasses());
  }

  @Test
  public void testParseWithUnqualifiedTestData() {
    recipe.add(new PassedTestData("A-1.0.0-r0", "a.suite", "a.tc", "msg"));
    recipe.add(new FailedTestData("A-1.0.0-r0", "a.suite", "b.tc", "msg"));
    recipe.add(new ErrorTestData("A-1.0.0-r0", "a.suite", "c.tc", "msg"));
    recipe.add(new SkippedTestData("A-1.0.0-r0", "d.suite", "c.tc", "msg"));
    metrics.parse(recipes);
    assertEvaluator(metrics.getTest(), true, false);
    assertCounter(metrics.getTest(), 4, 1, 0.25);
  }

  @Test
  public void testParseWithQualifiedTestData() {
    recipe.add(new PassedTestData("A-1.0.0-r0", "a.suite", "a.tc", "msg"));
    recipe.add(new FailedTestData("A-1.0.0-r0", "a.suite", "b.tc", "msg"));
    recipe.add(new ErrorTestData("A-1.0.0-r0", "a.suite", "c.tc", "msg"));
    recipe.add(new PassedTestData("A-1.0.0-r0", "e.suite", "a.tc", "msg"));
    metrics.parse(recipes);
    assertEvaluator(metrics.getTest(), true, true);
    assertCounter(metrics.getTest(), 4, 2, 0.5);
  }

  @Test
  public void testGetRatioWithMultipleData() {
    recipe.add(new SkippedMutationTestData("A-1.0.0-r0", "a.file", "C", "f()", 1, "AOR", "TC"));
    recipe.add(new RecipeSizeData("A-1.0.0-r0", "a.file", 5));
    recipe.add(new MajorRecipeViolationData("A-1.0.0-r0", "a.file", 1, "major", "major", "major"));
    recipe.add(new PassedTestData("A-1.0.0-r0", "a.suite", "a.tc", "msg"));
    metrics.parse(recipe);
    assertCounter(metrics, 3, 2, 0.6);
  }

  @Test
  public void testParseRecipeWithEmptyCacheData()
      throws IOException, InterruptedException {
    File directory = utils.createDirectory("report", "B-1.0.0-r0");
    builder
        .append("{")
        .append("  'Premirror': { 'Found': [], 'Missed': [] },")
        .append("  'Shared State': { 'Found': [], 'Missed': [] }")
        .append("}");
    utils.writeLines(builder, directory, "checkcache", "caches.json");
    recipe = new Recipe(new FilePath(directory));
    metrics.parse(recipe);
    assertEvaluator(metrics.getPremirrorCache(), true, false);
    assertCounter(metrics.getPremirrorCache(), 0, 0, 0.0);
    assertEvaluator(metrics.getSharedStateCache(), true, false);
    assertCounter(metrics.getSharedStateCache(), 0, 0, 0.0);
  }

  @Test
  public void testParseRecipeWithEmptyCodeSizeData()
      throws IOException, InterruptedException {
    File directory = utils.createDirectory("report", "B-1.0.0-r0");
    builder.append("{ 'size': [], 'violations': [], 'complexity': [] }");
    utils.writeLines(builder, directory, "checkcode", "sage_report.json");
    recipe = new Recipe(new FilePath(directory));
    metrics.parse(recipe);
    assertEvaluator(metrics.getCodeSize(), false, false);
    assertCounter(metrics.getCodeSize(), 0, 0, 0.0);
  }

  @Test
  public void testParseRecipeWithEmptyCodeViolationData()
      throws IOException, InterruptedException {
    File directory = utils.createDirectory("report", "B-1.0.0-r0");
    builder.append("{ 'size': [], 'violations': [], 'complexity': [] }");
    utils.writeLines(builder, directory, "checkcode", "sage_report.json");
    recipe = new Recipe(new FilePath(directory));
    metrics.parse(recipe);
    assertEvaluator(metrics.getCodeViolations(), true, true);
    assertCounter(metrics.getCodeViolations(), 0, 0, 0.0);
  }

  @Test
  public void testParseRecipeWithEmptyCommentData()
      throws IOException, InterruptedException {
    File directory = utils.createDirectory("report", "B-1.0.0-r0");
    builder.append("{ 'size': [], 'violations': [], 'complexity': [] }");
    utils.writeLines(builder, directory, "checkcode", "sage_report.json");
    recipe = new Recipe(new FilePath(directory));
    metrics.parse(recipe);
    assertEvaluator(metrics.getComments(), true, false);
    assertCounter(metrics.getComments(), 0, 0, 0.0);
  }

  @Test
  public void testParseRecipeWithEmptyComplexityData()
      throws IOException, InterruptedException {
    File directory = utils.createDirectory("report", "B-1.0.0-r0");
    builder.append("{ 'size': [], 'violations': [], 'complexity': [] }");
    utils.writeLines(builder, directory, "checkcode", "sage_report.json");
    recipe = new Recipe(new FilePath(directory));
    metrics.parse(recipe);
    assertEvaluator(metrics.getComplexity(), true, true);
    assertCounter(metrics.getComplexity(), 0, 0, 0.0);
  }

  @Test
  public void testParseRecipeWithEmptyCoverageData()
      throws IOException, InterruptedException {
    File directory = utils.createDirectory("report", "B-1.0.0-r0");
    builder.append("<classes> </classes>");
    utils.writeLines(builder, directory, "coverage", "coverage.xml");

    recipe = new Recipe(new FilePath(directory));
    recipe.add(new PassedTestData("B-1.0.0-r0", "test", "test", ""));

    metrics.parse(recipe);
    assertEvaluator(metrics.getStatementCoverage(), false, false);
    assertCounter(metrics.getStatementCoverage(), 0, 0, 0.0);
    assertEvaluator(metrics.getBranchCoverage(), false, false);
    assertCounter(metrics.getBranchCoverage(), 0, 0, 0.0);
  }

  @Test
  public void testParseRecipeWithEmptyDuplicationData()
      throws IOException, InterruptedException {
    File directory = utils.createDirectory("report", "B-1.0.0-r0");
    builder.append("{ 'size': [], 'violations': [], 'complexity': [] }");
    utils.writeLines(builder, directory, "checkcode", "sage_report.json");
    recipe = new Recipe(new FilePath(directory));
    metrics.parse(recipe);
    assertEvaluator(metrics.getDuplications(), true, true);
    assertCounter(metrics.getDuplications(), 0, 0, 0.0);
  }

  @Test
  public void testParseRecipeWithEmptyMutationTestData()
      throws IOException, InterruptedException {
    File directory = utils.createDirectory("report", "B-1.0.0-r0");
    builder.append("<mutations></mutations>");
    utils.writeLines(builder, directory, "checktest", "mutations.xml");
    recipe = new Recipe(new FilePath(directory));
    metrics.parse(recipe);
    assertEvaluator(metrics.getMutationTest(), true, false);
    assertCounter(metrics.getMutationTest(), 0, 0, 0.0);
  }

  @Test
  public void testParseRecipeWithEmptyRecipeViolationData()
      throws IOException, InterruptedException {
    File directory = utils.createDirectory("report", "B-1.0.0-r0");
    builder = new StringBuilder();
    builder.append("{ 'issues': [ ] }");
    utils.writeLines(builder, directory, "checkrecipe", "recipe_violations.json");
    builder = new StringBuilder();
    builder.append("{ 'lines_of_code' : [ ] }");
    utils.writeLines(builder, directory, "checkrecipe", "files.json");
    recipe = new Recipe(new FilePath(directory));
    metrics.parse(recipe);
    assertEvaluator(metrics.getRecipeViolations(), true, true);
    assertCounter(metrics.getRecipeViolations(), 0, 0, 0.0);
  }

  @Test
  public void testParseRecipeWithEmptyTestData()
      throws IOException, InterruptedException {
    File directory = utils.createDirectory("report", "C-1.0.0-r0");
    builder.append("<testsuites> </testsuites>");
    utils.writeLines(builder, directory, "test", "1.xml");
    recipe = new Recipe(new FilePath(directory));
    metrics.parse(recipe);
    assertEvaluator(metrics.getTest(), true, false);
    assertCounter(metrics.getTest(), 0, 0, 0.0);
  }

  @Test
  public void testUnstableBuildStatusWithQualifiedData() {
    BuildStatus status = new Configuration();
    status.setCommentsAsUnstable(true);
    status.setDuplicationsAsUnstable(true);
    recipe.add(new CommentData("A-1.0.0-r0", "a.file", 10, 5));
    recipe.add(new DuplicationData("A-1.0.0-r0", "b.file", 10, 0));
    metrics.parse(recipe);
    assertTrue(metrics.isStable(status));
  }

  @Test
  public void testUnstableBuildStatusWithUnqualifiedData() {
    BuildStatus status = new Configuration();
    status.setCommentsAsUnstable(true);
    status.setDuplicationsAsUnstable(true);
    recipe.add(new CommentData("A-1.0.0-r0", "a.file", 10, 5));
    recipe.add(new DuplicationData("A-1.0.0-r0", "b.file", 10, 10));
    metrics.parse(recipe);
    assertFalse(metrics.isStable(status));
  }

  @Test
  public void testStableBuildStatusWithUnqualifiedData() {
    BuildStatus status = new Configuration();
    status.setCommentsAsUnstable(false);
    status.setDuplicationsAsUnstable(false);
    recipe.add(new CommentData("A-1.0.0-r0", "a.file", 10, 5));
    recipe.add(new DuplicationData("A-1.0.0-r0", "b.file", 10, 10));
    metrics.parse(recipe);
    assertTrue(metrics.isStable(status));
  }

  @Test
  public void testToJsonObject() {
    metrics.parse(recipe);
    JSONObject object = metrics.toJsonObject();
    assertEquals(0, object.getLong("denominator"));
    assertEquals(0, object.getLong("numerator"));
    assertEquals(0.0, object.getDouble("ratio"), 0.1);
    assertFalse(object.getJSONObject("premirrorCache").getBoolean("available"));
    assertFalse(object.getJSONObject("premirrorCache").getBoolean("qualified"));
    assertFalse(object.getJSONObject("sharedStateCache").getBoolean("available"));
    assertFalse(object.getJSONObject("sharedStateCache").getBoolean("qualified"));
    assertFalse(object.getJSONObject("codeSize").getBoolean("available"));
    assertFalse(object.getJSONObject("codeSize").getBoolean("qualified"));
    assertFalse(object.getJSONObject("codeViolations").getBoolean("available"));
    assertFalse(object.getJSONObject("codeViolations").getBoolean("qualified"));
    assertFalse(object.getJSONObject("comments").getBoolean("available"));
    assertFalse(object.getJSONObject("comments").getBoolean("qualified"));
    assertFalse(object.getJSONObject("complexity").getBoolean("available"));
    assertFalse(object.getJSONObject("complexity").getBoolean("qualified"));
    assertFalse(object.getJSONObject("statementCoverage").getBoolean("available"));
    assertFalse(object.getJSONObject("statementCoverage").getBoolean("qualified"));
    assertFalse(object.getJSONObject("branchCoverage").getBoolean("available"));
    assertFalse(object.getJSONObject("branchCoverage").getBoolean("qualified"));
    assertFalse(object.getJSONObject("duplications").getBoolean("available"));
    assertFalse(object.getJSONObject("duplications").getBoolean("qualified"));
    assertFalse(object.getJSONObject("mutationTest").getBoolean("available"));
    assertFalse(object.getJSONObject("mutationTest").getBoolean("qualified"));
    assertFalse(object.getJSONObject("recipeViolations").getBoolean("available"));
    assertFalse(object.getJSONObject("recipeViolations").getBoolean("qualified"));
    assertFalse(object.getJSONObject("test").getBoolean("available"));
    assertFalse(object.getJSONObject("test").getBoolean("qualified"));
  }
}
