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
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Unit tests for the FakeCacheReport class.
 *
 * @author Sung Gon Kim
 */
public class FakeCacheReportTest {

  @Rule
  public final TemporaryFolder folder = new TemporaryFolder();
  private FakeRecipe fakeRecipe;
  private FakeCacheReport fakeReport;
  private File report;

  @Before
  public void setUp() {
    TemporaryFileUtils utils = new TemporaryFileUtils(folder);
    File source = utils.getPath("source");
    fakeRecipe = new FakeRecipe(source);
    fakeReport = new FakeCacheReport(fakeRecipe);
    report = utils.getPath("report");
  }

  @Test
  public void testToFile() throws IOException, InterruptedException {
    fakeRecipe.setPremirror(1, 2);
    fakeRecipe.setSharedState(3, 4);
    fakeReport.toFile(report);

    File file = FileUtils.getFile(report, fakeRecipe.getRecipe(), "checkcache", "caches.json");
    assertTrue(file.exists());

    JSONObject object = JsonUtils.createObject(new FilePath(file));
    JSONObject premirror = object.getJSONObject("Premirror");
    assertEquals(3, premirror.getJSONObject("Summary").getLong("Wanted"));
    assertEquals(1, premirror.getJSONObject("Summary").getLong("Found"));
    assertEquals(2, premirror.getJSONObject("Summary").getLong("Missed"));
    assertEquals(1, premirror.getJSONArray("Found").size());
    assertEquals(2, premirror.getJSONArray("Missed").size());
    JSONObject sharedState = object.getJSONObject("Shared State");
    assertEquals(7, sharedState.getJSONObject("Summary").getLong("Wanted"));
    assertEquals(3, sharedState.getJSONObject("Summary").getLong("Found"));
    assertEquals(4, sharedState.getJSONObject("Summary").getLong("Missed"));
    assertEquals(3, sharedState.getJSONArray("Found").size());
    assertEquals(4, sharedState.getJSONArray("Missed").size());
  }
}