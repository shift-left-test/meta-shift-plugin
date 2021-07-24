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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.sf.json.JSONObject;

/**
 * Represents a data object for all metrics.
 *
 * @author Sung Gon Kim
 */
public abstract class Data implements Serializable {

  /**
   * Represents the UUID of the class.
   */
  private static final long serialVersionUID = -7672797936775946949L;

  /**
   * Represents the name of the recipe.
   */
  private final String recipe;

  /**
   * Default constructor.
   *
   * @param recipe name
   * @throws IllegalArgumentException if the recipe name is malformed
   */
  public Data(final String recipe) {
    String regexp = "^(?<recipe>[\\w-.+]+)-(?<version>[\\w-.+]+)-(?<revision>[\\w-.+]+)$";
    Pattern pattern = Pattern.compile(regexp);
    Matcher matcher = pattern.matcher(recipe);
    if (!matcher.matches()) {
      throw new IllegalArgumentException("Invalid recipe name: " + recipe);
    }
    this.recipe = recipe;
  }

  /**
   * Indicates whether some other object is "equal to" this one.
   *
   * @param object the reference object with which to compare
   * @return true if this is the same as the other, false otherwise
   */
  @Override
  public abstract boolean equals(Object object);

  /**
   * Returns a hash code value for the object.
   *
   * @return a hash code value for this object
   */
  @Override
  public abstract int hashCode();

  /**
   * Returns the name of the recipe.
   *
   * @return the name of the recipe
   */
  public String getRecipe() {
    return recipe;
  }

  /**
   * Returns the object as a JSONObject instance.
   *
   * @return a JSONObject instance
   */
  public JSONObject toJsonObject() {
    return JSONObject.fromObject(this);
  }
}
