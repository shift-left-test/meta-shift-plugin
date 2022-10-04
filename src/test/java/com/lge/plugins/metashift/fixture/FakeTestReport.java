/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.fixture;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;

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
    report.add(String.format("<testsuite name=\"%s.Test\" tests=\"1\" >", recipe.getName()));
    report.add(String.format("<testcase name=\"%s\" classname=\"%s.Test\" />",
        FakeRandom.nextString(), recipe.getName()));
    report.add("</testsuite>");
    report.add("</testsuites>");
    FileUtils.writeLines(path, report);
  }

  private void createFailedTestFile(File path) throws IOException {
    List<String> report = new ArrayList<>();
    report.add("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    report.add("<testsuites tests=\"1\" name=\"AllTests\">");
    report.add(String.format("<testsuite name=\"%s.Test\" tests=\"1\" >", recipe.getName()));
    report.add(String.format("<testcase name=\"%s\" classname=\"%s.Test\">",
        FakeRandom.nextString(), recipe.getName()));
    report.add("<failure message=\"failure_message\"><![CDATA[failure details]]></failure>");
    report.add("</testcase>");
    report.add("</testsuite>");
    report.add("</testsuites>");
    FileUtils.writeLines(path, report);
  }

  private void createErrorTestFile(File path) throws IOException {
    List<String> report = new ArrayList<>();
    report.add("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    report.add("<testsuites tests=\"1\" name=\"AllTests\">");
    report.add(String.format("<testsuite name=\"%s.Test\" tests=\"1\" >", recipe.getName()));
    report.add(String.format("<testcase name=\"%s\" classname=\"%s.Test\">",
        FakeRandom.nextString(), recipe.getName()));
    report.add("<error message=\"error_message\"/>");
    report.add("</testcase>");
    report.add("</testsuite>");
    report.add("</testsuites>");
    FileUtils.writeLines(path, report);
  }

  private void createSkippedTestFile(File path) throws IOException {
    List<String> report = new ArrayList<>();
    report.add("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    report.add("<testsuites tests=\"1\" name=\"AllTests\">");
    report.add(String.format("<testsuite name=\"%s.Test\" tests=\"1\" >", recipe.getName()));
    report.add(String.format("<testcase name=\"%s\" classname=\"%s.Test\">",
        FakeRandom.nextString(), recipe.getName()));
    report.add("<skipped><![CDATA[skipped details]]></skipped>");
    report.add("</testcase>");
    report.add("</testsuite>");
    report.add("</testsuites>");
    FileUtils.writeLines(path, report);
  }

  private void createPassedTestFiles(FakeSource source, File report) throws IOException {
    String basename = FakeRandom.nextString();
    for (long i = 0; i < source.getTestPassed(); i++) {
      File path = FileUtils.getFile(report, String.format("passed_%s_%d.xml", basename, i));
      createPassedTestFile(path);
    }
  }

  private void createFailedTestFiles(FakeSource source, File report) throws IOException {
    String basename = FakeRandom.nextString();
    for (long i = 0; i < source.getTestFailed(); i++) {
      File path = FileUtils.getFile(report, String.format("failed_%s_%d.xml", basename, i));
      createFailedTestFile(path);
    }
  }

  private void createErrorTestFiles(FakeSource source, File report) throws IOException {
    String basename = FakeRandom.nextString();
    for (long i = 0; i < source.getTestError(); i++) {
      File path = FileUtils.getFile(report, String.format("error_%s_%d.xml", basename, i));
      createErrorTestFile(path);
    }
  }

  private void createSkippedTestFiles(FakeSource source, File report) throws IOException {
    String basename = FakeRandom.nextString();
    for (long i = 0; i < source.getTestSkipped(); i++) {
      File path = FileUtils.getFile(report, String.format("skipped_%s_%d.xml", basename, i));
      createSkippedTestFile(path);
    }
  }

  @Override
  public void toFile(File directory) throws IOException {
    File report = FileUtils.getFile(directory, recipe.getName(), "test");
    FileUtils.forceMkdir(report);
    for (FakeSource source : recipe.getSources()) {
      createPassedTestFiles(source, report);
      createFailedTestFiles(source, report);
      createErrorTestFiles(source, report);
      createSkippedTestFiles(source, report);
    }
  }
}
