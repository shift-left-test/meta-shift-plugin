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

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Test;

/**
 * Unit tests for the ExecutorServiceUtils class.
 *
 * @author Sung Gon Kim
 */
public class ExecutorServiceUtilsTest {

  private Callable<Void> function(AtomicBoolean o) {
    return () -> {
      o.set(true);
      return null;
    };
  }

  @Test
  public void testInvokeSingleTask() throws IOException, InterruptedException {
    AtomicBoolean called = new AtomicBoolean(false);
    ExecutorServiceUtils.invoke(function(called));
    assertTrue(called.get());
  }

  @Test
  public void testInvokeMultipleTasks() throws IOException, InterruptedException {
    AtomicBoolean called1 = new AtomicBoolean(false);
    AtomicBoolean called2 = new AtomicBoolean(false);
    ExecutorServiceUtils.invokeAll(function(called1), function(called2));
    assertTrue(called1.get());
    assertTrue(called2.get());
  }

  @Test(expected = IOException.class)
  public void testInvokeThrowsException() throws IOException, InterruptedException {
    Callable<Void> task = () -> {
      throw new IOException();
    };
    ExecutorServiceUtils.invoke(task);
  }
}
