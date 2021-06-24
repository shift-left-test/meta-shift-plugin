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
import com.lge.plugins.metashift.models.ComplexityData;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.persistence.DataSource;
import com.lge.plugins.metashift.ui.models.FileComplexitySortableItemList;
import com.lge.plugins.metashift.ui.models.SortableItemList;
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
 * MetaShift recipe's complexity detail view action class.
 */
public class RecipeComplexityAction
    extends RecipeActionChild {

  static final String STORE_KEY_COMPLEXITYLIST = "ComplexityList";
  static final String STORE_KEY_FILECOMPLEXITYSTAT = "FileComplexityStat";

  private long complexityLevel;

  /**
   * constructor.
   *
   * @param parent     parent action
   * @param listener   logger
   * @param dataSource datasource
   * @param recipe     recipe
   * @param metadata   metadata
   */
  public RecipeComplexityAction(
      RecipeAction parent, TaskListener listener, VirtualChannel channel,
      DataSource dataSource, Recipe recipe, JSONObject metadata) {
    super(parent);

    List<ComplexityData> complexityDataList =
        recipe.objects(ComplexityData.class).collect(Collectors.toList());

    HashMap<String, List<ComplexityData>> fileComplexityList = new HashMap<>();

    for (ComplexityData complexityData : complexityDataList) {
      String file = complexityData.getFile();

      if (!fileComplexityList.containsKey(file)) {
        fileComplexityList.put(file, new ArrayList<>());
      }

      fileComplexityList.get(file).add(complexityData);
    }

    FileComplexitySortableItemList fileComplexityStats = new FileComplexitySortableItemList();
    complexityLevel =
        this.getParentAction().getParentAction().getCriteria().getComplexityLevel();

    fileComplexityList.forEach((file, complexityList) -> {
      fileComplexityStats.addItem(file,
          complexityList.size(),
          complexityList.stream().filter(o -> o.getValue() >= complexityLevel).count()
      );

      try {
        this.saveFileContents(channel, metadata, file);
        dataSource.put(complexityList,
            this.getParentAction().getName(), file, STORE_KEY_COMPLEXITYLIST);
      } catch (IOException | InterruptedException e) {
        listener.getLogger().println(e.getMessage());
        e.printStackTrace(listener.getLogger());
      }
    });

    try {
      dataSource.put(fileComplexityStats,
          this.getParentAction().getName(), STORE_KEY_FILECOMPLEXITYSTAT);
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
    return "Complexity";
  }

  @Override
  public String getUrlName() {
    return "complexity";
  }

  @Override
  public String getScale() {
    Evaluator<?> evaluator = this.getParentAction().getMetrics().getComplexity();
    
    if (evaluator.isAvailable()) {
      return String.format("%d%%", (long) (evaluator.getRatio() * 100));
    }
    return "N/A";
  }

  @Override
  public JSONArray getStatistics() {
    Evaluator<?> evaluator = this.getParentAction().getMetrics().getComplexity();

    StatisticsItem[] result = new StatisticsItem[]{
        new StatisticsItem(
            "Complex",
            (int) (evaluator.getRatio() * 100),
            (int) evaluator.getNumerator(),
            "valid-bad"
        ),
        new StatisticsItem(
            "Normal",
            (int) ((1 - evaluator.getRatio()) * 100),
            (int) (evaluator.getDenominator() - evaluator.getNumerator()),
            "invalid"
        )
    };

    return JSONArray.fromObject(result);
  }

  /**
   * return paginated complexity list.
   *
   * @param pageIndex page index
   * @param pageSize  page size
   * @return complexity list
   */
  @JavaScriptMethod
  public JSONObject getRecipeFiles(int pageIndex, int pageSize,
      SortableItemList.SortInfo[] sortInfos) {
    FileComplexitySortableItemList dataList = this.getDataSource().get(
        this.getParentAction().getName(), STORE_KEY_FILECOMPLEXITYSTAT);

    if (dataList != null) {
      return dataList.sort(sortInfos).getPage(pageIndex, pageSize);
    } else {
      return null;
    }
  }

  /**
   * return file complexity detail.
   */
  @JavaScriptMethod
  public JSONObject getFileComplexityDetail(String codePath) {
    JSONObject result = new JSONObject();

    List<ComplexityData> dataList = this.getDataSource().get(
        this.getParentAction().getName(), codePath, STORE_KEY_COMPLEXITYLIST);

    result.put("complexityLevel", complexityLevel);
    result.put("dataList", dataList);
    result.put("content", this.readFileContents(codePath));

    return result;
  }
}
