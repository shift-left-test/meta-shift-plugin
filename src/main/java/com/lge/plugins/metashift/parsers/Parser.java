/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.parsers;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

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
