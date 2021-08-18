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

package com.lge.plugins.metashift.parsers;

import com.lge.plugins.metashift.analysis.LinesOfCodeCollector;
import com.lge.plugins.metashift.models.DataList;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.Recipes;
import com.lge.plugins.metashift.utils.ExecutorServiceUtils;
import hudson.FilePath;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

    logger.println("[meta-shift-plugin] Removing recipe data with no source files...");
    recipes.removeIf(recipe -> new LinesOfCodeCollector().parse(recipe).getLines() == 0);
    logger.printf("[meta-shift-plugin] -> %d recipe data removed.%n",
        directories.size() - recipes.size());

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
    String regexp = "^(?<recipe>[\\w-.+]+)-(?<version>[\\w-.+]+)-(?<revision>[\\w-.+]+)$";
    Pattern pattern = Pattern.compile(regexp);
    Matcher matcher = pattern.matcher(name);
    if (!matcher.matches()) {
      throw new IllegalArgumentException("Invalid recipe name: " + name);
    }
    DataList dataList = new DataList();
    ExecutorServiceUtils.invokeAll(
        new CodeSizeParser(path, dataList),
        new CodeViolationParser(path, dataList),
        new CommentParser(path, dataList),
        new ComplexityParser(path, dataList),
        new CoverageParser(path, dataList),
        new DuplicationParser(path, dataList),
        new MutationTestParser(path, dataList),
        new PremirrorCacheParser(path, dataList),
        new RecipeSizeParser(path, dataList),
        new RecipeViolationParser(path, dataList),
        new SharedStateCacheParser(path, dataList),
        new TestParser(path, dataList)
    );
    return new Recipe(name, dataList);
  }
}
