/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.fixture;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.jsoniter.any.Any;
import com.lge.plugins.metashift.utils.JsonUtils;
import com.lge.plugins.metashift.utils.TemporaryFileUtils;
import hudson.FilePath;
import java.io.File;
import java.io.IOException;
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

    File file = FileUtils.getFile(report, fakeRecipe.getName(), "checkcache", "caches.json");
    assertTrue(file.exists());

    Any object = JsonUtils.createObject(new FilePath(file));
    Any premirror = object.get("Premirror");
    assertEquals(3, premirror.get("Summary").toLong("Wanted"));
    assertEquals(1, premirror.get("Summary").toLong("Found"));
    assertEquals(2, premirror.get("Summary").toLong("Missed"));
    assertEquals(1, premirror.get("Found").size());
    assertEquals(2, premirror.get("Missed").size());
    Any sharedState = object.get("Shared State");
    assertEquals(7, sharedState.get("Summary").toLong("Wanted"));
    assertEquals(3, sharedState.get("Summary").toLong("Found"));
    assertEquals(4, sharedState.get("Summary").toLong("Missed"));
    assertEquals(3, sharedState.get("Found").asList().size());
    assertEquals(4, sharedState.get("Missed").asList().size());
  }
}
