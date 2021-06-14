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
import com.lge.plugins.metashift.models.TestData;
import com.lge.plugins.metashift.persistence.DataSource;
import com.lge.plugins.metashift.ui.models.StatisticsItem;
import com.lge.plugins.metashift.ui.models.TableSortInfo;
import com.lge.plugins.metashift.ui.models.TabulatorUtils;
import com.lge.plugins.metashift.ui.models.TestTableItem;
import hudson.model.TaskListener;
import hudson.remoting.VirtualChannel;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.bind.JavaScriptMethod;

/**
 * MetaShift recipe's test detail view action class.
 */
public class RecipeTestAction extends RecipeActionChild {

  static final String STORE_KEY_TESTLIST = "TestList";

  // statistics data
  private final long passedCount;
  private final long failedCount;
  private final long errorCount;
  private final long skippedCount;

  /**
   * constructor.
   *
   * @param parent     parent action
   * @param listener   logger
   * @param dataSource datasource
   * @param recipe     recipe
   * @param metadata   metadata
   */
  public RecipeTestAction(
      RecipeAction parent, TaskListener listener, VirtualChannel channel,
      DataSource dataSource, Recipe recipe, JSONObject metadata) {
    super(parent);

    List<TestTableItem> testList = recipe.objects(TestData.class)
        .map(TestTableItem::new).collect(Collectors.toList());

    passedCount = testList.stream().filter(o -> o.getStatus().equals("PASSED")).count();
    failedCount = testList.stream().filter(o -> o.getStatus().equals("FAILED")).count();
    errorCount = testList.stream().filter(o -> o.getStatus().equals("ERROR")).count();
    skippedCount = testList.stream().filter(o -> o.getStatus().equals("SKIPPED")).count();

    try {
      dataSource.put(testList, this.getParentAction().getName(), STORE_KEY_TESTLIST);
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
    return "Test";
  }

  @Override
  public String getUrlName() {
    return "test";
  }

  @Override
  public int getScale() {
    return (int) (this.getParentAction().getMetrics().getTest().getRatio() * 100);
  }

  @Override
  public JSONArray getStatistics() {
    long allCount = passedCount + failedCount + errorCount + skippedCount;

    StatisticsItem[] result = new StatisticsItem[]{
        new StatisticsItem(
            "Passed",
            allCount > 0 ? passedCount * 100 / allCount : 0,
            passedCount,
            "valid-good"
        ),
        new StatisticsItem(
            "Failed",
            allCount > 0 ? failedCount * 100 / allCount : 0,
            failedCount,
            "valid-bad"
        ),
        new StatisticsItem(
            "Error",
            allCount > 0 ? errorCount * 100 / allCount : 0,
            errorCount,
            "valid-error"
        ),
        new StatisticsItem(
            "Skipped",
            allCount > 0 ? skippedCount * 100 / allCount : 0,
            skippedCount,
            "invalid"
        )
    };

    return JSONArray.fromObject(result);
  }

  /**
   * return paginated test list.
   *
   * @param pageIndex page index
   * @param pageSize  page size
   * @return test list
   */
  @JavaScriptMethod
  public JSONObject getRecipeTests(int pageIndex, int pageSize, TableSortInfo[] sortInfos) {
    List<TestTableItem> testDataList = this.getDataSource().get(
        this.getParentAction().getName(), STORE_KEY_TESTLIST);

    if (sortInfos.length > 0) {
      testDataList.sort(TestTableItem.createComparator(sortInfos));
    }
    return TabulatorUtils.getPage(pageIndex, pageSize, testDataList);
  }
}