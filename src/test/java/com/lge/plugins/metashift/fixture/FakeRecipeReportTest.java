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

import com.lge.plugins.metashift.utils.JsonUtils;
import com.lge.plugins.metashift.utils.TemporaryFileUtils;
import hudson.FilePath;
import java.io.File;
import java.io.IOException;
import java.util.List;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Unit tests for the FakeRecipeReport class.
 *
 * @author Sung Gon Kim
 */
public class FakeRecipeReportTest {

  @Rule
  public final TemporaryFolder folder = new TemporaryFolder();
  private FakeRecipe fakeRecipe;
  private FakeRecipeReport fakeReport;
  private File report;

  @Before
  public void setUp() {
    TemporaryFileUtils utils = new TemporaryFileUtils(folder);
    File source = utils.getPath("source");
    fakeRecipe = new FakeRecipe(source);
    fakeRecipe.add(new FakeScript(fakeRecipe, 10, 1, 2, 3));
    fakeRecipe.add(new FakeScript(fakeRecipe, 20, 3, 2, 1));
    fakeReport = new FakeRecipeReport(fakeRecipe);
    report = utils.getPath("report");
  }

  @Test
  public void testToFileCreatesFilesJson() throws IOException, InterruptedException {
    fakeReport.toFile(report);

    File file = FileUtils.getFile(report, fakeRecipe.getName(), "checkrecipe", "files.json");
    assertTrue(file.exists());

    List<FakeScript> scripts = fakeRecipe.getScripts();
    JSONObject object = JsonUtils.createObject(new FilePath(file));

    JSONObject first = (JSONObject) object.getJSONArray("lines_of_code").get(0);
    File aFile = FileUtils.getFile(fakeRecipe.getSourcePath(),
        fakeRecipe.getName(), scripts.get(0).getFilename());
    assertEquals(aFile.getAbsolutePath(), first.getString("file"));
    assertEquals(10, first.getLong("code_lines"));

    JSONObject second = (JSONObject) object.getJSONArray("lines_of_code").get(1);
    File bFile = FileUtils.getFile(fakeRecipe.getSourcePath(),
        fakeRecipe.getName(), scripts.get(1).getFilename());
    assertEquals(bFile.getAbsolutePath(), second.getString("file"));
    assertEquals(20, second.getLong("code_lines"));
  }

  @Test
  public void testToFile() throws IOException, InterruptedException {
    fakeReport.toFile(report);

    File file = FileUtils.getFile(report, fakeRecipe.getName(),
        "checkrecipe", "recipe_violations.json");
    assertTrue(file.exists());

    JSONObject object = JsonUtils.createObject(new FilePath(file));
    assertEquals(12, object.getJSONArray("issues").size());
  }
}
