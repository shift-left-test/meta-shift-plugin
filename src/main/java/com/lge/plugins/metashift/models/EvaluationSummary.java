/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.models;

import java.io.Serializable;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * EvaluationSummary class.
 *
 * @author Sung Gon Kim
 */
public class EvaluationSummary extends Data implements Aggregate<EvaluationSummary.Group> {

  /**
   * EvaluationSummary.Group class.
   */
  public static class Group implements Serializable {

    private static final long serialVersionUID = -40855088536329406L;

    private final long denominator;
    private final long numerator;
    private final boolean available;
    private final boolean qualified;

    /**
     * Default constructor.
     *
     * @param denominator value
     * @param numerator   value
     * @param available   status
     * @param qualified   status
     */
    public Group(long denominator, long numerator, boolean available, boolean qualified) {
      this.denominator = denominator;
      this.numerator = numerator;
      this.available = available;
      this.qualified = qualified;
    }

    /**
     * Returns the denominator value.
     *
     * @return denominator
     */
    public long getDenominator() {
      return denominator;
    }

    /**
     * Returns the numerator value.
     *
     * @return numerator
     */
    public long getNumerator() {
      return numerator;
    }

    /**
     * Returns the ratio value.
     *
     * @return ratio value
     */
    public double getRatio() {
      return denominator != 0 ? (double) numerator / (double) denominator : 0;
    }

    /**
     * Returns the availability status.
     *
     * @return availability
     */
    public boolean isAvailable() {
      return available;
    }

    /**
     * Returns the qualification status.
     *
     * @return qualification status
     */
    public boolean isQualified() {
      return qualified;
    }

    /**
     * Create a Group instance using the given Evaluation object.
     *
     * @param evaluation object
     * @return a Group instance
     */
    public static Group of(Evaluation evaluation) {
      return new Group(evaluation.getDenominator(), evaluation.getNumerator(),
          evaluation.isAvailable(), evaluation.isQualified());
    }
  }

  private static final long serialVersionUID = -3037651377691916500L;

  private final long linesOfCode;
  private final Group premirrorCache;
  private final Group sharedStateCache;
  private final Group recipeViolations;
  private final Group comments;
  private final Group codeViolations;
  private final Group complexity;
  private final Group duplications;
  private final Group unitTests;
  private final Group statementCoverage;
  private final Group branchCoverage;
  private final Group mutationTests;

  /**
   * Default constructor.
   *
   * @param name              of the data
   * @param linesOfCode       value
   * @param premirrorCache    value
   * @param sharedStateCache  value
   * @param recipeViolations  value
   * @param comments          value
   * @param codeViolations    value
   * @param complexity        value
   * @param duplications      value
   * @param unitTests         value
   * @param statementCoverage value
   * @param branchCoverage    value
   * @param mutationTests     value
   */
  public EvaluationSummary(String name, LinesOfCode linesOfCode, Evaluation premirrorCache,
      Evaluation sharedStateCache, Evaluation recipeViolations, Evaluation comments,
      Evaluation codeViolations, Evaluation complexity, Evaluation duplications,
      Evaluation unitTests, Evaluation statementCoverage, Evaluation branchCoverage,
      Evaluation mutationTests) {
    super(name);
    this.linesOfCode = linesOfCode.getLines();
    this.premirrorCache = Group.of(premirrorCache);
    this.sharedStateCache = Group.of(sharedStateCache);
    this.recipeViolations = Group.of(recipeViolations);
    this.comments = Group.of(comments);
    this.codeViolations = Group.of(codeViolations);
    this.complexity = Group.of(complexity);
    this.duplications = Group.of(duplications);
    this.unitTests = Group.of(unitTests);
    this.statementCoverage = Group.of(statementCoverage);
    this.branchCoverage = Group.of(branchCoverage);
    this.mutationTests = Group.of(mutationTests);
  }

  @Override
  public boolean equals(Object object) {
    if (object == null) {
      return false;
    }
    if (this == object) {
      return true;
    }
    if (getClass() != object.getClass()) {
      return false;
    }
    EvaluationSummary other = (EvaluationSummary) object;
    return new EqualsBuilder()
        .append(getName(), other.getName())
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder()
        .append(getClass())
        .append(getName())
        .toHashCode();
  }

  /**
   * Returns the lines of code.
   *
   * @return the lines of code
   */
  public long getLinesOfCode() {
    return linesOfCode;
  }

  @Override
  public Group getPremirrorCache() {
    return premirrorCache;
  }

  @Override
  public Group getSharedStateCache() {
    return sharedStateCache;
  }

  @Override
  public Group getRecipeViolations() {
    return recipeViolations;
  }

  @Override
  public Group getComments() {
    return comments;
  }

  @Override
  public Group getCodeViolations() {
    return codeViolations;
  }

  @Override
  public Group getComplexity() {
    return complexity;
  }

  @Override
  public Group getDuplications() {
    return duplications;
  }

  @Override
  public Group getUnitTests() {
    return unitTests;
  }

  @Override
  public Group getStatementCoverage() {
    return statementCoverage;
  }

  @Override
  public Group getBranchCoverage() {
    return branchCoverage;
  }

  @Override
  public Group getMutationTests() {
    return mutationTests;
  }
}
