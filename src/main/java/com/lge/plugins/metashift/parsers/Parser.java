/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.parsers;

import com.jsoniter.spi.JsonException;
import com.lge.plugins.metashift.utils.JsonUtils;
import hudson.FilePath;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;
import net.sf.json.JSONException;

/**
 * Parser class.
 *
 * @author Sung Gon Kim
 */
public abstract class Parser implements Callable<Void> {

  /**
   * Test if the path of the file is hidden.
   *
   * @param path to a file
   * @return true if the path is hidden, false otherwise
   */
  protected boolean isHidden(String path) {
    File file = new File(path);
    do {
      String filename = file.getName();
      if (filename.equals(".") || filename.equals("..") || !filename.startsWith(".")) {
        file = file.getParentFile();
      } else {
        return true;
      }
    } while (file != null);
    return false;
  }

  /**
   * Reads the BitBake S variable of the task directory.
   *
   * @param taskDir task report directory (e.g. coverage/)
   * @return source root path, or null when not available
   */
  static String readSourceRoot(FilePath taskDir) throws InterruptedException {
    try {
      FilePath metadata = taskDir.child("metadata.json");
      if (!metadata.exists()) {
        return null;
      }
      String value = JsonUtils.createObject(metadata).toString("S");
      return (value == null || value.isEmpty()) ? null : value;
    } catch (IOException | JSONException | JsonException ignored) {
      return null;
    }
  }

  /**
   * Converts an absolute path under the source root to a relative one.
   *
   * @param sourceRoot source root path, nullable
   * @param path       file path from the report
   * @return source-root-relative path when applicable, the input otherwise
   */
  protected String relativize(String sourceRoot, String path) {
    if (sourceRoot == null || path == null) {
      return path;
    }
    String prefix = sourceRoot.endsWith("/") ? sourceRoot : sourceRoot + "/";
    return path.startsWith(prefix) ? path.substring(prefix.length()) : path;
  }

  /**
   * Parses the report files.
   *
   * @throws IOException          if failed to operate with the files
   * @throws InterruptedException if an interruption occurs
   */
  public abstract void parse() throws IOException, InterruptedException;

  @Override
  public Void call() throws Exception {
    parse();
    return null;
  }
}
