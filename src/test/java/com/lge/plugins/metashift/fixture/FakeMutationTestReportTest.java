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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.lge.plugins.metashift.utils.TemporaryFileUtils;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Unit tests for the FakeMutationTestReport class.
 *
 * @author Sung Gon Kim
 */
public class FakeMutationTestReportTest {

  @Rule
  public final TemporaryFolder folder = new TemporaryFolder();
  private FakeRecipe fakeRecipe;
  private FakeMutationTestReport fakeReport;
  private File report;

  @Before
  public void setUp() {
    TemporaryFileUtils utils = new TemporaryFileUtils(folder);
    File source = utils.getPath("source");
    fakeRecipe = new FakeRecipe(source);
    fakeReport = new FakeMutationTestReport(fakeRecipe);
    report = utils.getPath("report");
  }

  private void assertExists(List<String> lines, String expectedLine, long expectedCount) {
    long actualCount = lines.stream().filter(line -> line.trim().equals(expectedLine)).count();
    assertEquals(expectedCount, actualCount);
  }

  @Test
  public void testToFile() throws IOException {
    fakeRecipe.add(new FakeSource(fakeRecipe, 10, 5, 5, 0).setMutationTests(1, 2, 3));
    fakeReport.toFile(report);

    File file = FileUtils.getFile(report, fakeRecipe.getName(), "checktest", "mutations.xml");
    assertTrue(file.exists());

    List<String> lines = FileUtils.readLines(file, StandardCharsets.UTF_8);
    assertExists(lines, "<mutation detected=\"true\">", 1);
    assertExists(lines, "<mutation detected=\"false\">", 2);
    assertExists(lines, "<mutation detected=\"skip\">", 3);
  }
}
