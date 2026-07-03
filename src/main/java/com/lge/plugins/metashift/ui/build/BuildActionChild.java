/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.ui.build;

import com.lge.plugins.metashift.builders.ProjectGroup;
import com.lge.plugins.metashift.ui.ActionChildBase;
import com.lge.plugins.metashift.ui.ActionParentBase;
import com.lge.plugins.metashift.ui.tables.SummaryTableModel;
import com.lge.plugins.metashift.ui.tables.SummaryTableSpec;
import io.jenkins.plugins.datatables.TableModel;

/**
 * The main post build action child class.
 */
public class BuildActionChild extends ActionChildBase {

  private final ProjectGroup projectGroup;

  /**
   * constructor.
   *
   * @param parent parent action.
   */
  public BuildActionChild(ActionParentBase parent, ProjectGroup projectGroup,
      String name, String url, boolean percentScale) {
    super(parent, name, url, percentScale);

    this.projectGroup = projectGroup;
  }

  public final ProjectGroup getGroup() {
    return this.projectGroup;
  }

  @Override
  public boolean isAvailable() {
    return this.getGroup().getEvaluation().getBoolean("available");
  }

  /**
   * return threshold string.
   *
   * @return threshold string
   */
  public final String getThresholdString() {
    return this.getFormattedValue(this.getGroup().getEvaluation().getDouble("threshold"));
  }

  @Override
  public TableModel getTableModel(String id) {
    return new SummaryTableModel(SummaryTableSpec.forBuildMetric(id), getGroup().getSummaries());
  }
}
