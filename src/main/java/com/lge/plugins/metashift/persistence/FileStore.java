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
   *
   * <p>Not final: XStream writes a {@link File} as its absolute path, so a build record carries
   * where the store lived when it was published. Moving the job — a rename, a move in or out of a
   * folder, a different {@code JENKINS_HOME} — moves the store with it and leaves that path
   * pointing nowhere, and {@link #get} answers null for everything, so the report renders empty
   * with no error. {@link #relocate} rebinds both paths when the build record is loaded.
   */
  private File objects;

  /**
   * Represents the path to index.json.
   */
  private File referer;

  /**
   * In-memory cache of index.json, avoiding a disk read and JSON parse on every
   * {@link #has}/{@link #get}/{@link #size} call. Transient because this class is persisted
   * with the build (via {@code RecipeReport}/{@code ProjectReport}); it is lazily rebuilt after
   * deserialization the same way it is built on first use within a live instance.
   */
  private transient JSONObject indices;

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
   * Points this store at the given storage directory, which is where its files are now.
   *
   * @param storage directory holding {@code objects/} and {@code index.json}
   */
  public synchronized void relocate(final File storage) {
    File relocated = new File(storage, "objects");
    if (relocated.equals(objects)) {
      return;
    }
    objects = relocated;
    referer = new File(storage, "index.json");
    // the cached index describes the previous location
    indices = null;
  }

  /**
   * Returns the number of stored keys.
   *
   * @return the number of stored keys
   */
  public synchronized int size() {
    try {
      return indices().size();
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
  public synchronized boolean has(final String key) {
    try {
      return indices().has(key);
    } catch (IOException ignored) {
      return false;
    }
  }

  /**
   * Returns the cached index, loading it from {@link #referer} on first use. Callers must hold
   * the monitor of this instance, since the returned object is mutated in place by {@link #put}.
   *
   * @return the index
   * @throws IOException if failed to operate with the file
   */
  private JSONObject indices() throws IOException {
    if (indices == null) {
      indices = createObject(referer);
    }
    return indices;
  }

  /**
   * Create an JSONObject instance from the given file.
   *
   * @param path to a json file
   * @return JSONObject
   * @throws IOException if failed to operate with the file
   */
  private JSONObject createObject(final File path) throws IOException {
    // IOUtils does not close what it is handed, so the stream has to be closed here.
    try (InputStream is = new BufferedInputStream(Files.newInputStream(path.toPath()))) {
      return JSONObject.fromObject(IOUtils.toString(is, StandardCharsets.UTF_8));
    }
  }

  /**
   * Returns the bytes to which the specified key is mapped.
   *
   * @param key whose associated value is to be returned
   * @return the value to which the specified key is mapped, or null if no mapping found
   */
  public synchronized byte[] get(final String key) {
    try {
      JSONObject indices = indices();
      File file = new File(objects, indices.getString(key));
      // IOUtils does not close what it is handed. Rendering a report reads two stored objects per
      // recipe back to back, so a descriptor plus a native inflate stream left open per read can
      // exhaust the process limit long before GC reclaims any of them.
      try (GZIPInputStream gis = new GZIPInputStream(new BufferedInputStream(
          Files.newInputStream(file.toPath())))) {
        return IOUtils.toByteArray(gis);
      }
    } catch (IOException | JSONException ignored) {
      return null;
    }
  }

  /**
   * Writes the index of every key stored so far, making them readable by a later instance.
   *
   * @throws IOException if failed to operate with index.json
   */
  public synchronized void flush() throws IOException {
    FileUtils.writeStringToFile(referer, indices().toString(2), StandardCharsets.UTF_8);
  }

  /**
   * Associates the specified value with the specified key in the map.
   *
   * <p>Only the object blob is written here; the index is kept in memory until {@link #flush} so
   * that publishing a report does not serialize the entire index once per stored object. One
   * store is shared by the whole build, so the index grows monotonically across recipes and a
   * per-put rewrite makes the publish step quadratic in the number of objects. Callers must flush
   * once publishing ends, including on the failure path — an index that does not list its blobs
   * reads back as an empty report.
   *
   * @param key   with which the specified value is to be associated
   * @param value to be associated with the specified key
   * @throws IOException if failed to operate with the object
   */
  public synchronized void put(final String key, final byte[] value) throws IOException {
    JSONObject indices = indices();
    String checksum = DigestUtils.sha256Hex(value);
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
  }
}
