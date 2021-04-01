/*
 *
 *  * MIT License
 *  *
 *  * Copyright (c) 2021 LG Electronics, Inc.
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  * of this software and associated documentation files (the "Software"), to deal
 *  * in the Software without restriction, including without limitation the rights
 *  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  * copies of the Software, and to permit persons to whom the Software is
 *  * furnished to do so, subject to the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be included in
 *  * all copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  * THE SOFTWARE.
 *
 */

package com.lge.plugins.metashift.metrics;

import com.lge.plugins.metashift.models.SizeList;

/**
 * A dummy class for the SizeController.
 *
 * @author Sung Gon Kim
 */
public class SizeQualifier extends Qualifier<SizeCounter> {

  /**
   * Default constructor.
   */
  public SizeQualifier() {
    super(0.0f, new SizeCounter());
  }

  /**
   * Return the number of classes.
   *
   * @return the number of classes
   */
  public int getClasses() {
    return get(SizeCounter.class).getClasses();
  }

  /**
   * Return the number of files.
   *
   * @return the number of files
   */
  public int getFiles() {
    return get(SizeCounter.class).getFiles();
  }

  /**
   * Return the number of functions.
   *
   * @return the number of functions
   */
  public int getFunctions() {
    return get(SizeCounter.class).getFunctions();
  }

  /**
   * Return the number of lines.
   *
   * @return the number of lines
   */
  public int getLines() {
    return get(SizeCounter.class).getLines();
  }

  /**
   * Return the number of recipes.
   *
   * @return the number of recipes
   */
  public int getRecipes() {
    return get(SizeCounter.class).getRecipes();
  }

  @Override
  public int getDenominator() {
    return 0;
  }

  @Override
  public int getNumerator() {
    return 0;
  }

  @Override
  public boolean isQualified() {
    return false;
  }

  @Override
  public void visit(final SizeList object) {
    getCollection().values().forEach(object::accept);
  }
}
