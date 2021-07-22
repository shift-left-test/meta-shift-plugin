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
import com.lge.plugins.metashift.models.BranchCoverageData;
import com.lge.plugins.metashift.models.Recipe;
import hudson.model.TaskListener;
import hudson.remoting.VirtualChannel;
import java.nio.channels.ClosedByInterruptException;
import java.util.HashSet;
import java.util.List;
import net.sf.json.JSONObject;

/**
 * Branch coverage detail view action class.
 */
public class RecipeBranchCoverageAction
    extends RecipeCoverageActionBase<BranchCoverageData> {

  /**
   * constructor.
   *
   * @param parent   parent action
   * @param listener logger
   * @param recipe   recipe
   * @param metadata metadata
   */
  public RecipeBranchCoverageAction(
      RecipeAction parent, VirtualChannel channel, JSONObject metadata,
      String name, String url, boolean percentScale, TaskListener listener, Recipe recipe)
      throws InterruptedException, ClosedByInterruptException {
    super(parent, channel, metadata, name, url, percentScale, listener, recipe,
        BranchCoverageData.class);
  }

  /**
   * key for line + index.
   */
  public static class LineIndex {

    Long line;
    long index;

    public LineIndex(long line, long index) {
      this.line = line;
      this.index = index;
    }

    public long getLine() {
      return this.line;
    }

    public long getIndex() {
      return this.index;
    }
  }

  protected JSONObject generateFileCoverage(String file, List<BranchCoverageData> coverageList) {
    HashSet<LineIndex> indexes = new HashSet<>();
    HashSet<LineIndex> coveredIndexes = new HashSet<>();

    for (BranchCoverageData data : coverageList) {
      LineIndex lineIndex = new LineIndex(data.getLine(), data.getIndex());

      indexes.add(lineIndex);
      if (data.isCovered()) {
        coveredIndexes.add(lineIndex);
      }
    }

    JSONObject fileCoverage = new JSONObject();
    fileCoverage.put("file", file);
    fileCoverage.put("coverage",
        indexes.size() > 0 ? (double) coveredIndexes.size() / (double) indexes.size() : 0);

    return fileCoverage;
  }

  @Override
  public Evaluator<?> getEvaluator() {
    return this.getParentAction().getMetrics().getBranchCoverage();
  }
}
