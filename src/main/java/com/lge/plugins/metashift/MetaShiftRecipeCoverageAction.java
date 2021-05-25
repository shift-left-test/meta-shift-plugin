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
import hudson.model.TaskListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.bind.JavaScriptMethod;

/**
 * MetaShift recipe's coverage detail view action class.
 */
public class MetaShiftRecipeCoverageAction extends MetaShiftRecipeActionChild {

  static final String STORE_KEY_COVERAGELIST = "CoverageList";

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

    List<CoverageData> coverageList =
        recipe.objects(CoverageData.class).collect(Collectors.toList());

    try {
      dataSource.put(coverageList, this.getParentAction().getName(), STORE_KEY_COVERAGELIST);
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
  public static class FileCoverage {

    private final String file;
    private final Set<Long> lines;
    private final Set<Long> coveredLines;
    private final Set<LineIndex> indexs;
    private final Set<LineIndex> coveredIndexs;

    /**
     * constructor.
     */
    public FileCoverage(String file) {
      this.file = file;
      this.lines = new HashSet<>();
      this.coveredLines = new HashSet<>();
      this.indexs = new HashSet<>();
      this.coveredIndexs = new HashSet<>();
    }

    public String getFile() {
      return this.file;
    }

    /**
     * return line coverage.
     *
     * @return line coverage
     */
    public double getLineCoverage() {
      if (this.lines.isEmpty()) {
        return 0.0;
      }

      return (double) this.coveredLines.size() / (double) this.lines.size();
    }

    /**
     * return branch coverage.
     *
     * @return branch coverage
     */
    public double getBranchCoverage() {
      if (this.indexs.isEmpty()) {
        return 0.0;
      }

      return (double) this.coveredIndexs.size() / (double) this.indexs.size();
    }

    /**
     * add coverage info.
     *
     * @param line    line
     * @param index   index
     * @param covered is covered
     */
    public void addCoveredInfo(long line, long index, boolean covered) {
      LineIndex lineIndex = new LineIndex(line, index);

      this.lines.add(line);
      this.indexs.add(lineIndex);
      if (covered) {
        this.coveredLines.add(line);
        this.coveredIndexs.add(lineIndex);
      }
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
  public JSONObject getRecipeFiles(int pageIndex, int pageSize) {
    if (getParentAction().getMetrics().getCoverage().isAvailable()) {
      List<CoverageData> coverageDataList = this.getDataSource().get(
          this.getParentAction().getName(), STORE_KEY_COVERAGELIST);

      HashMap<String, FileCoverage> fileInfo = new HashMap<>();

      for (CoverageData coverageData : coverageDataList) {
        String file = coverageData.getFile();
        long index = coverageData.getIndex();
        long line = coverageData.getLine();

        if (!fileInfo.containsKey(file)) {
          fileInfo.put(file, new FileCoverage(file));
        }
        fileInfo.get(file).addCoveredInfo(line, index, coverageData.isCovered());
      }

      return getPagedDataList(pageIndex, pageSize, new ArrayList<>(fileInfo.values()));
    } else {
      return null;
    }
  }

  /**
   * return file coverage info.
   */
  @JavaScriptMethod
  public JSONObject getFileCoverageDetail(String codePath) {
    JSONObject result = new JSONObject();

    List<CoverageData> coverageDataList = this.getDataSource().get(
        this.getParentAction().getName(), STORE_KEY_COVERAGELIST);

    List<CoverageData> dataList =
        coverageDataList.stream().filter(o -> o.getFile().equals(codePath))
            .collect(Collectors.toList());

    result.put("dataList", dataList);
    result.put("content", this.readFileContents(codePath));

    return result;
  }
}
