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

package com.lge.plugins.metashift.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 * JsonUtils class.
 *
 * @author Sung Gon Kim
 */
public class JsonUtils {

  private static class LruCache<K, V> extends LinkedHashMap<K, V> {

    private final int capacity;

    public LruCache(final int capacity) {
      super(capacity, 0.75f, true);
      this.capacity = capacity;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
      return size() > capacity;
    }
  }

  /**
   * Represents the null json object.
   */
  public static final JSONObject EMPTY = new JSONObject();

  /**
   * Represents the singleton objects.
   */
  private static final Map<String, JSONObject> objects = new LruCache<>(10);

  /**
   * Create a JSONObject using the given file.
   *
   * @param file to a json file
   * @return a JSON object
   */
  public static synchronized JSONObject createObject(final File file) throws IOException {
    if (file == null) {
      return JsonUtils.EMPTY;
    }
    String key = getSha1sum(file, file.getAbsolutePath());
    if (!objects.containsKey(key)) {
      objects.put(key, getObject(file));
    }
    return objects.get(key);
  }

  /**
   * Returns the sha1sum of the file, or default value of failed.
   *
   * @param file         to digest
   * @param defaultValue default value
   * @return sha1sum of the file
   * @throws IOException if failed to operate with the file
   */
  private static String getSha1sum(final File file, final String defaultValue) throws IOException {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-1");
      digest.reset();
      digest.update(FileUtils.readFileToByteArray(file));
      return new String(digest.digest(), StandardCharsets.UTF_8);
    } catch (NoSuchAlgorithmException ignored) {
      return defaultValue;
    }
  }

  /**
   * Returns a JSON object using the file.
   *
   * @param file to parse
   * @return a JSON object
   * @throws IOException if failed to operate with the file
   */
  private static JSONObject getObject(final File file) throws IOException {
    InputStream is = new BufferedInputStream(new FileInputStream(file));
    return JSONObject.fromObject(IOUtils.toString(is, StandardCharsets.UTF_8));
  }
}
