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
   * Returns the digested sha1 checksum of the given file.
   *
   * @param file to digest
   * @return sha1 checksum
   * @throws IOException                            if failed to operate with the file
   * @throws java.security.NoSuchAlgorithmException if the algorithm does not exist
   */
  public static String sha1(final File file) throws IOException, NoSuchAlgorithmException {
    MessageDigest digest = MessageDigest.getInstance("SHA-1");
    digest.reset();
    digest.update(FileUtils.readFileToByteArray(file));
    return String.format("%040x", new BigInteger(1, digest.digest()));
  }

  /**
   * Returns the digested sha1 checksum of the given file.
   *
   * @param file         to digest
   * @param defaultValue default value if failed to digest the file
   * @return sha1 checksum
   */
  public static String sha1(final File file, final String defaultValue) {
    try {
      return sha1(file);
    } catch (IOException | NoSuchAlgorithmException ignored) {
      return defaultValue;
    }
  }
}
