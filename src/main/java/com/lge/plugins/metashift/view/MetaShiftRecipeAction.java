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

package com.lge.plugins.metashift.view;

import com.lge.plugins.metashift.metrics.CacheCounter;
import com.lge.plugins.metashift.metrics.CacheQualifier;
import com.lge.plugins.metashift.metrics.CodeViolationCounter;
import com.lge.plugins.metashift.metrics.CodeViolationQualifier;
import com.lge.plugins.metashift.metrics.CommentCounter;
import com.lge.plugins.metashift.metrics.CommentQualifier;
import com.lge.plugins.metashift.metrics.ComplexityCounter;
import com.lge.plugins.metashift.metrics.ComplexityQualifier;
import com.lge.plugins.metashift.metrics.Criteria;
import com.lge.plugins.metashift.metrics.DuplicationCounter;
import com.lge.plugins.metashift.metrics.DuplicationQualifier;
import com.lge.plugins.metashift.metrics.Metrics;
import com.lge.plugins.metashift.metrics.MutationTestCounter;
import com.lge.plugins.metashift.metrics.MutationTestQualifier;
import com.lge.plugins.metashift.metrics.RecipeViolationCounter;
import com.lge.plugins.metashift.metrics.RecipeViolationQualifier;
import com.lge.plugins.metashift.metrics.SizeCounter;
import com.lge.plugins.metashift.metrics.TestCounter;
import com.lge.plugins.metashift.metrics.TestQualifier;
import com.lge.plugins.metashift.models.Recipe;
import hudson.model.Action;
import hudson.model.Actionable;
import hudson.model.Run;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

/**
 * MetaShift recipe action class.
 */
@ExportedBean
public class MetaShiftRecipeAction extends Actionable implements Action {

  MetaShiftBuildAction parent;

  @Exported(visibility = 999)
  public String name; // TODO: should be recipe representation.

  SizeCounter sizeCounter;
  Metrics metrics;

  /**
   * Default constructor.
   */
  public MetaShiftRecipeAction(MetaShiftBuildAction parent, Criteria criteria, Recipe recipe) {
    this.name = recipe.getRecipe();
    this.parent = parent;

    this.metrics = new Metrics(criteria);
    recipe.accept(this.metrics);
    this.sizeCounter = new SizeCounter();
    recipe.accept(this.sizeCounter);
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

  /// api for front-end
  public SizeCounter getSizeCounter() {
    return this.sizeCounter;
  }

  public Metrics getMetrics() {
    return this.metrics;
  }

  public CacheQualifier getCacheQualifier() {
    return (CacheQualifier) this.metrics.get(CacheQualifier.class);
  }

  public CacheCounter getCacheCounter() {
    return getCacheQualifier().get(CacheCounter.class);
  }

  public RecipeViolationQualifier getRecipeViolationQualifier() {
    return (RecipeViolationQualifier) this.metrics.get(RecipeViolationQualifier.class);
  }

  public RecipeViolationCounter getRecipeViolationCounter() {
    return getRecipeViolationQualifier().get(RecipeViolationCounter.class);
  }

  public CommentQualifier getCommentQualifier() {
    return (CommentQualifier) this.metrics.get(CommentQualifier.class);
  }

  public CommentCounter getCommentCounter() {
    return getCommentQualifier().get(CommentCounter.class);
  }

  public CodeViolationQualifier getCodeViolationQualifier() {
    return (CodeViolationQualifier) this.metrics.get(CodeViolationQualifier.class);
  }

  public CodeViolationCounter getCodeViolationCounter() {
    return getCodeViolationQualifier().get(CodeViolationCounter.class);
  }

  public ComplexityQualifier getComplexityQualifier() {
    return (ComplexityQualifier) this.metrics.get(ComplexityQualifier.class);
  }

  public ComplexityCounter getComplexityCounter() {
    return getComplexityQualifier().get(ComplexityCounter.class);
  }

  public DuplicationQualifier getDuplicationQualifier() {
    return (DuplicationQualifier) this.metrics.get(DuplicationQualifier.class);
  }

  public DuplicationCounter getDuplicationCounter() {
    return getDuplicationQualifier().get(DuplicationCounter.class);
  }

  public TestQualifier getTestQualifier() {
    return (TestQualifier) this.metrics.get(TestQualifier.class);
  }

  public TestCounter getTestCounter() {
    return getTestQualifier().get(TestCounter.class);
  }

  // TODO: where is coverage qualifier?

  public MutationTestQualifier getMutationTestQualifier() {
    return (MutationTestQualifier) this.metrics.get(MutationTestQualifier.class);
  }

  public MutationTestCounter getMutationTestCounter() {
    return getMutationTestQualifier().get(MutationTestCounter.class);
  }
}
