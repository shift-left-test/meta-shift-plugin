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

import com.lge.plugins.metashift.models.CodeSizeData;
import com.lge.plugins.metashift.models.Data;
import hudson.AbortException;
import hudson.FilePath;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
   * Lists directories to parse in the given path.
   *
   * @param path to list subdirectories
   * @return list of directories
   * @throws IOException          if failed to operate with the directory
   * @throws InterruptedException if an interruption occurs
   */
  private List<FilePath> listDirectories(FilePath path) throws IOException, InterruptedException {
    String regexp = "^(?<recipe>[\\w-.+]+)-(?<version>[\\w-.+]+)-(?<revision>[\\w-.+]+)$";
    Pattern pattern = Pattern.compile(regexp);
    return path.listDirectories().stream()
        .filter(directory -> !directory.getName().startsWith("."))
        .filter(directory -> pattern.matcher(directory.getName()).matches())
        .collect(Collectors.toList());
  }

  /**
   * Parses the report files in the given directory and returns the list of data.
   *
   * @param directory to parse
   * @param dataList  to save data
   * @throws IOException          if failed to operate with the files
   * @throws InterruptedException if an interruption occurs
   */
  private void parseDirectory(FilePath directory, List<Data> dataList)
      throws IOException, InterruptedException {
    List<Callable<Void>> callables = Arrays.asList(
        new CodeSizeDataParser(directory, dataList),
        new CodeViolationDataParser(directory, dataList),
        new CommentDataParser(directory, dataList),
        new ComplexityDataParser(directory, dataList),
        new CoverageDataParser(directory, dataList),
        new DuplicationDataParser(directory, dataList),
        new MutationTestDataParser(directory, dataList),
        new PremirrorCacheDataParser(directory, dataList),
        new RecipeSizeDataParser(directory, dataList),
        new RecipeViolationDataParser(directory, dataList),
        new SharedStateCacheDataParser(directory, dataList),
        new TestDataParser(directory, dataList)
    );

    try {
      ExecutorService executor = Executors.newSingleThreadExecutor();
      for (Future<Void> future : executor.invokeAll(callables)) {
        future.get();
      }
    } catch (ExecutionException e) {
      Throwable cause = e.getCause();
      if (cause instanceof AbortException) {
        throw (AbortException) cause;
      }
      if (cause instanceof IllegalArgumentException) {
        throw (IllegalArgumentException) cause;
      }
      if (cause instanceof InterruptedException) {
        throw (InterruptedException) cause;
      }
      if (cause instanceof IOException) {
        throw (IOException) cause;
      }
      throw new RuntimeException("Unknown exception: " + cause.getMessage(), cause);
    }
  }

  /**
   * Removes recipes with empty source files.
   *
   * @param objects to inspect
   * @return the number of recipes removed
   */
  private int removeIfNoSourceFile(List<Data> objects) {
    List<String> candidates = objects.stream()
        .map(Data::getRecipe)
        .distinct()
        .collect(Collectors.toList());
    List<String> excluded = objects.stream()
        .filter(o -> CodeSizeData.class.isAssignableFrom(o.getClass()))
        .filter(o -> ((CodeSizeData) o).getLines() > 0)
        .map(Data::getRecipe)
        .distinct()
        .collect(Collectors.toList());
    candidates.removeAll(excluded);
    for (String candidate : candidates) {
      objects.removeIf(o -> o.getRecipe().equals(candidate));
    }
    return candidates.size();
  }

  /**
   * Parse the report files in the given directory and returns the list of data.
   *
   * @param path to parse
   * @return list of data
   * @throws IOException          if failed to operate with the file
   * @throws InterruptedException if an interruption occurs
   */
  public List<Data> parse(FilePath path) throws IOException, InterruptedException {
    if (!path.exists()) {
      throw new AbortException("No report directory found in " + path);
    }
    if (!path.isDirectory()) {
      throw new AbortException("Not a directory: " + path);
    }

    logger.printf("[meta-shift-plugin] Searching for all report files in %s%n", path);
    List<FilePath> directories = listDirectories(path);
    logger.printf("[meta-shift-plugin] -> Found %d recipe data%n", directories.size());

    List<Data> dataList = Collections.synchronizedList(new ArrayList<>());

    for (FilePath directory : directories) {
      logger.printf("[meta-shift-plugin] -> %s%n", directory.getName());
      parseDirectory(directory, dataList);
    }

    logger.println("[meta-shift-plugin] Removing recipe data with no source files...");
    int removed = removeIfNoSourceFile(dataList);
    logger.printf("[meta-shift-plugin] -> %d recipe data removed.%n", removed);

    logger.println("[meta-shift-plugin] Successfully parsed.");

    return dataList;
  }
}
