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

import com.lge.plugins.metashift.metrics.CodeSizeDelta;
import com.lge.plugins.metashift.metrics.CodeSizeEvaluator;
import com.lge.plugins.metashift.metrics.Criteria;
import com.lge.plugins.metashift.metrics.Metrics;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.persistence.DataSource;
import com.lge.plugins.metashift.ui.project.MetaShiftBuildAction;
import hudson.FilePath;
import hudson.model.Action;
import hudson.model.Actionable;
import hudson.model.Run;
import hudson.model.TaskListener;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

/**
 * MetaShift recipe action class.
 */
@ExportedBean
public class RecipeAction extends Actionable implements Action {

  MetaShiftBuildAction parent;
  private final Metrics metrics;

  @Exported(visibility = 999)
  public String name;

  /**
   * Default constructor.
   */
  public RecipeAction(MetaShiftBuildAction parent, TaskListener listener,
      Criteria criteria, FilePath reportRoot, DataSource dataSource, Recipe recipe)
      throws IOException, InterruptedException {
    super();

    this.name = recipe.getRecipe();
    this.parent = parent;
    this.metrics = new Metrics(criteria);
    this.metrics.parse(recipe);

    // parse metadata.json
    JSONObject metadata = JSONObject.fromObject(
        reportRoot.child(this.name).child("metadata.json").readToString());

    this.addAction(new RecipeSharedStateCacheAction(
        this, listener, reportRoot.getChannel(), dataSource, recipe, metadata));
    this.addAction(new RecipePremirrorCacheAction(
        this, listener, reportRoot.getChannel(), dataSource, recipe, metadata));
    this.addAction(new RecipeCodeViolationsAction(
        this, listener, reportRoot.getChannel(), dataSource, recipe, metadata));
    this.addAction(new RecipeCommentsAction(
        this, listener, reportRoot.getChannel(), dataSource, recipe, metadata));
    this.addAction(new RecipeComplexityAction(
        this, listener, reportRoot.getChannel(), dataSource, recipe, metadata));
    this.addAction(new RecipeCoverageAction(
        this, listener, reportRoot.getChannel(), dataSource, recipe, metadata));
    this.addAction(new RecipeDuplicationsAction(
        this, listener, reportRoot.getChannel(), dataSource, recipe, metadata));
    this.addAction(new RecipeMutationTestAction(
        this, listener, reportRoot.getChannel(), dataSource, recipe, metadata));
    this.addAction(new RecipeRecipeViolationsAction(
        this, listener, reportRoot.getChannel(), dataSource, recipe, metadata));
    this.addAction(new RecipeTestAction(
        this, listener, reportRoot.getChannel(), dataSource, recipe, metadata));
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
      List<RecipeAction> recipes =
          getParentAction().getPreviousBuildAction().getActions(RecipeAction.class);

      for (RecipeAction recipe : recipes) {
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

  /**
   * treemap data.
   *
   * @return recipe score
   */
  public int getRecipeScore() {
    int evaluated = 0;
    int qualified = 0;

    if (this.getMetrics().getPremirrorCache().isAvailable()) {
      evaluated++;
      if (this.getMetrics().getPremirrorCache().isQualified()) {
        qualified++;
      }
    }

    if (this.getMetrics().getSharedStateCache().isAvailable()) {
      evaluated++;
      if (this.getMetrics().getSharedStateCache().isQualified()) {
        qualified++;
      }
    }

    if (this.getMetrics().getRecipeViolations().isAvailable()) {
      evaluated++;
      if (this.getMetrics().getRecipeViolations().isQualified()) {
        qualified++;
      }
    }

    if (this.getMetrics().getComments().isAvailable()) {
      evaluated++;
      if (this.getMetrics().getComments().isQualified()) {
        qualified++;
      }
    }

    if (this.getMetrics().getCodeViolations().isAvailable()) {
      evaluated++;
      if (this.getMetrics().getCodeViolations().isQualified()) {
        qualified++;
      }
    }

    if (this.getMetrics().getPremirrorCache().isAvailable()) {
      evaluated++;
      if (this.getMetrics().getPremirrorCache().isQualified()) {
        qualified++;
      }
    }

    if (this.getMetrics().getComplexity().isAvailable()) {
      evaluated++;
      if (this.getMetrics().getComplexity().isQualified()) {
        qualified++;
      }
    }

    if (this.getMetrics().getDuplications().isAvailable()) {
      evaluated++;
      if (this.getMetrics().getDuplications().isQualified()) {
        qualified++;
      }
    }

    if (this.getMetrics().getTest().isAvailable()) {
      evaluated++;
      if (this.getMetrics().getTest().isQualified()) {
        qualified++;
      }
    }

    if (this.getMetrics().getCoverage().isAvailable()) {
      evaluated++;
      if (this.getMetrics().getCoverage().isQualified()) {
        qualified++;
      }
    }

    return qualified * 100 / evaluated;
  }
}
