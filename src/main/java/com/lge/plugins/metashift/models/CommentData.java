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
public final class CommentData extends Data<CommentData> {

  /**
   * Represents the UUID of the class.
   */
  private static final long serialVersionUID = -8223804714224191070L;

  /**
   * Represents the name of the file.
   */
  private final String file;

  /**
   * Represents the number of lines.
   */
  private final long lines;

  /**
   * Represents the number of comment lines.
   */
  private final long commentLines;

  /**
   * Default constructor.
   *
   * @param recipe       name
   * @param file         name
   * @param lines        the number of lines
   * @param commentLines the number of comment lines
   */
  public CommentData(final String recipe, final String file,
      final long lines, final long commentLines) {
    super(recipe);
    this.file = file;
    this.lines = lines;
    this.commentLines = commentLines;
  }

  @Override
  public int compareTo(final CommentData other) {
    return compareEach(
        getRecipe().compareTo(other.getRecipe()),
        file.compareTo(other.file)
    );
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
    return compareTo((CommentData) object) == 0;
  }

  @Override
  public int hashCode() {
    return computeHashCode(getClass(), getRecipe(), file);
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
  public long getLines() {
    return lines;
  }

  /**
   * Return the number of comment lines.
   *
   * @return the number of comment lines
   */
  public long getCommentLines() {
    return commentLines;
  }

  /**
   * Returns the ratio of the comment lines.
   *
   * @return comment line ratio
   */
  public double getRatio() {
    return getLines() > 0 ? (double) getCommentLines() / (double) getLines() : 0;
  }
}
