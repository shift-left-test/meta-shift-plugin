/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.ui.recipe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.junit.Test;

/**
 * Unit tests for the {@link FileDetailView} class.
 */
public class FileDetailViewTest {

  private static final String FILE = "a.c";

  private JSONArray summaries(double ratio, boolean qualified) {
    JSONObject row = new JSONObject();
    row.put("name", FILE);
    row.put("ratio", ratio);
    row.put("qualified", qualified);
    JSONArray array = new JSONArray();
    array.add(row);
    return array;
  }

  private JSONObject coverage(long line, boolean covered) {
    JSONObject o = new JSONObject();
    o.put("line", line);
    o.put("covered", covered);
    return o;
  }

  private JSONObject mutation(long line, String status) {
    JSONObject o = new JSONObject();
    o.put("line", line);
    o.put("status", status);
    o.put("mutator", "MATH");
    o.put("mutatedClass", "C");
    o.put("mutatedMethod", "m");
    o.put("killingTest", "t");
    return o;
  }

  private JSONArray array(JSONObject... objects) {
    JSONArray a = new JSONArray();
    for (JSONObject o : objects) {
      a.add(o);
    }
    return a;
  }

  private FileDetailView.MetricData empty() {
    return FileDetailView.MetricData.of(new JSONArray(), new JSONArray());
  }

  @Test
  public void testReturnsNullForUnknownFile() {
    assertNull(FileDetailView.of(null, empty(), empty(), empty()));
    assertNull(FileDetailView.of("", empty(), empty(), empty()));
    assertNull(FileDetailView.of("no-such.c", empty(), empty(), empty()));
  }

  @Test
  public void testUncoveredStatementLinesMergeIntoRanges() {
    JSONArray objects = array(
        coverage(12, false), coverage(13, false), coverage(14, false),
        coverage(20, true), coverage(45, false),
        coverage(102, false), coverage(103, false));
    FileDetailView view = FileDetailView.of(FILE,
        FileDetailView.MetricData.of(objects, summaries(0.3, false)), empty(), empty());
    assertTrue(view.isStatementDataAvailable());
    assertEquals("12-14, 45, 102-103", view.getUncoveredStatementLines());
  }

  @Test
  public void testFullyCoveredStatementsProduceEmptyRanges() {
    JSONArray objects = array(coverage(1, true), coverage(2, true));
    FileDetailView view = FileDetailView.of(FILE,
        FileDetailView.MetricData.of(objects, summaries(1.0, true)), empty(), empty());
    assertTrue(view.isStatementDataAvailable());
    assertEquals("", view.getUncoveredStatementLines());
  }

  @Test
  public void testPartiallyCoveredBranchLines() {
    // line 45: 2 of 4 branches covered; line 50: fully covered (omitted)
    JSONArray objects = array(
        coverage(45, true), coverage(45, true), coverage(45, false), coverage(45, false),
        coverage(50, true), coverage(50, true));
    FileDetailView view = FileDetailView.of(FILE, empty(),
        FileDetailView.MetricData.of(objects, summaries(0.75, false)), empty());
    assertTrue(view.isBranchDataAvailable());
    assertEquals(List.of("L45 — 2/4 covered"), view.getUncoveredBranches());
  }

  @Test
  public void testMutationsSortedSurvivedFirstThenLine() {
    JSONArray objects = array(
        mutation(10, "KILLED"), mutation(30, "SURVIVED"),
        mutation(5, "SKIPPED"), mutation(20, "SURVIVED"));
    FileDetailView view = FileDetailView.of(FILE, empty(), empty(),
        FileDetailView.MetricData.of(objects, summaries(0.25, false)));
    List<FileDetailView.MutationDetail> mutations = view.getMutations();
    assertEquals(4, mutations.size());
    assertEquals("SURVIVED", mutations.get(0).getStatus());
    assertEquals(20, mutations.get(0).getLine());
    assertEquals("SURVIVED", mutations.get(1).getStatus());
    assertEquals(30, mutations.get(1).getLine());
    assertEquals("text-bg-danger", mutations.get(0).getBadgeClass());
  }

  @Test
  public void testBadgesPerMetricWithSummary() {
    FileDetailView view = FileDetailView.of(FILE,
        FileDetailView.MetricData.of(array(coverage(1, true)), summaries(0.85, true)),
        empty(),
        FileDetailView.MetricData.of(array(mutation(1, "KILLED")), summaries(0.6, false)));
    List<FileDetailView.Badge> badges = view.getBadges();
    assertEquals(2, badges.size());
    assertEquals("Statement", badges.get(0).getName());
    assertEquals("85%", badges.get(0).getRatioText());
    assertEquals("text-bg-success", badges.get(0).getColor());
    assertEquals("Mutation", badges.get(1).getName());
    assertEquals("text-bg-danger", badges.get(1).getColor());
    assertFalse(view.isBranchDataAvailable());
  }
}
