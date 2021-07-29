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

package com.lge.plugins.metashift.models;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Test;

/**
 * Unit tests for the RecipeEvaluationSummary class.
 *
 * @author Sung Gon Kim
 */
public class RecipeEvaluationSummaryTest {

  private void assertLinesOfCode(LinesOfCode o, long lines, long functions, long classes,
      long files, long recipes) {
    assertEquals(lines, o.getLines());
    assertEquals(functions, o.getLines());
    assertEquals(classes, o.getFunctions());
    assertEquals(files, o.getFiles());
    assertEquals(recipes, o.getRecipes());
  }

  private void assertEvaluation(Evaluation o, boolean available, boolean qualified, double ratio,
      double threshold) {
    assertEquals(available, o.isAvailable());
    assertEquals(qualified, o.isQualified());
    assertEquals(ratio, o.getRatio(), 0.01);
    assertEquals(threshold, o.getThreshold(), 0.01);
  }

  private void assertStatistics(Statistics o, double min, double average, double max) {
    assertEquals(min, o.getMin(), 0.01);
    assertEquals(average, o.getAverage(), 0.01);
    assertEquals(max, o.getMax(), 0.01);
  }

  private void assertDistribution(Distribution o, long total) {
    assertEquals(total, o.getTotal());
  }

  @Test
  public void testInitialState() {
    RecipeEvaluationSummary summary = new RecipeEvaluationSummary();
    assertLinesOfCode(summary.getLinesOfCode(), 0, 0, 0, 0, 0);
    assertEvaluation(summary.getPremirrorCache().getEvaluation(), false, false, 0.0, 0.0);
    assertStatistics(summary.getPremirrorCache().getStatistics(), 0.0, 0.0, 0.0);
    assertDistribution(summary.getPremirrorCache().getDistribution(), 0);
  }

  @Test
  public void testCreateObject() {
    LinesOfCode linesOfCode = new LinesOfCode(1, 1, 1, 1, 1);
    List<String> integers = Arrays.asList("1", "12", "123");
    MetricDataSummary o = new MetricDataSummary(
        new PositiveEvaluation(true, 1, 1, 0.5),
        new Statistics(integers.stream().collect(Collectors.summarizingDouble(String::length)),
            0.5),
        new Distribution(1, 1)
    );
    RecipeEvaluationSummary summary =
        new RecipeEvaluationSummary(linesOfCode, o, o, o, o, o, o, o, o, o, o, o);
    assertLinesOfCode(summary.getLinesOfCode(), 1, 1, 1, 1, 1);
    assertEvaluation(summary.getPremirrorCache().getEvaluation(), true, true, 1.0, 0.5);
    assertStatistics(summary.getPremirrorCache().getStatistics(), 1.0, 2.0, 3.0);
    assertDistribution(summary.getPremirrorCache().getDistribution(), 2);
  }
}
