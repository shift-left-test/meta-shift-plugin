/**
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

/**
 * utility functions
 */
class Utils {
  /**
   * return fixed number.
   * @param {number} value
   * @param {number} fraction
   * @return {unknown}
   */
  static toFixedFloor(value, fraction=2) {
    const base = Math.pow(10, fraction);
    return (Math.floor(value * base) / base).toFixed(fraction);
  }
}

/**
 * constant values.
 */
class Constants {
  static LinesOfCodeWidth = 150;
  static IssueCountWidth = 100;
  static RatioWidth = 150;
  static QualifiedWidth = 110;
}

export {Utils, Constants};
