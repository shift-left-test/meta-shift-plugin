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
import hudson.model.Action;
import hudson.model.Run;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

/**
 * MetaShift recipe action class.
 */
@ExportedBean
public class MetaShiftRecipeAction extends MetaShiftActionBaseWithMetrics implements Action {

  MetaShiftBuildAction parent;

  @Exported(visibility = 999)
  public String name;

  public URI getRecipeUri() {
    URI baseUri = parent.getReportUri();
    return baseUri.resolve(baseUri.getPath() + '/' + this.name);
  }
  
  /**
   * Default constructor.
   */
  public MetaShiftRecipeAction(MetaShiftBuildAction parent, Criteria criteria, Recipe recipe) {
    super(criteria, recipe);

    this.name = recipe.getRecipe();

    this.parent = parent;

    this.addAction(new MetaShiftRecipeCacheAvailabilityAction(this));
    this.addAction(new MetaShiftRecipeCodeViolationsAction(this));
    this.addAction(new MetaShiftRecipeCommentsAction(this));
    this.addAction(new MetaShiftRecipeComplexityAction(this));
    this.addAction(new MetaShiftRecipeCoverageAction(this));
    this.addAction(new MetaShiftRecipeDuplicationsAction(this));
    this.addAction(new MetaShiftRecipeMutationTestAction(this));
    this.addAction(new MetaShiftRecipeRecipeViolationsAction(this));
    this.addAction(new MetaShiftRecipeTestAction(this));
  }

  public MetaShiftBuildAction getParentAction() {
    return this.parent;
  }

  public Run<?, ?> getRun() {
    return this.parent.getRun();
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
}
