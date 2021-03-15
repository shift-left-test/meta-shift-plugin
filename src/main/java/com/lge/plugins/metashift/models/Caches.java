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

import java.util.TreeSet;

/**
 * A container class for Caches.Data which provides a method for Visitor classes
 *
 * @author Sung Gon Kim
 */
public class Caches extends DataContainer<Caches.Data> {
  /**
   * Cache data type
   */
  public enum Type {
    /**
     * Shared state cache
     */
    SHAREDSTATE,
    /**
     * Premirror cache
     */
    PREMIRROR,
  }

  /**
   * Represents a data class for Caches
   *
   * @author Sung Gon Kim
   */
  static class Data implements com.lge.plugins.metashift.models.Data<Caches.Data> {
    private String recipe;
    private String task;
    private boolean available;
    private Caches.Type type;

    /**
     * Default constructor
     *
     * @param recipe name
     * @param task name
     * @param available the cache availability
     * @param type of the cache
     */
    public Data(String recipe, String task, boolean available, Caches.Type type) {
      this.recipe = recipe;
      this.task = task;
      this.available = available;
      this.type = type;
    }

    @Override
    public int compareTo(Caches.Data other) {
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
      Caches.Data other = (Caches.Data) object;
      if (!recipe.equals(other.recipe)) {
        return false;
      }
      if (!task.equals(other.task)) {
        return false;
      }
      if (available != other.available) {
        return false;
      }
      if (type != other.type) {
        return false;
      }
      return true;
    }

    @Override
    public String getRecipe() {
      return this.recipe;
    }

    /**
     * Return the task name
     *
     * @return task name
     */
    public String getTask() {
      return this.task;
    }

    /**
     * Return the cache availability
     *
     * @return true if the cache is available, false otherwise
     */
    public boolean isAvailable() {
      return this.available;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int hashCode = 1;
      hashCode = prime * hashCode + recipe.hashCode();
      hashCode = prime * hashCode + task.hashCode();
      hashCode = prime * hashCode + (available ? 1231 : 1237);
      hashCode = prime * hashCode + type.ordinal();
      return hashCode;
    }

    /**
     * Return the type of the cache
     *
     * @return type
     */
    public Caches.Type getType() {
      return this.type;
    }
  }

  @Override
  public void accept(Visitor visitor) {
    visitor.visit(this);
  }
}
