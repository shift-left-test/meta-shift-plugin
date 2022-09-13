/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
}
