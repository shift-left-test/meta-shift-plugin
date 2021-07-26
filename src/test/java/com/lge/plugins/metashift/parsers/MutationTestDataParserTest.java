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

import com.lge.plugins.metashift.models.Data;
import com.lge.plugins.metashift.models.MutationTestData;
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
 * Unit tests for the MutationTestDataParser class.
 *
 * @author Sung Gon Kim
 */
public class MutationTestDataParserTest {

  @Rule
  public final TemporaryFolder folder = new TemporaryFolder();
  private TemporaryFileUtils utils;
  private StringBuilder builder;
  private List<Data> objects;
  private File directory;
  private MutationTestDataParser parser;

  @Before
  public void setUp() throws IOException {
    utils = new TemporaryFileUtils(folder);
    builder = new StringBuilder();
    objects = Collections.synchronizedList(new ArrayList<>());
    directory = utils.createDirectory("A-A-A");
    parser = new MutationTestDataParser(new FilePath(directory), objects);
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
    assertEquals(available, of(objects, MutationTestDataParsed.class).findAny().isPresent());
    assertEquals(size, of(objects, MutationTestData.class).count());
  }

  private void assertValues(int index, String file, String mutatedClass, String mutatedMethod,
      long line, String mutator, String killingTest) {
    List<MutationTestData> data = of(objects, MutationTestData.class).collect(Collectors.toList());
    assertEquals(file, data.get(index).getFile());
    assertEquals(mutatedClass, data.get(index).getMutatedClass());
    assertEquals(mutatedMethod, data.get(index).getMutatedMethod());
    assertEquals(line, data.get(index).getLine());
    assertEquals(mutator, data.get(index).getMutator());
    assertEquals(killingTest, data.get(index).getKillingTest());
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
    utils.createDirectory(directory, "checktest");
    parse();
    assertDataList(false, 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithMalformedFile() throws IOException, InterruptedException {
    builder.append("<mutation>");
    utils.writeLines(builder, directory, "checktest", "mutations.xml");
    parse();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithInsufficientData() throws IOException, InterruptedException {
    builder
        .append("<mutations>")
        .append("  <mutation detected='true'>")
        .append("    <sourceFilePath>a.cpp</sourceFilePath>")
        .append("  </mutation>")
        .append("</mutations>");
    utils.writeLines(builder, directory, "checktest", "mutations.xml");
    parse();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithEmptyFile() throws IOException, InterruptedException {
    builder.append(" ");
    utils.writeLines(builder, directory, "checktest", "mutations.xml");
    parse();
  }

  @Test
  public void testCreateWithEmptyData() throws IOException, InterruptedException {
    builder.append("<mutations></mutations>");
    utils.writeLines(builder, directory, "checktest", "mutations.xml");
    parse();
    assertDataList(true, 0);
  }

  @Test
  public void testCreateWitHiddenFile() throws IOException, InterruptedException {
    builder
        .append("<mutations>")
        .append("  <mutation detected='true'>")
        .append("    <sourceFile>.hidden.file</sourceFile>")
        .append("    <sourceFilePath>path/to/.hidden.file</sourceFilePath>")
        .append("    <mutatedClass>A</mutatedClass>")
        .append("    <mutatedMethod>func1</mutatedMethod>")
        .append("    <lineNumber>1</lineNumber>")
        .append("    <mutator>AOR</mutator>")
        .append("    <killingTest>test1</killingTest>")
        .append("  </mutation>")
        .append("</mutations>");
    utils.writeLines(builder, directory, "checktest", "mutations.xml");
    parse();
    assertDataList(true, 0);
  }

  @Test
  public void testCreateWithSingleData() throws IOException, InterruptedException {
    builder
        .append("<mutations>")
        .append("  <mutation detected='true'>")
        .append("    <sourceFile>a.file</sourceFile>")
        .append("    <sourceFilePath>path/to/a.file</sourceFilePath>")
        .append("    <mutatedClass>A</mutatedClass>")
        .append("    <mutatedMethod>func1</mutatedMethod>")
        .append("    <lineNumber>1</lineNumber>")
        .append("    <mutator>AOR</mutator>")
        .append("    <killingTest>test1</killingTest>")
        .append("  </mutation>")
        .append("</mutations>");
    utils.writeLines(builder, directory, "checktest", "mutations.xml");
    parse();
    assertDataList(true, 1);
    assertValues(0, "path/to/a.file", "A", "func1", 1, "AOR", "test1");
  }

  @Test
  public void testCreateWithMultipleData() throws IOException, InterruptedException {
    builder
        .append("<mutations>")
        .append("  <mutation detected='true'>")
        .append("    <sourceFile>a.file</sourceFile>")
        .append("    <sourceFilePath>path/to/a.file</sourceFilePath>")
        .append("    <mutatedClass>A</mutatedClass>")
        .append("    <mutatedMethod>func1</mutatedMethod>")
        .append("    <lineNumber>1</lineNumber>")
        .append("    <mutator>AOR</mutator>")
        .append("    <killingTest>test1</killingTest>")
        .append("  </mutation>")
        .append("  <mutation detected='false'>")
        .append("    <sourceFile>b.file</sourceFile>")
        .append("    <sourceFilePath>path/to/b.file</sourceFilePath>")
        .append("    <mutatedClass>B</mutatedClass>")
        .append("    <mutatedMethod>func2</mutatedMethod>")
        .append("    <lineNumber>2</lineNumber>")
        .append("    <mutator>BOR</mutator>")
        .append("    <killingTest>test2</killingTest>")
        .append("  </mutation>")
        .append("  <mutation detected='skip'>")
        .append("    <sourceFile>c.file</sourceFile>")
        .append("    <sourceFilePath>path/to/c.file</sourceFilePath>")
        .append("    <mutatedClass>C</mutatedClass>")
        .append("    <mutatedMethod>func3</mutatedMethod>")
        .append("    <lineNumber>3</lineNumber>")
        .append("    <mutator>COR</mutator>")
        .append("    <killingTest>test3</killingTest>")
        .append("  </mutation>")
        .append("</mutations>");
    utils.writeLines(builder, directory, "checktest", "mutations.xml");
    parse();
    assertDataList(true, 3);
    assertValues(0, "path/to/a.file", "A", "func1", 1, "AOR", "test1");
    assertValues(1, "path/to/b.file", "B", "func2", 2, "BOR", "test2");
    assertValues(2, "path/to/c.file", "C", "func3", 3, "COR", "test3");
  }
}
