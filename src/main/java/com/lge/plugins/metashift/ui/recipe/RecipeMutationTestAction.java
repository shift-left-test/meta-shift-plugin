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

import com.lge.plugins.metashift.models.MutationTestData;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.persistence.DataSource;
import com.lge.plugins.metashift.ui.models.FileMutationTestSortableItemList;
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
 * MetaShift recipe's mutation test detail view action class.
 */
public class RecipeMutationTestAction extends RecipeActionChild {

  static final String STORE_KEY_MUTATIONTESTLIST = "MutationTestList";
  static final String STORE_KEY_FILEMUTATIONTESTSTAT = "FileMutationTestStat";

  // statistics data
  private final long killedCount;
  private final long survivedCount;
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
  public RecipeMutationTestAction(
      RecipeAction parent, TaskListener listener, VirtualChannel channel,
      DataSource dataSource, Recipe recipe, JSONObject metadata) {
    super(parent);

    List<MutationTestData> mutationTestList =
        recipe.objects(MutationTestData.class).collect(Collectors.toList());

    // get data for statistics
    this.killedCount = mutationTestList.stream().filter(
        o -> o.getStatus().equals("KILLED")).count();
    this.survivedCount = mutationTestList.stream().filter(
        o -> o.getStatus().equals("SURVIVED")).count();
    this.skippedCount = mutationTestList.stream().filter(
        o -> o.getStatus().equals("SKIPPED")).count();

    HashMap<String, List<MutationTestData>> fileMutationTestList = new HashMap<>();

    for (MutationTestData mutationTestData : mutationTestList) {
      String file = mutationTestData.getFile();

      if (!fileMutationTestList.containsKey(file)) {
        fileMutationTestList.put(file, new ArrayList<>());
      }

      fileMutationTestList.get(file).add(mutationTestData);
    }

    FileMutationTestSortableItemList fileMutationTestStats = new FileMutationTestSortableItemList();

    // TODO: need to check that skipped is 'ERROR' status.
    fileMutationTestList.forEach((file, testList) -> {
      fileMutationTestStats.addItem(file,
          testList.stream().filter(o -> o.getStatus().equals("KILLED")).count(),
          testList.stream().filter(o -> o.getStatus().equals("SURVIVED")).count(),
          testList.stream().filter(o -> o.getStatus().equals("ERROR")).count()
      );
      try {
        this.saveFileContents(channel, metadata, file);
        dataSource.put(testList,
            this.getParentAction().getName(), file, STORE_KEY_MUTATIONTESTLIST);
      } catch (IOException | InterruptedException e) {
        listener.getLogger().println(e.getMessage());
        e.printStackTrace(listener.getLogger());
      }
    });

    try {
      dataSource.put(fileMutationTestStats,
          this.getParentAction().getName(), STORE_KEY_FILEMUTATIONTESTSTAT);
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

  @Override
  public int getScale() {
    return (int) (this.getParentAction().getMetrics().getMutationTest().getRatio() * 100);
  }

  @Override
  public JSONArray getStatistics() {
    long allCount = killedCount + survivedCount + skippedCount;

    StatisticsItem[] result = new StatisticsItem[]{
        new StatisticsItem(
            "Killed",
            allCount > 0 ? killedCount * 100 / allCount : 0,
            killedCount,
            "valid-good"
        ),
        new StatisticsItem(
            "Survived",
            allCount > 0 ? survivedCount * 100 / allCount : 0,
            survivedCount,
            "valid-bad"
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
   * return paginated mutation test list.
   *
   * @param pageIndex page index
   * @param pageSize  page size
   * @return mutation test list
   */
  @JavaScriptMethod
  public JSONObject getRecipeMutationTests(
      int pageIndex, int pageSize, SortableItemList.SortInfo[] sortInfos) {
    FileMutationTestSortableItemList dataList = this.getDataSource().get(
        this.getParentAction().getName(), STORE_KEY_FILEMUTATIONTESTSTAT);

    if (dataList != null) {
      return dataList.sort(sortInfos).getPage(pageIndex, pageSize);
    } else {
      return null;
    }
  }

  /**
   * return file mutation test detail.
   */
  @JavaScriptMethod
  public JSONObject getFileMutationTestDetail(String codePath) {
    JSONObject result = new JSONObject();

    List<MutationTestData> dataList = this.getDataSource().get(
        this.getParentAction().getName(), codePath, STORE_KEY_MUTATIONTESTLIST);

    result.put("dataList", dataList);
    result.put("content", this.readFileContents(codePath));

    return result;
  }
}
