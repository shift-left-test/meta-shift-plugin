/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.persistence;

import hudson.FilePath;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Provides functionalities to map keys and values using the filesystem.
 *
 * @author Sung Gon Kim
 */
public class DataSource implements Serializable {

  private static final long serialVersionUID = -2877661754512254098L;

  /**
   * Represents the file store object.
   */
  private final FileStore fileStore;

  /**
   * Default constructor.
   *
   * @param path to store files
   */
  public DataSource(final FilePath path) throws IOException, InterruptedException {
    fileStore = new FileStore(path);
  }

  /**
   * Returns the number of stored keys.
   *
   * @return the number of keys
   */
  public int size() {
    return fileStore.size();
  }

  /**
   * Returns the unique id based on the given names.
   *
   * @param names to generate the unique key
   * @return unique key
   */
  private String uid(final String... names) {
    return String.join(":", names);
  }

  /**
   * Test if the given key exists.
   *
   * @param names to generate the unique key
   * @return true if the key exists, false otherwise
   */
  public boolean has(final String... names) {
    return fileStore.has(uid(names));
  }

  /**
   * Returns an object to which the specified key is mapped.
   *
   * @param names to generate key which associated value is to be returned
   * @param <T>   class type
   * @return the object to which the specified key is mapped, or null if no mapping found
   */
  @SuppressWarnings({"unchecked", "PMD.UnnecessaryModifier"})
  public <T> T get(final String... names) {
    byte[] bytes = fileStore.get(uid(names));
    try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes)) {
      try (ObjectInputStream ois = new ObjectInputStream(bis)) {
        return (T) ois.readObject();
      }
    } catch (IOException | NullPointerException | ClassNotFoundException ignored) {
      return null;
    }
  }

  /**
   * Associates the specified value with the specified key in the map.
   *
   * @param object to be associated with the specified key
   * @param names  with which the specified value is to be associated
   * @throws IOException if failed to operate with index.json or the object
   */
  public synchronized void put(final Object object, final String... names) throws IOException {
    String key = uid(names);
    try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
      try (ObjectOutputStream oos = new ObjectOutputStream(bos)) {
        oos.writeObject(object);
        oos.close();
        fileStore.put(key, bos.toByteArray());
      }
    }
  }
}
