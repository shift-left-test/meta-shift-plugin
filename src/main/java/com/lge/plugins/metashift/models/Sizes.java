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
 * Represents a container class for Sizes.Data and provides a method for Visitor classes
 *
 * @author Sung Gon Kim
 */
public class Sizes extends DataContainer<Sizes.Data> {
  /**
   * Represents data for the Sizes class
   *
   * @author Sung Gon Kim
   */
  static class Data implements com.lge.plugins.metashift.models.Data<Sizes.Data> {
    private String recipe;
    private String file;
    private int lines;
    private int functions;
    private int classes;

    /**
     * Default constructor
     *
     * @param recipe name
     * @param file name
     * @param lines the number of lines for the file
     * @param functions the number of functions for the file
     * @param classes the number of classes for the file
     */
    public Data(String recipe, String file, int lines, int functions, int classes) {
      this.recipe = recipe;
      this.file = file;
      this.lines = lines;
      this.functions = functions;
      this.classes = classes;
    }

    @Override
    public int compareTo(Sizes.Data other) {
      int compared;
      compared = recipe.compareTo(other.recipe);
      if (compared != 0) {
        return compared;
      }
      compared = file.compareTo(other.file);
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
      Sizes.Data other = (Sizes.Data) object;
      if (!recipe.equals(other.recipe)) {
        return false;
      }
      if (!file.equals(other.file)) {
        return false;
      }
      return true;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int hashCode = 1;
      hashCode = prime * hashCode + recipe.hashCode();
      hashCode = prime * hashCode + file.hashCode();
      return hashCode;
    }

    @Override
    public String getRecipe() {
      return recipe;
    }

    /**
     * Return the filename
     *
     * @return filename
     */
    public String getFile() {
      return file;
    }

    /**
     * Return the number of lines
     *
     * @return lines
     */
    public int getLines() {
      return lines;
    }

    /**
     * Return the number of functions
     *
     * @return functions
     */
    public int getFunctions() {
      return functions;
    }

    /**
     * Return the number of classes
     *
     * @return classes;
     */
    public int getClasses() {
      return classes;
    }
  }

  @Override
  public void accept(Visitor visitor) {
    visitor.visit(this);
  }
}