/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.ui.recipe;

import com.lge.plugins.metashift.ui.recipe.FileDetailView.MutationDetail;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Server-side view model for the annotated source page: maps the persisted per-file
 * coverage/mutation OBJECTS onto the source text stored with the build. Line background
 * priority: uncovered statement, partially covered branch, covered, no data.
 *
 * @author Sung Gon Kim
 */
public class SourceAnnotationView {

  private static final String COVERED = "msp-line--covered";
  private static final String PARTIAL = "msp-line--partial";
  private static final String UNCOVERED = "msp-line--uncovered";

  private final String file;
  private final List<Line> lines;
  private final List<MutationDetail> mutations;

  private SourceAnnotationView(String file, List<Line> lines, List<MutationDetail> mutations) {
    this.file = file;
    this.lines = lines;
    this.mutations = mutations;
  }

  /**
   * Creates the view model, or returns null when the source is not stored.
   *
   * @param file       file path from the request
   * @param source     stored source text, null or empty when unavailable
   * @param statements statement coverage OBJECTS rows ({@code line, covered})
   * @param branches   branch coverage OBJECTS rows ({@code line, covered, index})
   * @param mutations  mutation test OBJECTS rows
   * @return the view model or null
   */
  public static SourceAnnotationView of(String file, String source, JSONArray statements,
      JSONArray branches, JSONArray mutations) {
    if (source == null || source.isEmpty()) {
      return null;
    }
    Map<Long, long[]> statementByLine = FileDetailView.coveredTotalsByLine(statements);
    Map<Long, long[]> branchByLine = FileDetailView.coveredTotalsByLine(branches);
    List<MutationDetail> details = FileDetailView.toMutationDetails(mutations);
    Map<Long, List<Marker>> markersByLine = markersByLine(details);

    List<Line> lines = new ArrayList<>();
    String[] texts = source.split("\n", -1);
    // a file ending with a newline yields a spurious trailing empty element
    int count = texts.length > 0 && texts[texts.length - 1].isEmpty()
        ? texts.length - 1 : texts.length;
    for (int i = 0; i < count; i++) {
      long number = i + 1L;
      lines.add(new Line(number, texts[i],
          stateOf(statementByLine.get(number), branchByLine.get(number)),
          branchTextOf(branchByLine.get(number)),
          markersByLine.getOrDefault(number, List.of())));
    }
    return new SourceAnnotationView(file, lines, details);
  }

  private static Map<Long, List<Marker>> markersByLine(List<MutationDetail> details) {
    Map<Long, List<Marker>> byLine = new HashMap<>();
    for (int i = 0; i < details.size(); i++) {
      MutationDetail detail = details.get(i);
      byLine.computeIfAbsent(detail.getLine(), k -> new ArrayList<>())
          .add(new Marker(i, detail.getMutator(), detail.getBadgeClass()));
    }
    return byLine;
  }

  private static String stateOf(long[] statement, long[] branch) {
    if (statement != null && statement[0] == 0) {
      return UNCOVERED;
    }
    if (branch != null && branch[0] < branch[1]) {
      return PARTIAL;
    }
    if (statement != null || branch != null) {
      return COVERED;
    }
    return "";
  }

  private static String branchTextOf(long[] branch) {
    return branch == null ? "" : "⑂ " + branch[0] + "/" + branch[1];
  }

  public String getFile() {
    return file;
  }

  public List<Line> getLines() {
    return lines;
  }

  /**
   * Mutation rows for the details table, survived first. Marker indices of the source
   * lines point into this list.
   *
   * @return sorted mutation details
   */
  public List<MutationDetail> getMutations() {
    return mutations;
  }

  /**
   * A single annotated source line.
   */
  public static class Line {

    private final long number;
    private final String text;
    private final String stateClass;
    private final String branchText;
    private final List<Marker> markers;

    Line(long number, String text, String stateClass, String branchText, List<Marker> markers) {
      this.number = number;
      this.text = text;
      this.stateClass = stateClass;
      this.branchText = branchText;
      this.markers = markers;
    }

    public long getNumber() {
      return number;
    }

    public String getText() {
      return text;
    }

    public String getStateClass() {
      return stateClass;
    }

    public String getBranchText() {
      return branchText;
    }

    public List<Marker> getMarkers() {
      return markers;
    }
  }

  /**
   * A mutation chip of a source line, pointing at the mutation table row.
   */
  public static class Marker {

    private final int index;
    private final String mutator;
    private final String badgeClass;

    Marker(int index, String mutator, String badgeClass) {
      this.index = index;
      this.mutator = mutator;
      this.badgeClass = badgeClass;
    }

    public int getIndex() {
      return index;
    }

    public String getMutator() {
      return mutator;
    }

    public String getBadgeClass() {
      return badgeClass;
    }
  }
}
