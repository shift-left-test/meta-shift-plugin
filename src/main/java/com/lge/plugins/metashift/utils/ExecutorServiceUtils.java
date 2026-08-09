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
   * Runs all the tasks to completion, one after another, and rethrows the first failure.
   *
   * <p>The executor is deliberately single-threaded: this runs on the Jenkins controller inside
   * the publish step, so the tasks are sequential and only the exception handling of
   * {@code invokeAll} is being used. It is shut down before returning — its core worker never
   * times out, so without that every call would park a live platform thread until GC eventually
   * finalized the executor.
   *
   * @param tasks to invoke
   * @throws IOException          if failed to operate with files
   * @throws InterruptedException if an interruption occurs
   */
  @SafeVarargs
  public static void invokeAll(Callable<Void>... tasks) throws IOException, InterruptedException {
    ExecutorService executor = Executors.newSingleThreadExecutor();
    try {
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
    } finally {
      executor.shutdown();
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
