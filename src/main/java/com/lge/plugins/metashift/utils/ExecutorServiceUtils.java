/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.utils;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * ExecutorServiceUtils class.
 *
 * @author Sung Gon Kim
 */
public class ExecutorServiceUtils {

  /**
   * Function interface which throws exceptions.
   *
   * @param <T> input type
   * @param <R> output type
   */
  @FunctionalInterface
  public interface Function<T, R> {

    /**
     * Applies the function to get the result.
     *
     * @param t input
     * @return output
     * @throws Exception if failed to operate
     */
    R apply(T t) throws Exception;
  }

  /**
   * Invokes all the tasks.
   *
   * @param tasks to invoke
   * @throws IOException          if failed to operate with files
   * @throws InterruptedException if an interruption occurs
   */
  @SafeVarargs
  public static void invokeAll(Callable<Void>... tasks) throws IOException, InterruptedException {
    try {
      ExecutorService executor = Executors.newSingleThreadExecutor();
      for (Future<Void> future : executor.invokeAll(Arrays.asList(tasks))) {
        future.get();
      }
    } catch (ExecutionException e) {
      Throwable cause = e.getCause();
      if (cause instanceof IllegalArgumentException) {
        throw (IllegalArgumentException) cause;
      }
      if (cause instanceof IOException) {
        throw (IOException) cause;
      }
      throw new RuntimeException("Unknown exception: " + cause.getMessage(), cause);
    }
  }

  /**
   * Invokes the task.
   *
   * @param task to invoke
   * @throws IOException          if failed to operate with files
   * @throws InterruptedException if an interruption occurs
   */
  public static void invoke(Callable<Void> task) throws IOException, InterruptedException {
    invokeAll(task);
  }
}
