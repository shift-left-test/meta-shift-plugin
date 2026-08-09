/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
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

  /**
   * Live worker threads of {@code Executors} pools. The core worker of a single-thread executor
   * never times out, so it stays alive until the executor is shut down. Compared as a set rather
   * than a count, because pools left behind by other tests may terminate at any moment.
   */
  private static Set<Thread> poolThreads() {
    return Thread.getAllStackTraces().keySet().stream()
        .filter(Thread::isAlive)
        .filter(o -> o.getName().matches("pool-\\d+-thread-\\d+"))
        .collect(Collectors.toSet());
  }

  private static Set<Thread> awaitNoNewPoolThreads(Set<Thread> before)
      throws InterruptedException {
    Set<Thread> created = poolThreads();
    for (int i = 0; i < 100 && !created.isEmpty(); i++) {
      Thread.sleep(20);
      created = poolThreads();
      created.removeAll(before);
    }
    return created;
  }

  @Test
  public void testInvokeAllShutsDownTheExecutorItCreated() throws Exception {
    Set<Thread> before = poolThreads();

    for (int i = 0; i < 20; i++) {
      ExecutorServiceUtils.invoke(function(new AtomicBoolean(false)));
    }

    assertEquals("invokeAll left worker threads running",
        Collections.emptySet(), awaitNoNewPoolThreads(before));
  }

  @Test
  public void testInvokeAllShutsDownTheExecutorWhenATaskFails() throws Exception {
    Set<Thread> before = poolThreads();

    for (int i = 0; i < 20; i++) {
      try {
        ExecutorServiceUtils.invoke(() -> {
          throw new IOException();
        });
      } catch (IOException ignored) {
        // expected
      }
    }

    assertEquals("a failing task left its worker thread running",
        Collections.emptySet(), awaitNoNewPoolThreads(before));
  }
}
