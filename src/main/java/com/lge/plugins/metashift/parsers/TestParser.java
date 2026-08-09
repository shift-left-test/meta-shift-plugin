/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.parsers;

import com.lge.plugins.metashift.models.DataList;
import com.lge.plugins.metashift.models.ErrorTestData;
import com.lge.plugins.metashift.models.FailedTestData;
import com.lge.plugins.metashift.models.PassedTestData;
import com.lge.plugins.metashift.models.SkippedTestData;
import com.lge.plugins.metashift.models.TestData;
import com.lge.plugins.metashift.utils.xml.SimpleXmlParser;
import com.lge.plugins.metashift.utils.xml.Tag;
import hudson.FilePath;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.io.output.NullPrintStream;
import org.xml.sax.SAXException;

/**
 * A parsers class for the TestData objects.
 *
 * @author Sung Gon Kim
 */
public class TestParser extends Parser {

  private final FilePath path;
  private final DataList dataList;
  private final PrintStream logger;

  /**
   * Default constructor.
   *
   * @param path     to the report directory
   * @param dataList to store objects
   * @param logger   for logging
   */
  public TestParser(FilePath path, DataList dataList, PrintStream logger) {
    this.path = path;
    this.dataList = dataList;
    this.logger = logger;
  }

  /**
   * Default constructor.
   *
   * @param path     to the report directory
   * @param dataList to store objects
   */
  public TestParser(FilePath path, DataList dataList) {
    this(path, dataList, NullPrintStream.INSTANCE);
  }

  @Override
  public void parse() throws IOException, InterruptedException {
    List<TestData> objects = new ArrayList<>();
    try {
      FilePath[] files = path.list("test/**/*.xml");
      if (files.length == 0) {
        return;
      }
      String sourceRoot = readSourceRoot(path.child("test"));
      boolean parsedAny = false;
      for (FilePath file : files) {
        try {
          objects.addAll(parseFile(path.getName(), sourceRoot, file));
          parsedAny = true;
        } catch (ParserConfigurationException | SAXException e) {
          // One unreadable report file is not a reason to discard the recipes that did parse,
          // nor the other report files of this recipe.
          logger.printf("[meta-shift-plugin] -> skipping malformed unit test report %s: %s%n",
              file, e.getMessage());
        }
      }
      dataList.addAll(objects);
      if (parsedAny) {
        // Registering the type marker is what makes the metric available; when nothing could be
        // read the metric has no result, rather than a result of zero.
        dataList.add(TestData.class);
      }
    } catch (NoSuchFileException e) {
      logger.printf("[meta-shift-plugin] -> unit test report not found: %s%n", e.getFile());
    } catch (IOException ignored) {
      // ignored
    }
  }

  /**
   * Parses the report file to create list of test data.
   *
   * @param recipe     name
   * @param sourceRoot source root path, nullable
   * @param file       report file
   * @return a list of test data objects
   * @throws ParserConfigurationException if failed to parse the xml files
   * @throws IOException                  if failed to parse the xml files
   * @throws SAXException                 if failed to parse the xml files
   * @throws InterruptedException         if an interruption occurs
   */
  private Collection<? extends TestData> parseFile(final String recipe, final String sourceRoot,
      final FilePath file)
      throws ParserConfigurationException, IOException, SAXException, InterruptedException {
    List<TestData> list = new ArrayList<>();
    SimpleXmlParser parser = new SimpleXmlParser(file);
    for (Tag testsuite : parser.getChildNodes("testsuite")) {
      String suite = testsuite.getAttribute("name");
      for (Tag testcase : testsuite.getChildNodes("testcase")) {
        list.add(createInstance(recipe, suite, sourceRoot, testcase));
      }
    }
    return list;
  }

  /**
   * Creates a test data instance using the given tag.
   *
   * @param recipe     name
   * @param suite      name
   * @param sourceRoot source root path, nullable
   * @param testcase   object
   * @return a test data object
   */
  private TestData createInstance(final String recipe, final String suite,
      final String sourceRoot, final Tag testcase) throws SAXException {
    String name = testcase.getAttribute("name");
    String file = relativize(sourceRoot, testcase.getAttribute("file", ""));
    long line = parseLong(testcase.getAttribute("line", "0"));
    double duration = parseDouble(testcase.getAttribute("time", "0"));
    for (Tag tag : testcase.getChildNodes()) {
      String status = tag.getTagName().toLowerCase();
      switch (status) {
        case "failure":
          return withDuration(
              new FailedTestData(recipe, suite, name, message(tag), file, line), duration);
        case "error":
          return withDuration(
              new ErrorTestData(recipe, suite, name, message(tag), file, line), duration);
        case "skipped":
          return withDuration(
              new SkippedTestData(recipe, suite, name, message(tag), file, line), duration);
        default:
          // Anything else does not carry the test status: the standard JUnit children
          // (system-out, system-err, properties) as well as vendor extensions such as
          // Surefire's rerunFailure/flakyFailure or assertions. A report format the plugin
          // does not fully model is not a reason to discard the build's data.
          continue;
      }
    }
    return withDuration(new PassedTestData(recipe, suite, name, "", file, line), duration);
  }

  private static <T extends TestData> T withDuration(final T data, final double duration) {
    data.setDuration(duration);
    return data;
  }

  private static long parseLong(final String value) {
    try {
      return Long.parseLong(value);
    } catch (NumberFormatException ignored) {
      return 0;
    }
  }

  private static double parseDouble(final String value) {
    try {
      return Double.parseDouble(value);
    } catch (NumberFormatException ignored) {
      return 0;
    }
  }

  private static final String CDATA_PREFIX = "<![CDATA[";
  private static final String CDATA_SUFFIX = "]]>";

  private static String message(final Tag tag) {
    String message = tag.getAttribute("message", tag.getTextContent());
    if (message.startsWith(CDATA_PREFIX)) {
      message = message.substring(CDATA_PREFIX.length());
    }
    if (message.endsWith(CDATA_SUFFIX)) {
      message = message.substring(0, message.length() - CDATA_SUFFIX.length());
    }
    return message;
  }
}
