/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
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
