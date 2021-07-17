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

/**
 * FakeCoverageReport class.
 *
 * @author Sung Gon Kim
 */
public class FakeCoverageReport implements FakeReport {

  private final FakeRecipe recipe;

  public FakeCoverageReport(FakeRecipe recipe) {
    this.recipe = recipe;
  }

  private List<String> createMethodTags(FakeSource source) {
    List<String> lines = new ArrayList<>();
    lines.add("<methods>");
    lines.add(String.format("<method name=\"%s\" line-rate=\"1.0\" branch-rate=\"1.0\">",
        FakeRandom.nextString()));
    lines.add("<lines>");
    lines.add("<line hits=\"1\" number=\"0\" branch=\"false\"/>");
    lines.add("</lines>");
    lines.add("</method>");
    lines.add("</methods>");
    return lines;
  }

  private List<String> createBranchCoverageCovered(FakeSource source, long index) {
    List<String> lines = new ArrayList<>();
    if (source.getBranchCoverageCovered() == 0) {
      return lines;
    }
    long branch_number = 0;
    lines.add(String.format("<line branch=\"true\" hits=\"1\" number=\"%d\">", index));
    lines.add("<conds>");
    for (long i = 0; i < source.getBranchCoverageCovered(); i++) {
      lines.add(String.format("<cond block_number=\"0\" branch_number=\"%d\" hit=\"1\"/>",
          branch_number++));
    }
    lines.add("</conds>");
    lines.add("</line>");
    return lines;
  }

  private List<String> createBranchCoverageMissed(FakeSource source, long index) {
    List<String> lines = new ArrayList<>();
    if (source.getBranchCoverageCovered() == 0) {
      return lines;
    }
    long branch_number = 0;
    lines.add(String.format("<line branch=\"true\" hits=\"0\" number=\"%d\">", index));
    lines.add("<conds>");
    for (long i = 0; i < source.getBranchCoverageMissed(); i++) {
      lines.add(String.format("<cond block_number=\"0\" branch_number=\"%d\" hit=\"0\"/>",
          branch_number++));
    }
    lines.add("</conds>");
    lines.add("</line>");
    return lines;
  }

  private List<String> createLineTags(FakeSource source) {
    long index = 0;
    List<String> lines = new ArrayList<>();
    lines.add("<lines>");
    for (long i = 0; i < source.getStatementCoverageCovered(); i++) {
      lines.add(String.format("<line branch=\"false\" hits=\"1\" number=\"%d\"/>", ++index));
    }
    for (long i = 0; i < source.getStatementCoverageMissed(); i++) {
      lines.add(String.format("<line branch=\"false\" hits=\"0\" number=\"%d\"/>", ++index));
    }
    lines.addAll(createBranchCoverageCovered(source, ++index));
    lines.addAll(createBranchCoverageMissed(source, ++index));
    lines.add("</lines>");
    return lines;
  }

  private List<String> createClassTag(FakeSource source) {
    List<String> lines = new ArrayList<>();
    lines.add(String.format("<class filename=\"%s\" >", source.getFile().getAbsolutePath()));
    lines.addAll(createMethodTags(source));
    lines.addAll(createLineTags(source));
    lines.add("</class>");
    return lines;
  }

  @Override
  public void toFile(File directory) throws IOException {
    File file = FileUtils.getFile(directory, recipe.getRecipe(), "coverage", "coverage.xml");
    FileUtils.forceMkdirParent(file);
    List<String> lines = new ArrayList<>();
    lines.add("<?xml version=\"1.0\" ?>");
    lines.add("<!DOCTYPE coverage SYSTEM 'http://cobertura.sourceforge.net/xml/coverage-04.dtd'>");
    lines.add("<coverage>");
    lines.add("<sources><source>.</source></sources>");
    lines.add("<packages>");
    lines.add("<package>");
    lines.add("<classes>");
    for (FakeSource source : recipe.getSources()) {
      lines.addAll(createClassTag(source));
    }
    lines.add("</classes>");
    lines.add("</package>");
    lines.add("</packages>");
    lines.add("</coverage>");
    FileUtils.writeLines(file, lines);
  }
}
