/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.ui.build;

import com.lge.plugins.metashift.ui.tables.TableHtml;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Server-side view model for the "recipes needing attention" strip of the build
 * overview: recipes with at least one failed metric, ordered by their worst
 * failing ratio, capped to a small number of entries.
 *
 * @author Sung Gon Kim
 */
public class AttentionView {

  private static final String[][] METRICS = {
      {"unitTests", "Unit Tests"},
      {"statementCoverage", "Statement Coverage"},
      {"branchCoverage", "Branch Coverage"},
      {"mutationTests", "Mutation Tests"},
  };

  private final int failedCount;
  private final List<Entry> recipes;

  private AttentionView(int failedCount, List<Entry> recipes) {
    this.failedCount = failedCount;
    this.recipes = recipes;
  }

  /**
   * Creates the view from the per-recipe evaluation summary rows.
   *
   * @param summaries EvaluationSummary rows
   * @param limit     maximum number of entries to keep
   * @return the view
   */
  public static AttentionView of(JSONArray summaries, int limit) {
    List<Entry> failed = new ArrayList<>();
    for (Object o : summaries) {
      Entry entry = toEntry((JSONObject) o);
      if (entry != null) {
        failed.add(entry);
      }
    }
    failed.sort(Comparator.comparingDouble(Entry::getWorstRatio));
    List<Entry> top = new ArrayList<>(failed.subList(0, Math.min(limit, failed.size())));
    return new AttentionView(failed.size(), top);
  }

  private static Entry toEntry(JSONObject row) {
    List<Chip> chips = new ArrayList<>();
    double worst = Double.MAX_VALUE;
    for (String[] metric : METRICS) {
      JSONObject block = row.optJSONObject(metric[0]);
      if (block == null || !block.optBoolean("available", false)
          || block.optBoolean("qualified", false)) {
        continue;
      }
      double ratio = block.optDouble("ratio", 0);
      chips.add(new Chip(metric[1], TableHtml.percent(ratio) + "%"));
      worst = Math.min(worst, ratio);
    }
    if (chips.isEmpty()) {
      return null;
    }
    return new Entry(row.optString("name"), worst, chips);
  }

  public int getFailedCount() {
    return failedCount;
  }

  public List<Entry> getRecipes() {
    return recipes;
  }

  public boolean isTruncated() {
    return failedCount > recipes.size();
  }

  /**
   * A recipe with failed metrics.
   */
  public static class Entry {

    private final String name;
    private final double worstRatio;
    private final List<Chip> chips;

    Entry(String name, double worstRatio, List<Chip> chips) {
      this.name = name;
      this.worstRatio = worstRatio;
      this.chips = chips;
    }

    public String getName() {
      return name;
    }

    double getWorstRatio() {
      return worstRatio;
    }

    public List<Chip> getChips() {
      return chips;
    }
  }

  /**
   * One failed metric of a recipe.
   */
  public static class Chip {

    private final String name;
    private final String ratioText;

    Chip(String name, String ratioText) {
      this.name = name;
      this.ratioText = ratioText;
    }

    public String getName() {
      return name;
    }

    public String getRatioText() {
      return ratioText;
    }
  }
}
