/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

/**
 * Unit tests for the CommentData class.
 *
 * @author Sung Gon Kim
 */
public class CommentDataTest {

  private final CommentData origin = new CommentData("A-1.0.0-r0", "a.file", 10, 5);
  private final CommentData same = new CommentData("A-1.0.0-r0", "a.file", 10, 5);

  private void assertHashEquals(Object expected, Object actual) {
    assertEquals(expected.hashCode(), actual.hashCode());
  }

  private void assertHashNotEquals(Object expected, Object actual) {
    assertNotEquals(expected.hashCode(), actual.hashCode());
  }

  @Test
  public void testInitData() {
    assertEquals("A-1.0.0-r0", origin.getName());
    assertEquals("a.file", origin.getFile());
    assertEquals(10, origin.getLines());
    assertEquals(5, origin.getCommentLines());
  }

  @Test
  public void testEquality() {
    assertNotEquals(origin, null);
    assertNotEquals(origin, new Object());
    assertEquals(origin, origin);
    assertEquals(origin, same);
    assertNotEquals(origin, new CommentData("B-1.0.0-r0", "a.file", 10, 5));
    assertNotEquals(origin, new CommentData("A-1.0.0-r0", "b.file", 10, 5));
    assertEquals(origin, new CommentData("A-1.0.0-r0", "a.file", 10000, 5));
    assertEquals(origin, new CommentData("A-1.0.0-r0", "a.file", 10, 1000));
  }

  @Test
  public void testHashCode() {
    assertHashEquals(origin, same);
    assertHashNotEquals(origin, new CommentData("B-1.0.0-r0", "a.file", 10, 5));
    assertHashNotEquals(origin, new CommentData("A-1.0.0-r0", "b.file", 10, 5));
    assertHashEquals(origin, new CommentData("A-1.0.0-r0", "a.file", 10000, 5));
    assertHashEquals(origin, new CommentData("A-1.0.0-r0", "a.file", 10, 1000));
  }

  @Test
  public void testGetRatio() {
    CommentData data = new CommentData("A-1.0.0-r0", "a.file", 0, 0);
    assertEquals(0, data.getRatio(), 0.1);
    assertEquals(0.5, origin.getRatio(), 0.1);
  }
}
