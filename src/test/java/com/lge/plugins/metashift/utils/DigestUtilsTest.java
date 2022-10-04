/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.utils;

import static org.junit.Assert.assertEquals;

import java.nio.charset.StandardCharsets;
import org.junit.Test;

/**
 * Unit tests for the DigestUtils class.
 *
 * @author Sung Gon Kim
 */
public class DigestUtilsTest {

  @Test
  public void testSha1() {
    assertEquals("aaf4c61ddcc5e8a2dabede0f3b482cd9aea9434d",
        DigestUtils.sha1("hello".getBytes(StandardCharsets.UTF_8)));
  }

  @Test
  public void testSha1WithNonUTF8() {
    assertEquals("be69aca983a3e5541dd6905472962a32712c0567",
        DigestUtils.sha1("ĥȅŀḷở".getBytes(StandardCharsets.UTF_8)));
  }
}
