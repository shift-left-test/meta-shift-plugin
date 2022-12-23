/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.persistence;

import hudson.FilePath;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 * Provides functionalities to map keys and values using the filesystem.
 *
 * @author Sung Gon Kim
 */
public class FileStore implements Serializable {

  private static final long serialVersionUID = 4545484736312677003L;

  /**
   * Represents the path to storage directory.
   */
  private final File objects;

  /**
   * Represents the path to index.json.
   */
  private final File referer;

  /**
   * Default constructor.
   *
   * @param path to storage directory
   * @throws IOException          if failed to operate with the path
   * @throws InterruptedException if an interruption occurred
   */
  public FileStore(final FilePath path) throws IOException, InterruptedException {
    File storage = new File(path.toURI());
    objects = new File(storage, "objects");
    FileUtils.forceMkdir(objects);
    referer = new File(storage, "index.json");
    FileUtils.forceMkdirParent(referer);
    if (!referer.exists()) {
      FileUtils.writeStringToFile(referer, "{}", StandardCharsets.UTF_8);
    }
  }

  /**
   * Returns the number of stored keys.
   *
   * @return the number of stored keys
   */
  public int size() {
    try {
      return createObject(referer).size();
    } catch (IOException ignored) {
      return 0;
    }
  }

  /**
   * Test if the key exists.
   *
   * @param key to test
   * @return true if the key exists, false otherwise
   */
  public boolean has(final String key) {
    try {
      JSONObject indices = createObject(referer);
      return indices.has(key);
    } catch (IOException ignored) {
      return false;
    }
  }

  /**
   * Create an JSONObject instance from the given file.
   *
   * @param path to a json file
   * @return JSONObject
   * @throws IOException if failed to operate with the file
   */
  private JSONObject createObject(final File path) throws IOException {
    InputStream is = new BufferedInputStream(Files.newInputStream(path.toPath()));
    return JSONObject.fromObject(IOUtils.toString(is, StandardCharsets.UTF_8));
  }

  /**
   * Returns the bytes to which the specified key is mapped.
   *
   * @param key whose associated value is to be returned
   * @return the value to which the specified key is mapped, or null if no mapping found
   */
  public byte[] get(final String key) {
    try {
      JSONObject indices = createObject(referer);
      File file = new File(objects, indices.getString(key));
      GZIPInputStream gis = new GZIPInputStream(new BufferedInputStream(
          Files.newInputStream(file.toPath())));
      return IOUtils.toByteArray(gis);
    } catch (IOException | JSONException ignored) {
      return null;
    }
  }

  /**
   * Associates the specified value with the specified key in the map.
   *
   * @param key   with which the specified value is to be associated
   * @param value to be associated with the specified key
   * @throws IOException if failed to operate with index.json
   */
  public synchronized void put(final String key, final byte[] value) throws IOException {
    JSONObject indices = createObject(referer);
    String checksum = DigestUtils.sha1Hex(value);
    File file = FileUtils.getFile(objects, checksum.substring(0, 2), checksum.substring(2));
    if (!file.exists()) {
      FileUtils.forceMkdirParent(file);
      try (GZIPOutputStream gos = new GZIPOutputStream(
          new BufferedOutputStream(Files.newOutputStream(file.toPath())))) {
        IOUtils.write(value, gos);
      }
    }
    if (indices.has(key)) {
      indices.remove(key);
    }
    indices.put(key, objects.toURI().relativize(file.toURI()).getPath());
    FileUtils.writeStringToFile(referer, indices.toString(2), StandardCharsets.UTF_8);
  }
}
