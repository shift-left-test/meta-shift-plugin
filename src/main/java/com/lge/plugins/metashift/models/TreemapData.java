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

import java.io.Serializable;

/**
 * TreemapData class.
 *
 * @author Sung Gon Kim
 */
public class TreemapData implements Serializable {

  private static final long serialVersionUID = -5314580050953091011L;

  private final String recipe;
  private final long linesOfCode;
  private final double value;

  /**
   * Default constructor.
   *
   * @param recipe      name
   * @param linesOfCode the number of lines
   * @param value       of the data
   */
  public TreemapData(String recipe, long linesOfCode, double value) {
    this.recipe = recipe;
    this.linesOfCode = linesOfCode;
    this.value = Math.max(0.0, value);
  }

  /**
   * Copy constructor.
   *
   * @param other object
   */
  public TreemapData(TreemapData other) {
    this.recipe = other.recipe;
    this.linesOfCode = other.linesOfCode;
    this.value = other.value;
  }

  /**
   * Returns the name of the recipe.
   *
   * @return recipe name
   */
  public String getRecipe() {
    return recipe;
  }

  /**
   * Returns the lines of code.
   *
   * @return the lines of code
   */
  public long getLinesOfCode() {
    return linesOfCode;
  }

  /**
   * Returns the value of the data.
   *
   * @return the value of the data
   */
  public double getValue() {
    return value;
  }
}
