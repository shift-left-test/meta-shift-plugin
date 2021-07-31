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

package com.lge.plugins.metashift.models;

import com.lge.plugins.metashift.common.Aggregate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * EvaluationSummary class.
 *
 * @author Sung Gon Kim
 */
public class EvaluationSummary extends Data implements Aggregate<Double> {

  private static final long serialVersionUID = -3037651377691916500L;

  private final long linesOfCode;
  private final double premirrorCache;
  private final double sharedStateCache;
  private final double recipeViolations;
  private final double comments;
  private final double codeViolations;
  private final double complexity;
  private final double duplications;
  private final double unitTests;
  private final double statementCoverage;
  private final double branchCoverage;
  private final double mutationTests;

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
    this.premirrorCache = premirrorCache.getRatio();
    this.sharedStateCache = sharedStateCache.getRatio();
    this.recipeViolations = recipeViolations.getRatio();
    this.comments = comments.getRatio();
    this.codeViolations = codeViolations.getRatio();
    this.complexity = complexity.getRatio();
    this.duplications = duplications.getRatio();
    this.unitTests = unitTests.getRatio();
    this.statementCoverage = statementCoverage.getRatio();
    this.branchCoverage = branchCoverage.getRatio();
    this.mutationTests = mutationTests.getRatio();
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
  public Double getPremirrorCache() {
    return premirrorCache;
  }

  @Override
  public Double getSharedStateCache() {
    return sharedStateCache;
  }

  @Override
  public Double getRecipeViolations() {
    return recipeViolations;
  }

  @Override
  public Double getComments() {
    return comments;
  }

  @Override
  public Double getCodeViolations() {
    return codeViolations;
  }

  @Override
  public Double getComplexity() {
    return complexity;
  }

  @Override
  public Double getDuplications() {
    return duplications;
  }

  @Override
  public Double getUnitTests() {
    return unitTests;
  }

  @Override
  public Double getStatementCoverage() {
    return statementCoverage;
  }

  @Override
  public Double getBranchCoverage() {
    return branchCoverage;
  }

  @Override
  public Double getMutationTests() {
    return mutationTests;
  }
}
