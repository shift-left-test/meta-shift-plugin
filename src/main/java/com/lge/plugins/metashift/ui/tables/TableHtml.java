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
   * Converts a ratio in [0, 1] to an integer percent (0-100), rounding down.
   *
   * @param ratio value in [0, 1]
   * @return integer percent
   */
  public static int percent(double ratio) {
    return (int) Math.floor(ratio * 100);
  }

  /**
   * Builds an HTML anchor with an escaped label.
   *
   * @param href  link target, already URL-encoded
   * @param label visible, HTML-escaped text
   * @return anchor HTML
   */
  public static String anchor(String href, String label) {
    return "<a href=\"" + href + "\">" + escape(label) + "</a>";
  }

  /**
   * Builds a progress bar coloured by the qualification status (light green when qualified,
   * light red otherwise) with an always-visible label overlaid on the track.
   *
   * @param ratio     value in [0, 1]
   * @param qualified qualification status of the metric for this row
   * @param label     visible label, HTML-escaped by the caller when needed
   * @return progress bar HTML
   */
  public static String progressBar(double ratio, boolean qualified, String label) {
    int percent = percent(ratio);
    String color = qualified ? "success" : "danger";
    return "<div class=\"progress position-relative\" role=\"progressbar\" aria-valuenow=\""
        + percent + "\" aria-valuemin=\"0\" aria-valuemax=\"100\" style=\"height: 16px;\">"
        + "<div class=\"progress-bar bg-" + color + " bg-opacity-25\" style=\"width:"
        + percent + "%\"></div>"
        + "<span class=\"position-absolute top-50 start-0 translate-middle-y ps-2 small"
        + " text-body\">" + label + "</span></div>";
  }

  /**
   * Builds a progress bar whose label is the integer percent.
   *
   * @param ratio     value in [0, 1]
   * @param qualified qualification status of the metric for this row
   * @return progress bar HTML
   */
  public static String progressBar(double ratio, boolean qualified) {
    return progressBar(ratio, qualified, percent(ratio) + "%");
  }

  /**
   * Builds the qualified-status indicator (check when qualified, cross otherwise).
   *
   * @param qualified qualification status
   * @return indicator HTML
   */
  public static String qualifiedIcon(boolean qualified) {
    return qualifiedIcon(qualified, "");
  }

  /**
   * Builds the qualified-status indicator with a trailing label, e.g. "✔ 3/3".
   *
   * @param qualified qualification status
   * @param label     visible label, HTML-escaped by the caller when needed
   * @return indicator HTML
   */
  public static String qualifiedIcon(boolean qualified, String label) {
    String suffix = label.isEmpty() ? "" : " " + label;
    return qualified
        ? "<span class=\"text-success\">✔" + suffix + "</span>"
        : "<span class=\"text-danger\">✘" + suffix + "</span>";
  }
}
