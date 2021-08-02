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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * EvaluationSummary class.
 *
 * @author Sung Gon Kim
 */
public class EvaluationSummary extends Data implements Aggregate<Evaluation> {

  private static final long serialVersionUID = -3037651377691916500L;

  private final LinesOfCode linesOfCode;
  private final Evaluation premirrorCache;
  private final Evaluation sharedStateCache;
  private final Evaluation recipeViolations;
  private final Evaluation comments;
  private final Evaluation codeViolations;
  private final Evaluation complexity;
  private final Evaluation duplications;
  private final Evaluation unitTests;
  private final Evaluation statementCoverage;
  private final Evaluation branchCoverage;
  private final Evaluation mutationTests;

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
  public LinesOfCode getLinesOfCode() {
    return linesOfCode;
  }

  @Override
  public Evaluation getPremirrorCache() {
    return premirrorCache;
  }

  @Override
  public Evaluation getSharedStateCache() {
    return sharedStateCache;
  }

  @Override
  public Evaluation getRecipeViolations() {
    return recipeViolations;
  }

  @Override
  public Evaluation getComments() {
    return comments;
  }

  @Override
  public Evaluation getCodeViolations() {
    return codeViolations;
  }

  @Override
  public Evaluation getComplexity() {
    return complexity;
  }

  @Override
  public Evaluation getDuplications() {
    return duplications;
  }

  @Override
  public Evaluation getUnitTests() {
    return unitTests;
  }

  @Override
  public Evaluation getStatementCoverage() {
    return statementCoverage;
  }

  @Override
  public Evaluation getBranchCoverage() {
    return branchCoverage;
  }

  @Override
  public Evaluation getMutationTests() {
    return mutationTests;
  }
}
