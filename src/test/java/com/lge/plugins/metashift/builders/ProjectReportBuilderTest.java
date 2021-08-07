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

package com.lge.plugins.metashift.builders;

import static org.junit.Assert.assertEquals;

import com.lge.plugins.metashift.fixture.FakeRecipe;
import com.lge.plugins.metashift.fixture.FakeReportBuilder;
import com.lge.plugins.metashift.fixture.FakeScript;
import com.lge.plugins.metashift.fixture.FakeSource;
import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.Distribution;
import com.lge.plugins.metashift.models.LinesOfCode;
import com.lge.plugins.metashift.models.Recipes;
import com.lge.plugins.metashift.parsers.FileParser;
import com.lge.plugins.metashift.persistence.DataSource;
import com.lge.plugins.metashift.utils.ConfigurationUtils;
import hudson.FilePath;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Unit tests for the ProjectReportBuilder class.
 *
 * @author Sung Gon Kim
 */
public class ProjectReportBuilderTest {

  private static final String RECIPE1 = "A-A-A";
  private static final String RECIPE2 = "B-B-B";

  @Rule
  public final TemporaryFolder folder = new TemporaryFolder();
  private ProjectReport report;

  @Before
  public void setUp() throws IOException, InterruptedException {
    File source = new File(folder.newFolder(), "source");
    File report = new File(folder.newFolder(), "report");
    createReports(source, report);
    Recipes recipes = new FileParser().parse(new FilePath(report));
    Configuration configuration = ConfigurationUtils.of(50, 5, false);
    DataSource dataSource = new DataSource(new FilePath(folder.newFolder()));
    this.report = new ProjectReportBuilder(configuration, dataSource).parse(recipes);
  }

  private void createReports(File source, File report) throws IOException {
    FakeReportBuilder fakeReportBuilder = new FakeReportBuilder();
    fakeReportBuilder.add(new FakeRecipe(source, RECIPE1)
        .setPremirror(1, 1)
        .setSharedState(1, 1)
        .add(new FakeScript(1).setIssues(1, 1, 1))
        .add(new FakeSource(1, 1, 0, 0)
            .setCodeViolations(1, 1, 1)
            .setComplexity(10, 1, 0)
            .setTests(1, 1, 1, 1)
            .setStatementCoverage(1, 1)
            .setBranchCoverage(1, 1)
            .setMutationTests(1, 1, 1)));
    fakeReportBuilder.add(new FakeRecipe(source, RECIPE2)
        .setPremirror(0, 2)
        .setSharedState(0, 2)
        .add(new FakeScript(20).setIssues(2, 2, 2))
        .add(new FakeSource(20, 10, 10, 10)
            .setCodeViolations(2, 2, 2)
            .setComplexity(10, 10, 10)
            .setTests(2, 0, 0, 0)
            .setStatementCoverage(0, 2)
            .setBranchCoverage(0, 2)
            .setMutationTests(2, 0, 0)));
    fakeReportBuilder.toFile(report);
  }

  private JSONObject newEvaluation(boolean available, long denominator, long numerator,
      boolean qualified, double threshold, long tolerance, String type) {
    JSONObject o = new JSONObject();
    o.put("available", available);
    o.put("denominator", denominator);
    o.put("numerator", numerator);
    o.put("ratio", denominator == 0 ? 0.0 : (double) numerator / (double) denominator);
    o.put("qualified", qualified);
    o.put("threshold", threshold);
    o.put("tolerance", tolerance);
    o.put("type", type);
    return o;
  }

  private JSONObject newTreemapData(String name, long linesOfCode, double value, int grade) {
    JSONObject o = new JSONObject();
    o.put("name", name);
    o.put("linesOfCode", linesOfCode);
    o.put("value", value);
    o.put("grade", grade);
    return o;
  }

  private JSONObject newStatistics(double min, double average, double max) {
    JSONObject o = new JSONObject();
    o.put("min", min);
    o.put("average", average);
    o.put("max", max);
    return o;
  }

  private JSONObject newDistribution(long first, long second, long third, long fourth) {
    return JSONObject.fromObject(new Distribution(first, second, third, fourth));
  }

  private List<JSONObject> toList(JSONArray array) {
    return array.stream().map(o -> (JSONObject) o).collect(Collectors.toList());
  }

  @Test
  public void testGetLinesOfCode() {
    LinesOfCode linesOfCode = new LinesOfCode(21, 0, 0, 2, 2);
    assertEquals(JSONObject.fromObject(linesOfCode), report.getLinesOfCode());
  }

  @Test
  public void testGetTestedRecipes() {
    assertEquals(newEvaluation(false, 2, 2, false, 0.0, 0, "POSITIVE"), report.getTestedRecipes());
  }

  @Test
  public void testGetTreemap() {
    JSONArray expected = new JSONArray();
    expected.add(newTreemapData(RECIPE1, 1, 0.45454545454545453, 2));
    expected.add(newTreemapData(RECIPE2, 20, 0.6363636363636364, 0));
    assertEquals(expected, report.getTreemap());
  }

  @Test
  public void testGetSummaries() {
    assertEquals(2, report.getSummaries().size());
  }

  @Test
  public void testGetPremirrorCache() {
    ProjectGroup group = report.getPremirrorCache();
    assertEquals(newEvaluation(true, 4, 1, false, 0.5, 0, "POSITIVE"), group.getEvaluation());
    assertEquals(newStatistics(0, 0.25, 0.5), group.getStatistics());
    assertEquals(newDistribution(1, 3, 0, 0), group.getDistribution());
    JSONArray expected = new JSONArray();
    expected.add(newTreemapData(RECIPE1, 1, 0.5, 0));
    expected.add(newTreemapData(RECIPE2, 20, 0.0, 6));
    assertEquals(expected, group.getTreemap());
    List<JSONObject> summaries = toList(group.getSummaries());
    assertEquals(0.5, summaries.get(0).getDouble("ratio"), 0.01);
    assertEquals(0.0, summaries.get(1).getDouble("ratio"), 0.01);
  }

  @Test
  public void testGetSharedStateCache() {
    ProjectGroup group = report.getSharedStateCache();
    assertEquals(newEvaluation(true, 4, 1, false, 0.5, 0, "POSITIVE"), group.getEvaluation());
    assertEquals(newStatistics(0.0, 0.25, 0.5), group.getStatistics());
    assertEquals(newDistribution(1, 3, 0, 0), group.getDistribution());
    JSONArray expected = new JSONArray();
    expected.add(newTreemapData(RECIPE1, 1, 0.5, 0));
    expected.add(newTreemapData(RECIPE2, 20, 0.0, 6));
    assertEquals(expected, group.getTreemap());
    List<JSONObject> summaries = toList(group.getSummaries());
    assertEquals(0.5, summaries.get(0).getDouble("ratio"), 0.01);
    assertEquals(0.0, summaries.get(1).getDouble("ratio"), 0.01);
  }

  @Test
  public void testGetRecipeViolations() {
    ProjectGroup group = report.getRecipeViolations();
    assertEquals(newEvaluation(true, 21, 9, true, 0.5, 0, "NEGATIVE"), group.getEvaluation());
    assertEquals(newStatistics(0.3, 1.65, 3.0), group.getStatistics());
    assertEquals(newDistribution(3, 3, 3, 0), group.getDistribution());
    JSONArray expected = new JSONArray();
    expected.add(newTreemapData(RECIPE1, 1, 3.0, 6));
    expected.add(newTreemapData(RECIPE2, 20, 0.3, 0));
    assertEquals(expected, group.getTreemap());
    List<JSONObject> summaries = toList(group.getSummaries());
    assertEquals(3.0, summaries.get(0).getDouble("ratio"), 0.01);
    assertEquals(0.3, summaries.get(1).getDouble("ratio"), 0.01);
  }

  @Test
  public void testGetComments() {
    ProjectGroup group = report.getComments();
    assertEquals(newEvaluation(true, 21, 10, false, 0.5, 0, "POSITIVE"), group.getEvaluation());
    assertEquals(newStatistics(0.0, 0.25, 0.5), group.getStatistics());
    assertEquals(newDistribution(10, 11, 0, 0), group.getDistribution());
    JSONArray expected = new JSONArray();
    expected.add(newTreemapData(RECIPE1, 1, 0.0, 6));
    expected.add(newTreemapData(RECIPE2, 20, 0.5, 0));
    assertEquals(expected, group.getTreemap());
    List<JSONObject> summaries = toList(group.getSummaries());
    assertEquals(0.0, summaries.get(0).getDouble("ratio"), 0.01);
    assertEquals(0.5, summaries.get(1).getDouble("ratio"), 0.01);
  }

  @Test
  public void testGetCodeViolations() {
    ProjectGroup group = report.getCodeViolations();
    assertEquals(newEvaluation(true, 21, 9, true, 0.5, 0, "NEGATIVE"), group.getEvaluation());
    assertEquals(newStatistics(0.3, 1.65, 3.0), group.getStatistics());
    assertEquals(newDistribution(3, 3, 3, 0), group.getDistribution());
    JSONArray expected = new JSONArray();
    expected.add(newTreemapData(RECIPE1, 1, 3.0, 6));
    expected.add(newTreemapData(RECIPE2, 20, 0.3, 0));
    assertEquals(expected, group.getTreemap());
    List<JSONObject> summaries = toList(group.getSummaries());
    assertEquals(3.0, summaries.get(0).getDouble("ratio"), 0.01);
    assertEquals(0.3, summaries.get(1).getDouble("ratio"), 0.01);
  }

  @Test
  public void testGetComplexity() {
    ProjectGroup group = report.getComplexity();
    assertEquals(newEvaluation(true, 21, 11, false, 0.5, 5, "NEGATIVE"), group.getEvaluation());
    assertEquals(newStatistics(0.5, 0.75, 1.0), group.getStatistics());
    assertEquals(newDistribution(11, 10, 0, 0), group.getDistribution());
    JSONArray expected = new JSONArray();
    expected.add(newTreemapData(RECIPE1, 1, 1.0, 6));
    expected.add(newTreemapData(RECIPE2, 20, 0.5, 3));
    assertEquals(expected, group.getTreemap());
    List<JSONObject> summaries = toList(group.getSummaries());
    assertEquals(1.0, summaries.get(0).getDouble("ratio"), 0.01);
    assertEquals(0.5, summaries.get(1).getDouble("ratio"), 0.01);
  }

  @Test
  public void testGetDuplications() {
    ProjectGroup group = report.getDuplications();
    assertEquals(newEvaluation(true, 21, 10, true, 0.5, 5, "NEGATIVE"), group.getEvaluation());
    assertEquals(newStatistics(0.0, 0.25, 0.5), group.getStatistics());
    assertEquals(newDistribution(10, 11, 0, 0), group.getDistribution());
    JSONArray expected = new JSONArray();
    expected.add(newTreemapData(RECIPE1, 1, 0.0, 0));
    expected.add(newTreemapData(RECIPE2, 20, 0.5, 6));
    assertEquals(expected, group.getTreemap());
    List<JSONObject> summaries = toList(group.getSummaries());
    assertEquals(0.0, summaries.get(0).getDouble("ratio"), 0.01);
    assertEquals(0.5, summaries.get(1).getDouble("ratio"), 0.01);
  }

  @Test
  public void testGetUnitTests() {
    ProjectGroup group = report.getUnitTests();
    assertEquals(newEvaluation(true, 6, 3, true, 0.5, 0, "POSITIVE"), group.getEvaluation());
    assertEquals(newStatistics(0.25, 0.625, 1.0), group.getStatistics());
    assertEquals(newDistribution(3, 1, 1, 1), group.getDistribution());
    JSONArray expected = new JSONArray();
    expected.add(newTreemapData(RECIPE1, 1, 0.25, 5));
    expected.add(newTreemapData(RECIPE2, 20, 1.0, 0));
    assertEquals(expected, group.getTreemap());
    List<JSONObject> summaries = toList(group.getSummaries());
    assertEquals(0.25, summaries.get(0).getDouble("ratio"), 0.01);
    assertEquals(1.0, summaries.get(1).getDouble("ratio"), 0.01);
  }

  @Test
  public void testGetStatementCoverage() {
    ProjectGroup group = report.getStatementCoverage();
    assertEquals(newEvaluation(true, 4, 1, false, 0.5, 0, "POSITIVE"), group.getEvaluation());
    assertEquals(newStatistics(0.0, 0.25, 0.5), group.getStatistics());
    assertEquals(newDistribution(1, 3, 0, 0), group.getDistribution());
    JSONArray expected = new JSONArray();
    expected.add(newTreemapData(RECIPE1, 1, 0.5, 0));
    expected.add(newTreemapData(RECIPE2, 20, 0.0, 6));
    assertEquals(expected, group.getTreemap());
    List<JSONObject> summaries = toList(group.getSummaries());
    assertEquals(0.5, summaries.get(0).getDouble("ratio"), 0.01);
    assertEquals(0.0, summaries.get(1).getDouble("ratio"), 0.01);
  }

  @Test
  public void testGetBranchCoverage() {
    ProjectGroup group = report.getBranchCoverage();
    assertEquals(newEvaluation(true, 4, 1, false, 0.5, 0, "POSITIVE"), group.getEvaluation());
    assertEquals(newStatistics(0.0, 0.25, 0.5), group.getStatistics());
    assertEquals(newDistribution(1, 3, 0, 0), group.getDistribution());
    JSONArray expected = new JSONArray();
    expected.add(newTreemapData(RECIPE1, 1, 0.5, 0));
    expected.add(newTreemapData(RECIPE2, 20, 0.0, 6));
    assertEquals(expected, group.getTreemap());
    List<JSONObject> summaries = toList(group.getSummaries());
    assertEquals(0.5, summaries.get(0).getDouble("ratio"), 0.01);
    assertEquals(0.0, summaries.get(1).getDouble("ratio"), 0.01);
  }

  @Test
  public void testGetMutationTests() {
    ProjectGroup group = report.getMutationTests();
    assertEquals(newEvaluation(true, 5, 3, true, 0.5, 0, "POSITIVE"), group.getEvaluation());
    assertEquals(newStatistics(0.3333333333333333, 0.6666666666666666, 1.0), group.getStatistics());
    assertEquals(newDistribution(3, 1, 1, 0), group.getDistribution());
    JSONArray expected = new JSONArray();
    expected.add(newTreemapData(RECIPE1, 1, 0.3333333333333333, 4));
    expected.add(newTreemapData(RECIPE2, 20, 1.0, 0));
    assertEquals(expected, group.getTreemap());
    List<JSONObject> summaries = toList(group.getSummaries());
    assertEquals(0.3333333333333333,
        summaries.get(0).getDouble("ratio"), 0.01);
    assertEquals(1.0, summaries.get(1).getDouble("ratio"), 0.01);
  }
}
