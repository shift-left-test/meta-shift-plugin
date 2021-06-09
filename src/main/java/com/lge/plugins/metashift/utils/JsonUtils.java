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
   * Represents the singleton cache object.
   */
  private static final LruCache<String, JSONObject> objects = new LruCache<>();

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
    String checksum = DigestUtils.sha1(file, file.getAbsolutePath());
    if (!objects.containsKey(checksum)) {
      objects.put(checksum, getObject(file));
    }
    return objects.get(checksum);
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
}
