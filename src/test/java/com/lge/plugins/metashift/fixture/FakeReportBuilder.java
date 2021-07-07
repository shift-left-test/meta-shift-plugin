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
