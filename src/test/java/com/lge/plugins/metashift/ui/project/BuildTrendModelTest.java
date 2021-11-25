/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.ui.project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.lge.plugins.metashift.builders.ProjectReport;
import com.lge.plugins.metashift.builders.ProjectReportBuilder;
import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.PremirrorCacheData;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.Recipes;
import com.lge.plugins.metashift.persistence.DataSource;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import hudson.FilePath;

/**
 * Unit test for the BuildTrendModel class.
 */
public class BuildTrendModelTest {

  @Rule
  public final TemporaryFolder folder = new TemporaryFolder();

  ProjectReport report;

  @Before
  public void setUp() throws IOException, InterruptedException {
    Configuration config = new Configuration();
    Recipes recipes = new Recipes();
    Recipe recipe = new Recipe("A-1.0.0-r0");
    recipe.add(new PremirrorCacheData("A-1.0.0-r0", "X", false));
    recipes.add(recipe);

    DataSource dataSource = new DataSource(new FilePath(folder.newFolder()));
    report = new ProjectReportBuilder(config, dataSource).parse(recipes);
  }

  @SafeVarargs
  private final <T> JSONArray newJsonArray(T... values) {
    JSONArray objects = new JSONArray();
    objects.addAll(Arrays.asList(values));
    return objects;
  }

  private JSONObject newJsonObject(JSONArray data, String name, int axis) {
    JSONObject object = new JSONObject();
    object.put("data", data);
    object.put("name", name);
    object.put("type", "line");
    object.put("yAxisIndex", axis);
    return object;
  }

  @Test
  public void testInitData() {
    BuildTrendModel model = new BuildTrendModel(0);

    assertEquals(Arrays.asList("PremirrorCache", "SharedStateCache", "RecipeViolation",
        "Comment", "CodeViolation", "Complexity", "Duplication",
        "Test", "StatementCoverage", "BranchCoverage", "Mutation"),
        model.getLegend());

    assertEquals(Collections.emptyList(), model.getBuilds());

    JSONArray array = new JSONArray();
    array.add(newJsonObject(newJsonArray(), "PremirrorCache", 0));
    array.add(newJsonObject(newJsonArray(), "SharedStateCache", 0));
    array.add(newJsonObject(newJsonArray(), "RecipeViolation", 1));
    array.add(newJsonObject(newJsonArray(), "Comment", 0));
    array.add(newJsonObject(newJsonArray(), "CodeViolation", 1));
    array.add(newJsonObject(newJsonArray(), "Complexity", 0));
    array.add(newJsonObject(newJsonArray(), "Duplication", 0));
    array.add(newJsonObject(newJsonArray(), "Test", 0));
    array.add(newJsonObject(newJsonArray(), "StatementCoverage", 0));
    array.add(newJsonObject(newJsonArray(), "BranchCoverage", 0));
    array.add(newJsonObject(newJsonArray(), "Mutation", 0));

    assertEquals(array, JSONArray.fromObject(model.getSeries()));
  }

  @Test
  public void testAddData() throws IOException, InterruptedException {
    BuildTrendModel model = new BuildTrendModel(5);

    model.addData("#1", report);
    assertEquals(Collections.singletonList("#1"), model.getBuilds());

    JSONObject expected1 = newJsonObject(newJsonArray(0), "PremirrorCache", 0);
    System.out.println(expected1);
    System.out.println(JSONArray.fromObject(model.getSeries()));
    assertTrue(JSONArray.fromObject(model.getSeries()).contains(expected1));

    model.addData("#2", report);
    assertEquals(Arrays.asList("#2", "#1"), model.getBuilds());

    JSONObject expected2 = newJsonObject(newJsonArray(0, 0), "PremirrorCache", 0);
    assertTrue(JSONArray.fromObject(model.getSeries()).contains(expected2));
  }

  @Test
  public void testMaxBuildCount() {
    BuildTrendModel model = new BuildTrendModel(3);

    assertTrue(model.addData("#1", report));
    assertTrue(model.addData("#1", report));
    assertTrue(model.addData("#1", report));
    assertFalse(model.addData("#1", report));
  }
}
