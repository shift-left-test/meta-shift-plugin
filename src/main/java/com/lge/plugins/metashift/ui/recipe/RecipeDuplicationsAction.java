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
import com.lge.plugins.metashift.models.DuplicationData;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.persistence.DataSource;
import com.lge.plugins.metashift.ui.models.FileDuplicationTableItem;
import com.lge.plugins.metashift.ui.models.StatisticsItem;
import com.lge.plugins.metashift.ui.models.TableSortInfo;
import com.lge.plugins.metashift.ui.models.TabulatorUtils;
import hudson.model.TaskListener;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.bind.JavaScriptMethod;

/**
 * MetaShift recipe's duplication detail view action class.
 */
public class RecipeDuplicationsAction
    extends RecipeActionChild {

  static final String STORE_KEY_DUPLICATIONLIST = "DuplicationList";

  /**
   * constructor.
   *
   * @param parent     parent action
   * @param listener   logger
   * @param dataSource datasource
   * @param recipe     recipe
   * @param metadata   metadata
   */
  public RecipeDuplicationsAction(
      RecipeAction parent, TaskListener listener,
      DataSource dataSource, Recipe recipe, JSONObject metadata) {
    super(parent);

    List<FileDuplicationTableItem> duplicationList = recipe.objects(DuplicationData.class)
        .map(o -> new FileDuplicationTableItem(o)).collect(Collectors.toList());

    try {
      dataSource.put(duplicationList, this.getParentAction().getName(), STORE_KEY_DUPLICATIONLIST);
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
    return "Duplications";
  }

  @Override
  public String getUrlName() {
    return "duplications";
  }

  @Override
  public int getScale() {
    return (int) (this.getParentAction().getMetrics().getDuplications().getRatio() * 100);
  }

  @Override
  public JSONArray getStatistics() {
    Evaluator<?> evaluator = this.getParentAction().getMetrics().getDuplications();

    StatisticsItem [] result = new StatisticsItem [] {
      new StatisticsItem(
          "Duplicated",
          (int) (evaluator.getRatio() * 100),
          (int) evaluator.getNumerator(),
          "valid-bad"
      ),
      new StatisticsItem(
          "Code",
          (int) ((1 - evaluator.getRatio()) * 100),
          (int) (evaluator.getDenominator() - evaluator.getNumerator()),
          "invalid"
      )
    };

    return JSONArray.fromObject(result);
  }
  
  /**
   * return paginated duplication list.
   *
   * @param pageIndex page index
   * @param pageSize  page size
   * @return duplication list
   */
  @JavaScriptMethod
  public JSONObject getRecipeFiles(int pageIndex, int pageSize, TableSortInfo [] sortInfos) {
    if (getParentAction().getMetrics().getDuplications().isAvailable()) {
      List<FileDuplicationTableItem> duplicationDataList = this.getDataSource().get(
          this.getParentAction().getName(), STORE_KEY_DUPLICATIONLIST);

      if (sortInfos.length > 0) {
        duplicationDataList.sort(FileDuplicationTableItem.createComparator(sortInfos));
      }

      return TabulatorUtils.getPage(pageIndex, pageSize, duplicationDataList);
    } else {
      return null;
    }
  }
}
