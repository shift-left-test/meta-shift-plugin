/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.ui.project;

import static org.junit.Assert.assertEquals;

import com.lge.plugins.metashift.builders.ProjectReport;
import com.lge.plugins.metashift.builders.ProjectReportBuilder;
import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.PassedTestData;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.Recipes;
import com.lge.plugins.metashift.persistence.DataSource;
import edu.hm.hafner.echarts.line.LineSeries;
import edu.hm.hafner.echarts.line.LinesChartModel;
import hudson.FilePath;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Unit test for the {@link TrendChartModel} class.
 */
public class TrendChartModelTest {

  @Rule
  public final TemporaryFolder folder = new TemporaryFolder();

  private ProjectReport report;

  @Before
  public void setUp() throws IOException, InterruptedException {
    Configuration config = new Configuration();
    Recipes recipes = new Recipes();
    Recipe recipe = new Recipe("A-1.0.0-r0");
    recipe.add(new PassedTestData("A-1.0.0-r0", "A", "A", "A"));
    recipes.add(recipe);

    DataSource dataSource = new DataSource(new FilePath(folder.newFolder()));
    report = new ProjectReportBuilder(config, dataSource).parse(recipes);
  }

  private LineSeries seriesOf(LinesChartModel model, String name) {
    return model.getSeries().stream().filter(o -> o.getName().equals(name))
        .findFirst().orElseThrow();
  }

  @Test
  public void testEmptyModel() {
    LinesChartModel model = TrendChartModel.create(
        Collections.emptyList(), Collections.emptyList(), Collections.emptyList());

    assertEquals(Collections.emptyList(), model.getDomainAxisLabels());
    assertEquals(Arrays.asList("Test", "StatementCoverage", "BranchCoverage", "Mutation"),
        model.getSeries().stream().map(LineSeries::getName).collect(Collectors.toList()));
    assertEquals(Collections.emptyList(), seriesOf(model, "Test").getData());
  }

  @Test
  public void testAvailableMetricIsPercent() {
    LinesChartModel model = TrendChartModel.create(
        Collections.singletonList("#1"), Collections.singletonList(1),
        Collections.singletonList(report));

    assertEquals(Collections.singletonList("#1"), model.getDomainAxisLabels());
    assertEquals(Collections.singletonList(1), model.getBuildNumbers());
    // A single passed unit test yields a 100% qualification ratio.
    assertEquals(Collections.singletonList(100.0), seriesOf(model, "Test").getData());
  }

  @Test
  public void testUnavailableMetricIsGap() {
    LinesChartModel model = TrendChartModel.create(
        Collections.singletonList("#1"), Collections.singletonList(1),
        Collections.singletonList(report));

    // No coverage data was provided, so the metric is a gap (null), not zero.
    List<Double> data = seriesOf(model, "StatementCoverage").getData();
    assertEquals(Collections.singletonList(null), data);
  }

  @Test
  public void testMultipleBuildsPreserveOrder() {
    LinesChartModel model = TrendChartModel.create(
        Arrays.asList("#1", "#2"), Arrays.asList(1, 2), Arrays.asList(report, report));

    assertEquals(Arrays.asList("#1", "#2"), model.getDomainAxisLabels());
    assertEquals(Arrays.asList(100.0, 100.0), seriesOf(model, "Test").getData());
  }
}
