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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * FakeTestReport class.
 *
 * @author Sung Gon Kim
 */
public class FakeTestReport implements FakeReport {

  private final FakeRecipe recipe;

  public FakeTestReport(FakeRecipe recipe) {
    this.recipe = recipe;
  }

  private void createPassedTestFile(File path) throws IOException {
    List<String> report = new ArrayList<>();
    report.add("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    report.add("<testsuites tests=\"1\" name=\"AllTests\">");
    report.add(String.format("<testsuite name=\"%s.Test\" tests=\"1\" >", recipe.getRecipe()));
    report.add(String.format("<testcase name=\"%s\" classname=\"%s.Test\" />",
        RandomStringUtils.randomAlphabetic(50), recipe.getRecipe()));
    report.add("</testsuite>");
    report.add("</testsuites>");
    FileUtils.writeLines(path, report);
  }

  private void createFailedTestFile(File path) throws IOException {
    List<String> report = new ArrayList<>();
    report.add("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    report.add("<testsuites tests=\"1\" name=\"AllTests\">");
    report.add(String.format("<testsuite name=\"%s.Test\" tests=\"1\" >", recipe.getRecipe()));
    report.add(String.format("<testcase name=\"%s\" classname=\"%s.Test\">",
        RandomStringUtils.randomAlphabetic(50), recipe.getRecipe()));
    report.add("<failure message=\"failure_message\">failure details</failure>");
    report.add("</testcase>");
    report.add("</testsuite>");
    report.add("</testsuites>");
    FileUtils.writeLines(path, report);
  }

  private void createErrorTestFile(File path) throws IOException {
    List<String> report = new ArrayList<>();
    report.add("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    report.add("<testsuites tests=\"1\" name=\"AllTests\">");
    report.add(String.format("<testsuite name=\"%s.Test\" tests=\"1\" >", recipe.getRecipe()));
    report.add(String.format("<testcase name=\"%s\" classname=\"%s.Test\">",
        RandomStringUtils.randomAlphabetic(50), recipe.getRecipe()));
    report.add("<error message=\"error_message\">error details</error>");
    report.add("</testcase>");
    report.add("</testsuite>");
    report.add("</testsuites>");
    FileUtils.writeLines(path, report);
  }

  private void createSkippedTestFile(File path) throws IOException {
    List<String> report = new ArrayList<>();
    report.add("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    report.add("<testsuites tests=\"1\" name=\"AllTests\">");
    report.add(String.format("<testsuite name=\"%s.Test\" tests=\"1\" >", recipe.getRecipe()));
    report.add(String.format("<testcase name=\"%s\" classname=\"%s.Test\">",
        RandomStringUtils.randomAlphabetic(50), recipe.getRecipe()));
    report.add("<skipped message=\"skipped_message\">skipped details</skipped>");
    report.add("</testcase>");
    report.add("</testsuite>");
    report.add("</testsuites>");
    FileUtils.writeLines(path, report);
  }

  private void createPassedTestFiles(FakeSource source, File report) throws IOException {
    String basename = RandomStringUtils.randomAlphabetic(30);
    for (long i = 0; i < source.getTestPassed(); i++) {
      File path = FileUtils.getFile(report, String.format("passed_%s_%d.xml", basename, i));
      createPassedTestFile(path);
    }
  }

  private void createFailedTestFiles(FakeSource source, File report) throws IOException {
    String basename = RandomStringUtils.randomAlphabetic(30);
    for (long i = 0; i < source.getTestFailed(); i++) {
      File path = FileUtils.getFile(report, String.format("failed_%s_%d.xml", basename, i));
      createFailedTestFile(path);
    }
  }

  private void createErrorTestFiles(FakeSource source, File report) throws IOException {
    String basename = RandomStringUtils.randomAlphabetic(30);
    for (long i = 0; i < source.getTestError(); i++) {
      File path = FileUtils.getFile(report, String.format("error_%s_%d.xml", basename, i));
      createErrorTestFile(path);
    }
  }

  private void createSkippedTestFiles(FakeSource source, File report) throws IOException {
    String basename = RandomStringUtils.randomAlphabetic(30);
    for (long i = 0; i < source.getTestSkipped(); i++) {
      File path = FileUtils.getFile(report, String.format("skipped_%s_%d.xml", basename, i));
      createSkippedTestFile(path);
    }
  }

  @Override
  public void toFile(File directory) throws IOException {
    File report = FileUtils.getFile(directory, recipe.getRecipe(), "test");
    FileUtils.forceMkdir(report);
    for (FakeSource source : recipe.getSources()) {
      createPassedTestFiles(source, report);
      createFailedTestFiles(source, report);
      createErrorTestFiles(source, report);
      createSkippedTestFiles(source, report);
    }
  }
}
