/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.models;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Represents the comment data.
 *
 * @author Sung Gon Kim
 */
public final class CommentData extends Data {

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
    return new EqualsBuilder()
        .append(getName(), other.getName())
        .append(getFile(), other.getFile())
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder()
        .append(getClass())
        .append(getName())
        .append(getFile())
        .toHashCode();
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
