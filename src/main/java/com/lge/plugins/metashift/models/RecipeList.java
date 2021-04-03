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

import com.lge.plugins.metashift.metrics.Visitable;
import java.io.File;
import java.util.Collections;

/**
 * Represents a set of Recipe objects.
 *
 * @author Sung Gon Kim
 */
public final class RecipeList extends DataList<Recipe> {

  /**
   * Create a list of recipes using the given report directory.
   *
   * @param path to directory
   * @throws IllegalArgumentException if the path is invalid
   */
  public static RecipeList create(File path) throws IllegalArgumentException {
    if (!path.exists()) {
      throw new IllegalArgumentException("Directory not found: " + path);
    }
    if (!path.isDirectory()) {
      throw new IllegalArgumentException("Not a directory: " + path);
    }
    RecipeList recipes = new RecipeList();
    File[] directories = path.listFiles(File::isDirectory);
    if (directories != null) {
      for (File directory : directories) {
        recipes.add(Recipe.create(directory));
      }
    }
    Collections.sort(recipes);
    return recipes;
  }

  @Override
  public void accept(final Visitable visitor) {
    visitor.visit(this);
  }
}
