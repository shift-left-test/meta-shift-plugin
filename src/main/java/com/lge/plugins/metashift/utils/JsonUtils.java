/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.utils;

import hudson.FilePath;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 * JsonUtils class.
 *
 * @author Sung Gon Kim
 */
public class JsonUtils {

  /**
   * Represents the null json object.
   */
  public static final JSONObject EMPTY = new JSONObject();

  /**
   * Contents of recently read files, keyed by checksum.
   *
   * <p>Deliberately holds the raw text rather than the parsed object: a {@link JSONObject} is
   * mutable, so handing the same instance to two callers lets one of them change what the other
   * sees. A String is immutable and safe to share, and re-parsing it is far cheaper than the read
   * it saves when the workspace lives on an agent.
   */
  private static final LruCache<String, String> contents = new LruCache<>();

  /**
   * Creates a Json object using the given file.
   *
   * @param file to a json file
   * @return a Json object
   * @throws IOException          if a file IO fails
   * @throws InterruptedException if an interruption occurs
   */
  public static JSONObject createObject(final FilePath file)
      throws IOException, InterruptedException {
    if (file == null) {
      return JsonUtils.EMPTY;
    }
    // digest() and read() are channel round trips when the workspace lives on an agent. Holding
    // the JVM-wide class monitor across them serializes the parsing phase of every concurrent
    // meta-shift build; only the cache access needs it. Two threads may then read the same file
    // once each, which is cheaper than making every build wait for the others.
    String checksum = file.digest();
    String data = lookup(checksum);
    if (data == null) {
      try (InputStream is = file.read()) {
        // IOUtils does not close what it is handed.
        data = IOUtils.toString(is, StandardCharsets.UTF_8);
      } catch (IOException e) {
        throw new JSONException(e);
      }
      store(checksum, data);
    }
    return JSONObject.fromObject(data);
  }

  /**
   * Returns the cached contents of the given checksum, or null when absent. Synchronized because
   * the cache is a shared access-order map, which reorders itself even on a read.
   *
   * @param checksum of the file contents
   * @return cached contents, or null
   */
  private static synchronized String lookup(final String checksum) {
    return contents.get(checksum);
  }

  /**
   * Caches the contents of the given checksum.
   *
   * @param checksum of the file contents
   * @param data     contents of the file
   */
  private static synchronized void store(final String checksum, final String data) {
    contents.put(checksum, data);
  }

  /**
   * Saves the JSON object as the file.
   *
   * @param object to save
   * @param file   to save
   * @throws IOException if failed to operate with the file
   */
  public static void saveAs(final JSONObject object, final File file) throws IOException {
    FileUtils.forceMkdirParent(file);
    FileUtils.writeStringToFile(file, object.toString(2), StandardCharsets.UTF_8);
  }
}
