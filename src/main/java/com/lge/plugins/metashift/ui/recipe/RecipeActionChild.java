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

import com.lge.plugins.metashift.metrics.Evaluator;
import com.lge.plugins.metashift.persistence.DataSource;
import hudson.FilePath;
import hudson.model.Action;
import hudson.model.Run;
import hudson.remoting.VirtualChannel;
import java.io.IOException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.Stapler;
import org.kohsuke.stapler.bind.JavaScriptMethod;

/**
 * Detail view common feature class.
 */
public abstract class RecipeActionChild implements Action {

  static final String STORE_KEY_TABLE_MODEL = "TableModel";

  private final RecipeAction parent;

  // used for read recipe file from remote.
  private final transient VirtualChannel channel;
  private final transient JSONObject metadata;

  private final String name;
  private final String url;
  private final boolean percentScale;

  /**
   * constructor.
   *
   * @param parent parent action
   */
  public RecipeActionChild(RecipeAction parent, VirtualChannel channel, JSONObject metadata,
      String name, String url, boolean percentScale) {
    this.parent = parent;
    this.channel = channel;
    this.metadata = metadata;

    this.name = name;
    this.url = url;
    this.percentScale = percentScale;
  }

  @Override
  public String getIconFileName() {
    return "document.png";
  }

  @Override
  public String getDisplayName() {
    return this.name;
  }

  @Override
  public String getUrlName() {
    return this.url;
  }

  public RecipeAction getParentAction() {
    return this.parent;
  }

  public DataSource getDataSource() {
    return this.parent.getParentAction().getDataSource();
  }

  public Run<?, ?> getRun() {
    return this.parent.getRun();
  }

  public String getUrlParameter(String paramName) {
    return Stapler.getCurrentRequest().getParameter(paramName);
  }

  public abstract Evaluator<?> getEvaluator();

  public abstract JSONArray getDistributionJson();

  /**
   * save code path content to DataSource.
   */
  public void saveFileContents(String codePath)
      throws IOException, InterruptedException {
    if (this.getDataSource().has(this.parent.getName(), "FILE", codePath)) {
      return;
    }

    FilePath file;
    // if not absolute path, append recipe root.
    if (codePath.startsWith("/")) {
      file = new FilePath(this.channel, codePath);
    } else {
      file = new FilePath(new FilePath(this.channel, this.metadata.getString("S")), codePath);
    }

    String contents = file.readToString();

    this.getDataSource().put(contents, this.parent.getName(), "FILE", codePath);
  }

  public String readFileContents(String codePath) throws InterruptedException {
    return this.getDataSource().get(this.parent.getName(), "FILE", codePath);
  }

  /**
   * return scale.
   *
   * @return string
   */
  public String getScale() {
    Evaluator<?> evaluator = this.getEvaluator();
    if (evaluator.isAvailable()) {
      if (this.percentScale) {
        return String.format("%d%%", (long) (evaluator.getRatio() * 100));
      } else {
        return String.format("%.2f", evaluator.getRatio());
      }
    } else {
      return "N/A";
    }
  }

  protected void setTableModelJson(JSONArray model) throws IOException, InterruptedException {
    this.getDataSource().put(model,
        this.getParentAction().getName(), this.name, STORE_KEY_TABLE_MODEL);
  }

  @JavaScriptMethod
  public JSONArray getTableModelJson() throws InterruptedException {
    return this.getDataSource().get(
        this.getParentAction().getName(), this.name, STORE_KEY_TABLE_MODEL);
  }
}
