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

/**
 * Represents the mutation test data.
 *
 * @author Sung Gon Kim
 */
public abstract class MutationTestData extends Data<MutationTestData> {

  /**
   * Represents the name of the file.
   */
  private final String file;

  /**
   * Represents the name of the mutated class.
   */
  private final String mutatedClass;

  /**
   * Represents the name of the mutated method.
   */
  private final String mutatedMethod;

  /**
   * Represents the line number.
   */
  private final long line;

  /**
   * Represents the mutation operator.
   */
  private final String mutator;

  /**
   * Represents the test that kills the mutant.
   */
  private final String killingTest;

  /**
   * Default constructor.
   *
   * @param recipe        name
   * @param file          name
   * @param mutatedClass  the mutated class
   * @param mutatedMethod the mutated method
   * @param line          the line number
   * @param mutator       the mutation operator
   * @param killingTest   the test that kills the mutant
   */
  public MutationTestData(final String recipe, final String file, final String mutatedClass,
      final String mutatedMethod, final long line, final String mutator,
      final String killingTest) {
    super(recipe);
    this.file = file;
    this.mutatedClass = mutatedClass;
    this.mutatedMethod = mutatedMethod;
    this.line = line;
    this.mutator = mutator;
    this.killingTest = killingTest;
  }

  @Override
  public final int compareTo(final MutationTestData other) {
    return compareEach(
        getRecipe().compareTo(other.getRecipe()),
        file.compareTo(other.file),
        mutatedClass.compareTo(other.mutatedClass),
        mutatedMethod.compareTo(other.mutatedMethod),
        Long.compare(line, other.line),
        mutator.compareTo(other.mutator),
        killingTest.compareTo(other.killingTest)
    );
  }

  @Override
  public final boolean equals(final Object object) {
    if (object == null) {
      return false;
    }
    if (this == object) {
      return true;
    }
    if (getClass() != object.getClass()) {
      return false;
    }
    return compareTo((MutationTestData) object) == 0;
  }

  @Override
  public final int hashCode() {
    return computeHashCode(getClass(), getRecipe(), file, mutatedClass, mutatedMethod, line,
        mutator, killingTest);
  }

  /**
   * Returns the name of the file.
   *
   * @return filename
   */
  public final String getFile() {
    return file;
  }

  /**
   * Returns the name of the mutated class.
   *
   * @return mutated class name
   */
  public final String getMutatedClass() {
    return mutatedClass;
  }

  /**
   * Returns the name of the mutated method.
   *
   * @return mutated method name
   */
  public final String getMutatedMethod() {
    return mutatedMethod;
  }

  /**
   * Returns the line number.
   *
   * @return line number
   */
  public final long getLine() {
    return line;
  }

  /**
   * Returns the name of the mutation operator.
   *
   * @return the name of the mutation operator
   */
  public final String getMutator() {
    return mutator;
  }

  /**
   * Returns the test which kills the mutant.
   *
   * @return the name of the killing test
   */
  public final String getKillingTest() {
    return killingTest;
  }
}
