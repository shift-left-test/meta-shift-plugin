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
 * FakeMutationTest class.
 *
 * @author Sung Gon Kim
 */
public class FakeMutationTestReport implements FakeReport {

  private final FakeRecipe recipe;

  public FakeMutationTestReport(FakeRecipe recipe) {
    this.recipe = recipe;
  }

  private List<String> createKilledMutationTest(FakeSource source) {
    List<String> lines = new ArrayList<>();
    lines.add("<mutation detected=\"true\">");
    lines.add(String.format("<sourceFile>%s</sourceFile>", source.getFilename()));
    lines.add(String.format("<sourceFilePath>%s</sourceFilePath>",
        source.getFile().getAbsolutePath()));
    lines.add(String.format("<mutatedClass>%s</mutatedClass>", FakeRandom.nextString()));
    lines.add(String.format("<mutatedMethod>%s</mutatedMethod>", FakeRandom.nextString()));
    lines.add(String.format("<lineNumber>%s</lineNumber>", FakeRandom.nextNumber()));
    lines.add(String.format("<mutator>%s</mutator>", FakeRandom.nextString()));
    lines.add(String.format("<killingTest>%s</killingTest>", FakeRandom.nextString()));
    lines.add("</mutation>");
    return lines;
  }

  private List<String> createKilledMutationTests(FakeSource source) {
    List<String> lines = new ArrayList<>();
    for (long i = 0; i < source.getMutationTestKilled(); i++) {
      lines.addAll(createKilledMutationTest(source));
    }
    return lines;
  }

  private List<String> createSurvivedMutationTest(FakeSource source) {
    List<String> lines = new ArrayList<>();
    lines.add("<mutation detected=\"false\">");
    lines.add(String.format("<sourceFile>%s</sourceFile>", source.getFilename()));
    lines.add(String.format("<sourceFilePath>%s</sourceFilePath>",
        source.getFile().getAbsolutePath()));
    lines.add(String.format("<mutatedClass>%s</mutatedClass>", FakeRandom.nextString()));
    lines.add(String.format("<mutatedMethod>%s</mutatedMethod>", FakeRandom.nextString()));
    lines.add(String.format("<lineNumber>%s</lineNumber>", FakeRandom.nextNumber()));
    lines.add(String.format("<mutator>%s</mutator>", FakeRandom.nextString()));
    lines.add("<killingTest></killingTest>");
    lines.add("</mutation>");
    return lines;
  }

  private List<String> createSurvivedMutationTests(FakeSource source) {
    List<String> lines = new ArrayList<>();
    for (long i = 0; i < source.getMutationTestSurvived(); i++) {
      lines.addAll(createSurvivedMutationTest(source));
    }
    return lines;
  }

  private List<String> createSkippedMutationTest(FakeSource source) {
    List<String> lines = new ArrayList<>();
    lines.add("<mutation detected=\"skip\">");
    lines.add(String.format("<sourceFile>%s</sourceFile>", source.getFilename()));
    lines.add(String.format("<sourceFilePath>%s</sourceFilePath>",
        source.getFile().getAbsolutePath()));
    lines.add(String.format("<mutatedClass>%s</mutatedClass>", FakeRandom.nextString()));
    lines.add(String.format("<mutatedMethod>%s</mutatedMethod>", FakeRandom.nextString()));
    lines.add(String.format("<lineNumber>%s</lineNumber>", FakeRandom.nextNumber()));
    lines.add(String.format("<mutator>%s</mutator>", FakeRandom.nextString()));
    lines.add("<killingTest></killingTest>");
    lines.add("</mutation>");
    return lines;
  }

  private List<String> createSkippedMutationTests(FakeSource source) {
    List<String> lines = new ArrayList<>();
    for (long i = 0; i < source.getMutationTestSkipped(); i++) {
      lines.addAll(createSkippedMutationTest(source));
    }
    return lines;
  }

  @Override
  public void toFile(File directory) throws IOException {
    File file = FileUtils.getFile(directory, recipe.getName(), "checktest", "mutations.xml");
    FileUtils.forceMkdirParent(file);
    List<String> lines = new ArrayList<>();
    lines.add("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    lines.add("<mutations>");
    for (FakeSource source : recipe.getSources()) {
      lines.addAll(createKilledMutationTests(source));
      lines.addAll(createSurvivedMutationTests(source));
      lines.addAll(createSkippedMutationTests(source));
    }
    lines.add("</mutations>");
    FileUtils.writeLines(file, lines);
  }
}
