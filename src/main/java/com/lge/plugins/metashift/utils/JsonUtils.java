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

import hudson.FilePath;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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

    private static final int DEFAULT_CAPACITY = 30;

    private final int capacity;

    public LruCache() {
      this(DEFAULT_CAPACITY);
    }

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
  private static final Map<String, JSONObject> objects = new LruCache<>();

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
    String key = DigestUtils.sha1(file, file.getAbsolutePath());
    if (!objects.containsKey(key)) {
      objects.put(key, getObject(file));
    }
    return objects.get(key);
  }

  /**
   * Create a JSONObject using the given file.
   *
   * @param file to a json file
   * @return a JSON object
   */
  public static JSONObject createObject(final FilePath file)
      throws IOException, InterruptedException {
    return createObject(new File(file.toURI()));
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

  /**
   * Saves the JSON object as the file.
   *
   * @param object to save
   * @param file   to save
   * @throws IOException          if failed to operate with the file
   * @throws InterruptedException if an interruption occurred
   */
  public static void saveAs(final JSONObject object, final FilePath file)
      throws IOException, InterruptedException {
    saveAs(object, new File(file.toURI()));
  }
}
