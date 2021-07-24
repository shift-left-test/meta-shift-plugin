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
import com.lge.plugins.metashift.models.CoverageData;
import com.lge.plugins.metashift.models.Recipe;
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
 * coverage detail view action class base.
 */
public abstract class RecipeCoverageActionBase<T extends CoverageData>
    extends RecipeActionChild {

  static final String STORE_KEY_FILECOVERAGELIST = "FileCoverageList";

  /**
   * constructor.
   *
   * @param parent   parent action
   * @param listener logger
   * @param recipe   recipe
   * @param metadata metadata
   */
  public RecipeCoverageActionBase(
      RecipeAction parent, VirtualChannel channel, JSONObject metadata,
      String name, String url, boolean percentScale, TaskListener listener, Recipe recipe,
      Class<T> type) throws InterruptedException, ClosedByInterruptException {
    super(parent, channel, metadata, name, url, percentScale);
    List<T> coverageDataList =
        recipe.objects(type).collect(Collectors.toList());

    HashMap<String, List<T>> fileCoverageList = new HashMap<>();

    for (T coverageData : coverageDataList) {
      String file = coverageData.getFile();

      if (!fileCoverageList.containsKey(file)) {
        fileCoverageList.put(file, new ArrayList<>());
      }
      fileCoverageList.get(file).add(coverageData);
    }

    JSONArray fileCoverageArray = new JSONArray();

    for (Map.Entry<String, List<T>> entry : fileCoverageList.entrySet()) {
      String file = entry.getKey();
      List<T> coverageList = entry.getValue();
      fileCoverageArray.add(this.generateFileCoverage(file, coverageList));

      try {
        this.saveFileContents(file);
        this.getDataSource().put(coverageList,
            this.getParentAction().getName(), this.getDisplayName(),
            file, STORE_KEY_FILECOVERAGELIST);
      } catch (ClosedByInterruptException e) {
        throw e;
      } catch (IOException e) {
        listener.getLogger().println(e.getMessage());
        e.printStackTrace(listener.getLogger());
      }
    }

    try {
      this.setTableModelJson(fileCoverageArray);
    } catch (ClosedByInterruptException e) {
      throw e;
    } catch (IOException e) {
      listener.getLogger().println(e.getMessage());
      e.printStackTrace(listener.getLogger());
    }
  }

  protected abstract JSONObject generateFileCoverage(String file, List<T> coverageList);

  @Override
  public JSONArray getDistributionJson() {
    Evaluator<?> evaluator = this.getEvaluator();

    DistributionItemList stats = new DistributionItemList();
    stats.addItem("Covered", "valid-good",
        (int) (evaluator.getRatio() * 100),
        (int) evaluator.getNumerator());
    stats.addItem("Uncovered", "invalid",
        (int) ((1 - evaluator.getRatio()) * 100),
        (int) (evaluator.getDenominator() - evaluator.getNumerator()));

    return stats.toJsonArray();
  }


  /**
   * return file coverage info.
   */
  @JavaScriptMethod
  public JSONObject getFileCoverageDetail(String codePath) throws InterruptedException {
    JSONObject result = new JSONObject();

    List<T> dataList = this.getDataSource().get(
        this.getParentAction().getName(), this.getDisplayName(),
        codePath, STORE_KEY_FILECOVERAGELIST);

    result.put("dataList", dataList);
    result.put("content", this.readFileContents(codePath));

    return result;
  }
}
