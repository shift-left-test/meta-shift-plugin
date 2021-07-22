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
import com.lge.plugins.metashift.ui.models.DistributionItemList;
import hudson.model.TaskListener;
import hudson.remoting.VirtualChannel;
import java.io.IOException;
import java.nio.channels.ClosedByInterruptException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Duplication detail view action class.
 */
public class RecipeDuplicationsAction
    extends RecipeActionChild {

  /**
   * constructor.
   *
   * @param parent   parent action
   * @param listener logger
   * @param recipe   recipe
   * @param metadata metadata
   */
  public RecipeDuplicationsAction(
      RecipeAction parent, VirtualChannel channel, JSONObject metadata,
      String name, String url, boolean percentScale, TaskListener listener, Recipe recipe)
      throws InterruptedException, ClosedByInterruptException {
    super(parent, channel, metadata, name, url, percentScale);

    long duplicationTolerance = this.getParentAction().getParentAction().getConfiguration()
        .getDuplicationTolerance();
    JSONArray duplicationList = JSONArray.fromObject(
        recipe.objects(DuplicationData.class)
            .filter(o -> o.getDuplicatedLines() >= duplicationTolerance).toArray());
    try {
      this.setTableModelJson(duplicationList);
    } catch (ClosedByInterruptException e) {
      throw e;
    } catch (IOException e) {
      listener.getLogger().println(e.getMessage());
      e.printStackTrace(listener.getLogger());
    }
  }

  @Override
  public Evaluator<?> getEvaluator() {
    return this.getParentAction().getMetrics().getDuplications();
  }

  @Override
  public JSONArray getDistributionJson() {
    Evaluator<?> evaluator = this.getParentAction().getMetrics().getDuplications();

    DistributionItemList stats = new DistributionItemList();
    stats.addItem("Duplicated", "valid-bad",
        (int) (evaluator.getRatio() * 100),
        (int) evaluator.getNumerator());
    stats.addItem("Unique", "invalid",
        (int) ((1 - evaluator.getRatio()) * 100),
        (int) (evaluator.getDenominator() - evaluator.getNumerator()));

    return stats.toJsonArray();
  }
}
