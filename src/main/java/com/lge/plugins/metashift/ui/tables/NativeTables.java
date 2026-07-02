/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.ui.tables;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;

/**
 * Serializes data-tables-api row beans to JSON, mirroring the behaviour of
 * {@code DefaultAsyncTableContentProvider#getTableRows} (which we cannot extend because our
 * actions already extend a base class).
 */
public final class NativeTables {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  private NativeTables() {
  }

  /**
   * Serializes the given table rows to the JSON string consumed by the data-tables front end.
   *
   * @param rows table row beans
   * @return JSON array string
   */
  public static String toJson(List<Object> rows) {
    try {
      return MAPPER.writeValueAsString(rows);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("Failed to serialize table rows", e);
    }
  }
}
