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
import com.lge.plugins.metashift.models.CodeViolationData;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.persistence.DataSource;
import com.lge.plugins.metashift.ui.models.StatisticsItem;
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
  private final long majorCount;
  private final long minorCount;
  private final long infoCount;

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
      DataSource dataSource, Recipe recipe, JSONObject metadata) {
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

    HashMap<String, List<CodeViolationData>> fileCodeViolationList = new HashMap<>();

    for (CodeViolationData codeViolationData : codeViolationDataList) {
      String file = codeViolationData.getFile();

      if (!fileCodeViolationList.containsKey(file)) {
        fileCodeViolationList.put(file, new ArrayList<>());
      }

      fileCodeViolationList.get(file).add(codeViolationData);
    }

    JSONArray fileCodeViolationArray = new JSONArray();

    fileCodeViolationList.forEach((file, violationList) -> {
      JSONObject fileViolation = new JSONObject();
      fileViolation.put("file", file);
      fileViolation.put("major",
          violationList.stream().filter(o -> o.getLevel().equals("MAJOR")).count());
      fileViolation.put("minor",
          violationList.stream().filter(o -> o.getLevel().equals("MINOR")).count());
      fileViolation.put("info",
          violationList.stream().filter(o -> o.getLevel().equals("INFO")).count());
      fileCodeViolationArray.add(fileViolation);

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
      dataSource.put(fileCodeViolationArray,
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
  public String getScale() {
    Evaluator<?> evaluator = this.getParentAction().getMetrics().getCodeViolations();
    if (evaluator.isAvailable()) {
      return String.format("%.2f", evaluator.getRatio());
    } else {
      return "N/A";
    }
  }

  @Override
  public JSONObject getMetricStatistics() {
    JSONObject result = this.getParentAction().getMetricStatistics()
        .getCodeViolations().toJsonObject();

    Evaluator<?> evaluator = this.getParentAction().getMetrics().getCodeViolations();

    result.put("scale", evaluator.getRatio());
    result.put("available", evaluator.isAvailable());
    result.put("percent", false);
    
    return result;
  }

  @Override
  public JSONArray getStatistics() {
    long allCount = majorCount + minorCount + infoCount;

    StatisticsItem[] result = new StatisticsItem[]{
        new StatisticsItem(
            "Major",
            allCount > 0 ? majorCount * 100 / allCount : 0,
            majorCount,
            "major"
        ),
        new StatisticsItem(
            "Minor",
            allCount > 0 ? minorCount * 100 / allCount : 0,
            minorCount,
            "minor"
        ),
        new StatisticsItem(
            "Info",
            allCount > 0 ? infoCount * 100 / allCount : 0,
            infoCount,
            "informational"
        )
    };

    return JSONArray.fromObject(result);
  }

  /**
   * return paginated code violation list.
   *
   * @return code violation list
   * @throws IOException invalid recipe uri
   */
  @JavaScriptMethod
  public JSONArray getRecipeFiles() throws IOException {
    return this.getDataSource().get(
        this.getParentAction().getName(), STORE_KEY_FILECODEVIOLATIONSTAT);
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
