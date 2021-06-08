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

import com.lge.plugins.metashift.models.CodeViolationData;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.persistence.DataSource;
import com.lge.plugins.metashift.ui.models.FileViolationTableItem;
import com.lge.plugins.metashift.ui.models.StatisticsItem;
import com.lge.plugins.metashift.ui.models.TableSortInfo;
import com.lge.plugins.metashift.ui.models.TabulatorUtils;
import hudson.model.TaskListener;
import hudson.remoting.VirtualChannel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.bind.JavaScriptMethod;

/**
 * MetaShift recipe's code violation detail view action class.
 */
public class RecipeCodeViolationsAction
    extends RecipeActionChild {

  static final String STORE_KEY_CODEVIOLATIONLIST = "CodeViolationList";
  static final String STORE_KEY_FILECODEVIOLATIONSTAT = "FileCodeViolationStat";

  // statistics data
  private long majorCount;
  private long minorCount;
  private long infoCount;

  /**
   * constructor.
   *
   * @param parent     parent action
   * @param listener   logger
   * @param dataSource datasource
   * @param recipe     recipe
   * @param metadata   metadata
   */
  public RecipeCodeViolationsAction(
      RecipeAction parent, TaskListener listener, VirtualChannel channel,
      DataSource dataSource, Recipe recipe, JSONObject metadata)
      throws IOException {
    super(parent);

    List<CodeViolationData> codeViolationDataList =
        recipe.objects(CodeViolationData.class).collect(Collectors.toList());
    
    // get data for statistics
    this.majorCount = codeViolationDataList.stream().filter(
        o -> o.getLevel().equals("MAJOR")).count();
    this.minorCount = codeViolationDataList.stream().filter(
        o -> o.getLevel().equals("MINOR")).count();
    this.infoCount = codeViolationDataList.stream().filter(
        o -> o.getLevel().equals("INFO")).count();

    HashMap<String, List<CodeViolationData>> fileCodeViloationList = new HashMap<>();

    for (CodeViolationData codeViolationData : codeViolationDataList) {
      String file = codeViolationData.getFile();

      if (!fileCodeViloationList.containsKey(file)) {
        fileCodeViloationList.put(file, new ArrayList<>());
      }

      fileCodeViloationList.get(file).add(codeViolationData);
    }

    List<FileViolationTableItem> fileCodeViolationStats = new ArrayList<>();

    fileCodeViloationList.forEach((file, violationList) -> {
      fileCodeViolationStats.add(new FileViolationTableItem(file,
          violationList.stream().filter(o -> o.getLevel().equals("MAJOR")).count(),
          violationList.stream().filter(o -> o.getLevel().equals("MINOR")).count(),
          violationList.stream().filter(o -> o.getLevel().equals("INFO")).count()
      ));
      try {
        this.saveFileContents(channel, metadata, file);
        dataSource.put(violationList,
            this.getParentAction().getName(), file, STORE_KEY_CODEVIOLATIONLIST);
      } catch (IOException | InterruptedException e) {
        listener.getLogger().println(e.getMessage());
        e.printStackTrace(listener.getLogger());
      }
    });

    try {
      dataSource.put(fileCodeViolationStats,
          this.getParentAction().getName(), STORE_KEY_FILECODEVIOLATIONSTAT);
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
    return "Code Violations";
  }

  @Override
  public String getUrlName() {
    return "code_violations";
  }

  @Override
  public int getScale() {
    return (int) (this.getParentAction().getMetrics().getCodeViolations().getRatio() * 100);
  }

  @Override
  public JSONArray getStatistics() {
    long allCount = majorCount + minorCount + infoCount;

    StatisticsItem [] result = new StatisticsItem [] {
      new StatisticsItem(
          "Major",
          allCount > 0 ? (int) majorCount * 100 / allCount : 0,
          (int) majorCount,
          "major"
      ),
      new StatisticsItem(
          "Minor",
          allCount > 0 ? (int) minorCount * 100 / allCount : 0,
          (int) minorCount,
          "minor"
      ),
      new StatisticsItem(
          "Info",
          allCount > 0 ? (int) infoCount * 100 / allCount : 0,
          (int) infoCount,
          "informational"
      )
    };

    return JSONArray.fromObject(result);
  }
  
  /**
   * return paginated code violation list.
   *
   * @param pageIndex page index
   * @param pageSize  page size
   * @return code violation list
   * @throws IOException invalid recipe uri
   */
  @JavaScriptMethod
  public JSONObject getRecipeFiles(int pageIndex, int pageSize, TableSortInfo [] sortInfos)
      throws IOException {
    List<FileViolationTableItem> dataList = this.getDataSource().get(
        this.getParentAction().getName(), STORE_KEY_FILECODEVIOLATIONSTAT);

    if (sortInfos.length > 0) {
      dataList.sort(FileViolationTableItem.createComparator(sortInfos));
    }
    return TabulatorUtils.getPage(pageIndex, pageSize, dataList);
  }

  /**
   * return file code violation detail.
   */
  @JavaScriptMethod
  public JSONObject getFileCodeViolationDetail(String codePath) {
    JSONObject result = new JSONObject();

    List<CodeViolationData> codeViolationDataList = this.getDataSource().get(
        this.getParentAction().getName(), codePath, STORE_KEY_CODEVIOLATIONLIST);

    result.put("dataList", codeViolationDataList);
    result.put("content", this.readFileContents(codePath));

    return result;
  }
}
