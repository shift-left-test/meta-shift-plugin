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

import java.util.regex.*;

/**
 * Represents a recipe containing various data for metrics
 *
 * @author Sung Gon Kim
 */
public class Recipe implements Data<Recipe>, Acceptor {
  private Caches caches;
  private String recipe;
  private String revision;
  private String version;

  /**
   * Default constructor
   *
   * @param fullname of the recipe
   * @throws IllegalArgumentException if the fullname is malformed
   */
  public Recipe(String fullname) {
    String regexp = "^(?<recipe>[\\w-+]+)(?:-)(?<version>[\\w.+]+)(?:-)(?<revision>[\\w.+]+)$";
    Pattern pattern = Pattern.compile(regexp);
    Matcher matcher = pattern.matcher(fullname);
    try {
      matcher.matches();
      this.recipe = matcher.group("recipe");
      this.revision = matcher.group("revision");
      this.version = matcher.group("version");
    }
    catch (IllegalStateException | IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid argument: " + fullname);
    }

    this.caches = new Caches();
  }

  /**
   * Add the cache container
   *
   * @param object of the Caches
   */
  public void add(Caches object) {
    this.caches = object;
  }

  /**
   * Return the Cache container
   *
   * @return caches object
   */
  public Caches caches() {
    return caches;
  }

  @Override
  public void accept(Visitor visitor) {
    visitor.visit(this);
  }

  @Override
  public int compareTo(Recipe other) {
    int compared;
    compared = recipe.compareTo(other.recipe);
    if (compared != 0) {
      return compared;
    }
    compared = version.compareTo(other.version);
    if (compared != 0) {
      return compared;
    }
    compared = revision.compareTo(other.revision);
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
    Recipe other = (Recipe) object;
    if (!recipe.equals(other.recipe)) {
      return false;
    }
    if (!version.equals(other.version)) {
      return false;
    }
    if (!revision.equals(other.revision)) {
      return false;
    }
    return true;
  }

  @Override
  public String getRecipe() {
    return recipe;
  }

  /**
   * Return the revision of the recipe
   *
   * @return revision
   */
  public String getRevision() {
    return revision;
  }

  /**
   * Return the versino of the recipe
   *
   * @return version
   */
  public String getVersion() {
    return version;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int hashCode = 1;
    hashCode = prime * hashCode + recipe.hashCode();
    hashCode = prime * hashCode + version.hashCode();
    hashCode = prime * hashCode + revision.hashCode();
    return hashCode;
  }
}
