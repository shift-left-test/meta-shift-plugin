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
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.RecipeViolationData;
import com.lge.plugins.metashift.persistence.DataSource;
import com.lge.plugins.metashift.ui.models.StatisticsItemList;
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
 * MetaShift recipe's recipe violation detail view action class.
 */
public class RecipeRecipeViolationsAction extends RecipeActionChild {

  static final String STORE_KEY_RECIPEVIOLATIONLIST = "RecipeViolationList";
  static final String STORE_KEY_FILERECIPEVIOLATIONSTAT = "FileRecipeViolationStat";

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
  public RecipeRecipeViolationsAction(
      RecipeAction parent, TaskListener listener, VirtualChannel channel,
      DataSource dataSource, Recipe recipe, JSONObject metadata) {
    super(parent);

    List<RecipeViolationData> recipeViolationList =
        recipe.objects(RecipeViolationData.class).collect(Collectors.toList());

    // get data for statistics
    this.majorCount = recipeViolationList.stream().filter(
        o -> o.getLevel().equals("MAJOR")).count();
    this.minorCount = recipeViolationList.stream().filter(
        o -> o.getLevel().equals("MINOR")).count();
    this.infoCount = recipeViolationList.stream().filter(
        o -> o.getLevel().equals("INFO")).count();

    HashMap<String, List<RecipeViolationData>> fileRecipeViolationList = new HashMap<>();

    for (RecipeViolationData recipeViolationData : recipeViolationList) {
      String file = recipeViolationData.getFile();

      if (!fileRecipeViolationList.containsKey(file)) {
        fileRecipeViolationList.put(file, new ArrayList<>());
      }

      fileRecipeViolationList.get(file).add(recipeViolationData);
    }

    JSONArray fileRecipeViolationArray = new JSONArray();

    fileRecipeViolationList.forEach((file, violationList) -> {
      JSONObject fileViolation = new JSONObject();
      fileViolation.put("file", file);
      fileViolation.put("major",
          violationList.stream().filter(o -> o.getLevel().equals("MAJOR")).count());
      fileViolation.put("minor",
          violationList.stream().filter(o -> o.getLevel().equals("MINOR")).count());
      fileViolation.put("info",
          violationList.stream().filter(o -> o.getLevel().equals("INFO")).count());
      fileRecipeViolationArray.add(fileViolation);

      try {
        this.saveFileContents(channel, metadata, file);
        dataSource.put(violationList,
            this.getParentAction().getName(), file, STORE_KEY_RECIPEVIOLATIONLIST);
      } catch (IOException | InterruptedException e) {
        listener.getLogger().println(e.getMessage());
        e.printStackTrace(listener.getLogger());
      }
    });

    try {
      dataSource.put(fileRecipeViolationArray,
          this.getParentAction().getName(), STORE_KEY_FILERECIPEVIOLATIONSTAT);
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
    return "Recipe Violations";
  }

  @Override
  public String getUrlName() {
    return "recipe_violations";
  }

  @Override
  public String getScale() {
    Evaluator<?> evaluator = this.getParentAction().getMetrics().getRecipeViolations();
    if (evaluator.isAvailable()) {
      return String.format("%.2f", evaluator.getRatio());
    } else {
      return "N/A";
    }
  }

  @Override
  public JSONObject getMetricStatistics() {
    JSONObject result = this.getParentAction().getMetricStatistics()
        .getRecipeViolations().toJsonObject();

    Evaluator<?> evaluator = this.getParentAction().getMetrics().getRecipeViolations();

    result.put("scale", evaluator.getRatio());
    result.put("available", evaluator.isAvailable());
    result.put("percent", false);
    
    return result;
  }

  @Override
  public JSONArray getStatistics() {
    long allCount = majorCount + minorCount + infoCount;

    StatisticsItemList stats = new StatisticsItemList();
    stats.addItem("Major", "major",
        allCount > 0 ? majorCount * 100 / allCount : 0,
        majorCount);
    stats.addItem("Minor", "minor",
        allCount > 0 ? minorCount * 100 / allCount : 0,
        minorCount);
    stats.addItem("Info", "informational",
        allCount > 0 ? infoCount * 100 / allCount : 0,
        infoCount);

    return stats.toJsonArray();
  }

  /**
   * return paginated recipe violation list.
   *
   * @return recipe violation list
   */
  @JavaScriptMethod
  public JSONArray getRecipeFiles() throws IOException {
    JSONArray dataList = this.getDataSource().get(
        this.getParentAction().getName(), STORE_KEY_FILERECIPEVIOLATIONSTAT);
    return dataList;
  }

  /**
   * return file contents and violation list for that file.
   *
   * @param recipePath file path
   * @return json object
   */
  @JavaScriptMethod
  public JSONObject getFileRecipeViolationDetail(String recipePath) {
    JSONObject result = new JSONObject();

    List<RecipeViolationData> violationDataList = this.getDataSource().get(
        this.getParentAction().getName(), recipePath, STORE_KEY_RECIPEVIOLATIONLIST);

    result.put("dataList", violationDataList);
    result.put("content", this.readFileContents(recipePath));

    return result;
  }
}
