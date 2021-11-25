/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import hudson.FilePath;
import java.io.File;
import java.io.IOException;
import net.sf.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Unit tests for the DataSource class.
 *
 * @author Sung Gon Kim
 */
public class DataSourceTest {

  @Rule
  public final TemporaryFolder folder = new TemporaryFolder();
  private File storage;
  private DataSource dataSource;

  @Before
  public void setUp() throws IOException, InterruptedException {
    storage = folder.newFolder();
    dataSource = new DataSource(new FilePath(storage));
  }

  @Test
  public void testInitialState() {
    assertEquals(0, dataSource.size());
  }

  @Test
  public void testGetWithUnknownKeyReturnsNull() {
    assertFalse(dataSource.has((String) null));
    assertFalse(dataSource.has("unknown"));
    assertFalse(dataSource.has("A", "B", "C"));
    assertNull(dataSource.get((String) null));
    assertNull(dataSource.get("unknown"));
    assertNull(dataSource.get("A", "B", "C"));
  }

  @Test
  public void testPutObject() throws IOException {
    JSONObject source = new JSONObject();
    source.put("hello", "world");
    dataSource.put(source, "json", "object");
    assertEquals(1, dataSource.size());
    JSONObject actual = dataSource.get("json", "object");
    assertEquals("world", actual.getString("hello"));
  }

  @Test
  public void testGetPreparedData() throws IOException, InterruptedException {
    String source = "HELLO WORLD";
    dataSource.put(source, "hello", "world");

    DataSource newDataSource = new DataSource(new FilePath(storage));
    assertEquals(1, newDataSource.size());
    assertEquals(source, newDataSource.get("hello", "world"));
  }
}
