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

package com.lge.plugins.metashift;

import com.lge.plugins.metashift.metrics.Criteria;
import com.lge.plugins.metashift.models.CoverageData;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.persistence.DataSource;
import com.lge.plugins.metashift.utils.TableSortInfo;
import hudson.model.TaskListener;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.bind.JavaScriptMethod;

/**
 * MetaShift recipe's coverage detail view action class.
 */
public class MetaShiftRecipeCoverageAction extends MetaShiftRecipeActionChild {

  static final String STORE_KEY_COVERAGELIST = "CoverageList";
  static final String STORE_KEY_FILECOVERAGESTAT = "FileCoverageStat";

  /**
   * constructor.
   *
   * @param parent     parent action
   * @param listener   logger
   * @param criteria   criteria
   * @param dataSource datasource
   * @param recipe     recipe
   * @param metadata   metadata
   */
  public MetaShiftRecipeCoverageAction(
      MetaShiftRecipeAction parent, TaskListener listener,
      Criteria criteria, DataSource dataSource, Recipe recipe, JSONObject metadata) {
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

    List<FileCoverageStats> fileCoverageStats = new ArrayList<>();

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

      fileCoverageStats.add(new FileCoverageStats(file,
          lines.size() > 0 ? (double) coveredLines.size() / (double) lines.size() : 0,
          indexes.size() > 0 ? (double) coveredIndexes.size() / (double) indexes.size() : 0
      ));
      try {
        this.saveFileContents(metadata, file);
        dataSource.put(coverageList,
            this.getParentAction().getName(), file, STORE_KEY_COVERAGELIST);
      } catch (IOException e) {
        listener.getLogger().println(e.getMessage());
        e.printStackTrace(listener.getLogger());
      }
    });

    try {
      dataSource.put(fileCoverageStats,
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
   * coverage info for each file.
   */
  public static class FileCoverageStats implements Serializable {

    private final String file;
    private double lineCoverage;
    private double branchCoverage;

    /**
     * constructor.
     */
    public FileCoverageStats(String file, double lineCoverage, double branchCoverage) {
      this.file = file;
      this.lineCoverage = lineCoverage;
      this.branchCoverage = branchCoverage;
    }

    public String getFile() {
      return this.file;
    }

    public double getLineCoverage() {
      return this.lineCoverage;
    }

    public double getBranchCoverage() {
      return this.branchCoverage;
    }
  }

  /**
   * return paginated coverage list.
   *
   * @param pageIndex page index
   * @param pageSize  page size
   * @return coverage list
   */
  @JavaScriptMethod
  public JSONObject getRecipeFiles(int pageIndex, int pageSize, TableSortInfo [] sortInfos) {
    List<FileCoverageStats> dataList = this.getDataSource().get(
        this.getParentAction().getName(), STORE_KEY_FILECOVERAGESTAT);

    if (sortInfos.length > 0) {
      Comparator<FileCoverageStats> comparator = this.getComparator(sortInfos[0]);

      for (int i = 1; i < sortInfos.length; i++) {
        comparator = comparator.thenComparing(this.getComparator(sortInfos[i]));
      }

      dataList.sort(comparator);
    }
    return getPagedDataList(pageIndex, pageSize, dataList);
  }

  private Comparator<FileCoverageStats> getComparator(TableSortInfo sortInfo) {
    Comparator<FileCoverageStats> comparator;

    switch (sortInfo.getField()) {
      case "file":
        comparator = Comparator.<FileCoverageStats, String>comparing(
            a -> a.getFile());
        break;
      case "lineCoverage":
        comparator = Comparator.<FileCoverageStats, Double>comparing(
            a -> a.getLineCoverage());
        break;
      case "branchCoverage":
        comparator = Comparator.<FileCoverageStats, Double>comparing(
            a -> a.getBranchCoverage());
        break;
      default:
        throw new IllegalArgumentException(
            String.format("unknown field for coverage table : %s", sortInfo.getField()));
    }

    if (sortInfo.getDir().equals("desc")) {
      comparator = comparator.reversed();
    }

    return comparator;
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
