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
 * Represents the cache data
 *
 * @author Sung Gon Kim
 */
public abstract class CacheData implements Data<CacheData> {
  private String recipe;
  private String task;
  private boolean available;

  /**
   * Default constructor
   *
   * @param recipe name
   * @param task name
   * @param available the cache availability
   */
  public CacheData(String recipe, String task, boolean available) {
    this.recipe = recipe;
    this.task = task;
    this.available = available;
  }

  @Override
  public int compareTo(CacheData other) {
    int compared;
    compared = recipe.compareTo(other.recipe);
    if (compared != 0) {
      return compared;
    }
    compared = task.compareTo(other.task);
    if (compared != 0) {
      return compared;
    }
    return 0;
  }

  @Override
  public boolean equals(Object object) {
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
    if (!task.equals(other.task)) {
      return false;
    }
    if (available != other.available) {
      return false;
    }
    return true;
  }

  @Override
  public String getRecipe() {
    return recipe;
  }

  /**
   * Return the task name
   *
   * @return task name
   */
  public String getTask() {
    return task;
  }

  /**
   * Return the cache availability
   *
   * @return true if the cache is available, false otherwise
   */
  public boolean isAvailable() {
    return available;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int hashCode = 1;
    hashCode = prime * hashCode + getClass().hashCode();
    hashCode = prime * hashCode + recipe.hashCode();
    hashCode = prime * hashCode + task.hashCode();
    return hashCode;
  }
}
