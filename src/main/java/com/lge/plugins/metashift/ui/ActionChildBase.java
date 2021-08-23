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

package com.lge.plugins.metashift.ui;

import hudson.model.Action;
import hudson.model.Run;
import java.io.IOException;
import javax.servlet.ServletException;
import org.kohsuke.stapler.Stapler;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 * action child class.
 */
public class ActionChildBase implements Action {

  private final ActionParentBase parent;

  protected final String name;
  protected final String url;
  protected final boolean percentScale;

  /**
   * constructor.
   *
   * @param parent       parent action.
   * @param name         display name.
   * @param url          relative url from parent.
   * @param percentScale scale type.
   */
  public ActionChildBase(ActionParentBase parent, String name, String url, boolean percentScale) {
    this.parent = parent;
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

  public ActionParentBase getParent() {
    return this.parent;
  }

  public Run<?, ?> getRun() {
    return this.parent.getRun();
  }

  public String getUrlParameter(String paramName) {
    return Stapler.getCurrentRequest().getParameter(paramName);
  }

  /**
   * view each metrics jelly page.
   */
  public void doIndex(StaplerRequest req, StaplerResponse res)
      throws ServletException, IOException {
    if (req != null) {
      req.getView(this, this.url + ".jelly").forward(req, res);
    }
  }
}
