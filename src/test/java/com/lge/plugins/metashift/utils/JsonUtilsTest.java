/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import hudson.FilePath;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
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
  public void testCreateWithIdenticalFileReturnsIndependentObjects()
      throws IOException, InterruptedException {
    builder.append("{ \"message\": \"test\" }");
    File file = utils.createFile("test.json");
    utils.writeLines(builder, file);

    // Only the file contents are cached, never the parsed object: a JSONObject is mutable,
    // so two callers must not end up holding the same instance.
    JSONObject first = JsonUtils.createObject(new FilePath(file));
    JSONObject second = JsonUtils.createObject(new FilePath(file));
    assertNotSame(first, second);
    assertEquals("test", first.getString("message"));
    assertEquals("test", second.getString("message"));
  }

  @Test
  public void testCreateObject() throws IOException, InterruptedException {
    builder.append("{ \"message\": \"test\" }");
    File file = utils.createFile("test.json");
    utils.writeLines(builder, file);
    assertEquals("test", JsonUtils.createObject(new FilePath(file)).getString("message"));
  }

  @Test
  public void testCreateWithIdenticalContents() throws IOException, InterruptedException {
    builder.append("{ \"message\": \"test\" }");
    File file1 = utils.createFile("test1.json");
    utils.writeLines(builder, file1);
    File file2 = utils.createFile("test2.json");
    utils.writeLines(builder, file2);

    // the second file is served from the contents cache, keyed by checksum
    assertEquals("test", JsonUtils.createObject(new FilePath(file1)).getString("message"));
    assertEquals("test", JsonUtils.createObject(new FilePath(file2)).getString("message"));
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
    assertEquals("{}", JsonUtils.createObject(new FilePath(file2)).toString());
  }

  /**
   * Number of descriptors this JVM has open, or -1 where the platform does not expose it.
   */
  private static long openDescriptors() {
    File[] descriptors = new File("/proc/self/fd").listFiles();
    return descriptors == null ? -1 : descriptors.length;
  }

  @Test
  public void testCreateObjectDoesNotLeaveTheFileOpen() throws IOException, InterruptedException {
    assumeTrue("this platform does not expose /proc/self/fd", openDescriptors() >= 0);
    long before = openDescriptors();

    for (int i = 0; i < 300; i++) {
      // distinct contents, so the cache never short-circuits the read
      File file = utils.createFile("leak" + i + ".json");
      utils.writeLines(new StringBuilder().append("{ \"n\": ").append(i).append(" }"), file);
      assertEquals(String.valueOf(i), JsonUtils.createObject(new FilePath(file)).getString("n"));
    }
    long leaked = openDescriptors() - before;

    assertTrue("createObject left " + leaked + " descriptors open over 300 files", leaked < 20);
  }

  @Test
  public void testCreateObjectFromSeveralThreadsAtOnce() throws Exception {
    int files = 40;
    for (int i = 0; i < files; i++) {
      File file = utils.createFile("concurrent" + i + ".json");
      utils.writeLines(new StringBuilder().append("{ \"n\": ").append(i).append(" }"), file);
    }

    ExecutorService executor = Executors.newFixedThreadPool(8);
    List<Throwable> failures = Collections.synchronizedList(new ArrayList<>());
    CountDownLatch start = new CountDownLatch(1);
    List<Future<?>> futures = new ArrayList<>();
    for (int t = 0; t < 8; t++) {
      futures.add(executor.submit(() -> {
        try {
          start.await();
          for (int i = 0; i < files * 5; i++) {
            int number = i % files;
            assertEquals(String.valueOf(number),
                JsonUtils.createObject(new FilePath(utils.getPath("concurrent" + number + ".json")))
                    .getString("n"));
          }
        } catch (Throwable e) {
          failures.add(e);
        }
      }));
    }
    start.countDown();
    for (Future<?> future : futures) {
      future.get(60, TimeUnit.SECONDS);
    }
    executor.shutdown();

    assertEquals("concurrent reads failed: " + failures, Collections.emptyList(), failures);
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
