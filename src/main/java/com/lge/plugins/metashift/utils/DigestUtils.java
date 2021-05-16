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

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.io.FileUtils;

/**
 * DigestUtils class.
 *
 * @author Sung Gon Kim
 */
public class DigestUtils {

  /**
   * Returns the sha1 checksum of the given bytes.
   *
   * @param bytes        to digest
   * @param defaultValue default value
   * @return sha1 checksum of the bytes, or default value
   */
  public static String sha1(final byte[] bytes, String defaultValue) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-1");
      digest.reset();
      digest.update(bytes);
      return String.format("%040x", new BigInteger(1, digest.digest()));
    } catch (NoSuchAlgorithmException ignored) {
      return defaultValue;
    }
  }

  /**
   * Returns the sha1 checksum of the given bytes.
   *
   * @param bytes to digest
   * @return sha1 checksum of the bytes, or default value
   */
  public static String sha1(final byte[] bytes) {
    return sha1(bytes, "0000000000000000000000000000000000000000");
  }

  /**
   * Returns the sha1 checksum of the given string.
   *
   * @param string to digest
   * @return sha1 checksum of the bytes, or default value
   */
  public static String sha1(final String string, final String defaultValue) {
    return sha1(string.getBytes(StandardCharsets.UTF_8), defaultValue);
  }

  /**
   * Returns the sha1 checksum of the given string.
   *
   * @param string to digest
   * @return sha1 checksum of the bytes, or default value
   */
  public static String sha1(final String string) {
    return sha1(string.getBytes(StandardCharsets.UTF_8));
  }

  /**
   * Returns the sha1 checksum of the given file.
   *
   * @param file to digest
   * @return sha1 checksum
   * @throws IOException if failed to operate with the file
   */
  public static String sha1(final File file) throws IOException {
    return sha1(FileUtils.readFileToByteArray(file));
  }

  /**
   * Returns the sha1 checksum of the given file.
   *
   * @param file to digest
   * @return sha1 checksum, or default value
   */
  public static String sha1(final File file, final String defaultValue) {
    try {
      return sha1(file);
    } catch (IOException ignored) {
      return defaultValue;
    }
  }
}
