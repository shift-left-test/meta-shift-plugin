/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.utils;

import com.jsoniter.JsonIterator;
import com.jsoniter.any.Any;
import hudson.FilePath;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;

/**
 * JsonUtils class.
 *
 * @author Sung Gon Kim
 */
public class JsonUtils {

  /**
   * Represents the null json object.
   */
  public static final Any EMPTY = JsonIterator.deserialize("{}");

  /**
   * Represents the cache object.
   */
  private static final LruCache<String, Any> objects = new LruCache<>();

  /**
   * Creates a Json object using the given file.
   *
   * @param file to a json file
   * @return a Json object
   * @throws IOException          if a file IO fails
   * @throws InterruptedException if an interruption occurs
   */
  public static synchronized Any createObject(final FilePath file)
      throws IOException, InterruptedException {
    if (file == null) {
      return JsonUtils.EMPTY;
    }
    String checksum = file.digest();
    if (!objects.containsKey(checksum)) {
      try {
        objects.put(checksum, JsonIterator.deserialize(file.readToString()));
      } catch (IOException e) {
        throw new JSONException(e);
      }
    }
    return objects.get(checksum);
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
