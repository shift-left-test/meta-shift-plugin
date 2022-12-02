/*
 * Copyright (c) 2022 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Unit tests for NamingUtils class.
 *
 * @author Sung Gon Kim
 */
public class NamingUtilsTest {

  @Test
  public void testIsValid() {
    assertFalse(NamingUtils.isValid(""));
    assertFalse(NamingUtils.isValid("abc"));
    assertTrue(NamingUtils.isValid("abc-1.0.0-r0"));
    assertTrue(NamingUtils.isValid("abc-def.+-1.0.0.+-r0.+"));
  }

  @Test
  public void testGetRecipe() {
    assertEquals("", NamingUtils.getRecipe(""));
    assertEquals("", NamingUtils.getRecipe("abc"));
    assertEquals("abc", NamingUtils.getRecipe("abc-1.0.0-r0"));
    assertEquals("abc-def.+", NamingUtils.getRecipe("abc-def.+-1.0.0.+-r0.+"));
  }

  @Test
  public void testGetVersion() {
    assertEquals("", NamingUtils.getVersion(""));
    assertEquals("", NamingUtils.getVersion("abc"));
    assertEquals("1.0.0", NamingUtils.getVersion("abc-1.0.0-r0"));
    assertEquals("1.0.0.+", NamingUtils.getVersion("abc-def.+-1.0.0.+-r0.+"));
  }

  @Test
  public void testGetRevision() {
    assertEquals("", NamingUtils.getRevision(""));
    assertEquals("", NamingUtils.getRevision("abc"));
    assertEquals("r0", NamingUtils.getRevision("abc-1.0.0-r0"));
    assertEquals("r0.+", NamingUtils.getRevision("abc-def.+-1.0.0.+-r0.+"));
  }
}
