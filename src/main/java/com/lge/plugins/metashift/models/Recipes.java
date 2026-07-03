/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.models;

import java.util.ArrayList;
import java.util.stream.Stream;

/**
 * Represents a set of Recipe objects.
 *
 * @author Sung Gon Kim
 */
public final class Recipes extends ArrayList<Recipe> implements Streamable {

  /**
   * Represents the UUID of the class.
   */
  private static final long serialVersionUID = 9217713417115395018L;

  /**
   * Creates an empty list of Recipe objects.
   */
  public Recipes() {
    super();
  }

  @Override
  public <T> boolean contains(final Class<T> clazz) {
    return stream().anyMatch(recipe -> recipe.contains(clazz));
  }

  @Override
  public <T> Stream<T> objects(final Class<T> clazz) {
    return stream().flatMap(o -> o.objects(clazz));
  }
}
