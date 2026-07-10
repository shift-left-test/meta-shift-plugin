/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.fixture;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import com.lge.plugins.metashift.utils.TemporaryFileUtils;
import java.io.File;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Unit tests for the FakeRecipe class.
 *
 * @author Sung Gon Kim
 */
public class FakeRecipeTest {

  @Rule
  public final TemporaryFolder folder = new TemporaryFolder();
  private File source;
  private FakeRecipe fakeRecipe;

  @Before
  public void setUp() {
    TemporaryFileUtils utils = new TemporaryFileUtils(folder);
    source = utils.getPath("source");
    fakeRecipe = new FakeRecipe(source);
  }

  @Test
  public void testInitialState() {
    assertEquals(new File(source, fakeRecipe.getName()), fakeRecipe.getSourcePath());
    assertFalse(fakeRecipe.getSourcePath().exists());
    assertEquals(0, fakeRecipe.getSources().size());
  }

  @Test
  public void testSetValues() {
    fakeRecipe.add(new FakeSource(fakeRecipe, 10, 1, 2, 3));
    assertEquals(1, fakeRecipe.getSources().size());
  }
}
