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
