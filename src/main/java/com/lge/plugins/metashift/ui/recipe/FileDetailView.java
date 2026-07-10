/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.ui.recipe;

import com.lge.plugins.metashift.ui.tables.TableHtml;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Server-side view model for the per-file annotation summary: uncovered line
 * ranges, partially covered branches and mutation details derived from the
 * persisted report data. Used as the fallback of the annotated source page
 * when the source of the file is not stored with the build.
 *
 * @author Sung Gon Kim
 */
public class FileDetailView {

  private final String file;
  private final List<Badge> badges;
  private final boolean statementDataAvailable;
  private final String uncoveredStatementLines;
  private final boolean branchDataAvailable;
  private final List<String> uncoveredBranches;
  private final List<MutationDetail> mutations;

  private FileDetailView(String file, List<Badge> badges, boolean statementDataAvailable,
      String uncoveredStatementLines, boolean branchDataAvailable,
      List<String> uncoveredBranches, List<MutationDetail> mutations) {
    this.file = file;
    this.badges = badges;
    this.statementDataAvailable = statementDataAvailable;
    this.uncoveredStatementLines = uncoveredStatementLines;
    this.branchDataAvailable = branchDataAvailable;
    this.uncoveredBranches = uncoveredBranches;
    this.mutations = mutations;
  }

  /**
   * Creates the view model, or returns null when the file is unknown to every metric.
   *
   * @param file       file path from the request
   * @param statements statement coverage objects and summaries
   * @param branches   branch coverage objects and summaries
   * @param mutations  mutation test objects and summaries
   * @return the view model or null
   */
  public static FileDetailView of(String file, MetricData statements, MetricData branches,
      MetricData mutations) {
    if (file == null || file.isEmpty()) {
      return null;
    }
    List<Badge> badges = new ArrayList<>();
    addBadge(badges, "Statement", statements.summaries, file);
    addBadge(badges, "Branch", branches.summaries, file);
    addBadge(badges, "Mutation", mutations.summaries, file);
    if (badges.isEmpty() && statements.objects.isEmpty() && branches.objects.isEmpty()
        && mutations.objects.isEmpty()) {
      return null;
    }
    return new FileDetailView(file, badges,
        !statements.objects.isEmpty(), uncoveredLineRanges(statements.objects),
        !branches.objects.isEmpty(), partiallyCoveredBranches(branches.objects),
        toMutationDetails(mutations.objects));
  }

  private static void addBadge(List<Badge> badges, String label, JSONArray summaries,
      String file) {
    JSONObject row = findSummary(summaries, file);
    if (row != null && row.has("ratio")) {
      badges.add(new Badge(label, TableHtml.percent(row.optDouble("ratio", 0)) + "%",
          row.optBoolean("qualified", false) ? "text-bg-success" : "text-bg-danger"));
    }
  }

  private static JSONObject findSummary(JSONArray summaries, String file) {
    for (Object o : summaries) {
      JSONObject row = (JSONObject) o;
      if (file.equals(row.optString("name"))) {
        return row;
      }
    }
    return null;
  }

  static Map<Long, long[]> coveredTotalsByLine(JSONArray objects) {
    // value: [covered, total] per line, ordered by line number
    Map<Long, long[]> byLine = new TreeMap<>();
    for (Object o : objects) {
      JSONObject data = (JSONObject) o;
      long[] counts = byLine.computeIfAbsent(data.optLong("line"), k -> new long[2]);
      if (data.optBoolean("covered", false)) {
        counts[0]++;
      }
      counts[1]++;
    }
    return byLine;
  }

  private static String uncoveredLineRanges(JSONArray objects) {
    List<String> parts = new ArrayList<>();
    Long start = null;
    Long previous = null;
    for (Map.Entry<Long, long[]> entry : coveredTotalsByLine(objects).entrySet()) {
      if (entry.getValue()[0] > 0) {
        continue;
      }
      long line = entry.getKey();
      if (start == null) {
        start = line;
      } else if (line != previous + 1) {
        parts.add(rangeText(start, previous));
        start = line;
      }
      previous = line;
    }
    if (start != null) {
      parts.add(rangeText(start, previous));
    }
    return String.join(", ", parts);
  }

  private static String rangeText(long start, long end) {
    return start == end ? String.valueOf(start) : start + "-" + end;
  }

  private static List<String> partiallyCoveredBranches(JSONArray objects) {
    List<String> lines = new ArrayList<>();
    for (Map.Entry<Long, long[]> entry : coveredTotalsByLine(objects).entrySet()) {
      long covered = entry.getValue()[0];
      long total = entry.getValue()[1];
      if (covered < total) {
        lines.add(String.format("L%d — %d/%d covered", entry.getKey(), covered, total));
      }
    }
    return lines;
  }

  static List<MutationDetail> toMutationDetails(JSONArray objects) {
    List<MutationDetail> details = new ArrayList<>();
    for (Object o : objects) {
      details.add(new MutationDetail((JSONObject) o));
    }
    details.sort(Comparator.comparing((MutationDetail d) -> !"SURVIVED".equals(d.getStatus()))
        .thenComparingLong(MutationDetail::getLine));
    return details;
  }

  public String getFile() {
    return file;
  }

  public List<Badge> getBadges() {
    return badges;
  }

  public boolean isStatementDataAvailable() {
    return statementDataAvailable;
  }

  /**
   * Uncovered statement lines merged into ranges, e.g. {@code "12-18, 45, 102-110"}.
   *
   * @return range text, empty when everything is covered
   */
  public String getUncoveredStatementLines() {
    return uncoveredStatementLines;
  }

  public boolean isBranchDataAvailable() {
    return branchDataAvailable;
  }

  /**
   * Lines with at least one uncovered branch, e.g. {@code "L45 — 2/4 covered"}.
   *
   * @return formatted lines, empty when every branch is covered
   */
  public List<String> getUncoveredBranches() {
    return uncoveredBranches;
  }

  public List<MutationDetail> getMutations() {
    return mutations;
  }

  /**
   * Per-file objects and summaries of one metric.
   */
  public static final class MetricData {

    private final JSONArray objects;
    private final JSONArray summaries;

    private MetricData(JSONArray objects, JSONArray summaries) {
      this.objects = objects;
      this.summaries = summaries;
    }

    /**
     * Default factory.
     *
     * @param objects   per-file OBJECTS rows, empty when the metric has no data
     * @param summaries per-file summary rows of the metric
     * @return the metric data
     */
    public static MetricData of(JSONArray objects, JSONArray summaries) {
      return new MetricData(objects == null ? new JSONArray() : objects,
          summaries == null ? new JSONArray() : summaries);
    }
  }

  /**
   * A per-metric ratio badge of the header line.
   */
  public static final class Badge {

    private final String name;
    private final String ratioText;
    private final String color;

    Badge(String name, String ratioText, String color) {
      this.name = name;
      this.ratioText = ratioText;
      this.color = color;
    }

    public String getName() {
      return name;
    }

    public String getRatioText() {
      return ratioText;
    }

    public String getColor() {
      return color;
    }
  }

  /**
   * A single mutation row of the details table.
   */
  public static class MutationDetail {

    private final JSONObject data;

    MutationDetail(JSONObject data) {
      this.data = data;
    }

    public long getLine() {
      return data.optLong("line");
    }

    public String getStatus() {
      return data.optString("status");
    }

    /**
     * Bootstrap badge class: survived mutants use the loudest colour.
     *
     * @return badge class suffix
     */
    public String getBadgeClass() {
      switch (getStatus()) {
        case "SURVIVED":
          return "text-bg-danger";
        case "KILLED":
          return "text-bg-success";
        default:
          return "text-bg-secondary";
      }
    }

    /**
     * Row highlight class of the mutation table: survived mutants stand out.
     *
     * @return CSS class, empty when no highlight applies
     */
    public String getRowClass() {
      return "SURVIVED".equals(getStatus()) ? "msp-row--survived" : "";
    }

    public String getMutator() {
      return data.optString("mutator");
    }

    public String getMutatedClass() {
      return data.optString("mutatedClass");
    }

    public String getMutatedMethod() {
      return data.optString("mutatedMethod");
    }

    public String getKillingTest() {
      return data.optString("killingTest");
    }
  }
}
