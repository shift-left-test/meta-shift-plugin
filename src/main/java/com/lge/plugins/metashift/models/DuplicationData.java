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

import java.util.HashSet;
import java.util.Set;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Represents the duplication data.
 *
 * @author Sung Gon Kim
 */
public final class DuplicationData extends Data {

  /**
   * Represents the UUID of the class.
   */
  private static final long serialVersionUID = 5396591078130330879L;

  private final String file;
  private final long lines;
  private final long start;
  private final long end;
  private final Set<JSONObject> duplicateBlocks;

  /**
   * Default constructor.
   *
   * @param recipe name
   * @param file   name
   * @param lines  the number of lines
   * @param start  line of the block
   * @param end    line of the block
   */
  public DuplicationData(String recipe, String file, long lines, long start, long end) {
    super(recipe);
    this.file = file;
    this.lines = lines;
    this.start = start;
    this.end = end;
    duplicateBlocks = new HashSet<>();
  }

  /**
   * Returns the name of the file.
   *
   * @return file name
   */
  public String getFile() {
    return file;
  }

  /**
   * Returns the number of lines.
   *
   * @return the number of lines
   */
  public long getLines() {
    return lines;
  }

  /**
   * Returns the start line number of the block.
   *
   * @return the start line number
   */
  public long getStart() {
    return start;
  }

  /**
   * Returns the end line number of the block.
   *
   * @return the end line number
   */
  public long getEnd() {
    return end;
  }

  /**
   * Returns the number of duplicated lines.
   *
   * @return the number of duplicated lines
   */
  public long getDuplicatedLines() {
    return end - start;
  }

  /**
   * Returns the list of duplicate blocks.
   *
   * @return list of duplicate blocks
   */
  public Set<JSONObject> getDuplicateBlocks() {
    return duplicateBlocks;
  }

  /**
   * Adds the duplicate block object.
   *
   * @param other object
   */
  public void add(DuplicationData other) {
    JSONObject o = new JSONObject();
    o.put("name", other.getName());
    o.put("file", other.getFile());
    o.put("lines", other.getLines());
    o.put("start", other.getStart());
    o.put("end", other.getEnd());
    o.put("duplicatedLines", other.getDuplicatedLines());
    duplicateBlocks.add(o);
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
    DuplicationData other = (DuplicationData) object;
    return new EqualsBuilder()
        .append(getName(), other.getName())
        .append(getFile(), other.getFile())
        .append(getStart(), other.getStart())
        .append(getEnd(), other.getEnd())
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder()
        .append(getClass())
        .append(getName())
        .append(getFile())
        .append(getStart())
        .append(getEnd())
        .toHashCode();
  }
}
