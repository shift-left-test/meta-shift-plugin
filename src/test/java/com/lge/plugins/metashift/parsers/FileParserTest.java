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

package com.lge.plugins.metashift.parsers;

import static org.junit.Assert.assertEquals;

import com.lge.plugins.metashift.fixture.FakeRecipe;
import com.lge.plugins.metashift.fixture.FakeReportBuilder;
import com.lge.plugins.metashift.fixture.FakeScript;
import com.lge.plugins.metashift.fixture.FakeSource;
import com.lge.plugins.metashift.models.BranchCoverageData;
import com.lge.plugins.metashift.models.CodeSizeData;
import com.lge.plugins.metashift.models.CodeViolationData;
import com.lge.plugins.metashift.models.ComplexityData;
import com.lge.plugins.metashift.models.MutationTestData;
import com.lge.plugins.metashift.models.PremirrorCacheData;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.RecipeViolationData;
import com.lge.plugins.metashift.models.Recipes;
import com.lge.plugins.metashift.models.SharedStateCacheData;
import com.lge.plugins.metashift.models.StatementCoverageData;
import com.lge.plugins.metashift.models.TestData;
import com.lge.plugins.metashift.utils.TemporaryFileUtils;
import hudson.FilePath;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

/**
 * Unit tests for the FileParser class.
 *
 * @author Sung Gon Kim
 */
public class FileParserTest {

  @Rule
  public final TemporaryFolder folder = new TemporaryFolder();
  private TemporaryFileUtils utils;
  private File source;
  private File report;
  private Recipes recipes;
  private Recipe recipe;
  private FakeReportBuilder builder;
  private PrintStream logger;
  private FileParser parser;

  @Before
  public void setUp() throws IOException {
    utils = new TemporaryFileUtils(folder);
    source = utils.createDirectory("source");
    report = utils.createDirectory("report");
    recipes = new Recipes();
    recipe = new Recipe("A-A-A");
    builder = new FakeReportBuilder();
    logger = Mockito.mock(PrintStream.class);
    parser = new FileParser(logger);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithUnknownPath() throws IOException, InterruptedException {
    parser.parse(new FilePath(utils.getPath("path-to-unknown")));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithoutDirectory() throws IOException, InterruptedException {
    parser.parse(new FilePath(utils.createFile("path-to-file")));
  }

  @Test
  public void testCreateWithEmptyReportDirectory() throws IOException, InterruptedException {
    File report = utils.createDirectory("report");
    recipes = parser.parse(new FilePath(report));
    assertEquals(0, recipes.size());
  }

  @Test
  public void testCreateWithoutSubDirectories() throws IOException, InterruptedException {
    File report = utils.createDirectory("report");
    utils.createFile(report, "a.file");
    recipes = parser.parse(new FilePath(report));
    assertEquals(0, recipes.size());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithInvalidDirectories() throws IOException, InterruptedException {
    File report = utils.createDirectory("report");
    utils.createDirectory(report, "invalid");
    parser.parse(new FilePath(report));
  }

  @Test
  public void testCreateWithMultipleDirectories() throws IOException, InterruptedException {
    builder
        .add(new FakeRecipe(source)
            .add(new FakeScript(10))
            .add(new FakeSource(10, 3, 2, 0)))
        .add(new FakeRecipe(source)
            .add(new FakeScript(20))
            .add(new FakeSource(20, 6, 5, 0)))
        .add(new FakeRecipe(source)
            .add(new FakeScript(30))
            .add(new FakeSource(30, 9, 8, 0)));
    builder.toFile(report);

    recipes = parser.parse(new FilePath(report));
    assertEquals(3, recipes.size());
    assertEquals(3, recipes.objects(CodeSizeData.class).count());
  }

  @Test
  public void testParseSingleRecipeReportFiles() throws IOException, InterruptedException {
    builder.add(new FakeRecipe(source).setPremirror(1, 2).setSharedState(3, 4)
        .add(new FakeScript(10).setIssues(1, 2, 3))
        .add(new FakeSource(10, 3, 2, 1)
            .setMutationTests(1, 2, 3)
            .setTests(1, 2, 3, 4)
            .setStatementCoverage(1, 2)
            .setBranchCoverage(1, 2)
            .setComplexity(10, 1, 2)
            .setCodeViolations(1, 2, 3)
        )
    );
    builder.toFile(report);

    recipes = parser.parse(new FilePath(report));
    assertEquals(1, recipes.size());
    assertEquals(3, recipes.objects(PremirrorCacheData.class).count());
    assertEquals(7, recipes.objects(SharedStateCacheData.class).count());
    assertEquals(6, recipes.objects(RecipeViolationData.class).count());
    assertEquals(6, recipes.objects(MutationTestData.class).count());
    assertEquals(10, recipes.objects(TestData.class).count());
    assertEquals(3, recipes.objects(StatementCoverageData.class).count());
    assertEquals(3, recipes.objects(BranchCoverageData.class).count());
    assertEquals(3, recipes.objects(ComplexityData.class).count());
    assertEquals(6, recipes.objects(CodeViolationData.class).count());
  }

  @Test
  public void testParseMultipleRecipeReportFiles() throws IOException, InterruptedException {
    builder.add(new FakeRecipe(source).setPremirror(1, 2).setSharedState(3, 4)
        .add(new FakeScript(10).setIssues(1, 2, 3))
        .add(new FakeSource(10, 3, 2, 1)
            .setMutationTests(1, 2, 3)
            .setTests(1, 2, 3, 4)
            .setStatementCoverage(1, 2)
            .setBranchCoverage(1, 2)
            .setComplexity(10, 1, 2)
            .setCodeViolations(1, 2, 3)
        )
    );
    builder.add(new FakeRecipe(source).setPremirror(1, 2).setSharedState(3, 4)
        .add(new FakeScript(10).setIssues(1, 2, 3))
        .add(new FakeSource(10, 3, 2, 1)
            .setMutationTests(1, 2, 3)
            .setTests(1, 2, 3, 4)
            .setStatementCoverage(1, 2)
            .setBranchCoverage(1, 2)
            .setComplexity(10, 1, 2)
            .setCodeViolations(1, 2, 3)
        )
    );
    builder.toFile(report);

    recipes = parser.parse(new FilePath(report));
    assertEquals(2, recipes.size());
    assertEquals(6, recipes.objects(PremirrorCacheData.class).count());
    assertEquals(14, recipes.objects(SharedStateCacheData.class).count());
    assertEquals(12, recipes.objects(RecipeViolationData.class).count());
    assertEquals(12, recipes.objects(MutationTestData.class).count());
    assertEquals(20, recipes.objects(TestData.class).count());
    assertEquals(6, recipes.objects(StatementCoverageData.class).count());
    assertEquals(6, recipes.objects(BranchCoverageData.class).count());
    assertEquals(6, recipes.objects(ComplexityData.class).count());
    assertEquals(12, recipes.objects(CodeViolationData.class).count());
  }

  @Test
  public void testRemovesRecipesWithNoSourceFiles() throws IOException, InterruptedException {
    builder.add(new FakeRecipe(source).add(new FakeSource(0, 0, 1, 1)));
    builder.add(new FakeRecipe(source).add(new FakeSource(0, 0, 1, 1)));
    builder.add(new FakeRecipe(source).add(new FakeSource(0, 0, 1, 1)));
    builder.toFile(report);
    recipes = parser.parse(new FilePath(report));
    Mockito.verify(logger).printf("[meta-shift-plugin] -> Found %d recipe data%n", 3);
    Mockito.verify(logger).printf("[meta-shift-plugin] -> %d recipe data removed.%n", 3);
  }

  @Test
  public void testRemovesRecipesWithHiddenDirectoryName() throws IOException, InterruptedException {
    builder.add(new FakeRecipe(source, ".hidden-project-1.0.0-r0").add(new FakeSource(1, 1, 1, 1)));
    builder.add(new FakeRecipe(source, "qmake5-project-1.0.0-r0").add(new FakeSource(0, 0, 1, 1)));
    builder.toFile(report);
    recipes = parser.parse(new FilePath(report));
    Mockito.verify(logger).printf("[meta-shift-plugin] -> Found %d recipe data%n", 1);
    Mockito.verify(logger).printf("[meta-shift-plugin] -> %d recipe data removed.%n", 1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseDirectoryWithInvalidName() throws IOException, InterruptedException {
    builder.add(new FakeRecipe(source, "invalid_name").add(new FakeSource(1, 1, 1, 1)));
    builder.toFile(report);
    new FileParser().parse(new FilePath(report));
  }

  @Test
  public void testInitWithComplexName() throws IOException, InterruptedException {
    String expected = "A.B.C.qtbase+-native-5.15.2+gitAUTOINC+40143c189b-X-r+1.0-X";
    builder.add(new FakeRecipe(source, expected).add(new FakeSource(1, 1, 1, 1)));
    builder.toFile(report);
    recipes = new FileParser().parse(new FilePath(report));
    assertEquals(1, recipes.size());
    assertEquals(expected, recipes.get(0).getName());
  }
}
