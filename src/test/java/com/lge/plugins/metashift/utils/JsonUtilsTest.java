/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import hudson.FilePath;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Unit tests for the JsonUtils class.
 *
 * @author Sung Gon Kim
 */
public class JsonUtilsTest {

  @Rule
  public final TemporaryFolder folder = new TemporaryFolder();
  private TemporaryFileUtils utils;
  private StringBuilder builder;

  @Before
  public void setUp() {
    utils = new TemporaryFileUtils(folder);
    builder = new StringBuilder();
  }

  @Test
  public void testCreateWithNullFile() throws IOException, InterruptedException {
    assertEquals(JsonUtils.EMPTY, JsonUtils.createObject(null));
  }

  @Test(expected = IOException.class)
  public void testCreateWithUnknownPath() throws IOException, InterruptedException {
    JsonUtils.createObject(new FilePath(utils.getPath("path-to-unknown")));
  }

  @Test(expected = IOException.class)
  public void testCreateWithNoneFile() throws IOException, InterruptedException {
    JsonUtils.createObject(new FilePath(utils.createDirectory("directory")));
  }

  @Test
  public void testCreateWithIdenticalFile() throws IOException, InterruptedException {
    builder.append("{ }");
    File file = utils.createFile("test.json");
    utils.writeLines(builder, file);
    assertSame(JsonUtils.createObject(new FilePath(file)),
        JsonUtils.createObject(new FilePath(file)));
  }

  @Test
  public void testCreateObject() throws IOException, InterruptedException {
    builder.append("{ \"message\": \"test\" }");
    File file = utils.createFile("test.json");
    utils.writeLines(builder, file);
    assertEquals("test", JsonUtils.createObject(new FilePath(file)).toString("message"));
  }

  @Test
  public void testCreateWithIdenticalContents() throws IOException, InterruptedException {
    builder.append("{ }");
    File file1 = utils.createFile("test1.json");
    utils.writeLines(builder, file1);
    File file2 = utils.createFile("test2.json");
    utils.writeLines(builder, file2);

    assertSame(JsonUtils.createObject(new FilePath(file1)),
        JsonUtils.createObject(new FilePath(file2)));
  }

  @Test
  public void testCreateWithDifferentContents() throws IOException, InterruptedException {
    builder = new StringBuilder();
    builder.append("{ }");
    File file1 = utils.createFile("test1.json");
    utils.writeLines(builder, file1);

    builder = new StringBuilder();
    builder.append("{}");
    File file2 = utils.createFile("test2.json");
    utils.writeLines(builder, file2);

    assertNotSame(JsonUtils.createObject(new FilePath(file1)),
        JsonUtils.createObject(new FilePath(file2)));
  }

  @Test
  public void testSaveAs() throws IOException {
    JSONObject object = new JSONObject();
    object.put("A", "1");
    object.put("B", "2");
    File file = utils.getPath("path", "to", "file.json");
    JsonUtils.saveAs(object, file);
    assertEquals("{\n  \"A\": \"1\",\n  \"B\": \"2\"\n}",
        FileUtils.readFileToString(file, StandardCharsets.UTF_8));
  }
}
