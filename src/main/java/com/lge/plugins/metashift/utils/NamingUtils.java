/*
 * Copyright (c) 2022 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Naming Utils class.
 *
 * @author Sung Gon Kim
 */
public class NamingUtils {

  private static Matcher parse(String name) {
    String regexp = "^(?<recipe>[\\w-.+]+)-(?<version>[\\w-.+]+)-(?<revision>[\\w-.+]+)$";
    Pattern pattern = Pattern.compile(regexp);
    return pattern.matcher(name);
  }

  /**
   * Check if the given name complies the Yocto recipe naming convention.
   *
   * @param name of a recipe
   * @return true if the name complies the recipe naming convention, false otherwise
   */
  public static boolean isValid(String name) {
    return parse(name).matches();
  }

  /**
   * Return the recipe name.
   *
   * @param name of a recipe
   * @return name of the recipe, or empty string if the name does not comply the naming convention
   */
  public static String getRecipe(String name) {
    Matcher matcher = parse(name);
    return matcher.matches() ? matcher.group("recipe") : "";
  }

  /**
   * Return the recipe version.
   *
   * @param name of a recipe
   * @return version of the recipe, or empty string if the name does not comply the naming
   * convention
   */
  public static String getVersion(String name) {
    Matcher matcher = parse(name);
    return matcher.matches() ? matcher.group("version") : "";
  }

  /**
   * Return the recipe revision.
   *
   * @param name of a recipe
   * @return revision of the recipe, or empty string if the name does not comply the naming
   * convention
   */
  public static String getRevision(String name) {
    Matcher matcher = parse(name);
    return matcher.matches() ? matcher.group("revision") : "";
  }
}
