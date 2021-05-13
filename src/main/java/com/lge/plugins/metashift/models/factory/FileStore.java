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

package com.lge.plugins.metashift.models.factory;

import com.lge.plugins.metashift.models.CodeViolationData;
import com.lge.plugins.metashift.models.ComplexityData;
import com.lge.plugins.metashift.models.CoverageData;
import com.lge.plugins.metashift.models.MutationTestData;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.RecipeViolationData;
import com.lge.plugins.metashift.models.Recipes;
import com.lge.plugins.metashift.utils.DigestUtils;
import com.lge.plugins.metashift.utils.JsonUtils;
import hudson.FilePath;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.NullPrintStream;

/**
 * FileStore class.
 *
 * @author Sung Gon Kim
 */
public class FileStore {

  /**
   * Represents the path to the stored report directory.
   */
  private final FilePath storage;

  /**
   * Default constructor.
   *
   * @param storage path to store files
   */
  public FileStore(final FilePath storage) {
    this.storage = storage;
  }

  /**
   * Manages the report files and returns Recipes object.
   *
   * @param path to the report directory
   * @return Recipes object
   */
  public Recipes load(final FilePath path) throws IOException, InterruptedException {
    return load(path, NullPrintStream.NULL_PRINT_STREAM);
  }

  /**
   * Manages the report files and returns Recipes object.
   *
   * @param path   to the report directory
   * @param logger logging object
   * @return Recipes object
   */
  public Recipes load(final FilePath path, final PrintStream logger)
      throws IOException, InterruptedException {
    if (!path.exists()) {
      throw new FileNotFoundException(String.format("Failed to find the directory: %s", path));
    }

    logger.printf("[FileStore] Copying files: '%s' -> '%s'%n", path, storage);
    path.copyRecursiveTo(storage);

    Recipes recipes = new Recipes(new File(storage.toURI()));
    for (Recipe recipe : recipes) {
      File directory = new File(new File(storage.toURI()), recipe.getRecipe());
      JSONObject indices = new JSONObject();
      String parent;
      try {
        parent = JsonUtils.createObject(new File(directory, "metadata.json")).getString("S");
      } catch (IOException e) {
        logger.printf("[FileStore] %s: Failed to read metadata.json: %s", recipe.getRecipe(), e);
        continue;
      }

      logger.printf("[FileStore] %s: Collecting code violation data files", recipe.getRecipe());
      recipes.objects(CodeViolationData.class)
          .forEach(o -> copySourceFile(parent, o.getFile(), directory, indices, logger));

      logger.printf("[FileStore] %s: Collecting complexity data files", recipe.getRecipe());
      recipes.objects(ComplexityData.class)
          .forEach(o -> copySourceFile(parent, o.getFile(), directory, indices, logger));

      logger.printf("[FileStore] %s: Collecting coverage data files", recipe.getRecipe());
      recipe.objects(CoverageData.class)
          .forEach(o -> copySourceFile(parent, o.getFile(), directory, indices, logger));

      logger.printf("[FileStore] %s: Collecting mutation test data files", recipe.getRecipe());
      recipe.objects(MutationTestData.class)
          .forEach(o -> copySourceFile(parent, o.getFile(), directory, indices, logger));

      logger.printf("[FileStore] %s: Collecting recipe violation data files", recipe.getRecipe());
      recipe.objects(RecipeViolationData.class)
          .forEach(o -> copySourceFile(parent, o.getFile(), directory, indices, logger));

      logger.printf("[FileStore] %s: Creating index.json", recipe.getRecipe());
      JsonUtils.saveAs(indices, FileUtils.getFile(directory, "objects", "index.json"));
    }
    return recipes;
  }

  /**
   * Copies the given file as a hashed one.
   *
   * @param parent    directory
   * @param file      path
   * @param directory recipe directory
   * @param indices   path to hashed file mapping
   * @param logger    for logging
   */
  private void copySourceFile(final String parent, final String file, final File directory,
      final JSONObject indices, final PrintStream logger) {
    String recipe = directory.getName();
    File source = getAbsoluteFile(parent, file);
    String checksum = DigestUtils.sha1(source, "0000000000000000000000000000000000000000");
    File target = FileUtils.getFile(directory, "objects",
        checksum.substring(0, 2), checksum.substring(2));
    try {
      if (!target.exists()) {
        FileUtils.forceMkdirParent(target);
        FileUtils.copyFile(source, target);
      }
      indices.putIfAbsent(file, target.getAbsolutePath());
    } catch (IOException e) {
      logger.printf("[FileStore] %s: Failed to copy the file: '%s' -> '%s'",
          recipe, source, target);
    }
  }

  /**
   * Returns the absolute path to the file.
   *
   * @param parent directory
   * @param path   to a file
   * @return the absolute path of the file
   */
  private File getAbsoluteFile(final String parent, final String path) {
    return new File(path).isAbsolute() ? new File(path) : new File(parent, path);
  }

  /**
   * Returns the matching hashed file object.
   *
   * @param recipe name
   * @param path   of a file
   * @return File object, or null if the file does not exist
   */
  public File get(final String recipe, final String path) {
    try {
      File file = FileUtils.getFile(new File(storage.toURI()), recipe, "objects", "index.json");
      JSONObject indices = JsonUtils.createObject(file);
      return new File(indices.getString(path));
    } catch (IOException | InterruptedException | JSONException ignored) {
      return null;
    }
  }
}
