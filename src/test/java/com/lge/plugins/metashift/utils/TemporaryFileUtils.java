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

package com.lge.plugins.metashift.utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.FileUtils;
import org.junit.rules.TemporaryFolder;

/**
 * A utility class for managing temporary files.
 *
 * @author Sung Gon Kim
 */
public class TemporaryFileUtils {

  private final TemporaryFolder folder;
  private final char from;
  private final char to;

  public TemporaryFileUtils(final TemporaryFolder folder) {
    this(folder, '\'', '"');
  }

  public TemporaryFileUtils(final TemporaryFolder folder, final char from, final char to) {
    this.folder = folder;
    this.from = from;
    this.to = to;
  }

  public File getPath(final String... names) {
    return FileUtils.getFile(folder.getRoot(), names);
  }

  public File createDirectory(final String... names) throws IOException {
    File directory = FileUtils.getFile(folder.getRoot(), names);
    FileUtils.forceMkdir(directory);
    return directory;
  }

  public File createDirectory(final File parent, final String... names) throws IOException {
    File directory = FileUtils.getFile(parent, names);
    FileUtils.forceMkdir(directory);
    return directory;
  }

  public File createFile(final String... names) throws IOException {
    File file = getPath(names);
    FileUtils.touch(file);
    return file;
  }

  public File createFile(final File parent, final String... names) throws IOException {
    File file = FileUtils.getFile(parent, names);
    FileUtils.forceMkdir(file.getParentFile());
    FileUtils.touch(file);
    return file;
  }

  public File writeLines(final StringBuilder builder, final File file) {
    try {
      FileUtils.forceMkdirParent(file);
      FileUtils.write(file, builder.toString().replace(from, to), StandardCharsets.UTF_8);
    } catch (IOException ignored) {
    }
    return file;
  }

  public File writeLines(final StringBuilder builder, final File parent, final String... names) {
    return writeLines(builder, FileUtils.getFile(parent, names));
  }
}
