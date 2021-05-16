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

package com.lge.plugins.metashift.persistence;

import hudson.FilePath;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Provides functionalities to map keys and values using the filesystem.
 *
 * @author Sung Gon Kim
 */
public class DataSource {

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
