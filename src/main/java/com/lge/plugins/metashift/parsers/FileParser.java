/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.parsers;

import com.lge.plugins.metashift.models.CoverageData;
import com.lge.plugins.metashift.models.DataList;
import com.lge.plugins.metashift.models.MutationTestData;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.Recipes;
import com.lge.plugins.metashift.models.TestData;
import com.lge.plugins.metashift.utils.ExecutorServiceUtils;
import com.lge.plugins.metashift.utils.NamingUtils;
import hudson.FilePath;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.io.output.NullPrintStream;

/**
 * FileParser class.
 *
 * @author Sung Gon Kim
 */
public class FileParser {

  private final PrintStream logger;

  /**
   * Default constructor.
   *
   * @param logger for logging
   */
  public FileParser(PrintStream logger) {
    this.logger = logger;
  }

  /**
   * Default constructor.
   */
  public FileParser() {
    this(NullPrintStream.NULL_PRINT_STREAM);
  }

  /**
   * Parses the files in the directory to create the list of recipes.
   *
   * @param path to the files
   * @return list of recipes
   */
  public Recipes parse(FilePath path) throws IOException, InterruptedException {
    if (!path.exists()) {
      throw new IllegalArgumentException("Directory not found: " + path);
    }
    if (!path.isDirectory()) {
      throw new IllegalArgumentException("Not a directory: " + path);
    }

    List<FilePath> directories = path.listDirectories().stream()
        .filter(directory -> !directory.getName().startsWith("."))
        .collect(Collectors.toList());
    logger.printf("[meta-shift-plugin] -> Found %d recipe data%n", directories.size());

    logger.println("[meta-shift-plugin] Parsing the meta-shift report...");
    Recipes recipes = new Recipes();
    for (FilePath directory : directories) {
      logger.printf("[meta-shift-plugin] -> %s%n", directory.getName());
      recipes.add(parseEach(directory));
    }

    logger.println("[meta-shift-plugin] Removing recipe data with no report data...");
    recipes.removeIf(recipe ->
        !recipe.contains(CoverageData.class)
        && !recipe.contains(TestData.class)
        && !recipe.contains(MutationTestData.class));
    logger.printf("[meta-shift-plugin] -> %d recipe data removed.%n",
        directories.size() - recipes.size());

    warnOnDuplicateNames(recipes);
    Collections.sort(recipes);

    logger.println("[meta-shift-plugin] Successfully parsed.");
    return recipes;
  }

  /**
   * Creates a Recipe object using the given recipe directory.
   *
   * @param path to the recipe directory
   * @throws IllegalArgumentException if the recipe name is malformed or the path is invalid
   * @throws IOException              if a file IO fails
   * @throws InterruptedException     if an interruption occurs
   */
  public Recipe parseEach(FilePath path) throws IOException, InterruptedException {
    if (!path.exists()) {
      throw new IllegalArgumentException("Directory not found: " + path);
    }
    if (!path.isDirectory()) {
      throw new IllegalArgumentException("Not a directory: " + path);
    }
    String name = path.getName();
    if (!NamingUtils.isValid(name)) {
      throw new IllegalArgumentException("Invalid recipe name: " + name);
    }
    DataList dataList = new DataList();
    ExecutorServiceUtils.invokeAll(
        new CoverageParser(path, dataList, logger),
        new MutationTestParser(path, dataList, logger),
        new TestParser(path, dataList, logger)
    );
    Recipe recipe = new Recipe(NamingUtils.getRecipe(name), dataList);
    recipe.setSourceDir(readSourceDir(path));
    recipe.setVersion(versionWithRevision(name));
    recipe.setReportDir(name);
    return recipe;
  }

  /**
   * Combines the version and revision (BitBake PV/PR) parsed from the report directory name
   * into a single display string, e.g. {@code "1.0.0-r0"}.
   *
   * @param name report directory name
   * @return combined version string
   */
  private static String versionWithRevision(String name) {
    String version = NamingUtils.getVersion(name);
    String revision = NamingUtils.getRevision(name);
    return revision.isEmpty() ? version : version + "-" + revision;
  }

  /**
   * Logs a warning for every recipe name shared by more than one report directory (e.g. two
   * versions of the same recipe published in the same report root). {@link Recipe#getName()} is
   * intentionally version-stripped for build-over-build delta matching, so these recipes are not
   * deduplicated here; their persisted data and recipe page URL collide under that shared name,
   * so it is surfaced instead of failing silently.
   *
   * @param recipes to inspect
   */
  private void warnOnDuplicateNames(Recipes recipes) {
    Set<String> seen = new HashSet<>();
    Set<String> duplicates = new HashSet<>();
    for (Recipe recipe : recipes) {
      if (!seen.add(recipe.getName())) {
        duplicates.add(recipe.getName());
      }
    }
    for (String name : duplicates) {
      logger.printf(
          "[meta-shift-plugin] WARNING: multiple report directories map to recipe '%s'; "
              + "only one recipe page will be shown and their data may overwrite each other.%n",
          name);
    }
  }

  /**
   * Reads the BitBake S variable from the first available task metadata.json.
   *
   * @param path to the recipe directory
   * @return source directory path, or null when not available
   */
  private String readSourceDir(FilePath path) throws InterruptedException {
    for (String task : new String[]{"coverage", "checktest", "test"}) {
      String value = Parser.readSourceRoot(path.child(task));
      if (value != null) {
        return value;
      }
    }
    return null;
  }
}
