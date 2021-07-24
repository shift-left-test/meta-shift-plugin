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

import com.lge.plugins.metashift.metrics.CodeSizeEvaluator;
import hudson.FilePath;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.io.output.NullPrintStream;

/**
 * Represents a set of Recipe objects.
 *
 * @author Sung Gon Kim
 */
public final class Recipes extends ArrayList<Recipe> implements Streamable {

  /**
   * Represents the UUID of the class.
   */
  private static final long serialVersionUID = 9217713417115395018L;

  /**
   * Creates an empty list of Recipe objects.
   */
  public Recipes() {
    super();
  }

  /**
   * Create a list of Recipe objects using the given report directory.
   *
   * @param path to the directory
   * @throws IllegalArgumentException if the path is invalid
   * @throws InterruptedException     if an interruption occurs
   * @throws IOException              if the file IO fails
   */
  public Recipes(final FilePath path)
      throws IllegalArgumentException, InterruptedException, IOException {
    this(path, NullPrintStream.NULL_PRINT_STREAM);
  }

  /**
   * Create a list of Recipe objects using the given report directory.
   *
   * @param path   to the directory
   * @param logger object
   * @throws IllegalArgumentException if the path is invalid
   * @throws InterruptedException     if an interruption occurs
   * @throws IOException              if the file IO fails
   */
  public Recipes(final FilePath path, final PrintStream logger)
      throws IllegalArgumentException, InterruptedException, IOException {
    this();
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
    for (FilePath directory : directories) {
      logger.printf("[meta-shift-plugin] -> %s%n", directory.getName());
      this.add(new Recipe(directory));
    }

    logger.println("[meta-shift-plugin] Removing recipe data with no source files...");
    this.removeIf(recipe -> new CodeSizeEvaluator().parse(recipe).getLines() == 0);
    logger.printf("[meta-shift-plugin] -> %d recipe data removed.%n",
        directories.size() - this.size());

    logger.println("[meta-shift-plugin] Successfully parsed.");
  }

  @Override
  public <T> boolean isAvailable(final Class<T> clazz) {
    return stream().anyMatch(recipe -> recipe.isAvailable(clazz));
  }

  @Override
  public <T> Stream<T> objects(final Class<T> clazz) {
    Stream<T> merged = Stream.empty();
    List<Stream<T>> streams = stream().map(o -> o.objects(clazz)).collect(Collectors.toList());
    for (Stream<T> stream : streams) {
      merged = Stream.concat(merged, stream);
    }
    return merged;
  }
}
