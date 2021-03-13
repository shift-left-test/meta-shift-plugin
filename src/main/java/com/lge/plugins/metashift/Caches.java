/*
 * MIT License
 *
 * Copyright (c) 2021 Sung Gon Kim
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

package com.lge.plugins.metashift;

import java.util.TreeSet;

public class Caches extends TreeSet<Caches.Data> implements Acceptor {
  public enum Type {
    SHAREDSTATE,
    PREMIRROR,
  }

  static class Data implements com.lge.plugins.metashift.Data, Comparable<Caches.Data> {
    private String recipe;
    private String task;
    private boolean available;
    private Caches.Type type;

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
      if (this == object) {
        return true;
      }
      if (object == null) {
        return false;
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

    public String getTask() {
      return this.task;
    }

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

    public Caches.Type getType() {
      return this.type;
    }
  }

  @Override
  public void accept(Visitor visitor) {
    visitor.visit(this);
  }
}
