/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.ui;

import hudson.Functions;
import hudson.model.Actionable;
import hudson.model.Run;
import org.kohsuke.stapler.Stapler;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 * action parent class.
 */
public abstract class ActionParentBase extends Actionable {

  protected ActionChildBase childActionUnitTests;
  protected ActionChildBase childActionStatementCoverage;
  protected ActionChildBase childActionBranchCoverage;
  protected ActionChildBase childActionMutationTests;

  public abstract Run<?, ?> getRun();

  private void addActionToMenu(ContextMenu menu, ActionChildBase action) {
    if (action != null && action.isAvailable()) {
      String base = Functions.getIconFilePath(action);
      String icon = Stapler.getCurrentRequest().getContextPath()
          + (base.startsWith("images/") ? Functions.getResourcePath() : "")
          + '/' + base;

      menu.add(action.getUrlName(), icon, action.getDisplayName());
    }
  }

  /**
   * context menu provider for recipe action.
   */
  public ContextMenu doContextMenu(StaplerRequest request, StaplerResponse response)
      throws Exception {
    ContextMenu menu = new ContextMenu();

    final MenuItem headerCodeQuality = new MenuItem().withDisplayName("Code Quality");
    headerCodeQuality.header = true;
    menu.add(headerCodeQuality);
    this.addActionToMenu(menu, this.childActionUnitTests);
    this.addActionToMenu(menu, this.childActionStatementCoverage);
    this.addActionToMenu(menu, this.childActionBranchCoverage);
    this.addActionToMenu(menu, this.childActionMutationTests);

    return menu;
  }
}
