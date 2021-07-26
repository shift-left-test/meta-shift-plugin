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
import static org.junit.Assert.assertTrue;

import com.lge.plugins.metashift.fixture.FakeRecipe;
import com.lge.plugins.metashift.fixture.FakeReportBuilder;
import com.lge.plugins.metashift.fixture.FakeScript;
import com.lge.plugins.metashift.fixture.FakeSource;
import com.lge.plugins.metashift.models.BranchCoverageData;
import com.lge.plugins.metashift.models.CacheData;
import com.lge.plugins.metashift.models.CodeSizeData;
import com.lge.plugins.metashift.models.CommentData;
import com.lge.plugins.metashift.models.ComplexityData;
import com.lge.plugins.metashift.models.CoverageData;
import com.lge.plugins.metashift.models.Data;
import com.lge.plugins.metashift.models.DuplicationData;
import com.lge.plugins.metashift.models.ErrorTestData;
import com.lge.plugins.metashift.models.FailedTestData;
import com.lge.plugins.metashift.models.InfoCodeViolationData;
import com.lge.plugins.metashift.models.InfoRecipeViolationData;
import com.lge.plugins.metashift.models.KilledMutationTestData;
import com.lge.plugins.metashift.models.MajorCodeViolationData;
import com.lge.plugins.metashift.models.MajorRecipeViolationData;
import com.lge.plugins.metashift.models.MinorCodeViolationData;
import com.lge.plugins.metashift.models.MinorRecipeViolationData;
import com.lge.plugins.metashift.models.PassedTestData;
import com.lge.plugins.metashift.models.PremirrorCacheData;
import com.lge.plugins.metashift.models.SkippedMutationTestData;
import com.lge.plugins.metashift.models.SkippedTestData;
import com.lge.plugins.metashift.models.StatementCoverageData;
import com.lge.plugins.metashift.models.SurvivedMutationTestData;
import com.lge.plugins.metashift.utils.TemporaryFileUtils;
import hudson.AbortException;
import hudson.FilePath;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Stream;
import org.apache.commons.io.FileUtils;
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
  private FakeReportBuilder builder;
  private PrintStream logger;
  private FileParser parser;
  private File source;
  private File report;

  @Before
  public void setUp() throws IOException {
    utils = new TemporaryFileUtils(folder);
    builder = new FakeReportBuilder();
    logger = Mockito.mock(PrintStream.class);
    parser = new FileParser(logger);
    source = utils.createDirectory("source");
    report = utils.createDirectory("report");
  }

  @SuppressWarnings({"unchecked", "PMD.UnnecessaryModifier"})
  private static <T> Stream<T> of(List<Data> objects, Class<T> clazz) {
    return (Stream<T>) objects.stream().filter(o -> clazz.isAssignableFrom(o.getClass()));
  }

  private void assertPremirror(List<Data> objects, long found, long missed) {
    assertTrue(of(objects, PremirrorCacheDataParsed.class).findAny().isPresent());
    assertEquals(found, of(objects, PremirrorCacheData.class)
        .filter(CacheData::isAvailable).count());
    assertEquals(missed, of(objects, PremirrorCacheData.class)
        .filter(o -> !o.isAvailable()).count());
  }

  private void assertSharedState(List<Data> objects, long found, long missed) {
    assertTrue(of(objects, SharedStateCacheDataParsed.class).findAny().isPresent());
    assertEquals(found, of(objects, PremirrorCacheData.class)
        .filter(CacheData::isAvailable).count());
    assertEquals(missed, of(objects, PremirrorCacheData.class)
        .filter(o -> !o.isAvailable()).count());
  }

  private void assertRecipeViolations(List<Data> objects, long major, long minor, long info) {
    assertTrue(of(objects, RecipeViolationDataParsed.class).findAny().isPresent());
    assertEquals(major, of(objects, MajorRecipeViolationData.class).count());
    assertEquals(minor, of(objects, MinorRecipeViolationData.class).count());
    assertEquals(info, of(objects, InfoRecipeViolationData.class).count());
  }

  private void assertTotalLines(List<Data> objects, long totalLines) {
    assertTrue(of(objects, CodeSizeDataParsed.class).findAny().isPresent());
    assertEquals(totalLines, of(objects, CodeSizeData.class)
        .mapToLong(CodeSizeData::getLines).sum());
  }

  private void assertComments(List<Data> objects, long comments) {
    assertTrue(of(objects, CommentDataParsed.class).findAny().isPresent());
    assertEquals(comments, of(objects, CommentData.class).count());
  }

  private void assertDuplications(List<Data> objects, long duplications) {
    assertTrue(of(objects, DuplicationDataParsed.class).findAny().isPresent());
    assertEquals(duplications, of(objects, DuplicationData.class).count());
  }

  private void assertComplexity(List<Data> objects, long tolerance, long abnormal, long normal) {
    assertTrue(of(objects, ComplexityDataParsed.class).findAny().isPresent());
    assertEquals(abnormal,
        of(objects, ComplexityData.class)
            .filter(o -> o.getValue() >= tolerance)
            .count());
    assertEquals(normal,
        of(objects, ComplexityData.class)
            .filter(o -> o.getValue() < tolerance)
            .count());
  }

  private void assertCodeViolations(List<Data> objects, long major, long minor, long info) {
    assertTrue(of(objects, CodeViolationDataParsed.class).findAny().isPresent());
    assertEquals(major, of(objects, MajorCodeViolationData.class).count());
    assertEquals(minor, of(objects, MinorCodeViolationData.class).count());
    assertEquals(info, of(objects, InfoCodeViolationData.class).count());
  }

  private void assertTests(List<Data> objects, long passed, long failed, long error, long skipped) {
    assertTrue(of(objects, TestDataParsed.class).findAny().isPresent());
    assertEquals(passed, of(objects, PassedTestData.class).count());
    assertEquals(failed, of(objects, FailedTestData.class).count());
    assertEquals(error, of(objects, ErrorTestData.class).count());
    assertEquals(skipped, of(objects, SkippedTestData.class).count());
  }

  private void assertStatementCoverage(List<Data> objects, long covered, long uncovered) {
    assertTrue(of(objects, StatementCoverageDataParsed.class).findAny().isPresent());
    assertEquals(covered, of(objects, StatementCoverageData.class)
        .filter(CoverageData::isCovered).count());
    assertEquals(uncovered, of(objects, StatementCoverageData.class)
        .filter(o -> !o.isCovered()).count());
  }

  private void assertBranchCoverage(List<Data> objects, long covered, long uncovered) {
    assertTrue(of(objects, BranchCoverageDataParsed.class).findAny().isPresent());
    assertEquals(covered,
        of(objects, BranchCoverageData.class)
            .filter(CoverageData::isCovered)
            .count());
    assertEquals(uncovered,
        of(objects, BranchCoverageData.class)
            .filter(o -> !o.isCovered())
            .count());
  }

  private void assertMutationTests(List<Data> objects, long killed, long survived, long skipped) {
    assertTrue(of(objects, MutationTestDataParsed.class).findAny().isPresent());
    assertEquals(killed, of(objects, KilledMutationTestData.class).count());
    assertEquals(survived, of(objects, SurvivedMutationTestData.class).count());
    assertEquals(skipped, of(objects, SkippedMutationTestData.class).count());
  }

  @Test(expected = AbortException.class)
  public void testParseRecipeWithUnknownPath() throws IOException, InterruptedException {
    parser.parse(new FilePath(utils.getPath("path-to-unknown")));
  }

  @Test(expected = AbortException.class)
  public void testParseWithNoDirectory() throws IOException, InterruptedException {
    parser.parse(new FilePath(utils.createFile("path-to-file")));
  }

  @Test
  public void testParseWithInvalidDirectoryName() throws IOException, InterruptedException {
    List<Data> objects = parser.parse(new FilePath(utils.createDirectory(report, "X")));
    Mockito.verify(logger).printf("[meta-shift-plugin] -> Found %d recipe data%n", 0);
    assertEquals(0, objects.size());
  }

  @Test
  public void testParseWithEmptySubdirectory() throws IOException, InterruptedException {
    utils.createDirectory(report, "A-1.0.0-r0");
    List<Data> objects = parser.parse(new FilePath(report));
    Mockito.verify(logger).printf("[meta-shift-plugin] -> Found %d recipe data%n", 1);
    assertEquals(0, objects.size());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseInvalidFormatFiles() throws IOException, InterruptedException {
    FakeRecipe fakeRecipe = new FakeRecipe(source);
    fakeRecipe.add(new FakeScript(10));
    fakeRecipe.add(new FakeSource(10, 10, 0, 0));
    builder.add(fakeRecipe);
    builder.toFile(report);

    File file = FileUtils.getFile(report, fakeRecipe.getRecipe(), "checkcode", "sage_report.json");
    FileUtils.write(file, "{ }", StandardCharsets.UTF_8);

    parser.parse(new FilePath(report));
  }

  @Test
  public void testRemovesRecipesWithNoSourceFiles() throws IOException, InterruptedException {
    builder.add(new FakeRecipe(source).add(new FakeSource(0, 0, 0, 0)));
    builder.add(new FakeRecipe(source).add(new FakeSource(0, 0, 0, 0)));
    builder.add(new FakeRecipe(source).add(new FakeSource(0, 0, 0, 0)));
    builder.toFile(report);

    List<Data> objects = parser.parse(new FilePath(report));
    assertEquals(0, objects.size());
    Mockito.verify(logger).printf("[meta-shift-plugin] -> Found %d recipe data%n", 3);
    Mockito.verify(logger).printf("[meta-shift-plugin] -> %d recipe data removed.%n", 3);
  }

  @Test
  public void testRemovesRecipesWithHiddenDirectoryName() throws IOException, InterruptedException {
    builder.add(new FakeRecipe(source, ".hidden").add(new FakeSource(1, 1, 1, 1)));
    builder.toFile(report);
    List<Data> objects = parser.parse(new FilePath(report));
    assertEquals(0, objects.size());
    Mockito.verify(logger).printf("[meta-shift-plugin] -> Found %d recipe data%n", 0);
  }

  @Test
  public void testRemovesRecipesWithHiddenFileName() throws IOException, InterruptedException {
    builder.add(new FakeRecipe(source).add(new FakeSource(".hidden", 1, 1, 1, 1)));
    builder.toFile(report);
    List<Data> objects = parser.parse(new FilePath(report));
    assertEquals(0, objects.size());
    Mockito.verify(logger).printf("[meta-shift-plugin] -> Found %d recipe data%n", 1);
    Mockito.verify(logger).printf("[meta-shift-plugin] -> %d recipe data removed.%n", 1);
  }

  @Test
  public void testParseSingleSourceFiles() throws IOException, InterruptedException {
    FakeRecipe fakeRecipe = new FakeRecipe(source)
        .setPremirror(1, 1)
        .setSharedState(1, 1)
        .add(new FakeScript(1, 1, 1, 1))
        .add(new FakeSource(1, 1, 1, 1)
            .setComplexity(10, 1, 1)
            .setCodeViolations(1, 1, 1)
            .setTests(1, 1, 1, 1)
            .setStatementCoverage(1, 1)
            .setBranchCoverage(1, 1)
            .setMutationTests(1, 1, 1));
    builder.add(fakeRecipe);
    builder.toFile(report);

    List<Data> objects = parser.parse(new FilePath(report));
    assertPremirror(objects, 1, 1);
    assertSharedState(objects, 1, 1);
    assertRecipeViolations(objects, 1, 1, 1);
    assertTotalLines(objects, 1);
    assertComments(objects, 1);
    assertDuplications(objects, 1);
    assertComplexity(objects, 1, 1, 1);
    assertCodeViolations(objects, 1, 1, 1);
    assertTests(objects, 1, 1, 1, 1);
    assertStatementCoverage(objects, 1, 1);
    assertBranchCoverage(objects, 1, 1);
    assertMutationTests(objects, 1, 1, 1);
  }

  @Test
  public void testParseMultipleSourceFiles() throws IOException, InterruptedException {
    FakeRecipe fakeRecipe = new FakeRecipe(source)
        .setPremirror(2, 2)
        .setSharedState(2, 2)
        .add(new FakeScript(1, 1, 1, 1))
        .add(new FakeScript(1, 1, 1, 1))
        .add(new FakeSource(1, 1, 1, 1)
            .setComplexity(10, 1, 1)
            .setCodeViolations(1, 1, 1)
            .setTests(1, 1, 1, 1)
            .setStatementCoverage(1, 1)
            .setBranchCoverage(1, 1)
            .setMutationTests(1, 1, 1))
        .add(new FakeSource(1, 1, 1, 1)
            .setComplexity(10, 1, 1)
            .setCodeViolations(1, 1, 1)
            .setTests(1, 1, 1, 1)
            .setStatementCoverage(1, 1)
            .setBranchCoverage(1, 1)
            .setMutationTests(1, 1, 1));
    builder.add(fakeRecipe);
    builder.toFile(report);

    List<Data> objects = parser.parse(new FilePath(report));
    assertPremirror(objects, 2, 2);
    assertSharedState(objects, 2, 2);
    assertRecipeViolations(objects, 2, 2, 2);
    assertTotalLines(objects, 2);
    assertComments(objects, 2);
    assertDuplications(objects, 2);
    assertComplexity(objects, 10, 2, 2);
    assertCodeViolations(objects, 2, 2, 2);
    assertTests(objects, 2, 2, 2, 2);
    assertStatementCoverage(objects, 2, 2);
    assertBranchCoverage(objects, 2, 2);
    assertMutationTests(objects, 2, 2, 2);
  }
}
