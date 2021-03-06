/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.models;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Represents the mutation test data.
 *
 * @author Sung Gon Kim
 */
public abstract class MutationTestData extends Data {

  /**
   * Represents the mutation test status.
   */
  protected enum Status {
    KILLED,
    SURVIVED,
    SKIPPED,
  }

  /**
   * Represents the UUID of the class.
   */
  private static final long serialVersionUID = -5700729307383480244L;

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
   * Represents the status of the mutation test.
   */
  private final Status status;

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
   * @param status        of the mutation test
   */
  public MutationTestData(final String recipe, final String file, final String mutatedClass,
      final String mutatedMethod, final long line, final String mutator,
      final String killingTest, final Status status) {
    super(recipe);
    this.file = file;
    this.mutatedClass = mutatedClass;
    this.mutatedMethod = mutatedMethod;
    this.line = line;
    this.mutator = mutator;
    this.killingTest = killingTest;
    this.status = status;
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
    MutationTestData other = (MutationTestData) object;
    return new EqualsBuilder()
        .append(getName(), other.getName())
        .append(getFile(), other.getFile())
        .append(getMutatedClass(), other.getMutatedClass())
        .append(getMutatedMethod(), other.getMutatedMethod())
        .append(getLine(), other.getLine())
        .append(getMutator(), other.getMutator())
        .append(getKillingTest(), other.getKillingTest())
        .isEquals();
  }

  @Override
  public final int hashCode() {
    return new HashCodeBuilder()
        .append(getClass())
        .append(getName())
        .append(getFile())
        .append(getMutatedClass())
        .append(getMutatedMethod())
        .append(getLine())
        .append(getMutator())
        .append(getKillingTest())
        .toHashCode();
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

  /**
   * Returns the status of the mutation test.
   *
   * @return status of the mutation test
   */
  public final String getStatus() {
    return status.name();
  }
}
