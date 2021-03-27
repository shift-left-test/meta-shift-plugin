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
import org.apache.commons.lang3.StringUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.selectors.TypeSelector;
import org.apache.tools.ant.types.selectors.TypeSelector.FileType;

/**
 * Finds all files matching the given ant style patterns.
 *
 * @author Sung Gon Kim
 */
public final class FileFinder {

  /**
   * Represents a list of patterns of files that must be included.
   */
  private final String includes;
  /**
   * Represents a list of patterns of files that must be excluded.
   */
  private final String excludes;

  /**
   * Default constructor.
   *
   * @param includes patterns of files that must be included
   */
  public FileFinder(final String includes) {
    this(includes, StringUtils.EMPTY);
  }

  /**
   * Default constructor.
   *
   * @param includes patterns of files that must be included
   * @param excludes patterns of files that must be excluded
   */
  public FileFinder(final String includes, final String excludes) {
    this.includes = includes;
    this.excludes = excludes;
  }

  /**
   * Returns an array of file names matching the given patterns.
   *
   * @param path to the root directory
   * @return a list of all matching file names
   */
  public String[] find(final File path) {
    try {
      if (StringUtils.isBlank(includes)) {
        return new String[0];
      }
      FileSet fileSet = new FileSet();
      Project antProject = new Project();
      fileSet.setProject(antProject);
      fileSet.setDir(path);
      fileSet.setIncludes(includes);
      TypeSelector selector = new TypeSelector();
      FileType fileType = new FileType();
      fileType.setValue(FileType.FILE);
      selector.setType(fileType);
      fileSet.addType(selector);
      if (StringUtils.isNotBlank(excludes)) {
        fileSet.setExcludes(excludes);
      }
      return fileSet.getDirectoryScanner(antProject).getIncludedFiles();
    } catch (BuildException e) {
      return new String[0];
    }
  }
}
