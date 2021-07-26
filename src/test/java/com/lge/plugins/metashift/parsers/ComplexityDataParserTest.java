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

import com.lge.plugins.metashift.models.ComplexityData;
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
 * Unit tests for the ComplexityDataParser class.
 *
 * @author Sung Gon Kim
 */
public class ComplexityDataParserTest {

  @Rule
  public final TemporaryFolder folder = new TemporaryFolder();
  private TemporaryFileUtils utils;
  private StringBuilder builder;
  private List<Data> objects;
  private File directory;
  private ComplexityDataParser parser;

  @Before
  public void setUp() throws IOException {
    utils = new TemporaryFileUtils(folder);
    builder = new StringBuilder();
    objects = Collections.synchronizedList(new ArrayList<>());
    directory = utils.createDirectory("A-A-A");
    parser = new ComplexityDataParser(new FilePath(directory), objects);
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
    assertEquals(available, of(objects, ComplexityDataParsed.class).findAny().isPresent());
    assertEquals(size, of(objects, ComplexityData.class).count());
  }

  private void assertValues(int index, String file, String function, long start, long end,
      long value) {
    List<ComplexityData> data = of(objects, ComplexityData.class).collect(Collectors.toList());
    assertEquals(file, data.get(index).getFile());
    assertEquals(function, data.get(index).getFunction());
    assertEquals(start, data.get(index).getStart());
    assertEquals(end, data.get(index).getEnd());
    assertEquals(value, data.get(index).getValue());
  }

  @Test
  public void testCreateWithUnknownPath() throws IOException, InterruptedException {
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
  public void testCreateWithMalformedData() throws IOException, InterruptedException {
    builder.append("{ {");
    utils.writeLines(builder, directory, "checkcode", "sage_report.json");
    parse();
  }

  @Test
  public void testCreateWithEmptyData() throws IOException, InterruptedException {
    builder.append("{ 'complexity': [ ] }");
    utils.writeLines(builder, directory, "checkcode", "sage_report.json");
    parse();
    assertDataList(true, 0);
  }

  @Test
  public void testCreateWithInsufficientData() throws IOException, InterruptedException {
    builder.append("{ 'complexity': [ { 'file': 'a.file' } ] }");
    utils.writeLines(builder, directory, "checkcode", "sage_report.json");
    parse();
    assertDataList(true, 1);
  }

  @Test
  public void testCreateWithHiddenFile() throws IOException, InterruptedException {
    builder
        .append("{")
        .append("  'complexity': [")
        .append("    {")
        .append("      'file': '.hidden.file',")
        .append("      'function': 'func1()',")
        .append("      'start': 5,")
        .append("      'end': 10,")
        .append("      'value': 1")
        .append("    }")
        .append("  ]")
        .append("}");
    utils.writeLines(builder, directory, "checkcode", "sage_report.json");
    parse();
    assertDataList(true, 0);
  }

  @Test
  public void testCreateWithSingleData() throws IOException, InterruptedException {
    builder
        .append("{")
        .append("  'complexity': [")
        .append("    {")
        .append("      'file': 'a.file',")
        .append("      'function': 'func1()',")
        .append("      'start': 5,")
        .append("      'end': 10,")
        .append("      'value': 1")
        .append("    }")
        .append("  ]")
        .append("}");
    utils.writeLines(builder, directory, "checkcode", "sage_report.json");
    parse();
    assertDataList(true, 1);
    assertValues(0, "a.file", "func1()", 5, 10, 1);
  }

  @Test
  public void testCreateWithOverlappedData() throws IOException, InterruptedException {
    builder
        .append("{")
        .append("  'complexity': [")
        .append("    {")
        .append("      'file': 'a.file',")
        .append("      'function': 'func1()',")
        .append("      'start': 5,")
        .append("      'end': 10,")
        .append("      'value': 1")
        .append("    },")
        .append("    {")
        .append("      'file': 'a.file',")
        .append("      'function': 'func1()',")
        .append("      'start': 15,")
        .append("      'end': 20,")
        .append("      'value': 3")
        .append("    },")
        .append("    {")
        .append("      'file': 'a.file',")
        .append("      'function': 'func1()',")
        .append("      'start': 25,")
        .append("      'end': 30,")
        .append("      'value': 7")
        .append("    }")
        .append("  ]")
        .append("}");
    utils.writeLines(builder, directory, "checkcode", "sage_report.json");
    parse();
    assertDataList(true, 3);
    assertValues(0, "a.file", "func1()", 5, 10, 1);
    assertValues(1, "a.file", "func1()", 15, 20, 3);
    assertValues(2, "a.file", "func1()", 25, 30, 7);
  }

  @Test
  public void testCreateWithMultipleData() throws IOException, InterruptedException {
    builder
        .append("{")
        .append("  'complexity': [")
        .append("    {")
        .append("      'file': 'a.file',")
        .append("      'function': 'func1()',")
        .append("      'start': 5,")
        .append("      'end': 10,")
        .append("      'value': 1")
        .append("    },")
        .append("    {")
        .append("      'file': 'b.file',")
        .append("      'function': 'func2()',")
        .append("      'start': 15,")
        .append("      'end': 20,")
        .append("      'value': 3")
        .append("    },")
        .append("    {")
        .append("      'file': 'c.file',")
        .append("      'function': 'func3()',")
        .append("      'start': 25,")
        .append("      'end': 30,")
        .append("      'value': 7")
        .append("    }")
        .append("  ]")
        .append("}");
    utils.writeLines(builder, directory, "checkcode", "sage_report.json");
    parse();
    assertDataList(true, 3);
    assertValues(0, "a.file", "func1()", 5, 10, 1);
    assertValues(1, "b.file", "func2()", 15, 20, 3);
    assertValues(2, "c.file", "func3()", 25, 30, 7);
  }
}
