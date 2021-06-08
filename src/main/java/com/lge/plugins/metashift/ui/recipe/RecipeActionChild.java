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

package com.lge.plugins.metashift.ui.recipe;

import com.lge.plugins.metashift.persistence.DataSource;

import hudson.FilePath;
import hudson.model.Action;
import hudson.model.Run;
import hudson.remoting.VirtualChannel;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;

/**
 * MetaShift recipe's detail view common feature class.
 */
public abstract class RecipeActionChild implements Action {

  private final RecipeAction parent;

  /**
   * constructor.
   *
   * @param parent parent action
   */
  public RecipeActionChild(RecipeAction parent) {
    this.parent = parent;
  }

  public RecipeAction getParentAction() {
    return this.parent;
  }

  public DataSource getDataSource() {
    return this.parent.getParentAction().getDataSource();
  }

  /**
   * save code path content to DataSource.
   */
  public void saveFileContents(VirtualChannel channel, JSONObject metadata, String codePath)
      throws IOException, InterruptedException {
    if (this.getDataSource().has(this.parent.getName(), "FILE", codePath)) {
      return;
    }

    FilePath file;
    // if not absolute path, append recipe root.
    if (codePath.startsWith("/")) {
      file = new FilePath(channel, codePath);
    } else {
      file = new FilePath(new FilePath(channel, metadata.getString("S")), codePath);
    }
    
    String contents = file.readToString();

    this.getDataSource().put(contents, this.parent.getName(), "FILE", codePath);
  }

  public String readFileContents(String codePath) {
    return this.getDataSource().get(this.parent.getName(), "FILE", codePath);
  }

  public Run<?, ?> getRun() {
    return this.parent.getRun();
  }

  public abstract int getScale();

  public abstract JSONArray getStatistics();
}
