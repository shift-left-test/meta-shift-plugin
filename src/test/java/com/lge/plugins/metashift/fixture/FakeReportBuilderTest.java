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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.lge.plugins.metashift.utils.TemporaryFileUtils;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Unit tests for the FakeReportBuilder class.
 *
 * @author Sung Gon Kim
 */
public class FakeReportBuilderTest {

  @Rule
  public final TemporaryFolder folder = new TemporaryFolder();
  private File source;
  private File report;
  private FakeReportBuilder builder;

  @Before
  public void setUp() {
    TemporaryFileUtils utils = new TemporaryFileUtils(folder);
    source = utils.getPath("path", "to", "source");
    report = utils.getPath("report");
    builder = new FakeReportBuilder();
  }

  @Test
  public void testCreateWithNoData() throws IOException {
    builder.toFile(report);
    assertNull(report.listFiles());
  }

  @Test
  public void testCreateWithSingleRecipe() throws IOException {
    FakeRecipe recipe = new FakeRecipe(source);
    recipe
        .add(new FakeScript(10, 1, 2, 3))
        .add(new FakeSource(10, 4, 5, 6)
            .setComplexity(10, 5, 6)
            .setCodeViolations(1, 2, 3)
            .setTests(1, 2, 3, 4)
            .setStatementCoverage(1, 2)
            .setBranchCoverage(3, 4)
            .setMutationTests(1, 2, 3));
    builder.add(recipe);
    builder.toFile(report);

    File root = FileUtils.getFile(report, recipe.getRecipe());
    assertTrue(root.exists());
    assertTrue(FileUtils.getFile(root, "metadata.json").exists());
    assertTrue(FileUtils.getFile(root, "checkcache", "caches.json").exists());
    assertTrue(FileUtils.getFile(root, "checkcode", "sage_report.json").exists());
    assertTrue(FileUtils.getFile(root, "checkrecipe", "files.json").exists());
    assertTrue(FileUtils.getFile(root, "checkrecipe", "recipe_violations.json").exists());
    assertTrue(FileUtils.getFile(root, "checktest", "mutations.xml").exists());
    assertTrue(FileUtils.getFile(root, "coverage", "coverage.xml").exists());
    assertNotNull(FileUtils.getFile(root, "test").listFiles());
  }
}
