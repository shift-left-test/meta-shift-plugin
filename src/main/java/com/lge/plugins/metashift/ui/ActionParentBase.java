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

  protected ActionChildBase childActionPremirrorCache;
  protected ActionChildBase childActionSharedStateCache;
  protected ActionChildBase childActionRecipeViolations;
  protected ActionChildBase childActionComments;
  protected ActionChildBase childActionCodeViolations;
  protected ActionChildBase childActionComplexity;
  protected ActionChildBase childActionDuplications;
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

    final MenuItem headerBuildSystem = new MenuItem().withDisplayName("Build System");
    headerBuildSystem.header = true;
    menu.add(headerBuildSystem);
    this.addActionToMenu(menu, this.childActionPremirrorCache);
    this.addActionToMenu(menu, this.childActionSharedStateCache);
    this.addActionToMenu(menu, this.childActionRecipeViolations);
    final MenuItem headerCodeQuality = new MenuItem().withDisplayName("Code Quality");
    headerCodeQuality.header = true;
    menu.add(headerCodeQuality);
    this.addActionToMenu(menu, this.childActionComments);
    this.addActionToMenu(menu, this.childActionCodeViolations);
    this.addActionToMenu(menu, this.childActionComplexity);
    this.addActionToMenu(menu, this.childActionDuplications);
    this.addActionToMenu(menu, this.childActionUnitTests);
    this.addActionToMenu(menu, this.childActionStatementCoverage);
    this.addActionToMenu(menu, this.childActionBranchCoverage);
    this.addActionToMenu(menu, this.childActionMutationTests);

    return menu;
  }
}
