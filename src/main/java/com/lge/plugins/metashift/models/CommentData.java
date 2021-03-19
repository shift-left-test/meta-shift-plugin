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
 * Represents the comment data.
 *
 * @author Sung Gon Kim
 */
public final class CommentData implements Data<CommentData> {
  /**
   * Represents the name of the recipe.
   */
  private String recipe;
  /**
   * Represents the name of the file.
   */
  private String file;
  /**
   * Represents the number of lines.
   */
  private int lines;
  /**
   * Reprents the number of comment lines.
   */
  private int commentLines;

  /**
   * Default constructor.
   *
   * @param recipe name
   * @param file name
   * @param lines the number of lines
   * @param commentLines the number of comment lines
   */
  public CommentData(final String recipe, final String file,
                     final int lines, final int commentLines) {
    this.recipe = recipe;
    this.file = file;
    this.lines = lines;
    this.commentLines = commentLines;
  }

  @Override
  public int compareTo(final CommentData other) {
    return 0;
  }

  @Override
  public boolean equals(final Object object) {
    if (object == null) {
      return false;
    }
    if (this == object) {
      return true;
    }
    if (getClass() != object.getClass()) {
      return false;
    }
    CommentData other = (CommentData) object;
    if (!recipe.equals(other.recipe)) {
      return false;
    }
    if (!file.equals(other.file)) {
      return false;
    }
    return true;
  }

  @Override
  public String getRecipe() {
    return recipe;
  }

  /**
   * Return the name of the file.
   *
   * @return filename
   */
  public String getFile() {
    return file;
  }

  /**
   * Return the number of lines.
   *
   * @return the number of lines
   */
  public int getLines() {
    return lines;
  }

  /**
   * Return the number of comment lines.
   *
   * @return the number of comment lines
   */
  public int getCommentLines() {
    return commentLines;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int hashCode = 1;
    hashCode = prime * hashCode + getClass().hashCode();
    hashCode = prime * hashCode + recipe.hashCode();
    hashCode = prime * hashCode + file.hashCode();
    return hashCode;
  }
}
