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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * A heterogeneous data list class.
 *
 * @author Sung Gon Kim
 */
public class DataList implements Streamable, Serializable {

  /**
   * Represents the UUID of the class.
   */
  private static final long serialVersionUID = 7792884940286821063L;

  /**
   * Represents the object list.
   */
  private final List<Object> objects;

  /**
   * Represents the object types.
   */
  private final Set<Class<?>> types;

  public DataList() {
    objects = Collections.synchronizedList(new ArrayList<>());
    types = Collections.synchronizedSet(new HashSet<>());
  }

  /**
   * Returns the number of elements in the list.
   *
   * @return the number of elements in the list
   */
  public int size() {
    return objects.size();
  }

  /**
   * Adds the given object to the list.
   *
   * @param object to add
   * @param <T>    object type
   */
  public <T> void add(final T object) {
    objects.add(object);
    types.add(object.getClass());
  }

  /**
   * Adds the given type to the list.
   *
   * @param type to add
   */
  public <T> void add(final Class<T> type) {
    types.add(type);
  }

  /**
   * Adds the given objects to the list.
   *
   * @param collection to add
   * @param <T>        object type
   */
  public <T> void addAll(final Collection<? extends T> collection) {
    objects.addAll(collection);
    collection.stream().map(Object::getClass).distinct().forEach(types::add);
  }

  @Override
  public <T> boolean contains(Class<T> clazz) {
    return types.stream().anyMatch(clazz::isAssignableFrom);
  }

  @Override
  @SuppressWarnings({"unchecked", "PMD.UnnecessaryModifier"})
  public <T> Stream<T> objects(final Class<T> clazz) {
    return (Stream<T>) objects.stream().filter(o -> clazz.isAssignableFrom(o.getClass()));
  }
}
