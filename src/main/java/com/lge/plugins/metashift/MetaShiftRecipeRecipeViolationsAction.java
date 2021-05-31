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
import com.lge.plugins.metashift.utils.TableSortInfo;
import hudson.model.TaskListener;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.bind.JavaScriptMethod;

/**
 * MetaShift recipe's recipe violation detail view action class.
 */
public class MetaShiftRecipeRecipeViolationsAction extends MetaShiftRecipeActionChild {

  static final String STORE_KEY_RECIPEVIOLATIONLIST = "RecipeViolationList";
  static final String STORE_KEY_FILERECIPEVIOLATIONSTAT = "FileRecipeViolationStat";

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

    HashMap<String, List<RecipeViolationData>> fileRecipeViolationList = new HashMap<>();

    for (RecipeViolationData recipeViolationData : recipeViolationList) {
      String file = recipeViolationData.getFile();

      if (!fileRecipeViolationList.containsKey(file)) {
        fileRecipeViolationList.put(file, new ArrayList<>());
      }

      fileRecipeViolationList.get(file).add(recipeViolationData);
    }

    List<FileRecipeViolationStats> fileRecipeViolationStats = new ArrayList<>();

    fileRecipeViolationList.forEach((file, violationList) -> {
      fileRecipeViolationStats.add(new FileRecipeViolationStats(file,
          violationList.stream().filter(o -> o.getLevel().equals("MAJOR")).count(),
          violationList.stream().filter(o -> o.getLevel().equals("MINOR")).count(),
          violationList.stream().filter(o -> o.getLevel().equals("INFO")).count()
      ));
      try {
        this.saveFileContents(metadata, file);
        dataSource.put(violationList,
            this.getParentAction().getName(), file, STORE_KEY_RECIPEVIOLATIONLIST);
      } catch (IOException e) {
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

  /**
   * count for severity.
   */
  public static class FileRecipeViolationStats implements Serializable {

    String file;
    private long info;
    private long minor;
    private long major;
    
    /**
     * consttructor.
     */
    public FileRecipeViolationStats(String file, long major, long minor, long info) {
      this.file = file;
      this.major = major;
      this.minor = minor;
      this.info = info;
    }

    public String getFile() {
      return this.file;
    }

    public long getInfo() {
      return this.info;
    }

    public long getMinor() {
      return this.minor;
    }

    public long getMajor() {
      return this.major;
    }
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
    List<FileRecipeViolationStats> dataList = this.getDataSource().get(
        this.getParentAction().getName(), STORE_KEY_FILERECIPEVIOLATIONSTAT);

    if (sortInfos.length > 0) {
      Comparator<FileRecipeViolationStats> comparator = this.getComparator(sortInfos[0]);

      for (int i = 1; i < sortInfos.length; i++) {
        comparator = comparator.thenComparing(this.getComparator(sortInfos[i]));
      }

      dataList.sort(comparator);
    }

    return getPagedDataList(pageIndex, pageSize, dataList);
  }

  private Comparator<FileRecipeViolationStats> getComparator(TableSortInfo sortInfo) {
    Comparator<FileRecipeViolationStats> comparator;

    switch (sortInfo.getField()) {
      case "file":
        comparator = Comparator.<FileRecipeViolationStats, String>comparing(
            a -> a.getFile());
        break;
      case "major":
        comparator = Comparator.<FileRecipeViolationStats, Long>comparing(
            a -> a.getMajor());
        break;
      case "minor":
        comparator = Comparator.<FileRecipeViolationStats, Long>comparing(
            a -> a.getMinor());
        break;
      case "info":
        comparator = Comparator.<FileRecipeViolationStats, Long>comparing(
            a -> a.getInfo());
        break;
      default:
        throw new IllegalArgumentException(
            String.format("unknown field for recipe violations table : %s", sortInfo.getField()));
    }

    if (sortInfo.getDir().equals("desc")) {
      comparator = comparator.reversed();
    }

    return comparator;
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
