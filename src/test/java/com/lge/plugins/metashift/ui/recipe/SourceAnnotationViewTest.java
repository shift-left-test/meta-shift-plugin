/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.ui.recipe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import net.sf.json.JSONArray;
import org.junit.Test;

/**
 * Unit tests for the SourceAnnotationView class.
 */
public class SourceAnnotationViewTest {

  private static final JSONArray STATEMENTS = JSONArray.fromObject(
      "[{'line':2,'covered':true},{'line':3,'covered':false}]");
  private static final JSONArray BRANCHES = JSONArray.fromObject(
      "[{'line':2,'covered':true,'index':0},{'line':2,'covered':false,'index':1}]");
  private static final JSONArray MUTATIONS = JSONArray.fromObject(
      "[{'line':2,'status':'KILLED','mutator':'AOR','mutatedClass':'C','mutatedMethod':'m',"
          + "'killingTest':'T'},"
          + "{'line':3,'status':'SURVIVED','mutator':'ROR','mutatedClass':'C',"
          + "'mutatedMethod':'m','killingTest':''}]");

  private SourceAnnotationView newView() {
    return SourceAnnotationView.of("a.cpp", "int a;\nint b;\nint c;\n",
        STATEMENTS, BRANCHES, MUTATIONS);
  }

  @Test
  public void testReturnsNullWithoutSource() {
    assertNull(SourceAnnotationView.of("a.cpp", "", STATEMENTS, BRANCHES, MUTATIONS));
    assertNull(SourceAnnotationView.of("a.cpp", null, STATEMENTS, BRANCHES, MUTATIONS));
  }

  @Test
  public void testTrailingNewlineDoesNotAddALine() {
    assertEquals(3, newView().getLines().size());
    assertEquals("int a;", newView().getLines().get(0).getText());
  }

  @Test
  public void testInteriorBlankLinesArePreserved() {
    SourceAnnotationView view = SourceAnnotationView.of("a.cpp", "int a;\n\nint b;\n",
        new JSONArray(), new JSONArray(), new JSONArray());
    assertEquals(3, view.getLines().size());
    assertEquals("", view.getLines().get(1).getText());
  }

  @Test
  public void testLineStatesFollowPriority() {
    SourceAnnotationView view = newView();
    assertEquals("", view.getLines().get(0).getStateClass());
    // line 2: statement covered but branch 1/2 -> partial wins over covered
    assertEquals("msp-line--partial", view.getLines().get(1).getStateClass());
    assertEquals("⑂ 1/2", view.getLines().get(1).getBranchText());
    // line 3: statement uncovered -> uncovered wins
    assertEquals("msp-line--uncovered", view.getLines().get(2).getStateClass());
  }

  @Test
  public void testCoveredLineWithoutBranchesIsGreen() {
    SourceAnnotationView view = SourceAnnotationView.of("a.cpp", "int a;\n",
        JSONArray.fromObject("[{'line':1,'covered':true}]"), new JSONArray(), new JSONArray());
    assertEquals("msp-line--covered", view.getLines().get(0).getStateClass());
    assertEquals("", view.getLines().get(0).getBranchText());
  }

  @Test
  public void testFullyCoveredBranchLineIsGreen() {
    SourceAnnotationView view = SourceAnnotationView.of("a.cpp", "int a;\n",
        new JSONArray(),
        JSONArray.fromObject("[{'line':1,'covered':true,'index':0}]"), new JSONArray());
    assertEquals("msp-line--covered", view.getLines().get(0).getStateClass());
    assertEquals("⑂ 1/1", view.getLines().get(0).getBranchText());
  }

  @Test
  public void testMutationsSortSurvivedFirstAndMarkersPointAtThem() {
    SourceAnnotationView view = newView();
    assertEquals("SURVIVED", view.getMutations().get(0).getStatus());
    assertEquals("KILLED", view.getMutations().get(1).getStatus());
    // line 3 has the survived mutant -> marker index 0
    assertEquals(1, view.getLines().get(2).getMarkers().size());
    assertEquals(0, view.getLines().get(2).getMarkers().get(0).getIndex());
    assertEquals("ROR", view.getLines().get(2).getMarkers().get(0).getMutator());
    assertEquals("text-bg-danger", view.getLines().get(2).getMarkers().get(0).getBadgeClass());
    // line 2 has the killed mutant -> marker index 1
    assertEquals(1, view.getLines().get(1).getMarkers().get(0).getIndex());
  }
}
