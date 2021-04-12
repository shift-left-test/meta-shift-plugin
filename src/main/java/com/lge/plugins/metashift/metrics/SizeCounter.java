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

import com.lge.plugins.metashift.models.SizeData;
import com.lge.plugins.metashift.models.SizeList;

/**
 * Collects the size information from the given data containers.
 *
 * @author Sung Gon Kim
 */
public final class SizeCounter implements Visitable {

  /**
   * Represents the sum of the number of classes.
   */
  private int classes;

  /**
   * Represents the sum of the number of files.
   */
  private int files;

  /**
   * Represents the sum of the number of functions.
   */
  private int functions;

  /**
   * Represents the sum of the number of lines.
   */
  private int lines;

  /**
   * Represents the sum of the number of recipes.
   */
  private int recipes;

  /**
   * Default constructor.
   */
  public SizeCounter() {
    this.classes = 0;
    this.files = 0;
    this.functions = 0;
    this.lines = 0;
    this.recipes = 0;
  }

  /**
   * Return the number of classes.
   *
   * @return the number of classes
   */
  public int getClasses() {
    return classes;
  }

  /**
   * Return the number of files.
   *
   * @return the number of files
   */
  public int getFiles() {
    return files;
  }

  /**
   * Return the number of functions.
   *
   * @return the number of functions
   */
  public int getFunctions() {
    return functions;
  }

  /**
   * Return the number of lines.
   *
   * @return the number of lines
   */
  public int getLines() {
    return lines;
  }

  /**
   * Return the number of recipes.
   *
   * @return the number of recipes
   */
  public int getRecipes() {
    return recipes;
  }

  @Override
  public void visit(final SizeList objects) {
    recipes += objects.stream().map(SizeData::getRecipe).distinct().count();
    files += objects.stream().distinct().count();
    lines += objects.stream().mapToLong(SizeData::getLines).sum();
    classes += objects.stream().mapToLong(SizeData::getClasses).sum();
    functions += objects.stream().mapToLong(SizeData::getFunctions).sum();
  }
}
