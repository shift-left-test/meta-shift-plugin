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

import com.lge.plugins.metashift.metrics.CodeSizeDelta;
import com.lge.plugins.metashift.metrics.CodeSizeEvaluator;
import com.lge.plugins.metashift.metrics.Criteria;
import com.lge.plugins.metashift.metrics.Metrics;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.persistence.DataSource;
import com.lge.plugins.metashift.utils.JsonUtils;
import hudson.FilePath;
import hudson.model.Action;
import hudson.model.Actionable;
import hudson.model.Run;
import hudson.model.TaskListener;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

/**
 * MetaShift recipe action class.
 */
@ExportedBean
public class MetaShiftRecipeAction extends Actionable implements Action {

  MetaShiftBuildAction parent;
  private final Metrics metrics;

  @Exported(visibility = 999)
  public String name;

  /**
   * Default constructor.
   */
  public MetaShiftRecipeAction(MetaShiftBuildAction parent, TaskListener listener,
      Criteria criteria, FilePath reportRoot, DataSource dataSource, Recipe recipe)
      throws IOException, InterruptedException {
    super();

    this.name = recipe.getRecipe();
    this.parent = parent;
    this.metrics = new Metrics(criteria);
    this.metrics.parse(recipe);

    // parse metadata.json
    File metadataFile = FileUtils.getFile(new File(reportRoot.toURI()),
        this.name, "metadata.json");
    JSONObject metadata = JsonUtils.createObject(metadataFile);

    this.addAction(new MetaShiftRecipeSharedStateCacheAction(
        this, listener, criteria, dataSource, recipe, metadata));
    this.addAction(new MetaShiftRecipePremirrorCacheAction(
        this, listener, criteria, dataSource, recipe, metadata));
    this.addAction(new MetaShiftRecipeCodeViolationsAction(
        this, listener, criteria, dataSource, recipe, metadata));
    this.addAction(new MetaShiftRecipeCommentsAction(
        this, listener, criteria, dataSource, recipe, metadata));
    this.addAction(new MetaShiftRecipeComplexityAction(
        this, listener, criteria, dataSource, recipe, metadata));
    this.addAction(new MetaShiftRecipeCoverageAction(
        this, listener, criteria, dataSource, recipe, metadata));
    this.addAction(new MetaShiftRecipeDuplicationsAction(
        this, listener, criteria, dataSource, recipe, metadata));
    this.addAction(new MetaShiftRecipeMutationTestAction(
        this, listener, criteria, dataSource, recipe, metadata));
    this.addAction(new MetaShiftRecipeRecipeViolationsAction(
        this, listener, criteria, dataSource, recipe, metadata));
    this.addAction(new MetaShiftRecipeTestAction(
        this, listener, criteria, dataSource, recipe, metadata));
  }

  public MetaShiftBuildAction getParentAction() {
    return this.parent;
  }

  public Run<?, ?> getRun() {
    return this.parent.getRun();
  }

  public Metrics getMetrics() {
    return this.metrics;
  }

  public String getName() {
    return this.name;
  }

  @Override
  public String getIconFileName() {
    return "document.png";
  }

  @Override
  public String getDisplayName() {
    return this.name;
  }

  @Override
  public String getUrlName() {
    return this.name;
  }

  @Override
  public String getSearchUrl() {
    return getUrlName();
  }

  private Metrics getPreviousMetrics() {
    if (getParentAction().getPreviousBuildAction() != null) {
      List<MetaShiftRecipeAction> recipes =
          getParentAction().getPreviousBuildAction().getActions(MetaShiftRecipeAction.class);

      for (MetaShiftRecipeAction recipe : recipes) {
        if (recipe.name.equals(this.name)) {
          return recipe.getMetrics();
        }
      }

      return null;
    }

    return null;
  }

  /**
   * Returns the delta between the previous and current builds.
   *
   * @return CodeSizeDelta object
   */
  public CodeSizeDelta getCodeSizeDelta() {
    CodeSizeEvaluator previous =
        Optional.ofNullable(getPreviousMetrics())
            .map(Metrics::getCodeSize).orElse(null);
    CodeSizeEvaluator current = getMetrics().getCodeSize();
    return CodeSizeDelta.between(previous, current);
  }

  public long getTotalRacipeCount() {
    return this.parent.getRecipes().size();
  }

  public long getLineOfCodeRank() {
    return this.parent.getRecipeLineOfCodeRank(this);
  }

  public long getBuildPerformanceRank() {
    return this.parent.getRecipeBuildPerformanceRank(this);
  }

  public long getCodeQualityRank() {
    return this.parent.getRecipeCodeQualityRank(this);
  }

  public double getLineOfCodeValue() {
    return this.getMetrics().getCodeSize().getLines();
  }

  public double getBuildPerformanceValue() {
    return (this.getMetrics().getPremirrorCache().getRatio()
        + this.getMetrics().getSharedStateCache().getRatio()
        - this.getMetrics().getRecipeViolations().getRatio()) * 100;
  }

  /**
   * return codequality score for rank.
   *
   * @return codequality score
   */
  public double getCodeQualityValue() {
    return (this.getMetrics().getComments().getRatio()
        - this.getMetrics().getCodeViolations().getRatio()
        - this.getMetrics().getComplexity().getRatio()
        - this.getMetrics().getDuplications().getRatio()
        + this.getMetrics().getTest().getRatio()
        + this.getMetrics().getCoverage().getRatio()
        + this.getMetrics().getMutationTest().getRatio()) * 100;
  }
}
