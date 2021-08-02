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
