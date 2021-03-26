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

package com.lge.plugins.metashift.metrics;

import com.lge.plugins.metashift.models.CacheList;
import com.lge.plugins.metashift.models.CodeViolationList;
import com.lge.plugins.metashift.models.CommentList;
import com.lge.plugins.metashift.models.ComplexityList;
import com.lge.plugins.metashift.models.DuplicationList;
import com.lge.plugins.metashift.models.MutationTestList;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.RecipeList;
import com.lge.plugins.metashift.models.RecipeViolationList;
import com.lge.plugins.metashift.models.SizeList;
import com.lge.plugins.metashift.models.TestList;

/**
 * A base visitor class which provides access to various containers.
 *
 * @author Sung Gon Kim
 */
public class Visitor {
  /**
   * Visits the Recipe object.
   *
   * @param object to visit
   */
  public void visit(final Recipe object) {
    object.forEach((k, v) -> v.accept(this));
  }

  /**
   * Visits the RecipeList object.
   *
   * @param object to visit
   */
  public void visit(final RecipeList object) {
    object.forEach(recipe -> recipe.accept(this));
  }

  /**
   * visits the CacheList object.
   *
   * @param object to visit
   */
  public void visit(final CacheList object) {
  }

  /**
   * Visits the SizeList object.
   *
   * @param object to visit
   */
  public void visit(final SizeList object) {
  }

  /**
   * Visits the CommentList object.
   *
   * @param object to visit
   */
  public void visit(final CommentList object) {
  }

  /**
   * Visits the RecipeViolationList object.
   *
   * @param object to visit
   */
  public void visit(final RecipeViolationList object) {
  }

  /**
   * Visits the CodeViolationList class.
   *
   * @param object to visit
   */
  public void visit(final CodeViolationList object) {
  }

  /**
   * Visits the ComplexityList object.
   *
   * @param object to visit
   */
  public void visit(final ComplexityList object) {
  }

  /**
   * Visits the DuplicationList objects.
   *
   * @param object to visit
   */
  public void visit(final DuplicationList object) {
  }

  /**
   * Visits the TestList object.
   *
   * @param object to visit
   */
  public void visit(final TestList object) {
  }

  /**
   * Visits the MutationTestList object.
   *
   * @param object to visit
   */
  public void visit(final MutationTestList object) {
  }
}
