/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.models;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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
    Stream<T> merged = Stream.empty();
    List<Stream<T>> streams = stream().map(o -> o.objects(clazz)).collect(Collectors.toList());
    for (Stream<T> stream : streams) {
      merged = Stream.concat(merged, stream);
    }
    return merged;
  }
}
