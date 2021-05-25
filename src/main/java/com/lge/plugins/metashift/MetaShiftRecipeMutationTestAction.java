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
import com.lge.plugins.metashift.models.MutationTestData;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.persistence.DataSource;
import hudson.model.TaskListener;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.bind.JavaScriptMethod;

/**
 * MetaShift recipe's mutation test detail view action class.
 */
public class MetaShiftRecipeMutationTestAction extends MetaShiftRecipeActionChild {

  static final String STORE_KEY_MUTATIONTESTLIST = "MutationTestList";

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
  public MetaShiftRecipeMutationTestAction(
      MetaShiftRecipeAction parent, TaskListener listener,
      Criteria criteria, DataSource dataSource, Recipe recipe, JSONObject metadata) {
    super(parent);

    List<MutationTestData> cacheList =
        recipe.objects(MutationTestData.class).collect(Collectors.toList());

    try {
      dataSource.put(cacheList, this.getParentAction().getName(), STORE_KEY_MUTATIONTESTLIST);
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
    return "Mutation Test";
  }

  @Override
  public String getUrlName() {
    return "mutation_test";
  }

  /**
   * return paginated mutation test list.
   *
   * @param pageIndex page index
   * @param pageSize  page size
   * @return mutation test list
   */
  @JavaScriptMethod
  public JSONObject getRecipeMutationTests(int pageIndex, int pageSize) {
    if (getParentAction().getMetrics().getMutationTest().isAvailable()) {
      List<MutationTestData> mutationTestDataList = this.getDataSource().get(
          this.getParentAction().getName(), STORE_KEY_MUTATIONTESTLIST);

      return getPagedDataList(pageIndex, pageSize, mutationTestDataList);
    } else {
      return null;
    }
  }

  /**
   * return file mutation test detail.
   */
  @JavaScriptMethod
  public JSONObject getFileMutationTestDetail(String recipePath) {
    JSONObject result = new JSONObject();

    List<MutationTestData> mutationTestDataList = this.getDataSource().get(
        this.getParentAction().getName(), STORE_KEY_MUTATIONTESTLIST);

    List<MutationTestData> dataList =
        mutationTestDataList.stream().filter(o -> o.getFile().equals(recipePath))
            .collect(Collectors.toList());

    result.put("dataList", dataList);
    result.put("content", this.readFileContents(recipePath));

    return result;
  }
}
