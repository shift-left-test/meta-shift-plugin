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
import com.lge.plugins.metashift.models.MutationTestData;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.SummaryStatistics;
import com.lge.plugins.metashift.ui.models.DistributionItemList;
import hudson.model.TaskListener;
import hudson.remoting.VirtualChannel;
import java.io.IOException;
import java.nio.channels.ClosedByInterruptException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.bind.JavaScriptMethod;

/**
 * Mutation test detail view action class.
 */
public class RecipeMutationTestAction extends RecipeActionChild {

  static final String STORE_KEY_MUTATIONTESTLIST = "MutationTestList";

  // statistics data
  private final long killedCount;
  private final long survivedCount;
  private final long skippedCount;

  /**
   * constructor.
   *
   * @param parent   parent action
   * @param listener logger
   * @param recipe   recipe
   * @param metadata metadata
   */
  public RecipeMutationTestAction(
      RecipeAction parent, VirtualChannel channel, JSONObject metadata,
      String name, String url, boolean percentScale, TaskListener listener, Recipe recipe)
      throws InterruptedException, ClosedByInterruptException {
    super(parent, channel, metadata, name, url, percentScale);

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

    JSONArray fileMutationTestArray = new JSONArray();

    for (Map.Entry<String, List<MutationTestData>> entry : fileMutationTestList.entrySet()) {
      String file = entry.getKey();
      List<MutationTestData> testList = entry.getValue();
      JSONObject fileMutationTest = new JSONObject();
      fileMutationTest.put("file", file);
      fileMutationTest.put("killed",
          testList.stream().filter(o -> o.getStatus().equals("KILLED")).count());
      fileMutationTest.put("survived",
          testList.stream().filter(o -> o.getStatus().equals("SURVIVED")).count());
      fileMutationTest.put("skipped",
          testList.stream().filter(o -> o.getStatus().equals("SKIPPED")).count());
      fileMutationTestArray.add(fileMutationTest);

      try {
        this.saveFileContents(file);
        this.getDataSource().put(testList,
            this.getParentAction().getName(), file, STORE_KEY_MUTATIONTESTLIST);
      } catch (ClosedByInterruptException e) {
        throw e;
      } catch (IOException e) {
        listener.getLogger().println(e.getMessage());
        e.printStackTrace(listener.getLogger());
      }
    }

    try {
      this.setTableModelJson(fileMutationTestArray);
    } catch (ClosedByInterruptException e) {
      throw e;
    } catch (IOException e) {
      listener.getLogger().println(e.getMessage());
      e.printStackTrace(listener.getLogger());
    }
  }

  @Override
  public SummaryStatistics getMetricStatistics() {
    return this.getParentAction().getMetricStatistics()
        .getMutationTest();
  }

  @Override
  public Evaluator<?> getEvaluator() {
    return this.getParentAction().getMetrics().getMutationTest();
  }

  @Override
  public JSONArray getDistributionJson() {
    long allCount = killedCount + survivedCount + skippedCount;

    DistributionItemList stats = new DistributionItemList();
    stats.addItem("Killed", "valid-good",
        allCount > 0 ? killedCount * 100 / allCount : 0,
        killedCount);
    stats.addItem("Survived", "valid-bad",
        allCount > 0 ? survivedCount * 100 / allCount : 0,
        survivedCount);
    stats.addItem("Skipped", "invalid",
        allCount > 0 ? skippedCount * 100 / allCount : 0,
        skippedCount);

    return stats.toJsonArray();
  }

  /**
   * return file mutation test detail.
   */
  @JavaScriptMethod
  public JSONObject getFileMutationTestDetail(String codePath) throws InterruptedException {
    JSONObject result = new JSONObject();

    List<MutationTestData> dataList = this.getDataSource().get(
        this.getParentAction().getName(), codePath, STORE_KEY_MUTATIONTESTLIST);

    result.put("dataList", dataList);
    result.put("content", this.readFileContents(codePath));

    return result;
  }
}
