/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.persistence;

import hudson.FilePath;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
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
   * Points this data source at the given storage directory, which is where its files are now.
   *
   * @param storage directory holding the stored objects and their index
   */
  public synchronized void relocate(final File storage) {
    fileStore.relocate(storage);
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
    if (bytes == null) {
      return null;
    }
    try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes)) {
      try (ObjectInputStream ois = new ObjectInputStream(bis)) {
        return (T) ois.readObject();
      }
    } catch (IOException | ClassNotFoundException ignored) {
      return null;
    }
  }

  /**
   * Writes the index of everything stored so far, making it readable by a later instance.
   *
   * <p>{@link #put} only writes the object itself, so publishing has to call this once it is
   * done — including when it failed part way, so that whatever was published is still readable.
   *
   * @throws IOException if failed to operate with index.json
   */
  public synchronized void flush() throws IOException {
    fileStore.flush();
  }

  /**
   * Associates the specified value with the specified key in the map.
   *
   * @param object to be associated with the specified key
   * @param names  with which the specified value is to be associated
   * @throws IOException if failed to operate with the object
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
