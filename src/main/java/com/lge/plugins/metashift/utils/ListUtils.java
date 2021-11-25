/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.utils;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Provides utility methods for List objects.
 *
 * @author Sung Gon Kim
 */
public class ListUtils {

  /**
   * Returns the consecutive sublist of the list, each of the same size.
   *
   * @param list to return consecutive sublist of
   * @param size of each sublist
   * @param <T>  the element type
   * @return a list of consecutive sublist
   */
  public static <T> List<List<T>> partition(List<T> list, int size) {
    if (list == null || size <= 0) {
      return Collections.emptyList();
    }
    return new Partition<>(list, size);
  }

  private static class Partition<T> extends AbstractList<List<T>> {

    private final List<T> list;
    private final int size;

    public Partition(List<T> list, int size) {
      this.list = list;
      this.size = size;
    }

    @Override
    public List<T> get(int index) {
      int start = index * size;
      int end = Math.min(start + size, list.size());
      if (start > end || index < 0) {
        throw new IndexOutOfBoundsException(
            index + ": Index out of range <0, " + (size() - 1) + ">");
      }
      return new ArrayList<>(list.subList(start, end));
    }

    @Override
    public int size() {
      return (int) Math.ceil((double) list.size() / (double) size);
    }
  }
}
