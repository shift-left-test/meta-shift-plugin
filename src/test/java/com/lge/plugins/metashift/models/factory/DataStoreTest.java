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

package com.lge.plugins.metashift.models.factory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.lge.plugins.metashift.models.CodeViolationData;
import com.lge.plugins.metashift.models.ComplexityData;
import com.lge.plugins.metashift.models.CoverageData;
import com.lge.plugins.metashift.models.Data;
import com.lge.plugins.metashift.models.MutationTestData;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.RecipeViolationData;
import com.lge.plugins.metashift.models.Recipes;
import com.lge.plugins.metashift.utils.TemporaryFileUtils;
import hudson.FilePath;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

/**
 * Unit tests for the DataStore class.
 *
 * @author Sung Gon Kim
 */
public class DataStoreTest {

  @Rule
  public final TemporaryFolder folder = new TemporaryFolder();
  private TemporaryFileUtils utils;
  private DataStore dataStore;
  private File report;
  private File source;
  private File recipe;
  private File objects;
  private File test_cpp;
  private File test_bb;

  @Before
  public void setUp() throws IOException {
    utils = new TemporaryFileUtils(folder);
    File storage = utils.getPath("storage");
    report = utils.createDirectory("my-report");
    source = utils.createDirectory("my-source");
    recipe = FileUtils.getFile(storage, "A-1.0.0-r0");
    objects = FileUtils.getFile(storage, "A-1.0.0-r0", "objects");
    test_cpp = FileUtils.getFile(objects, "10", "5edad8d18ec788380ae11c9dd9093cac79abb8");
    test_bb = FileUtils.getFile(objects, "ba", "8602f544e3ad92b8de87096d26f9f3f03037b8");
    dataStore = new DataStore(new FilePath(storage));
  }

  private void prepareFixture(boolean metadata, boolean sourceFiles, boolean sageReport,
      boolean mutationTest, boolean coverage, boolean recipeViolations) {
    if (metadata) {
      prepareMetadata();
    }
    if (sourceFiles) {
      prepareSourceFiles();
    }
    if (sageReport) {
      prepareSageReport();
    }
    if (mutationTest) {
      prepareMutationTestFile();
    }
    if (coverage) {
      prepareCoverageFile();
    }
    if (recipeViolations) {
      prepareRecipeViolationFile();
    }
  }

  private void prepareMetadata() {
    StringBuilder sb = new StringBuilder();
    sb.append(String.format("{ 'S': '%s' }", source.getAbsolutePath().replace('\\', '/')));
    utils.writeLines(sb, report, "A-1.0.0-r0", "metadata.json");
  }

  private void prepareSourceFiles() {
    StringBuilder sb = new StringBuilder();
    sb.append("#include<iostream>").append("int main() {").append("  return 0;").append("}");
    utils.writeLines(sb, source, "test.cpp");

    sb = new StringBuilder();
    sb.append("inherit cmake").append("SRC_URI = 'git://path/to/repo'");
    utils.writeLines(sb, source, "test.bb");
  }

  private void prepareSageReport() {
    String absolutePath = source.getAbsolutePath().replace('\\', '/');
    StringBuilder sb = new StringBuilder();
    sb
        .append("{")
        .append("  'size': [], ")
        .append("  'violations': [")
        .append("    {")
        .append("      'file': '../my-source/test.cpp',")
        .append("      'line': 1,")
        .append("      'column': 100,")
        .append("      'rule': 'NPE',")
        .append("      'message': 'NPE_message',")
        .append("      'description': 'NPE_desc',")
        .append("      'severity': 'error',")
        .append("      'level': 'major',")
        .append("      'tool': 'cppcheck'")
        .append("    }")
        .append("  ], ")
        .append("  'complexity': [ ")
        .append("    {")
        .append(String.format("      'file': '%s/test.cpp', ", absolutePath))
        .append("      'function': 'main()', ")
        .append("      'start': 1, ")
        .append("      'end': 1, ")
        .append("      'value': 1 ")
        .append("    }")
        .append("  ]")
        .append("}");
    utils.writeLines(sb, report, "A-1.0.0-r0", "checkcode", "sage_report.json");
  }

  private void prepareCoverageFile() {
    StringBuilder sb = new StringBuilder();
    sb
        .append("<classes>")
        .append("  <class filename='test.cpp'>")
        .append("    <methods>")
        .append("      <method name='main()'>")
        .append("        <lines>")
        .append("          <line number='1'/>")
        .append("        </lines>")
        .append("      </method>")
        .append("    </methods>")
        .append("    <lines>")
        .append("      <line branch='false' hits='1' number='2'/>")
        .append("    </lines>")
        .append("  </class>")
        .append("</classes>");
    utils.writeLines(sb, report, "A-1.0.0-r0", "coverage", "coverage.xml");
  }

  private void prepareMutationTestFile() {
    StringBuilder sb = new StringBuilder();
    sb
        .append("<mutations>")
        .append("  <mutation detected='true'>")
        .append("    <sourceFile>test.cpp</sourceFile>")
        .append("    <sourceFilePath>test.cpp</sourceFilePath>")
        .append("    <mutatedClass>A</mutatedClass>")
        .append("    <mutatedMethod>main</mutatedMethod>")
        .append("    <lineNumber>1</lineNumber>")
        .append("    <mutator>AOR</mutator>")
        .append("    <killingTest>test1</killingTest>")
        .append("  </mutation>")
        .append("</mutations>");
    utils.writeLines(sb, report, "A-1.0.0-r0", "checktest", "mutations.xml");
  }

  private void prepareRecipeViolationFile() {
    String absolutePath = source.getAbsolutePath().replace('\\', '/');
    StringBuilder sb = new StringBuilder();
    sb
        .append("{")
        .append("  'issues': [")
        .append("    {")
        .append(String.format("      'file': '%s/test.bb', ", absolutePath))
        .append("      'line': 1,")
        .append("      'rule': 'checksum',")
        .append("      'severity': 'error',")
        .append("      'description': 'checksum error'")
        .append("    }")
        .append("  ]")
        .append("}");
    utils.writeLines(sb, report, "A-1.0.0-r0", "checkrecipe", "recipe_violations.json");
  }

  @Test(expected = FileNotFoundException.class)
  public void testLoadFromUnknownPath() throws IOException, InterruptedException {
    dataStore.load(new FilePath(utils.getPath("path/to/unknown")));
  }

  @Test
  public void testLoadPrintsLogs() throws IOException, InterruptedException {
    prepareFixture(true, true, true, true, true, true);
    PrintStream logger = Mockito.mock(PrintStream.class);
    dataStore.load(new FilePath(report), logger);
    Mockito.verify(logger).printf(Mockito.startsWith("[DataStore] Copying files:"),
        Mockito.any(), Mockito.any());
    Mockito.verify(logger, Mockito.times(5))
        .printf(Mockito.startsWith("[DataStore] %s: Collecting"), Mockito.anyString());
    Mockito.verify(logger)
        .printf(Mockito.eq("[DataStore] %s: Creating index.json"), Mockito.anyString());
  }

  @Test
  public void testLoadPrintFailureMessageWhenNotMetadataFound()
      throws IOException, InterruptedException {
    prepareFixture(false, true, true, true, true, true);
    PrintStream logger = Mockito.mock(PrintStream.class);
    dataStore.load(new FilePath(report), logger);
    Mockito.verify(logger)
        .printf(Mockito.eq("[DataStore] %s: Failed to read metadata.json: %s"),
            Mockito.anyString(), Mockito.any());
  }

  @Test
  public void testLoadPrintFailureMessageWhenNoSourceFileFound()
      throws IOException, InterruptedException {
    prepareFixture(true, false, true, true, true, true);
    PrintStream logger = Mockito.mock(PrintStream.class);
    dataStore.load(new FilePath(report), logger);
    Mockito.verify(logger, Mockito.times(5))
        .printf(Mockito.eq("[DataStore] %s: Failed to copy the file: '%s' -> '%s'"),
            Mockito.anyString(), Mockito.any(), Mockito.any());
  }

  @Test
  public void testLoadSavesFiles() throws IOException, InterruptedException {
    prepareFixture(true, true, true, true, true, true);
    dataStore.load(new FilePath(report));
    assertTrue(FileUtils.getFile(recipe, "metadata.json").exists());
    assertTrue(FileUtils.getFile(recipe, "checkcode", "sage_report.json").exists());
    assertTrue(FileUtils.getFile(recipe, "coverage", "coverage.xml").exists());
    assertTrue(FileUtils.getFile(recipe, "checktest", "mutations.xml").exists());
    assertTrue(FileUtils.getFile(recipe, "checkrecipe", "recipe_violations.json").exists());
    assertTrue(FileUtils.getFile(objects, "index.json").exists());
    assertTrue(test_cpp.exists());
    assertTrue(test_bb.exists());
  }

  @Test
  public void testLoadReturnedRecipes() throws IOException, InterruptedException {
    prepareFixture(true, true, true, true, true, true);
    Recipes recipes = dataStore.load(new FilePath(report));
    assertEquals(1, recipes.size());
    Recipe recipe = recipes.get(0);
    assertEquals("A-1.0.0-r0", recipe.getRecipe());
    assertEquals(5, recipe.objects(Data.class).count());
    File index = FileUtils.getFile(objects, "index.json");
    assertEquals(6, FileUtils.readLines(index, StandardCharsets.UTF_8).size());
  }

  @Test
  public void testGetSavedFileOfCodeViolationData() throws IOException, InterruptedException {
    prepareFixture(true, true, true, false, false, false);
    Recipes recipes = dataStore.load(new FilePath(report));
    Recipe recipe = recipes.get(0);

    CodeViolationData o = recipe.objects(CodeViolationData.class).findFirst()
        .orElseThrow(NullPointerException::new);
    assertEquals(test_cpp, dataStore.get(recipe.getRecipe(), o.getFile()));
  }

  @Test
  public void testGetSavedFileOfComplexityData() throws IOException, InterruptedException {
    prepareFixture(true, true, true, false, false, false);
    Recipe recipe = dataStore.load(new FilePath(report)).get(0);
    ComplexityData o = recipe.objects(ComplexityData.class).findFirst()
        .orElseThrow(NullPointerException::new);
    assertEquals(test_cpp, dataStore.get(recipe.getRecipe(), o.getFile()));
  }

  @Test
  public void testGetSavedFileOfCoverageData() throws IOException, InterruptedException {
    prepareFixture(true, true, false, false, true, false);
    Recipe recipe = dataStore.load(new FilePath(report)).get(0);
    CoverageData o = recipe.objects(CoverageData.class).findFirst()
        .orElseThrow(NullPointerException::new);
    assertEquals(test_cpp, dataStore.get(recipe.getRecipe(), o.getFile()));
  }

  @Test
  public void testGetSavedFileOfMutationTestData() throws IOException, InterruptedException {
    prepareFixture(true, true, false, true, false, false);
    Recipe recipe = dataStore.load(new FilePath(report)).get(0);
    MutationTestData o = recipe.objects(MutationTestData.class).findFirst()
        .orElseThrow(NullPointerException::new);
    assertEquals(test_cpp, dataStore.get(recipe.getRecipe(), o.getFile()));
  }

  @Test
  public void testGetSavedFileOfRecipeViolationData() throws IOException, InterruptedException {
    prepareFixture(true, true, false, false, false, true);
    Recipe recipe = dataStore.load(new FilePath(report)).get(0);
    RecipeViolationData o = recipe.objects(RecipeViolationData.class).findFirst()
        .orElseThrow(NullPointerException::new);
    assertEquals(test_bb, dataStore.get(recipe.getRecipe(), o.getFile()));
  }

  @Test
  public void testGetReturnsNullWhenUnknownRecipeGiven() throws IOException, InterruptedException {
    prepareFixture(true, true, true, true, true, true);
    dataStore.load(new FilePath(report));
    assertNull(dataStore.get("unknown-1.0.0-r0", "test.cpp"));
  }

  @Test
  public void testGetReturnsNullWhenUnknownFileGiven() throws IOException, InterruptedException {
    prepareFixture(true, true, true, true, true, true);
    dataStore.load(new FilePath(report));
    assertNull(dataStore.get("A-1.0.0-r0", "unknown.file"));
  }
}
