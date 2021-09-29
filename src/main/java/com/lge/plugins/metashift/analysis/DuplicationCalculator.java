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
import com.lge.plugins.metashift.models.Configuration;
import com.lge.plugins.metashift.models.DuplicationData;
import com.lge.plugins.metashift.models.Streamable;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * DuplicationCalculator class.
 *
 * @author Sung Gon Kim
 */
public class DuplicationCalculator implements Collector<Streamable, DuplicationCalculator> {

  private final Configuration configuration;
  private long lines;
  private long duplicateLines;

  /**
   * Default constructor.
   *
   * @param configuration for evaluation
   */
  public DuplicationCalculator(Configuration configuration) {
    this.configuration = configuration;
    this.lines = 0;
    this.duplicateLines = 0;
  }

  private long calculateDuplicateLines(List<DuplicationData> objects) {
    BitSet marks = new BitSet();
    objects.forEach(o -> marks.set((int) o.getStart(), (int) (o.getEnd() + 1)));
    return marks.cardinality();
  }

  private List<List<DuplicationData>> getGroups(List<DuplicationData> s) {
    Map<String, Map<String, List<DuplicationData>>> groups = s.stream()
        .collect(Collectors.groupingBy(DuplicationData::getName,
            Collectors.groupingBy(DuplicationData::getFile)));
    List<List<DuplicationData>> objects = new ArrayList<>();
    groups.values().forEach(o -> objects.addAll(o.values()));
    return objects;
  }

  @Override
  public DuplicationCalculator parse(Streamable s) {
    lines = s.objects(CodeSizeData.class).mapToLong(CodeSizeData::getLines).sum();
    long tolerance = configuration.getDuplicationTolerance();
    List<DuplicationData> objects = s.objects(DuplicationData.class)
        .filter(o -> o.getDuplicatedLines() >= tolerance)
        .collect(Collectors.toList());
    duplicateLines = getGroups(objects).stream().mapToLong(this::calculateDuplicateLines).sum();
    return this;
  }

  /**
   * Returns the calculated lines of code.
   *
   * @return the lines of code
   */
  public long getLines() {
    return lines;
  }

  /**
   * Returns the calculated duplicate lines of code.
   *
   * @return the duplicate lines of code
   */
  public long getDuplicateLines() {
    return duplicateLines;
  }

  /**
   * Returns the calculated unique lines of code.
   *
   * @return the unique lines of code
   */
  public long getUniqueLines() {
    return getLines() - getDuplicateLines();
  }
}
