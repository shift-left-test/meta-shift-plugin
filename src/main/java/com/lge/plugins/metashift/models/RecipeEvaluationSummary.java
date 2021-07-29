package com.lge.plugins.metashift.models;

import java.io.Serializable;

/**
 * RecipeEvaluationSummary class.
 *
 * @author Sung Gon Kim
 */
public class RecipeEvaluationSummary implements Serializable {

  private static final long serialVersionUID = -4255555325565388741L;

  private final LinesOfCode linesOfCode;
  private final MetricDataSummary premirrorCache;
  private final MetricDataSummary sharedStateCache;
  private final MetricDataSummary recipeViolations;
  private final MetricDataSummary comments;
  private final MetricDataSummary codeViolations;
  private final MetricDataSummary complexity;
  private final MetricDataSummary duplications;
  private final MetricDataSummary unitTests;
  private final MetricDataSummary statementCoverage;
  private final MetricDataSummary branchCoverage;
  private final MetricDataSummary mutationTests;

  /**
   * Default constructor.
   *
   * @param linesOfCode       data
   * @param premirrorCache    data
   * @param sharedStateCache  data
   * @param recipeViolations  data
   * @param comments          data
   * @param codeViolations    data
   * @param complexity        data
   * @param duplications      data
   * @param unitTests         data
   * @param statementCoverage data
   * @param branchCoverage    data
   * @param mutationTests     data
   */
  public RecipeEvaluationSummary(LinesOfCode linesOfCode, MetricDataSummary premirrorCache,
      MetricDataSummary sharedStateCache, MetricDataSummary recipeViolations,
      MetricDataSummary comments, MetricDataSummary codeViolations, MetricDataSummary complexity,
      MetricDataSummary duplications, MetricDataSummary unitTests,
      MetricDataSummary statementCoverage, MetricDataSummary branchCoverage,
      MetricDataSummary mutationTests) {
    this.linesOfCode = linesOfCode;
    this.premirrorCache = premirrorCache;
    this.sharedStateCache = sharedStateCache;
    this.recipeViolations = recipeViolations;
    this.comments = comments;
    this.codeViolations = codeViolations;
    this.complexity = complexity;
    this.duplications = duplications;
    this.unitTests = unitTests;
    this.statementCoverage = statementCoverage;
    this.branchCoverage = branchCoverage;
    this.mutationTests = mutationTests;
  }

  /**
   * Default constructor.
   */
  public RecipeEvaluationSummary() {
    this(new LinesOfCode(), new MetricDataSummary(), new MetricDataSummary(),
        new MetricDataSummary(), new MetricDataSummary(), new MetricDataSummary(),
        new MetricDataSummary(), new MetricDataSummary(), new MetricDataSummary(),
        new MetricDataSummary(), new MetricDataSummary(), new MetricDataSummary());
  }

  /**
   * Returns the lines of code.
   *
   * @return lines of code
   */
  public LinesOfCode getLinesOfCode() {
    return linesOfCode;
  }

  /**
   * Returns the metric data summary.
   *
   * @return metric data summary
   */
  public MetricDataSummary getPremirrorCache() {
    return premirrorCache;
  }

  /**
   * Returns the metric data summary.
   *
   * @return metric data summary
   */
  public MetricDataSummary getSharedStateCache() {
    return sharedStateCache;
  }

  /**
   * Returns the metric data summary.
   *
   * @return metric data summary
   */
  public MetricDataSummary getRecipeViolations() {
    return recipeViolations;
  }

  /**
   * Returns the metric data summary.
   *
   * @return metric data summary
   */
  public MetricDataSummary getComments() {
    return comments;
  }

  /**
   * Returns the metric data summary.
   *
   * @return metric data summary
   */
  public MetricDataSummary getCodeViolations() {
    return codeViolations;
  }

  /**
   * Returns the metric data summary.
   *
   * @return metric data summary
   */
  public MetricDataSummary getComplexity() {
    return complexity;
  }

  /**
   * Returns the metric data summary.
   *
   * @return metric data summary
   */
  public MetricDataSummary getDuplications() {
    return duplications;
  }

  /**
   * Returns the metric data summary.
   *
   * @return metric data summary
   */
  public MetricDataSummary getUnitTests() {
    return unitTests;
  }

  /**
   * Returns the metric data summary.
   *
   * @return metric data summary
   */
  public MetricDataSummary getStatementCoverage() {
    return statementCoverage;
  }

  /**
   * Returns the metric data summary.
   *
   * @return metric data summary
   */
  public MetricDataSummary getBranchCoverage() {
    return branchCoverage;
  }

  /**
   * Returns the metric data summary.
   *
   * @return metric data summary
   */
  public MetricDataSummary getMutationTests() {
    return mutationTests;
  }
}
