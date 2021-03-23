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
 * Represents the cache data.
 *
 * @author Sung Gon Kim
 */
public abstract class CacheData implements Data<CacheData> {
  /**
   * Represents the name of the recipe.
   */
  private String recipe;
  /**
   * Represents the cache signature.
   */
  private String signature;
  /**
   * Indicates the cache availability.
   */
  private boolean available;

  /**
   * Default constructor.
   *
   * @param recipe name
   * @param signature name
   * @param available the cache availability
   */
  public CacheData(final String recipe, final String signature, final boolean available) {
    this.recipe = recipe;
    this.signature = signature;
    this.available = available;
  }

  @Override
  public final int compareTo(final CacheData other) {
    int compared;
    compared = recipe.compareTo(other.recipe);
    if (compared != 0) {
      return compared;
    }
    compared = signature.compareTo(other.signature);
    if (compared != 0) {
      return compared;
    }
    return 0;
  }

  @Override
  public final boolean equals(final Object object) {
    if (object == null) {
      return false;
    }
    if (this == object) {
      return true;
    }
    if (getClass() != object.getClass()) {
      return false;
    }
    CacheData other = (CacheData) object;
    if (!recipe.equals(other.recipe)) {
      return false;
    }
    if (!signature.equals(other.signature)) {
      return false;
    }
    if (available != other.available) {
      return false;
    }
    return true;
  }

  @Override
  public final int hashCode() {
    final int prime = 31;
    int hashCode = 1;
    hashCode = prime * hashCode + getClass().hashCode();
    hashCode = prime * hashCode + recipe.hashCode();
    hashCode = prime * hashCode + signature.hashCode();
    return hashCode;
  }

  @Override
  public final String getRecipe() {
    return recipe;
  }

  /**
   * Return the cache signature.
   *
   * @return cache signature
   */
  public final String getSignature() {
    return signature;
  }

  /**
   * Return the cache availability.
   *
   * @return true if the cache is available, false otherwise.
   */
  public final boolean isAvailable() {
    return available;
  }
}
