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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Represents the test data.
 *
 * @author Sung Gon Kim
 */
public abstract class TestData extends Data {

  /**
   * Represents the test data status.
   */
  protected enum Status {
    PASSED,
    FAILED,
    ERROR,
    SKIPPED,
  }

  /**
   * Represents the UUID of the class.
   */
  private static final long serialVersionUID = 246499080494784249L;

  /**
   * Represents the name of the test suite.
   */
  private final String suite;

  /**
   * Represents the name of the test.
   */
  private final String test;

  /**
   * Represents the message of the test.
   */
  private final String message;

  /**
   * Represents the status of the test.
   */
  private final Status status;

  /**
   * Default constructor.
   *
   * @param recipe  name
   * @param suite   name
   * @param test    name
   * @param message of the test
   * @param status  of the test
   */
  public TestData(final String recipe, final String suite, final String test,
      final String message, final Status status) {
    super(recipe);
    this.suite = suite;
    this.test = test;
    this.message = message;
    this.status = status;
  }

  @Override
  public final boolean equals(final Object object) {
    if (object == null) {
      return false;
    }
    if (this == object) {
      return true;
    }
    if (getClass() != object.getClass()) {
      return false;
    }
    TestData other = (TestData) object;
    return new EqualsBuilder()
        .append(getName(), other.getName())
        .append(getSuite(), other.getSuite())
        .append(getTest(), other.getTest())
        .isEquals();
  }

  @Override
  public final int hashCode() {
    return new HashCodeBuilder()
        .append(getClass())
        .append(getName())
        .append(getSuite())
        .append(getTest())
        .toHashCode();
  }

  /**
   * Returns the name of the test suite.
   *
   * @return the name of the test suite
   */
  public final String getSuite() {
    return suite;
  }

  /**
   * Returns the name of the test.
   *
   * @return the name of the test
   */
  public final String getTest() {
    return test;
  }

  /**
   * Returns the message of the test.
   *
   * @return the message of the test
   */
  public final String getMessage() {
    return message;
  }

  /**
   * Returns the status of the test.
   *
   * @return status of the test
   */
  public final String getStatus() {
    return status.name();
  }
}
