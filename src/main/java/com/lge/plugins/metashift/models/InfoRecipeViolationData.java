/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.models;

/**
 * Represents the info recipe violation data.
 *
 * @author Sung Gon Kim
 */
public final class InfoRecipeViolationData extends RecipeViolationData {

  /**
   * Represents the UUID of the class.
   */
  private static final long serialVersionUID = -7166769406751508127L;

  /**
   * Default constructor.
   *
   * @param recipe      name
   * @param file        name
   * @param line        number
   * @param rule        name
   * @param description of the recipe violation
   * @param severity    of the recipe violation
   */
  public InfoRecipeViolationData(final String recipe, final String file,
      final long line, final String rule,
      final String description,
      final String severity) {
    super(recipe, file, line, rule, description, severity, Level.INFO);
  }
}
