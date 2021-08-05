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

package com.lge.plugins.metashift.persistence;

import static org.junit.Assert.assertEquals;

import com.lge.plugins.metashift.aggregators.EvaluationSummaryAggregator;
import com.lge.plugins.metashift.aggregators.TreemapDataAggregator;
import com.lge.plugins.metashift.analysis.RecipeEvaluator;
import com.lge.plugins.metashift.models.CodeSizeData;
import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.FailedTestData;
import com.lge.plugins.metashift.models.KilledMutationTestData;
import com.lge.plugins.metashift.models.PositiveTreemapData;
import com.lge.plugins.metashift.models.PremirrorCacheData;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.Recipes;
import com.lge.plugins.metashift.models.SharedStateCacheData;
import com.lge.plugins.metashift.utils.ConfigurationUtils;
import hudson.FilePath;
import java.io.IOException;
import java.util.ArrayList;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Unit tests for the ArchiveWriter class.
 *
 * @author Sung Gon Kim
 */
public class ArchiveWriterTest {

  private static final String RECIPE1 = "A-A-A";
  private static final String RECIPE2 = "B-B-B";

  @Rule
  public final TemporaryFolder folder = new TemporaryFolder();
  private EvaluationSummaryAggregator summaryAggregator;
  private TreemapDataAggregator treemapAggregator;
  private ArchiveReader reader;
  private ArchiveWriter writer;
  private Recipes recipes;
  private Recipe recipe1;
  private Recipe recipe2;

  @Before
  public void setUp() throws IOException, InterruptedException {
    Configuration configuration = ConfigurationUtils.of(50, 5, false);
    summaryAggregator = new EvaluationSummaryAggregator(configuration);
    treemapAggregator = new TreemapDataAggregator(new RecipeEvaluator(configuration));
    DataSource dataSource = new DataSource(new FilePath(folder.newFolder()));
    reader = new ArchiveReader(dataSource);
    writer = new ArchiveWriter(dataSource);
    recipe1 = new Recipe(RECIPE1);
    recipe2 = new Recipe(RECIPE2);
    recipes = new Recipes();
    recipes.add(recipe1);
    recipes.add(recipe2);
  }

  private JSONObject newSummary(String name, long linesOfCode, double premirrorCache,
      double sharedStateCache, double recipeViolations, double comments, double codeViolations,
      double complexity, double duplications, double unitTests, double statementCoverage,
      double branchCoverage, double mutationTests) {
    JSONObject o = new JSONObject();
    o.put("name", name);
    o.put("linesOfCode", linesOfCode);
    o.put("premirrorCache", premirrorCache);
    o.put("sharedStateCache", sharedStateCache);
    o.put("recipeViolations", recipeViolations);
    o.put("comments", comments);
    o.put("codeViolations", codeViolations);
    o.put("complexity", complexity);
    o.put("duplications", duplications);
    o.put("unitTests", unitTests);
    o.put("statementCoverage", statementCoverage);
    o.put("branchCoverage", branchCoverage);
    o.put("mutationTests", mutationTests);
    return o;
  }

  private void assertTreemap(JSONArray expected) throws IOException {
    writer.addTreemap(treemapAggregator.parse(recipes));
    assertEquals(expected, reader.getTreemap());
  }

  private void assertSummaries(JSONArray expected) throws IOException {
    writer.addSummaries(summaryAggregator.parse(recipes));
    assertEquals(expected, reader.getSummaries());
  }

  @Test
  public void testInitialStatus() throws IOException {
    assertEquals(new JSONArray(), reader.getTreemap());
    assertEquals(new JSONArray(), reader.getSummaries());
  }

  @Test
  public void testGetWithEmptyData() throws IOException {
    writer.addTreemap(new ArrayList<>());
    writer.addSummaries(new ArrayList<>());
    assertEquals(new JSONArray(), reader.getTreemap());
    assertEquals(new JSONArray(), reader.getSummaries());
  }

  @Test
  public void testGetTreemap() throws IOException {
    recipe1.add(new CodeSizeData(RECIPE1, "a.file", 10, 0, 0));
    recipe1.add(new PremirrorCacheData(RECIPE1, "A", true));
    recipe1.add(new SharedStateCacheData(RECIPE1, "B", true));
    recipe1.add(new SharedStateCacheData(RECIPE1, "C", false));
    recipe2.add(new CodeSizeData(RECIPE2, "b.file", 20, 0, 0));
    recipe2.add(new FailedTestData(RECIPE2, "B", "B", "B"));
    recipe2.add(new KilledMutationTestData(RECIPE2, "b.file", "B", "B", 2, "B", "B"));

    JSONArray expected = new JSONArray();
    expected.add(new PositiveTreemapData(RECIPE1, 10, 1.0, 1.0));
    expected.add(new PositiveTreemapData(RECIPE2, 20, 1.0, 0.5));
    assertTreemap(expected);
  }

  @Test
  public void testGetSummaries() throws IOException {
    recipe1.add(new CodeSizeData(RECIPE1, "a.file", 10, 0, 0));
    recipe1.add(new PremirrorCacheData(RECIPE1, "A", true));
    recipe1.add(new SharedStateCacheData(RECIPE1, "B", true));
    recipe1.add(new SharedStateCacheData(RECIPE1, "C", false));
    recipe2.add(new CodeSizeData(RECIPE2, "b.file", 20, 0, 0));
    recipe2.add(new FailedTestData(RECIPE2, "B", "B", "B"));
    recipe2.add(new KilledMutationTestData(RECIPE2, "b.file", "B", "B", 2, "B", "B"));

    JSONArray expected = new JSONArray();
    expected.add(newSummary(RECIPE1, 10, 1.0, 0.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0));
    expected.add(newSummary(RECIPE2, 20, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0));
    assertSummaries(expected);
  }
}
