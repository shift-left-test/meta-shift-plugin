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

package com.lge.plugins.metashift.ui.recipe;

import com.lge.plugins.metashift.metrics.Evaluator;
import com.lge.plugins.metashift.models.CoverageData;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.persistence.DataSource;
import com.lge.plugins.metashift.ui.models.StatisticsItem;
import hudson.model.TaskListener;
import hudson.remoting.VirtualChannel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.bind.JavaScriptMethod;

/**
 * MetaShift recipe's coverage detail view action class.
 */
public class RecipeCoverageAction extends RecipeActionChild {

  static final String STORE_KEY_COVERAGELIST = "CoverageList";
  static final String STORE_KEY_FILECOVERAGESTAT = "FileCoverageStat";

  /**
   * constructor.
   *
   * @param parent     parent action
   * @param listener   logger
   * @param dataSource datasource
   * @param recipe     recipe
   * @param metadata   metadata
   */
  public RecipeCoverageAction(
      RecipeAction parent, TaskListener listener, VirtualChannel channel,
      DataSource dataSource, Recipe recipe, JSONObject metadata) {
    super(parent);

    List<CoverageData> coverageDataList =
        recipe.objects(CoverageData.class).collect(Collectors.toList());

    HashMap<String, List<CoverageData>> fileCoverageList = new HashMap<>();

    for (CoverageData coverageData : coverageDataList) {
      String file = coverageData.getFile();

      if (!fileCoverageList.containsKey(file)) {
        fileCoverageList.put(file, new ArrayList<>());
      }

      fileCoverageList.get(file).add(coverageData);
    }

    JSONArray fileCoverageArray = new JSONArray();

    fileCoverageList.forEach((file, coverageList) -> {
      HashSet<Long> lines = new HashSet<>();
      HashSet<Long> coveredLines = new HashSet<>();
      HashSet<LineIndex> indexes = new HashSet<>();
      HashSet<LineIndex> coveredIndexes = new HashSet<>();

      for (CoverageData data : coverageList) {
        LineIndex lineIndex = new LineIndex(data.getLine(), data.getIndex());

        lines.add(data.getLine());
        indexes.add(lineIndex);
        if (data.isCovered()) {
          coveredLines.add(data.getLine());
          coveredIndexes.add(lineIndex);
        }
      }
      
      JSONObject fileCoverage = new JSONObject();
      fileCoverage.put("file", file);
      fileCoverage.put("lineCoverage",
          lines.size() > 0 ? (double) coveredLines.size() / (double) lines.size() : 0);
      fileCoverage.put("branchCoverage",
          indexes.size() > 0 ? (double) coveredIndexes.size() / (double) indexes.size() : 0);
      fileCoverageArray.add(fileCoverage);

      try {
        this.saveFileContents(channel, metadata, file);
        dataSource.put(coverageList,
            this.getParentAction().getName(), file, STORE_KEY_COVERAGELIST);
      } catch (IOException | InterruptedException e) {
        listener.getLogger().println(e.getMessage());
        e.printStackTrace(listener.getLogger());
      }
    });

    try {
      dataSource.put(fileCoverageArray,
          this.getParentAction().getName(), STORE_KEY_FILECOVERAGESTAT);
    } catch (IOException e) {
      listener.getLogger().println(e.getMessage());
      e.printStackTrace(listener.getLogger());
    }
  }

  @Override
  public String getIconFileName() {
    return "document.png";
  }

  @Override
  public String getDisplayName() {
    return "Coverage";
  }

  @Override
  public String getUrlName() {
    return "coverage";
  }

  @Override
  public String getScale() {
    Evaluator<?> evaluator = this.getParentAction().getMetrics().getCoverage();
    if (evaluator.isAvailable()) {
      return String.format("%d%%", (long) (evaluator.getRatio() * 100));
    }
    return "N/A";
  }

  @Override
  public JSONArray getStatistics() {
    Evaluator<?> evaluator = this.getParentAction().getMetrics().getCoverage();

    StatisticsItem[] result = new StatisticsItem[]{
        new StatisticsItem(
            "Covered",
            (int) (evaluator.getRatio() * 100),
            (int) evaluator.getNumerator(),
            "valid-good"
        ),
        new StatisticsItem(
            "UnCovered",
            (int) ((1 - evaluator.getRatio()) * 100),
            (int) (evaluator.getDenominator() - evaluator.getNumerator()),
            "invalid"
        )
    };

    return JSONArray.fromObject(result);
  }

  /**
   * key for line + index.
   */
  public static class LineIndex {

    Long line;
    long index;

    public LineIndex(long line, long index) {
      this.line = line;
      this.index = index;
    }

    public long getLine() {
      return this.line;
    }

    public long getIndex() {
      return this.index;
    }
  }

  /**
   * return paginated coverage list.
   *
   * @return coverage list
   */
  @JavaScriptMethod
  public JSONArray getRecipeFiles() {
    JSONArray dataList = this.getDataSource().get(
        this.getParentAction().getName(), STORE_KEY_FILECOVERAGESTAT);

    return dataList;
  }

  /**
   * return file coverage info.
   */
  @JavaScriptMethod
  public JSONObject getFileCoverageDetail(String codePath) {
    JSONObject result = new JSONObject();

    List<CoverageData> dataList = this.getDataSource().get(
        this.getParentAction().getName(), codePath, STORE_KEY_COVERAGELIST);

    result.put("dataList", dataList);
    result.put("content", this.readFileContents(codePath));

    return result;
  }
}
