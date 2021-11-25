/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
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
public abstract class ActionChildBase implements Action {

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

  public abstract boolean isAvailable();

  /**
   * return formatted string(precision 2).
   */
  protected String getFormattedValue(double value) {
    if (this.percentScale) {
      return String.format("%d%%", (int) (value * 100));
    } else {
      return String.format("%.2f", Math.floor(value * 100) / 100);
    }
  }
}
