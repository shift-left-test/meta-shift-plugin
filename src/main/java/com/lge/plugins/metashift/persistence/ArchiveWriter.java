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

package com.lge.plugins.metashift.persistence;

import com.lge.plugins.metashift.models.Aggregate;
import com.lge.plugins.metashift.models.EvaluationSummary;
import com.lge.plugins.metashift.models.TreemapData;
import com.lge.plugins.metashift.persistence.Archiver.Data;
import com.lge.plugins.metashift.persistence.Archiver.Metric;
import com.lge.plugins.metashift.persistence.Archiver.Scope;
import hudson.FilePath;
import java.io.IOException;
import java.util.List;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * ArchiveWriter class.
 *
 * @author Sung Gon Kim
 */
public class ArchiveWriter implements Aggregate<DataWriter> {

  private final DataSource dataSource;
  private final FilePath path;

  /**
   * Default constructor.
   *
   * @param dataSource for persistent objects
   * @param path       to the report directory
   */
  public ArchiveWriter(DataSource dataSource, FilePath path) {
    this.dataSource = dataSource;
    this.path = path;
  }

  /**
   * Adds the treemap data objects.
   *
   * @param objects to add
   */
  public void addTreemap(List<TreemapData> objects) throws IOException {
    JSONArray array = JSONArray.fromObject(objects);
    dataSource.put(array, Scope.PROJECT.name(), Data.TREEMAP.name());
  }

  /**
   * Adds the evaluation summaries.
   *
   * @param summaries to add
   */
  public void addSummaries(List<EvaluationSummary> summaries) throws IOException {
    JSONArray array = new JSONArray();
    for (EvaluationSummary summary : summaries) {
      JSONObject o = new JSONObject();
      o.put("name", summary.getName());
      o.put("linesOfCode", summary.getLinesOfCode().getLines());
      o.put("premirrorCache", summary.getPremirrorCache().getRatio());
      o.put("sharedStateCache", summary.getSharedStateCache().getRatio());
      o.put("recipeViolations", summary.getRecipeViolations().getRatio());
      o.put("comments", summary.getComments().getRatio());
      o.put("codeViolations", summary.getCodeViolations().getRatio());
      o.put("complexity", summary.getComplexity().getRatio());
      o.put("duplications", summary.getDuplications().getRatio());
      o.put("unitTests", summary.getUnitTests().getRatio());
      o.put("statementCoverage", summary.getStatementCoverage().getRatio());
      o.put("branchCoverage", summary.getBranchCoverage().getRatio());
      o.put("mutationTests", summary.getMutationTests().getRatio());
      array.add(o);
    }
    dataSource.put(array, Scope.PROJECT.name(), Data.SUMMARIES.name());
  }

  @Override
  public CacheDataWriter getPremirrorCache() {
    return new CacheDataWriter(Metric.PREMIRROR_CACHE, dataSource, path);
  }

  @Override
  public CacheDataWriter getSharedStateCache() {
    return new CacheDataWriter(Metric.SHARED_STATE_CACHE, dataSource, path);
  }

  @Override
  public RecipeViolationDataWriter getRecipeViolations() {
    return new RecipeViolationDataWriter(dataSource, path);
  }

  @Override
  public CommentDataWriter getComments() {
    return new CommentDataWriter(dataSource, path);
  }

  @Override
  public CodeViolationDataWriter getCodeViolations() {
    return new CodeViolationDataWriter(dataSource, path);
  }

  @Override
  public ComplexityDataWriter getComplexity() {
    return new ComplexityDataWriter(dataSource, path);
  }

  @Override
  public DuplicationDataWriter getDuplications() {
    return new DuplicationDataWriter(dataSource, path);
  }

  @Override
  public UnitTestDataWriter getUnitTests() {
    return new UnitTestDataWriter(dataSource, path);
  }

  @Override
  public StatementCoverageDataWriter getStatementCoverage() {
    return new StatementCoverageDataWriter(dataSource, path);
  }

  @Override
  public BranchCoverageDataWriter getBranchCoverage() {
    return new BranchCoverageDataWriter(dataSource, path);
  }

  @Override
  public MutationTestDataWriter getMutationTests() {
    return new MutationTestDataWriter(dataSource, path);
  }
}
