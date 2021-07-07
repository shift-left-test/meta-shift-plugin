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
    assertEquals(0, fakeRecipe.getPremirrorFound());
    assertEquals(0, fakeRecipe.getPremirrorMissed());
    assertEquals(0, fakeRecipe.getSharedStateFound());
    assertEquals(0, fakeRecipe.getSharedStateMissed());
    assertEquals(new File(source, fakeRecipe.getRecipe()), fakeRecipe.getSourcePath());
    assertFalse(fakeRecipe.getSourcePath().exists());
    assertEquals(0, fakeRecipe.getSources().size());
    assertEquals(0, fakeRecipe.getScripts().size());
  }

  @Test
  public void testSetValues() {
    fakeRecipe.setPremirror(1, 2);
    fakeRecipe.setSharedState(3, 4);
    fakeRecipe.add(new FakeSource(fakeRecipe, 10, 1, 2, 3));
    fakeRecipe.add(new FakeScript(fakeRecipe, 20));
    assertEquals(1, fakeRecipe.getPremirrorFound());
    assertEquals(2, fakeRecipe.getPremirrorMissed());
    assertEquals(3, fakeRecipe.getSharedStateFound());
    assertEquals(4, fakeRecipe.getSharedStateMissed());
    assertEquals(1, fakeRecipe.getSources().size());
    assertEquals(1, fakeRecipe.getScripts().size());
  }
}
