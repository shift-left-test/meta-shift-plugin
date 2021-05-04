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
 * Represents the minor code violation data.
 *
 * @author Sung Gon Kim
 */
public final class MinorCodeViolationData extends CodeViolationData {

  /**
   * Default constructor.
   *
   * @param recipe      name
   * @param file        name
   * @param line        number
   * @param column      number
   * @param rule        name
   * @param message     of the violation
   * @param description of the violation
   * @param severity    of the violation
   * @param tool        used for analysis
   */
  public MinorCodeViolationData(final String recipe, final String file,
      final long line, final long column,
      final String rule, final String message,
      final String description,
      final String severity, final String tool) {
    super(recipe, file, line, column, rule,
        message, description, severity, tool, "MINOR");
  }
}
