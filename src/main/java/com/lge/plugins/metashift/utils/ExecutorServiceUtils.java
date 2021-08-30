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
