/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
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
