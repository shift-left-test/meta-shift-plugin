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

package com.lge.plugins.metashift.models;

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
   * Visits the RecipeSet object.
   *
   * @param object to visit
   */
  public void visit(final RecipeSet object) {
    object.forEach(recipe -> recipe.accept(this));
  }

  /**
   * visits the CacheSet object.
   *
   * @param object to visit
   */
  public void visit(final CacheSet object) {
  }

  /**
   * Visits the SizeSet object.
   *
   * @param object to visit
   */
  public void visit(final SizeSet object) {
  }

  /**
   * Visits the CommentSet object.
   *
   * @param object to visit
   */
  public void visit(final CommentSet object) {
  }

  /**
   * Visits the RecipeViolationSet object.
   *
   * @param object to visit
   */
  public void visit(final RecipeViolationSet object) {
  }

  /**
   * Visits the CodeViolationSet class.
   *
   * @param object to visit
   */
  public void visit(final CodeViolationSet object) {
  }

  /**
   * Visits the ComplexitySet object.
   *
   * @param object to visit
   */
  public void visit(final ComplexitySet object) {
  }

  /**
   * Visits the DuplicationSet objects.
   *
   * @param object to visit
   */
  public void visit(final DuplicationSet object) {
  }

  /**
   * Visits the TestSet object.
   *
   * @param object to visit
   */
  public void visit(final TestSet object) {
  }
}
