/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.ui.tables;

/**
 * Small helpers that build the raw HTML rendered inside data-tables
 * {@link io.jenkins.plugins.datatables.DetailedCell} display values, using only Bootstrap 5
 * classes (already loaded by the {@code <dt:table>} tag).
 */
public final class TableHtml {

  private TableHtml() {
  }

  /**
   * Escapes a value for safe inclusion in HTML text/attribute content.
   *
   * @param value raw value
   * @return HTML-escaped value ({@code ""} when null)
   */
  public static String escape(String value) {
    if (value == null) {
      return "";
    }
    StringBuilder builder = new StringBuilder(value.length());
    for (int i = 0; i < value.length(); i++) {
      char c = value.charAt(i);
      switch (c) {
        case '&':
          builder.append("&amp;");
          break;
        case '<':
          builder.append("&lt;");
          break;
        case '>':
          builder.append("&gt;");
          break;
        case '"':
          builder.append("&quot;");
          break;
        case '\'':
          builder.append("&#39;");
          break;
        default:
          builder.append(c);
      }
    }
    return builder.toString();
  }

  /**
   * Builds a Bootstrap progress bar whose width is the ratio rendered as an integer percent.
   *
   * @param ratio value in [0, 1]
   * @return progress bar HTML
   */
  public static String progressBar(double ratio) {
    int percent = (int) Math.floor(ratio * 100);
    return "<div class=\"progress\">"
        + "<div class=\"progress-bar\" role=\"progressbar\" style=\"width:" + percent + "%\" "
        + "aria-valuenow=\"" + percent + "\" aria-valuemin=\"0\" aria-valuemax=\"100\">"
        + percent + "%</div></div>";
  }

  /**
   * Builds the qualified-status indicator (check when qualified, cross otherwise).
   *
   * @param qualified qualification status
   * @return indicator HTML
   */
  public static String qualifiedIcon(boolean qualified) {
    return qualified
        ? "<span class=\"text-success\">✔</span>"
        : "<span class=\"text-danger\">✘</span>";
  }
}
