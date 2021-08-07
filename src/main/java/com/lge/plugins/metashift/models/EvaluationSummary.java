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

    private final double ratio;
    private final boolean available;
    private final boolean qualified;

    /**
     * Default constructor.
     *
     * @param ratio     value
     * @param available status
     * @param qualified status
     */
    public Group(double ratio, boolean available, boolean qualified) {
      this.ratio = ratio;
      this.available = available;
      this.qualified = qualified;
    }

    /**
     * Returns the ratio value.
     *
     * @return ratio value
     */
    public double getRatio() {
      return ratio;
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
      return new Group(evaluation.getRatio(), evaluation.isAvailable(), evaluation.isQualified());
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
