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

package com.lge.plugins.metashift.metrics;

import com.lge.plugins.metashift.models.Recipes;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Represents the group parser class.
 *
 * @author Sung Gon Kim
 */
public abstract class GroupParser<T> implements Queryable<T> {

  /**
   * Represents the object types.
   */
  private enum Type {

    /**
     * Premirror cache type.
     */
    PREMIRROR_CACHE,

    /**
     * Shared state cache type.
     */
    SHARED_STATE_CACHE,

    /**
     * Code violation type.
     */
    CODE_VIOLATIONS,

    /**
     * Comment type.
     */
    COMMENTS,

    /**
     * Complexity type.
     */
    COMPLEXITY,

    /**
     * Statement coverage type.
     */
    STATEMENT_COVERAGE,

    /**
     * Branch coverage type.
     */
    BRANCH_COVERAGE,

    /**
     * Duplication type.
     */
    DUPLICATIONS,

    /**
     * Mutation test type.
     */
    MUTATION_TEST,

    /**
     * Recipe violation type.
     */
    RECIPE_VIOLATIONS,

    /**
     * Test type.
     */
    TEST,
  }

  /**
   * Represents the counter objects.
   */
  private final Map<Type, T> collection;

  /**
   * Default constructor.
   *
   * @param nullObject for initialization
   */
  public GroupParser(final T nullObject) {
    collection = new EnumMap<>(Type.class);
    Stream.of(Type.values()).forEach(type -> collection.put(type, nullObject));
  }

  @Override
  public T getPremirrorCache() {
    return collection.get(Type.PREMIRROR_CACHE);
  }

  /**
   * Sets the premirror cache object.
   *
   * @param object for the premirror cache
   */
  protected void setPremirrorCache(T object) {
    collection.put(Type.PREMIRROR_CACHE, object);
  }

  @Override
  public T getSharedStateCache() {
    return collection.get(Type.SHARED_STATE_CACHE);
  }

  /**
   * Sets the shared state cache object.
   *
   * @param object for the shared state cache
   */
  protected void setSharedStateCache(T object) {
    collection.put(Type.SHARED_STATE_CACHE, object);
  }

  @Override
  public T getCodeViolations() {
    return collection.get(Type.CODE_VIOLATIONS);
  }

  /**
   * Sets the code violation object.
   *
   * @param object for the code violations
   */
  protected void setCodeViolations(T object) {
    collection.put(Type.CODE_VIOLATIONS, object);
  }

  @Override
  public T getComments() {
    return collection.get(Type.COMMENTS);
  }

  /**
   * Sets the comment object.
   *
   * @param object for the comments
   */
  protected void setComments(T object) {
    collection.put(Type.COMMENTS, object);
  }

  @Override
  public T getComplexity() {
    return collection.get(Type.COMPLEXITY);
  }

  /**
   * Sets the complexity object.
   *
   * @param object for the complexity
   */
  protected void setComplexity(T object) {
    collection.put(Type.COMPLEXITY, object);
  }

  @Override
  public T getStatementCoverage() {
    return collection.get(Type.STATEMENT_COVERAGE);
  }

  /**
   * Sets the statement coverage object.
   *
   * @param object for the statement coverage
   */
  protected void setStatementCoverage(T object) {
    collection.put(Type.STATEMENT_COVERAGE, object);
  }

  @Override
  public T getBranchCoverage() {
    return collection.get(Type.BRANCH_COVERAGE);
  }

  /**
   * Sets the branch coverage object.
   *
   * @param object for the branch coverage
   */
  protected void setBranchCoverage(T object) {
    collection.put(Type.BRANCH_COVERAGE, object);
  }

  @Override
  public T getDuplications() {
    return collection.get(Type.DUPLICATIONS);
  }

  /**
   * Sets the duplication object.
   *
   * @param object for the duplications
   */
  protected void setDuplications(T object) {
    collection.put(Type.DUPLICATIONS, object);
  }

  @Override
  public T getMutationTest() {
    return collection.get(Type.MUTATION_TEST);
  }

  /**
   * Sets the mutation test object.
   *
   * @param object for the mutation test
   */
  protected void setMutationTest(T object) {
    collection.put(Type.MUTATION_TEST, object);
  }

  @Override
  public T getRecipeViolations() {
    return collection.get(Type.RECIPE_VIOLATIONS);
  }

  /**
   * Sets the recipe violation object.
   *
   * @param object for the recipe violations
   */
  protected void setRecipeViolations(T object) {
    collection.put(Type.RECIPE_VIOLATIONS, object);
  }

  @Override
  public T getTest() {
    return collection.get(Type.TEST);
  }

  /**
   * Sets the test object.
   *
   * @param object for the test
   */
  protected void setTest(T object) {
    collection.put(Type.TEST, object);
  }

  /**
   * Parses the given recipes for analysis.
   *
   * @param recipes to parse
   */
  public abstract void parse(final Recipes recipes);

  /**
   * Parses the given metrics for analysis.
   *
   * @param metrics to parse
   */
  public abstract void parse(final List<Metrics> metrics);
}
