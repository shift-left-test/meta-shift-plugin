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

    final MenuItem headerBuildPerformance = new MenuItem().withDisplayName("Build Performance");
    headerBuildPerformance.header = true;
    menu.add(headerBuildPerformance);
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
