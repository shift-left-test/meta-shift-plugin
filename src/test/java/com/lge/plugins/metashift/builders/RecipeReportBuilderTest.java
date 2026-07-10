/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.builders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.lge.plugins.metashift.fixture.FakeRecipe;
import com.lge.plugins.metashift.fixture.FakeReportBuilder;
import com.lge.plugins.metashift.fixture.FakeSource;
import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.Distribution;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.Recipes;
import com.lge.plugins.metashift.models.StatementCoverageData;
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
  private Configuration configuration;
  private DataSource dataSource;
  private RecipeReport report;
  private String FILE1;
  private String FILE2;

  @Before
  public void setUp() throws IOException, InterruptedException {
    File source = new File(folder.newFolder(), "source");
    File report = new File(folder.newFolder(), "report");
    createReports(source, report);
    Recipes recipes = new FileParser().parse(new FilePath(report));
    Recipe recipe = recipes.get(0);
    configuration = ConfigurationUtils.of(50, 5, false);
    dataSource = new DataSource(new FilePath(folder.newFolder()));
    new ProjectReportBuilder(configuration, dataSource).parse(recipes);
    this.report =
        new RecipeReportBuilder(configuration, dataSource, new FilePath(report)).parse(recipe);
  }

  private void createReports(File source, File report) throws IOException {
    FakeSource source1 = new FakeSource(1, 1, 0, 0);
    FakeSource source2 = new FakeSource(20, 10, 10, 10);

    FakeReportBuilder fakeReportBuilder = new FakeReportBuilder();
    fakeReportBuilder.add(new FakeRecipe(source, RECIPE + "-1.0.0-r0")
        .add(source1
            .setTests(1, 1, 1, 1)
            .setStatementCoverage(2, 1)
            .setBranchCoverage(1, 1)
            .setMutationTests(1, 1, 1))
        .add(source2
            .setTests(2, 0, 0, 0)
            .setStatementCoverage(0, 2)
            .setBranchCoverage(0, 2)
            .setMutationTests(2, 0, 0)));
    fakeReportBuilder.toFile(report);

    FILE1 = source1.getFilename();
    FILE2 = source2.getFilename();
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

  private JSONObject newDistribution(long first, long second, long third, long fourth) {
    return JSONObject.fromObject(new Distribution(first, second, third, fourth));
  }

  private List<JSONObject> toList(JSONArray array) {
    return array.stream().map(o -> (JSONObject) o).collect(Collectors.toList());
  }

  @Test
  public void testGetUnitTests() {
    RecipeGroup group = report.getUnitTests();
    assertEquals(newEvaluation(true, 6, 3, true, 0.5, 0, "POSITIVE"), group.getEvaluation());
    assertEquals(newDistribution(3, 1, 1, 1), group.getDistribution());
    assertEquals(6, group.getSummaries().size());
  }

  @Test
  public void testGetStatementCoverage() {
    RecipeGroup group = report.getStatementCoverage();
    assertEquals(newEvaluation(true, 8, 3, false, 0.5, 0, "POSITIVE"), group.getEvaluation());
    assertEquals(newDistribution(3, 5, 0, 0), group.getDistribution());
    List<JSONObject> summaries = toList(group.getSummaries());
    assertEquals(0.6, summaries.get(0).getDouble("ratio"), 0.01);
    assertEquals(0.0, summaries.get(1).getDouble("ratio"), 0.01);
    assertEquals(5, group.getObjects(FILE1).size());
    assertEquals(3, group.getObjects(FILE2).size());
  }

  @Test
  public void testGetBranchCoverage() {
    RecipeGroup group = report.getBranchCoverage();
    assertEquals(newEvaluation(true, 4, 1, false, 0.5, 0, "POSITIVE"), group.getEvaluation());
    assertEquals(newDistribution(1, 3, 0, 0), group.getDistribution());
    List<JSONObject> summaries = toList(group.getSummaries());
    assertEquals(0.5, summaries.get(0).getDouble("ratio"), 0.01);
    assertEquals(0.0, summaries.get(1).getDouble("ratio"), 0.01);
    assertEquals(2, group.getObjects(FILE1).size());
    assertEquals(2, group.getObjects(FILE2).size());
  }

  @Test
  public void testGetMutationTests() {
    RecipeGroup group = report.getMutationTests();
    assertEquals(newEvaluation(true, 5, 3, true, 0.5, 0, "POSITIVE"), group.getEvaluation());
    assertEquals(newDistribution(3, 1, 1, 0), group.getDistribution());
    List<JSONObject> summaries = toList(group.getSummaries());
    assertEquals(0.3333333333333333,
        summaries.get(0).getDouble("ratio"), 0.01);
    assertEquals(1.0, summaries.get(1).getDouble("ratio"), 0.01);
    assertEquals(3, group.getObjects(FILE1).size());
    assertEquals(2, group.getObjects(FILE2).size());
  }

  @Test
  public void testStoresSourceFilesOfReportedFiles() throws Exception {
    File source = new File(folder.newFolder(), "source");
    File reportDir = new File(folder.newFolder(), "report");
    FakeRecipe fakeRecipe = new FakeRecipe(source);
    FakeSource fakeSource = new FakeSource(fakeRecipe, 10, 5, 5, 0)
        .setStatementCoverage(1, 1).setMutationTests(1, 0, 0).setTests(1, 0, 0, 0);
    fakeRecipe.add(fakeSource);
    new FakeReportBuilder().add(fakeRecipe).toFile(reportDir);
    Recipe recipe = new FileParser().parse(new FilePath(reportDir)).get(0);

    new RecipeReportBuilder(configuration, dataSource, new FilePath(reportDir)).parse(recipe);

    String stored = dataSource.get("RECIPE", "NONE", "FILE",
        recipe.getName(), fakeSource.getFilename());
    assertNotNull(stored);
    assertTrue(stored.contains(FakeSource.SOURCE_LINE));
  }

  @Test
  public void testSkipsSourceLargerThanCap() throws Exception {
    File source = new File(folder.newFolder(), "source");
    File reportDir = new File(folder.newFolder(), "report");
    FakeRecipe fakeRecipe = new FakeRecipe(source);
    // over 1MB: 12 bytes ("HELLO WORLD\n") x 100,000 lines
    FakeSource fakeSource = new FakeSource(fakeRecipe, 100000, 5, 5, 0)
        .setStatementCoverage(1, 1);
    fakeRecipe.add(fakeSource);
    new FakeReportBuilder().add(fakeRecipe).toFile(reportDir);
    Recipe recipe = new FileParser().parse(new FilePath(reportDir)).get(0);

    new RecipeReportBuilder(configuration, dataSource, new FilePath(reportDir)).parse(recipe);

    assertNull(dataSource.get("RECIPE", "NONE", "FILE",
        recipe.getName(), fakeSource.getFilename()));
  }

  @Test
  public void testSkipsSourceStorageWithoutSourceDir() throws Exception {
    File source = new File(folder.newFolder(), "source");
    File reportDir = new File(folder.newFolder(), "report");
    FakeRecipe fakeRecipe = new FakeRecipe(source);
    FakeSource fakeSource = new FakeSource(fakeRecipe, 10, 5, 5, 0)
        .setStatementCoverage(1, 1);
    fakeRecipe.add(fakeSource);
    new FakeReportBuilder().add(fakeRecipe).toFile(reportDir);
    Recipe recipe = new FileParser().parse(new FilePath(reportDir)).get(0);
    recipe.setSourceDir(null);

    new RecipeReportBuilder(configuration, dataSource, new FilePath(reportDir)).parse(recipe);

    assertNull(dataSource.get("RECIPE", "NONE", "FILE",
        recipe.getName(), fakeSource.getFilename()));
  }

  @Test
  public void testSkipsAbsolutePathsOutsideSourceRoot() throws Exception {
    // files still absolute after relativization live outside S and are not stored
    File source = new File(folder.newFolder(), "source");
    Recipe recipe = new Recipe("outside");
    recipe.setSourceDir(source.getAbsolutePath());
    recipe.add(new StatementCoverageData("outside", "/etc/passwd", 1, true));
    recipe.add(StatementCoverageData.class);

    new RecipeReportBuilder(configuration, dataSource, new FilePath(folder.newFolder()))
        .parse(recipe);

    assertNull(dataSource.get("RECIPE", "NONE", "FILE", "outside", "/etc/passwd"));
  }

  @Test
  public void testSkipsPathTraversalOutsideSourceRoot() throws Exception {
    File source = new File(folder.newFolder(), "source");
    File secret = new File(source.getParentFile(), "secret.txt");
    org.apache.commons.io.FileUtils.writeStringToFile(secret, "top secret",
        java.nio.charset.StandardCharsets.UTF_8);
    Recipe recipe = new Recipe("traversal");
    recipe.setSourceDir(source.getAbsolutePath());
    recipe.add(new StatementCoverageData("traversal", "../secret.txt", 1, true));
    recipe.add(StatementCoverageData.class);

    new RecipeReportBuilder(configuration, dataSource, new FilePath(folder.newFolder()))
        .parse(recipe);

    assertNull(dataSource.get("RECIPE", "NONE", "FILE", "traversal", "../secret.txt"));
  }
}
