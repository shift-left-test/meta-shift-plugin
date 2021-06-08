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

import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.RecipeViolationData;
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
 * MetaShift recipe's recipe violation detail view action class.
 */
public class RecipeRecipeViolationsAction extends RecipeActionChild {

  static final String STORE_KEY_RECIPEVIOLATIONLIST = "RecipeViolationList";
  static final String STORE_KEY_FILERECIPEVIOLATIONSTAT = "FileRecipeViolationStat";

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

    List<FileViolationTableItem> fileRecipeViolationStats = new ArrayList<>();

    fileRecipeViolationList.forEach((file, violationList) -> {
      fileRecipeViolationStats.add(new FileViolationTableItem(file,
          violationList.stream().filter(o -> o.getLevel().equals("MAJOR")).count(),
          violationList.stream().filter(o -> o.getLevel().equals("MINOR")).count(),
          violationList.stream().filter(o -> o.getLevel().equals("INFO")).count()
      ));
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
      dataSource.put(fileRecipeViolationStats,
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
  public int getScale() {
    return (int) (this.getParentAction().getMetrics().getRecipeViolations().getRatio() * 100);
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
   * return paginated recipe violation list.
   *
   * @param pageIndex page index
   * @param pageSize  page size
   * @return recipe violation list
   */
  @JavaScriptMethod
  public JSONObject getRecipeFiles(int pageIndex, int pageSize, TableSortInfo [] sortInfos)
      throws IOException {
    List<FileViolationTableItem> dataList = this.getDataSource().get(
        this.getParentAction().getName(), STORE_KEY_FILERECIPEVIOLATIONSTAT);

    if (sortInfos.length > 0) {
      dataList.sort(FileViolationTableItem.createComparator(sortInfos));
    }

    return TabulatorUtils.getPage(pageIndex, pageSize, dataList);
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
