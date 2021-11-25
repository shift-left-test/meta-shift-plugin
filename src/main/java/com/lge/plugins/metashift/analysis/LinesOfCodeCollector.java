/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.analysis;

import com.lge.plugins.metashift.models.CodeSizeData;
import com.lge.plugins.metashift.models.LinesOfCode;
import com.lge.plugins.metashift.models.Streamable;

/**
 * LinesOfCodeCollector class.
 *
 * @author Sung Gon Kim
 */
public class LinesOfCodeCollector implements Collector<Streamable, LinesOfCode> {

  @Override
  public LinesOfCode parse(Streamable s) {
    long lines = s.objects(CodeSizeData.class).mapToLong(CodeSizeData::getLines).sum();
    long functions = s.objects(CodeSizeData.class).mapToLong(CodeSizeData::getFunctions).sum();
    long classes = s.objects(CodeSizeData.class).mapToLong(CodeSizeData::getClasses).sum();
    long files = s.objects(CodeSizeData.class).map(CodeSizeData::getFile).count();
    long recipes = s.objects(CodeSizeData.class).map(CodeSizeData::getName).distinct().count();
    return new LinesOfCode(lines, functions, classes, files, recipes);
  }
}
