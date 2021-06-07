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
import com.lge.plugins.metashift.models.PremirrorCacheData;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.persistence.DataSource;
import com.lge.plugins.metashift.ui.models.CacheTableItem;
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
 * MetaShift recipe's cache availability detail view action class.
 */
public class RecipePremirrorCacheAction
    extends RecipeActionChild {

  static final String STORE_KEY_CACHELIST = "PremirrorCacheList";

  /**
   * constructor.
   *
   * @param parent     parent action
   * @param listener   logger
   * @param dataSource datasource
   * @param recipe     recipe
   * @param metadata   metadata
   */
  public RecipePremirrorCacheAction(
      RecipeAction parent, TaskListener listener,
      DataSource dataSource, Recipe recipe, JSONObject metadata) {
    super(parent);

    List<CacheTableItem> cacheList = recipe.objects(PremirrorCacheData.class)
        .map(o -> new CacheTableItem(o)).collect(Collectors.toList());

    try {
      dataSource.put(cacheList, this.getParentAction().getName(), STORE_KEY_CACHELIST);
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
    return "Premirror Cache";
  }

  @Override
  public String getUrlName() {
    return "premirror_cache";
  }

  @Override
  public int getScale() {
    return (int) (this.getParentAction().getMetrics().getPremirrorCache().getRatio() * 100);
  }

  @Override
  public JSONArray getStatistics() {
    Evaluator<?> evaluator = this.getParentAction().getMetrics().getPremirrorCache();

    StatisticsItem [] result = new StatisticsItem [] {
      new StatisticsItem(
          "Cached",
          (int) (evaluator.getRatio() * 100),
          (int) evaluator.getNumerator(),
          "valid-good"
      ),
      new StatisticsItem(
          "Uncached",
          (int) ((1 - evaluator.getRatio()) * 100),
          (int) (evaluator.getDenominator() - evaluator.getNumerator()),
          "invalid"
      )
    };

    return JSONArray.fromObject(result);
  }
  
  /**
   * return paginated cache availability list.
   *
   * @param pageIndex page index
   * @param pageSize  page size
   * @return cache availability list
   */
  @JavaScriptMethod
  public JSONObject getRecipeCaches(int pageIndex, int pageSize, TableSortInfo [] sortInfos) {
    List<CacheTableItem> cacheList = this.getDataSource().get(
        this.getParentAction().getName(), STORE_KEY_CACHELIST);

    if (sortInfos.length > 0) {

      cacheList.sort(CacheTableItem.createComparator(sortInfos));
    }
    
    return TabulatorUtils.getPage(pageIndex, pageSize, cacheList);
  }
}
