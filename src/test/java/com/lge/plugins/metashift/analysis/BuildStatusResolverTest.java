/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.analysis;

import static org.junit.Assert.assertEquals;

import com.lge.plugins.metashift.models.BranchCoverageData;
import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.FailedTestData;
import com.lge.plugins.metashift.models.PassedTestData;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.Recipes;
import com.lge.plugins.metashift.models.StatementCoverageData;
import com.lge.plugins.metashift.models.SurvivedMutationTestData;
import com.lge.plugins.metashift.utils.ConfigurationUtils;
import hudson.model.Result;
import java.util.function.Function;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the BuildStatusResolver class.
 *
 * @author Sung Gon Kim
 */
public class BuildStatusResolverTest {

  private Configuration configuration;
  private BuildStatusResolver resolver;
  private Recipes recipes;
  private Recipe recipe;

  @Before
  public void setUp() {
    configuration = ConfigurationUtils.of(50, 10, false);
    resolver = new BuildStatusResolver(configuration);
    recipe = new Recipe("A-A-A");
    recipes = new Recipes();
    recipes.add(recipe);
  }

  private void assertAs(Result expected, Function<BuildStatusResolver, Result> functor) {
    resolver.parse(recipes);
    assertEquals(expected, functor.apply(resolver));
    assertEquals(expected, resolver.getCombined());
  }

  @Test
  public void testInitialStatus() {
    resolver.parse(recipes);
    assertEquals(Result.SUCCESS, resolver.getCombined());
  }

  @Test
  public void testStableUnitTests() {
    configuration.setTestAsUnstable(false);
    recipe.add(new FailedTestData("A-A-A", "A", "A", "A"));
    assertAs(Result.SUCCESS, BuildStatusResolver::getUnitTests);
  }

  @Test
  public void testUnstableUnitTests() {
    configuration.setTestAsUnstable(true);
    recipe.add(new FailedTestData("A-A-A", "A", "A", "A"));
    assertAs(Result.UNSTABLE, BuildStatusResolver::getUnitTests);
  }

  @Test
  public void testStableStatementCoverage() {
    configuration.setStatementCoverageAsUnstable(false);
    recipe.add(new PassedTestData("A-A-A", "A", "A", "A"));
    recipe.add(new StatementCoverageData("A-A-A", "a.file", 1, false));
    assertAs(Result.SUCCESS, BuildStatusResolver::getStatementCoverage);
  }

  @Test
  public void testUnstableStatementCoverage() {
    configuration.setStatementCoverageAsUnstable(true);
    recipe.add(new PassedTestData("A-A-A", "A", "A", "A"));
    recipe.add(new StatementCoverageData("A-A-A", "a.file", 1, false));
    assertAs(Result.UNSTABLE, BuildStatusResolver::getStatementCoverage);
  }

  @Test
  public void testStableBranchCoverage() {
    configuration.setBranchCoverageAsUnstable(false);
    recipe.add(new PassedTestData("A-A-A", "A", "A", "A"));
    recipe.add(new BranchCoverageData("A-A-A", "a.file", 1, 1, false));
    assertAs(Result.SUCCESS, BuildStatusResolver::getBranchCoverage);
  }

  @Test
  public void testUnstableBranchCoverage() {
    configuration.setBranchCoverageAsUnstable(true);
    recipe.add(new PassedTestData("A-A-A", "A", "A", "A"));
    recipe.add(new BranchCoverageData("A-A-A", "a.file", 1, 1, false));
    assertAs(Result.UNSTABLE, BuildStatusResolver::getBranchCoverage);
  }

  @Test
  public void testStableMutationTests() {
    configuration.setMutationTestAsUnstable(false);
    recipe.add(new PassedTestData("A-A-A", "A", "A", "A"));
    recipe.add(new SurvivedMutationTestData("A-A-A", "a.file", "A", "A", 1, "X", "X"));
    assertAs(Result.SUCCESS, BuildStatusResolver::getMutationTests);
  }

  @Test
  public void testUnstableMutationTests() {
    configuration.setMutationTestAsUnstable(true);
    recipe.add(new PassedTestData("A-A-A", "A", "A", "A"));
    recipe.add(new SurvivedMutationTestData("A-A-A", "a.file", "A", "A", 1, "X", "X"));
    assertAs(Result.UNSTABLE, BuildStatusResolver::getMutationTests);
  }
}
