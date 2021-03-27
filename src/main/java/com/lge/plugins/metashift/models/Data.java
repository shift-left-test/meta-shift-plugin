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
 * Represents a data object for all metrics.
 *
 * @param <T> the class type
 * @author Sung Gon Kim
 */
public abstract class Data<T> implements Comparable<T> {

  /**
   * Represents the name of the recipe.
   */
  private final String recipe;

  /**
   * Default constructor.
   *
   * @param recipe name
   */
  public Data(final String recipe) {
    this.recipe = recipe;
  }

  /**
   * Indicates whether some other object is "equal to" this one.
   *
   * @param object the reference object with which to compare
   * @return true if this is the same as the other, false otherwise
   */
  public abstract boolean equals(Object object);

  /**
   * Returns the name of the recipe.
   *
   * @return the name of the recipe
   */
  public String getRecipe() {
    return recipe;
  }

  /**
   * Returns a hash code value for the object.
   *
   * @return a hash code value for this object
   */
  public abstract int hashCode();
}
