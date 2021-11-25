/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.fixture;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * FakeReportBuilder class.
 *
 * @author Sung Gon Kim
 */
public class FakeReportBuilder implements FakeReport {

  private final List<FakeRecipe> recipes;

  public FakeReportBuilder() {
    recipes = new ArrayList<>();
  }

  public FakeReportBuilder add(FakeRecipe recipe) {
    recipes.add(recipe);
    return this;
  }

  @Override
  public void toFile(File directory) throws IOException {
    for (FakeRecipe recipe : recipes) {
      recipe.toFile(directory);
      new FakeCacheReport(recipe).toFile(directory);
      new FakeCodeReport(recipe).toFile(directory);
      new FakeRecipeReport(recipe).toFile(directory);
      new FakeCoverageReport(recipe).toFile(directory);
      new FakeMutationTestReport(recipe).toFile(directory);
      new FakeTestReport(recipe).toFile(directory);
    }
  }
}
