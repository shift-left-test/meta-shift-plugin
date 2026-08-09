/*
 * Copyright (c) 2026 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.ui.tables;

import io.jenkins.plugins.datatables.DetailedCell;
import java.lang.reflect.Method;

/**
 * Reads a table row cell the way data-tables does, so a test can assert on what the browser
 * actually receives regardless of whether the getter returns a plain value or a
 * {@link DetailedCell}.
 *
 * <p>data-tables serializes a row as a bean: a {@code DetailedCell} property becomes
 * {@code {display, sort}} and only {@code display} is written into the cell, while a plain
 * {@code String} property is written into {@code td.innerHTML} verbatim. {@link #html} therefore
 * returns the markup that reaches the DOM, and {@link #sort} the value used for sorting,
 * filtering and search.
 */
final class CellValues {

  private CellValues() {
  }

  private static Object read(Object row, String property) {
    String getter = "get" + Character.toUpperCase(property.charAt(0)) + property.substring(1);
    try {
      Method method = row.getClass().getMethod(getter);
      return method.invoke(row);
    } catch (ReflectiveOperationException e) {
      throw new IllegalArgumentException("No such row property: " + property, e);
    }
  }

  /**
   * Returns the markup written into the cell for the given row property.
   *
   * @param row      table row bean
   * @param property bean property name, matching the column's data property key
   * @return cell markup as the browser receives it
   */
  static String html(Object row, String property) {
    Object value = read(row, property);
    return value instanceof DetailedCell
        ? String.valueOf(((DetailedCell<?>) value).getDisplay())
        : String.valueOf(value);
  }

  /**
   * Returns the value data-tables sorts, filters and searches on for the given row property.
   *
   * @param row      table row bean
   * @param property bean property name, matching the column's data property key
   * @return sort value
   */
  static String sort(Object row, String property) {
    Object value = read(row, property);
    return value instanceof DetailedCell
        ? String.valueOf(((DetailedCell<?>) value).getSort())
        : String.valueOf(value);
  }
}
