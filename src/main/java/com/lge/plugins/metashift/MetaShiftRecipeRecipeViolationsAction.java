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
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.RecipeViolationData;
import com.lge.plugins.metashift.persistence.DataSource;
import hudson.model.TaskListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.bind.JavaScriptMethod;

/**
 * MetaShift recipe's recipe violation detail view action class.
 */
public class MetaShiftRecipeRecipeViolationsAction extends MetaShiftRecipeActionChild {

  static final String STORE_KEY_RECIPEVIOLATIONLIST = "RecipeViolationList";

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
  public MetaShiftRecipeRecipeViolationsAction(
      MetaShiftRecipeAction parent, TaskListener listener,
      Criteria criteria, DataSource dataSource, Recipe recipe, JSONObject metadata) {
    super(parent);

    List<RecipeViolationData> recipeViolationList =
        recipe.objects(RecipeViolationData.class).collect(Collectors.toList());

    try {
      dataSource.put(recipeViolationList,
          this.getParentAction().getName(), STORE_KEY_RECIPEVIOLATIONLIST);

      for (RecipeViolationData data : recipeViolationList) {
        this.saveFileContents(metadata, data.getFile());
      }
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

  /**
   * count for severity.
   */
  public static class RecipeViolationStats {

    String file;
    int info = 0;
    int minor = 0;
    int major = 0;
    int total = 0;

    public RecipeViolationStats(String file) {
      this.file = file;
    }

    public String getFile() {
      return this.file;
    }

    public int getInfo() {
      return this.info;
    }

    public int getMinor() {
      return this.minor;
    }

    public int getMajor() {
      return this.major;
    }
  }

  /**
   * return paginated recipe violation list.
   *
   * @param pageIndex page index
   * @param pageSize  page size
   * @return recipe violation list
   * @throws IOException invalid recipe uri
   */
  @JavaScriptMethod
  public JSONObject getRecipeFiles(int pageIndex, int pageSize) throws IOException {
    if (getParentAction().getMetrics().getCodeViolations().isAvailable()) {
      List<RecipeViolationData> recipeViolationDataList = this.getDataSource().get(
          this.getParentAction().getName(), STORE_KEY_RECIPEVIOLATIONLIST);

      Map<String, RecipeViolationStats> recipeFilesMap = new HashMap<>();

      for (RecipeViolationData violationData : recipeViolationDataList) {
        if (!recipeFilesMap.containsKey(violationData.getFile())) {
          recipeFilesMap.put(violationData.getFile(),
              new RecipeViolationStats(violationData.getFile()));
        }
        RecipeViolationStats stats = recipeFilesMap.get(violationData.getFile());
        switch (violationData.getLevel()) {
          case "MAJOR":
            stats.major++;
            break;
          case "MINOR":
            stats.minor++;
            break;
          case "INFO":
            stats.info++;
            break;
          default:
            // TODO: invalid level case?
            // ignore
            break;
        }
        stats.total++;
      }

      return getPagedDataList(pageIndex, pageSize, new ArrayList<>(recipeFilesMap.values()));
    } else {
      return null;
    }
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

    List<RecipeViolationData> recipeViolationDataList = this.getDataSource().get(
        this.getParentAction().getName(), STORE_KEY_RECIPEVIOLATIONLIST);

    List<RecipeViolationData> violationDataList =
        recipeViolationDataList.stream().filter(o -> o.getFile().equals(recipePath))
            .collect(Collectors.toList());

    result.put("dataList", violationDataList);
    result.put("content", this.readFileContents(recipePath));

    return result;
  }
}
