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
  public static final JSONObject EMPTY = new JSONObject();

  /**
   * Represents the null json object.
   */
  public static final Any EMPTY2 = JsonIterator.deserialize("{}");

  /**
   * Represents the cache object.
   */
  private static final LruCache<String, JSONObject> objects = new LruCache<>();

  /**
   * Represents the cache object.
   */
  private static final LruCache<String, Any> objects2 = new LruCache<>();

  /**
   * Create a JSONObject using the given file.
   *
   * @param file to a json file
   * @return a JSON object
   * @deprecated replaced by createObject2, due to the slow performance
   */
  @Deprecated
  public static synchronized JSONObject createObject(final FilePath file)
      throws IOException, InterruptedException {
    if (file == null) {
      return JsonUtils.EMPTY;
    }
    String checksum = file.digest();
    if (!objects.containsKey(checksum)) {
      objects.put(checksum, JSONObject.fromObject(file.readToString()));
    }
    return objects.get(checksum);
  }

  /**
   * Creates a Json object using the given file.
   *
   * @param file to a json file
   * @return a Json object
   * @throws IOException          if a file IO fails
   * @throws InterruptedException if an interruption occurs
   */
  public static synchronized Any createObject2(final FilePath file)
      throws IOException, InterruptedException {
    if (file == null) {
      return JsonUtils.EMPTY2;
    }
    String checksum = file.digest();
    if (!objects2.containsKey(checksum)) {
      try {
        objects2.put(checksum, JsonIterator.deserialize(file.readToString()));
      } catch (IOException e) {
        throw new JSONException(e);
      }
    }
    return objects2.get(checksum);
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
