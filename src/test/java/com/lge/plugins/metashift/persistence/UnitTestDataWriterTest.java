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

import com.lge.plugins.metashift.aggregators.TreemapDataAggregator;
import com.lge.plugins.metashift.aggregators.UnitTestDataSummaryAggregator;
import com.lge.plugins.metashift.analysis.UnitTestEvaluator;
import com.lge.plugins.metashift.models.CodeSizeData;
import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.ErrorTestData;
import com.lge.plugins.metashift.models.FailedTestData;
import com.lge.plugins.metashift.models.PassedTestData;
import com.lge.plugins.metashift.models.PremirrorCacheData;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.Recipes;
import com.lge.plugins.metashift.models.SkippedTestData;
import com.lge.plugins.metashift.models.TestData;
import com.lge.plugins.metashift.utils.ConfigurationUtils;
import hudson.FilePath;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Unit tests for the UnitTestDataWriter class.
 *
 * @author Sung Gon Kim
 */
public class UnitTestDataWriterTest {

  private static final String UNKNOWN = "UNKNOWN";
  private static final String RECIPE1 = "A-A-A";
  private static final String RECIPE2 = "B-B-B";

  @Rule
  public final TemporaryFolder folder = new TemporaryFolder();
  private UnitTestDataSummaryAggregator summaryAggregator;
  private TreemapDataAggregator treemapAggregator;
  private DataReader reader;
  private UnitTestDataWriter writer;
  private Recipes recipes;
  private Recipe recipe1;
  private Recipe recipe2;

  @Before
  public void setUp() throws IOException, InterruptedException {
    Configuration configuration = ConfigurationUtils.of(50, 5, false);
    summaryAggregator = new UnitTestDataSummaryAggregator(configuration);
    treemapAggregator = new TreemapDataAggregator(new UnitTestEvaluator(configuration));
    DataSource dataSource = new DataSource(new FilePath(folder.newFolder()));
    reader = new ArchiveReader(dataSource).getUnitTests();
    writer = new ArchiveWriter(dataSource).getUnitTests();
    recipe1 = new Recipe(RECIPE1);
    recipe2 = new Recipe(RECIPE2);
    recipes = new Recipes();
    recipes.add(recipe1);
    recipes.add(recipe2);
  }

  private JSONObject newTreemap(String name, long linesOfCode, double value, int grade) {
    JSONObject o = new JSONObject();
    o.put("name", name);
    o.put("linesOfCode", linesOfCode);
    o.put("value", value);
    o.put("grade", grade);
    return o;
  }

  private JSONObject newSummaries(String name, long linesOfCode, long passed, long failed,
      long error, long skipped, double ratio, boolean qualified) {
    JSONObject o = new JSONObject();
    o.put("name", name);
    o.put("linesOfCode", linesOfCode);
    o.put("tests", passed + failed + error + skipped);
    o.put("passed", passed);
    o.put("failed", failed);
    o.put("error", error);
    o.put("skipped", skipped);
    o.put("ratio", ratio);
    o.put("qualified", qualified);
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

  private void assertSummaries(Recipe recipe, JSONArray expected) throws IOException {
    writer.addSummaries(recipe.getName(), summaryAggregator.parse(recipe));
    assertEquals(expected, reader.getSummaries(recipe.getName()));
  }

  @Test
  public void testInitialStatus() {
    assertEquals(new JSONArray(), reader.getTreemap());
    assertEquals(new JSONArray(), reader.getSummaries());
    assertEquals(new JSONArray(), reader.getSummaries(UNKNOWN));
    assertEquals(new JSONArray(), reader.getObjects(RECIPE1, UNKNOWN));
    assertEquals("", reader.readFile(RECIPE1, UNKNOWN));
  }

  @Test
  public void testGetWithEmptyData() throws IOException {
    writer.addTreemap(new ArrayList<>());
    writer.addSummaries(new ArrayList<>());
    writer.addSummaries(RECIPE1, new ArrayList<>());
    assertEquals(new JSONArray(), reader.getTreemap());
    assertEquals(new JSONArray(), reader.getSummaries());
    assertEquals(new JSONArray(), reader.getSummaries(RECIPE1));
  }

  @Test
  public void testGetProjectTreemap() throws IOException {
    recipe1.add(new CodeSizeData(RECIPE1, "a.file", 10, 0, 0));
    recipe1.add(new PassedTestData(RECIPE1, "A", "A", "A"));
    recipe2.add(new CodeSizeData(RECIPE2, "b.file", 20, 0, 0));
    recipe2.add(new FailedTestData(RECIPE2, "B", "B", "B"));
    recipe2.add(new ErrorTestData(RECIPE2, "C", "C", "C"));
    recipe2.add(new SkippedTestData(RECIPE2, "D", "D", "D"));

    JSONArray expected = new JSONArray();
    expected.add(newTreemap(RECIPE1, 10, 1.0, 0));
    expected.add(newTreemap(RECIPE2, 20, 0.0, 6));
    assertTreemap(expected);
  }

  @Test
  public void testGetProjectSummaries() throws IOException {
    recipe1.add(new CodeSizeData(RECIPE1, "a.file", 10, 0, 0));
    recipe1.add(new PassedTestData(RECIPE1, "A", "A", "A"));
    recipe2.add(new CodeSizeData(RECIPE2, "b.file", 20, 0, 0));
    recipe2.add(new FailedTestData(RECIPE2, "B", "B", "B"));
    recipe2.add(new ErrorTestData(RECIPE2, "C", "C", "C"));
    recipe2.add(new SkippedTestData(RECIPE2, "D", "D", "D"));

    JSONArray expected = new JSONArray();
    expected.add(newSummaries(RECIPE1, 10, 1, 0, 0, 0, 1.0, true));
    expected.add(newSummaries(RECIPE2, 20, 0, 1, 1, 1, 0.0, false));
    assertSummaries(expected);
  }

  @Test
  public void testGetRecipeSummaries() throws IOException {
    recipe1.add(new CodeSizeData(RECIPE1, "a.file", 10, 0, 0));
    List<TestData> data = Arrays.asList(
        new PassedTestData(RECIPE1, "A", "A", "A"),
        new FailedTestData(RECIPE2, "B", "B", "B"),
        new ErrorTestData(RECIPE2, "C", "C", "C"),
        new SkippedTestData(RECIPE2, "D", "D", "D")
    );
    data.forEach(recipe1::add);
    assertSummaries(recipe1, JSONArray.fromObject(data));
  }
}