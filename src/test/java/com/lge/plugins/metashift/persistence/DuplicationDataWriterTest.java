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

import com.lge.plugins.metashift.aggregators.DuplicationDataSummaryAggregator;
import com.lge.plugins.metashift.aggregators.TreemapDataAggregator;
import com.lge.plugins.metashift.analysis.DuplicationEvaluator;
import com.lge.plugins.metashift.models.CodeSizeData;
import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.DuplicationData;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.Recipes;
import com.lge.plugins.metashift.utils.ConfigurationUtils;
import hudson.FilePath;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Unit tests for the DuplicationDataWriter class.
 *
 * @author Sung Gon Kim
 */
public class DuplicationDataWriterTest {

  private static final String UNKNOWN = "unknown";
  private static final String RECIPE1 = "A-A-A";
  private static final String RECIPE2 = "B-B-B";

  @Rule
  public final TemporaryFolder folder = new TemporaryFolder();
  private DuplicationDataSummaryAggregator summaryAggregator;
  private TreemapDataAggregator treemapAggregator;
  private DataReader reader;
  private DuplicationDataWriter writer;
  private Recipes recipes;
  private Recipe recipe1;
  private Recipe recipe2;

  @Before
  public void setUp() throws IOException, InterruptedException {
    Configuration configuration = ConfigurationUtils.of(50, 5, false);
    summaryAggregator = new DuplicationDataSummaryAggregator(configuration);
    treemapAggregator = new TreemapDataAggregator(new DuplicationEvaluator(configuration));
    DataSource dataSource = new DataSource(new FilePath(folder.newFolder()));
    reader = new ArchiveReader(dataSource).getDuplications();
    writer = new ArchiveWriter(dataSource).getDuplications();
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

  private JSONObject newSummaries(String name, long linesOfCode, long duplicate, long unique,
      double ratio, boolean qualified) {
    JSONObject o = new JSONObject();
    o.put("name", name);
    o.put("linesOfCode", linesOfCode);
    o.put("duplicate", duplicate);
    o.put("unique", unique);
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

  private void assertObjects(Recipe recipe, String file, JSONArray expected) throws IOException {
    writer.addObjects(recipe);
    assertEquals(expected, reader.getObjects(recipe.getName(), file));
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
    recipe1.add(new CodeSizeData(RECIPE1, "a.file", 1, 0, 0));
    recipe1.add(new DuplicationData(RECIPE1, "a.file", 1, 0));
    recipe2.add(new CodeSizeData(RECIPE2, "b.file", 20, 0, 0));
    recipe2.add(new DuplicationData(RECIPE2, "b.file", 20, 20));

    JSONArray expected = new JSONArray();
    expected.add(newTreemap(RECIPE1, 1, 0.0, 0));
    expected.add(newTreemap(RECIPE2, 20, 1.0, 6));
    assertTreemap(expected);
  }

  @Test
  public void testGetProjectSummaries() throws IOException {
    recipe1.add(new CodeSizeData(RECIPE1, "a.file", 1, 0, 0));
    recipe1.add(new DuplicationData(RECIPE1, "a.file", 1, 0));
    recipe2.add(new CodeSizeData(RECIPE2, "b.file", 20, 0, 0));
    recipe2.add(new DuplicationData(RECIPE2, "b.file", 20, 20));

    JSONArray expected = new JSONArray();
    expected.add(newSummaries(RECIPE1, 1, 0, 1, 0.0, true));
    expected.add(newSummaries(RECIPE2, 20, 20, 0, 1.0, false));
    assertSummaries(expected);
  }

  @Test
  public void testGetRecipeSummaries() throws IOException {
    recipe1.add(new CodeSizeData(RECIPE1, "a.file", 1, 0, 0));
    recipe1.add(new DuplicationData(RECIPE1, "a.file", 1, 0));
    recipe1.add(new CodeSizeData(RECIPE1, "b.file", 20, 0, 0));
    recipe1.add(new DuplicationData(RECIPE1, "b.file", 20, 20));

    JSONArray expected = new JSONArray();
    expected.add(newSummaries("a.file", 1, 0, 1, 0.0, true));
    expected.add(newSummaries("b.file", 20, 20, 0, 1.0, false));
    assertSummaries(recipe1, expected);
  }

  @Test
  public void getRecipeObjects() throws IOException {
    recipe1.add(new CodeSizeData(RECIPE1, "a.file", 1, 0, 0));
    List<DuplicationData> first = Collections.singletonList(
        new DuplicationData(RECIPE1, "a.file", 1, 0));
    first.forEach(recipe1::add);
    recipe1.add(new CodeSizeData(RECIPE1, "b.file", 20, 0, 0));
    List<DuplicationData> second = Collections.singletonList(
        new DuplicationData(RECIPE1, "b.file", 20, 20));
    second.forEach(recipe1::add);
    assertObjects(recipe1, "a.file", JSONArray.fromObject(first));
    assertObjects(recipe1, "b.file", JSONArray.fromObject(second));
  }
}
