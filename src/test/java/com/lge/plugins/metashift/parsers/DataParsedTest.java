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

package com.lge.plugins.metashift.parsers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.lge.plugins.metashift.models.Data;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the DataParsed class.
 *
 * @author Sung Gon Kim
 */
public class DataParsedTest {

  private static final String RECIPE = "A-1.0.0-r0";
  private static final String UNKNOWN = "B-1.0.0-r0";

  private List<Data> objects;

  @Before
  public void setUp() {
    objects = Arrays.asList(
        new BranchCoverageDataParsed(RECIPE),
        new CodeSizeDataParsed(RECIPE),
        new CodeViolationDataParsed(RECIPE),
        new CommentDataParsed(RECIPE),
        new ComplexityDataParsed(RECIPE),
        new DuplicationDataParsed(RECIPE),
        new MutationTestDataParsed(RECIPE),
        new PremirrorCacheDataParsed(RECIPE),
        new RecipeSizeDataParsed(RECIPE),
        new RecipeViolationDataParsed(RECIPE),
        new SharedStateCacheDataParsed(RECIPE),
        new TestDataParsed(RECIPE)
    );
  }

  @Test
  public void testContainsPassesWhenValidDataGiven() {
    assertTrue(objects.contains(new CodeSizeDataParsed(RECIPE)));
  }

  @Test
  public void testContainsFailsWhenUnknownDataGiven() {
    assertFalse(objects.contains(new CodeSizeDataParsed(UNKNOWN)));
  }
}
