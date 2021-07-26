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

import com.lge.plugins.metashift.models.CommentData;
import com.lge.plugins.metashift.models.Data;
import com.lge.plugins.metashift.utils.TemporaryFileUtils;
import hudson.FilePath;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Unit tests for the CommentDataParser class.
 *
 * @author Sung Gon Kim
 */
public class CommentDataParserTest {

  @Rule
  public final TemporaryFolder folder = new TemporaryFolder();
  private TemporaryFileUtils utils;
  private StringBuilder builder;
  private List<Data> objects;
  private File directory;
  private CommentDataParser parser;

  @Before
  public void setUp() throws IOException {
    utils = new TemporaryFileUtils(folder);
    builder = new StringBuilder();
    objects = Collections.synchronizedList(new ArrayList<>());
    directory = utils.createDirectory("A-A-A");
    parser = new CommentDataParser(new FilePath(directory), objects);
  }


  private void parse() throws IOException, InterruptedException {
    ExecutorService executor = Executors.newSingleThreadExecutor();
    try {
      executor.submit(parser).get();
    } catch (ExecutionException e) {
      Throwable cause = e.getCause();
      if (cause instanceof IllegalArgumentException) {
        throw (IllegalArgumentException) cause;
      }
      if (cause instanceof InterruptedException) {
        throw (InterruptedException) cause;
      }
      if (cause instanceof IOException) {
        throw (IOException) cause;
      }
      throw new RuntimeException("Unknown exception: " + cause.getMessage(), cause);
    }
  }

  @SuppressWarnings({"unchecked", "PMD.UnnecessaryModifier"})
  private static <T> Stream<T> of(List<Data> objects, Class<T> clazz) {
    return (Stream<T>) objects.stream().filter(o -> clazz.isAssignableFrom(o.getClass()));
  }

  private void assertDataList(boolean available, int size) {
    assertEquals(available, of(objects, CommentDataParsed.class).findAny().isPresent());
    assertEquals(size, of(objects, CommentData.class).count());
  }

  private void assertValues(int index, String file, long lines, long commentLines) {
    List<CommentData> data = of(objects, CommentData.class).collect(Collectors.toList());
    assertEquals(file, data.get(index).getFile());
    assertEquals(lines, data.get(index).getLines());
    assertEquals(commentLines, data.get(index).getCommentLines());
  }

  @Test
  public void testCreateSetWithUnknownPath() throws IOException, InterruptedException {
    directory = utils.getPath("path-to-unknown");
    parse();
    assertDataList(false, 0);
  }

  @Test
  public void testCreateWithNoTaskDirectory() throws IOException, InterruptedException {
    parse();
    assertDataList(false, 0);
  }

  @Test
  public void testCreateWithNoFile() throws IOException, InterruptedException {
    utils.createDirectory(directory, "checkcode");
    parse();
    assertDataList(false, 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithMalformedData() throws Exception {
    builder.append("{ {");
    utils.writeLines(builder, directory, "checkcode", "sage_report.json");
    parse();
  }

  @Test
  public void testCreateWithEmptyData() throws Exception {
    builder.append("{ 'size': [ ] }");
    utils.writeLines(builder, directory, "checkcode", "sage_report.json");
    parse();
    assertDataList(true, 0);
  }

  @Test
  public void testCreateWithInsufficientData() throws Exception {
    builder
        .append("{")
        .append("  'size': [")
        .append("    { 'file': 'a.file' }")
        .append("  ]")
        .append("}");
    utils.writeLines(builder, directory, "checkcode", "sage_report.json");
    parse();
    assertDataList(true, 1);
  }

  @Test
  public void testCreateWithHiddenFile() throws IOException, InterruptedException {
    builder
        .append("{")
        .append("  'size': [")
        .append("    {")
        .append("      'file': '.hidden.file',")
        .append("      'total_lines': 20,")
        .append("      'code_lines': 1,")
        .append("      'comment_lines': 5,")
        .append("      'duplicated_lines': 2,")
        .append("      'functions': 15,")
        .append("      'classes': 6")
        .append("    }")
        .append("  ]")
        .append("}");
    utils.writeLines(builder, directory, "checkcode", "sage_report.json");
    parse();
    assertDataList(true, 0);
  }

  @Test
  public void testCreateWithSingleData() throws Exception {
    builder
        .append("{")
        .append("  'size': [")
        .append("    {")
        .append("      'file': 'a.file',")
        .append("      'total_lines': 20,")
        .append("      'code_lines': 1,")
        .append("      'comment_lines': 5,")
        .append("      'duplicated_lines': 2,")
        .append("      'functions': 15,")
        .append("      'classes': 6")
        .append("    }")
        .append("  ]")
        .append("}");
    utils.writeLines(builder, directory, "checkcode", "sage_report.json");
    parse();
    assertValues(0, "a.file", 20, 5);
  }

  @Test
  public void testCreateWithMultipleData() throws Exception {
    builder
        .append("{")
        .append("  'size': [")
        .append("    {")
        .append("      'file': 'a.file',")
        .append("      'total_lines': 10,")
        .append("      'comment_lines': 5")
        .append("    },")
        .append("    {")
        .append("      'file': 'b.file',")
        .append("      'total_lines': 20,")
        .append("      'comment_lines': 3")
        .append("    }")
        .append("  ]")
        .append("}");
    utils.writeLines(builder, directory, "checkcode", "sage_report.json");
    parse();
    assertValues(0, "a.file", 10, 5);
    assertValues(1, "b.file", 20, 3);
  }
}
