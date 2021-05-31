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
import com.lge.plugins.metashift.models.TestData;
import com.lge.plugins.metashift.persistence.DataSource;
import com.lge.plugins.metashift.utils.TableSortInfo;
import hudson.model.TaskListener;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.bind.JavaScriptMethod;

/**
 * MetaShift recipe's test detail view action class.
 */
public class MetaShiftRecipeTestAction extends MetaShiftRecipeActionChild {

  static final String STORE_KEY_TESTLIST = "TestList";
  static final String STORE_KEY_PASSRATE = "PassRate";
  static final String STORE_KEY_FAILRATE = "FailRate";
  static final String STORE_KEY_ERRORRATE = "ErrorRate";

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
  public MetaShiftRecipeTestAction(
      MetaShiftRecipeAction parent, TaskListener listener,
      Criteria criteria, DataSource dataSource, Recipe recipe, JSONObject metadata) {
    super(parent);

    List<TestData> testList = recipe.objects(TestData.class).collect(Collectors.toList());
    long totalTest = testList.size();

    double passRate = (double) testList.stream().filter(o -> o.getStatus().equals("PASSED"))
        .count() / (double) totalTest;
    double failRate = (double) testList.stream().filter(o -> o.getStatus().equals("FAILED"))
        .count() / (double) totalTest;
    double errorRate = (double) testList.stream().filter(o -> o.getStatus().equals("ERROR"))
        .count() / (double) totalTest;

    try {
      dataSource.put(testList, this.getParentAction().getName(), STORE_KEY_TESTLIST);
      dataSource.put(passRate, this.getParentAction().getName(), STORE_KEY_PASSRATE);
      dataSource.put(failRate, this.getParentAction().getName(), STORE_KEY_FAILRATE);
      dataSource.put(errorRate, this.getParentAction().getName(), STORE_KEY_ERRORRATE);
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

  public double getPassRate() {
    return this.getDataSource().get(this.getParentAction().getName(), STORE_KEY_PASSRATE);
  }

  public double getFailRate() {
    return this.getDataSource().get(this.getParentAction().getName(), STORE_KEY_FAILRATE);
  }

  public double getErrorRate() {
    return this.getDataSource().get(this.getParentAction().getName(), STORE_KEY_ERRORRATE);
  }
  
  /**
   * return paginated test list.
   *
   * @param pageIndex page index
   * @param pageSize  page size
   * @return test list
   */
  @JavaScriptMethod
  public JSONObject getRecipeTests(int pageIndex, int pageSize, TableSortInfo [] sortInfos) {
    if (getParentAction().getMetrics().getTest().isAvailable()) {
      List<TestData> testDataList = this.getDataSource().get(
          this.getParentAction().getName(), STORE_KEY_TESTLIST);

      if (sortInfos.length > 0) {
        Comparator<TestData> comparator = this.getComparator(sortInfos[0]);

        for (int i = 1; i < sortInfos.length; i++) {
          comparator = comparator.thenComparing(this.getComparator(sortInfos[i]));
        }

        testDataList.sort(comparator);
      }
      return getPagedDataList(pageIndex, pageSize, testDataList);
    } else {
      return null;
    }
  }

  private Comparator<TestData> getComparator(TableSortInfo sortInfo) {
    Comparator<TestData> comparator;

    switch (sortInfo.getField()) {
      case "suite":
        comparator = Comparator.<TestData, String>comparing(
            a -> a.getSuite());
        break;
      case "name":
        comparator = Comparator.<TestData, String>comparing(
            a -> a.getName());
        break;
      case "status":
        comparator = Comparator.<TestData, String>comparing(
            a -> a.getStatus());
        break;
      case "message":
        comparator = Comparator.<TestData, String>comparing(
            a -> a.getMessage());
        break;
      default:
        throw new IllegalArgumentException(
          String.format("unknown field for unit test table : %s", sortInfo.getField()));
    }

    if (sortInfo.getDir().equals("desc")) {
      comparator = comparator.reversed();
    }

    return comparator;
  }
}
