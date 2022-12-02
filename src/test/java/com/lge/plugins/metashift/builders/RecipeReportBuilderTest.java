/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.builders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import com.lge.plugins.metashift.fixture.FakeRecipe;
import com.lge.plugins.metashift.fixture.FakeReportBuilder;
import com.lge.plugins.metashift.fixture.FakeScript;
import com.lge.plugins.metashift.fixture.FakeSource;
import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.Distribution;
import com.lge.plugins.metashift.models.LinesOfCode;
import com.lge.plugins.metashift.models.Recipe;
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
 * Unit tests for the RecipeReportBuilder class.
 *
 * @author Sung Gon Kim
 */
public class RecipeReportBuilderTest {

  private static final String RECIPE = "AAA";

  @Rule
  public final TemporaryFolder folder = new TemporaryFolder();
  private RecipeReport report;
  private String RECIPE1;
  private String RECIPE2;
  private String FILE1;
  private String FILE2;

  @Before
  public void setUp() throws IOException, InterruptedException {
    File source = new File(folder.newFolder(), "source");
    File report = new File(folder.newFolder(), "report");
    createReports(source, report);
    Recipes recipes = new FileParser().parse(new FilePath(report));
    Recipe recipe = recipes.get(0);
    Configuration configuration = ConfigurationUtils.of(50, 5, false);
    DataSource dataSource = new DataSource(new FilePath(folder.newFolder()));
    new ProjectReportBuilder(configuration, dataSource).parse(recipes);
    this.report = new RecipeReportBuilder(configuration, dataSource, new FilePath(report))
        .parse(recipe);
  }

  private void createReports(File source, File report) throws IOException {
    FakeScript script1 = new FakeScript(1);
    FakeScript script2 = new FakeScript(20);
    FakeSource source1 = new FakeSource(1, 1, 0, 0);
    FakeSource source2 = new FakeSource(20, 10, 10, 10);

    FakeReportBuilder fakeReportBuilder = new FakeReportBuilder();
    fakeReportBuilder.add(new FakeRecipe(source, RECIPE + "-1.0.0-r0")
        .setPremirror(1, 3)
        .setSharedState(1, 3)
        .add(script1.setIssues(1, 1, 1))
        .add(source1
            .setCodeViolations(1, 1, 1)
            .setComplexity(10, 1, 0)
            .setTests(1, 1, 1, 1)
            .setStatementCoverage(2, 1)
            .setBranchCoverage(1, 1)
            .setMutationTests(1, 1, 1))
        .add(script2.setIssues(2, 2, 2))
        .add(source2
            .setCodeViolations(2, 2, 2)
            .setComplexity(10, 10, 10)
            .setTests(2, 0, 0, 0)
            .setStatementCoverage(0, 2)
            .setBranchCoverage(0, 2)
            .setMutationTests(2, 0, 0)));
    fakeReportBuilder.toFile(report);

    RECIPE1 = script1.getAbsolutePath();
    RECIPE2 = script2.getAbsolutePath();
    FILE1 = source1.getAbsolutePath();
    FILE2 = source2.getAbsolutePath();
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

  private JSONObject newStatistics(double value) {
    JSONObject o = new JSONObject();
    o.put("min", value);
    o.put("average", value);
    o.put("max", value);
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
    LinesOfCode linesOfCode = new LinesOfCode(21, 0, 0, 2, 1);
    assertEquals(JSONObject.fromObject(linesOfCode), report.getLinesOfCode());
  }

  @Test
  public void testGetPremirrorCache() {
    RecipeGroup group = report.getPremirrorCache();
    assertEquals(newEvaluation(true, 4, 1, false, 0.5, 0, "POSITIVE"), group.getEvaluation());
    assertEquals(newStatistics(0.25), group.getStatistics());
    assertEquals(newDistribution(1, 3, 0, 0), group.getDistribution());
    assertEquals(4, group.getSummaries().size());
  }

  @Test
  public void testGetSharedStateCache() {
    RecipeGroup group = report.getSharedStateCache();
    assertEquals(newEvaluation(true, 4, 1, false, 0.5, 0, "POSITIVE"), group.getEvaluation());
    assertEquals(newStatistics(0.25), group.getStatistics());
    assertEquals(newDistribution(1, 3, 0, 0), group.getDistribution());
    assertEquals(4, group.getSummaries().size());
  }

  @Test
  public void testGetRecipeViolations() {
    RecipeGroup group = report.getRecipeViolations();
    assertEquals(newEvaluation(true, 21, 9, true, 0.5, 0, "NEGATIVE"), group.getEvaluation());
    assertEquals(newStatistics(0.42857142857142855), group.getStatistics());
    assertEquals(newDistribution(3, 3, 3, 0), group.getDistribution());
    List<JSONObject> summaries = toList(group.getSummaries());
    assertEquals(3.0, summaries.get(0).getDouble("ratio"), 0.01);
    assertEquals(0.3, summaries.get(1).getDouble("ratio"), 0.01);
    assertEquals(3, group.getObjects(RECIPE1).size());
    assertEquals(6, group.getObjects(RECIPE2).size());
    assertFalse(group.readFile(RECIPE1).isEmpty());
    assertFalse(group.readFile(RECIPE2).isEmpty());
  }

  @Test
  public void testGetComments() {
    RecipeGroup group = report.getComments();
    assertEquals(newEvaluation(true, 21, 10, false, 0.5, 0, "POSITIVE"), group.getEvaluation());
    assertEquals(newStatistics(0.47619047619047616), group.getStatistics());
    assertEquals(newDistribution(10, 11, 0, 0), group.getDistribution());
    List<JSONObject> summaries = toList(group.getSummaries());
    assertEquals(0.0, summaries.get(0).getDouble("ratio"), 0.01);
    assertEquals(0.5, summaries.get(1).getDouble("ratio"), 0.01);
  }

  @Test
  public void testGetCodeViolations() {
    RecipeGroup group = report.getCodeViolations();
    assertEquals(newEvaluation(true, 21, 9, true, 0.5, 0, "NEGATIVE"), group.getEvaluation());
    assertEquals(newStatistics(0.42857142857142855), group.getStatistics());
    assertEquals(newDistribution(3, 3, 3, 0), group.getDistribution());
    List<JSONObject> summaries = toList(group.getSummaries());
    assertEquals(3.0, summaries.get(0).getDouble("ratio"), 0.01);
    assertEquals(0.3, summaries.get(1).getDouble("ratio"), 0.01);
    assertEquals(3, group.getObjects(FILE1).size());
    assertEquals(6, group.getObjects(FILE2).size());
    assertFalse(group.readFile(FILE1).isEmpty());
    assertFalse(group.readFile(FILE2).isEmpty());
  }

  @Test
  public void testGetComplexity() {
    RecipeGroup group = report.getComplexity();
    assertEquals(newEvaluation(true, 21, 11, false, 0.5, 5, "NEGATIVE"), group.getEvaluation());
    assertEquals(newStatistics(0.5238095238095238), group.getStatistics());
    assertEquals(newDistribution(11, 10, 0, 0), group.getDistribution());
    List<JSONObject> summaries = toList(group.getSummaries());
    assertEquals(1.0, summaries.get(0).getDouble("ratio"), 0.01);
    assertEquals(0.5, summaries.get(1).getDouble("ratio"), 0.01);
    assertEquals(1, group.getObjects(FILE1).size());
    assertEquals(10, group.getObjects(FILE2).size());
    assertFalse(group.readFile(FILE1).isEmpty());
    assertFalse(group.readFile(FILE2).isEmpty());
  }

  @Test
  public void testGetDuplications() {
    RecipeGroup group = report.getDuplications();
    assertEquals(newEvaluation(true, 21, 10, true, 0.5, 5, "NEGATIVE"), group.getEvaluation());
    assertEquals(newStatistics(0.47619047619047616), group.getStatistics());
    assertEquals(newDistribution(10, 11, 0, 0), group.getDistribution());
    List<JSONObject> summaries = toList(group.getSummaries());
    assertEquals(1, summaries.size());
    assertEquals(0.5, summaries.get(0).getDouble("ratio"), 0.01);
    assertEquals(0, group.getObjects(FILE1).size());
    assertFalse(group.readFile(FILE1).isEmpty());
  }

  @Test
  public void testGetUnitTests() {
    RecipeGroup group = report.getUnitTests();
    assertEquals(newEvaluation(true, 6, 3, true, 0.5, 0, "POSITIVE"), group.getEvaluation());
    assertEquals(newStatistics(0.5), group.getStatistics());
    assertEquals(newDistribution(3, 1, 1, 1), group.getDistribution());
    assertEquals(6, group.getSummaries().size());
  }

  @Test
  public void testGetStatementCoverage() {
    RecipeGroup group = report.getStatementCoverage();
    assertEquals(newEvaluation(true, 8, 3, false, 0.5, 0, "POSITIVE"), group.getEvaluation());
    assertEquals(newStatistics(0.375), group.getStatistics());
    assertEquals(newDistribution(3, 5, 0, 0), group.getDistribution());
    List<JSONObject> summaries = toList(group.getSummaries());
    assertEquals(0.6, summaries.get(0).getDouble("ratio"), 0.01);
    assertEquals(0.0, summaries.get(1).getDouble("ratio"), 0.01);
    assertEquals(5, group.getObjects(FILE1).size());
    assertEquals(3, group.getObjects(FILE2).size());
    assertFalse(group.readFile(FILE1).isEmpty());
    assertFalse(group.readFile(FILE2).isEmpty());
  }

  @Test
  public void testGetBranchCoverage() {
    RecipeGroup group = report.getBranchCoverage();
    assertEquals(newEvaluation(true, 4, 1, false, 0.5, 0, "POSITIVE"), group.getEvaluation());
    assertEquals(newStatistics(0.25), group.getStatistics());
    assertEquals(newDistribution(1, 3, 0, 0), group.getDistribution());
    List<JSONObject> summaries = toList(group.getSummaries());
    assertEquals(0.5, summaries.get(0).getDouble("ratio"), 0.01);
    assertEquals(0.0, summaries.get(1).getDouble("ratio"), 0.01);
    assertEquals(2, group.getObjects(FILE1).size());
    assertEquals(2, group.getObjects(FILE2).size());
    assertFalse(group.readFile(FILE1).isEmpty());
    assertFalse(group.readFile(FILE2).isEmpty());
  }

  @Test
  public void testGetMutationTests() {
    RecipeGroup group = report.getMutationTests();
    assertEquals(newEvaluation(true, 5, 3, true, 0.5, 0, "POSITIVE"), group.getEvaluation());
    assertEquals(newStatistics(0.6), group.getStatistics());
    assertEquals(newDistribution(3, 1, 1, 0), group.getDistribution());
    List<JSONObject> summaries = toList(group.getSummaries());
    assertEquals(0.3333333333333333,
        summaries.get(0).getDouble("ratio"), 0.01);
    assertEquals(1.0, summaries.get(1).getDouble("ratio"), 0.01);
    assertEquals(3, group.getObjects(FILE1).size());
    assertEquals(2, group.getObjects(FILE2).size());
    assertFalse(group.readFile(FILE1).isEmpty());
    assertFalse(group.readFile(FILE2).isEmpty());
  }
}
