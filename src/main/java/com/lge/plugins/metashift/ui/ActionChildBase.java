/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.ui;

import com.lge.plugins.metashift.builders.Group;
import hudson.model.Action;
import hudson.model.Run;
import java.io.IOException;
import jakarta.servlet.ServletException;
import org.kohsuke.stapler.Stapler;
import org.kohsuke.stapler.StaplerRequest2;
import org.kohsuke.stapler.StaplerResponse2;

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
    return Stapler.getCurrentRequest2().getParameter(paramName);
  }

  /**
   * view each metrics jelly page.
   */
  public void doIndex(StaplerRequest2 req, StaplerResponse2 res)
      throws ServletException, IOException {
    if (req != null) {
      req.getView(this, this.url + ".jelly").forward(req, res);
    }
  }

  public abstract boolean isAvailable();

  /**
   * Returns the persisted data group for this metric.
   *
   * @return data group
   */
  public abstract Group getGroup();

  /**
   * Returns the statistics/distribution header view for this metric page.
   *
   * @return stats header view
   */
  public StatsHeaderView getStatsHeader() {
    return new StatsHeaderView(getGroup().getEvaluation(), getGroup().getStatistics(),
        getGroup().getDistribution(), this.url);
  }

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
