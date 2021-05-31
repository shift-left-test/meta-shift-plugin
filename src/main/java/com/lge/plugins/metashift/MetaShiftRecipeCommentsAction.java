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

package com.lge.plugins.metashift;

import com.lge.plugins.metashift.metrics.Criteria;
import com.lge.plugins.metashift.models.CommentData;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.persistence.DataSource;
import com.lge.plugins.metashift.utils.TableSortInfo;
import hudson.model.TaskListener;
import java.io.IOException;
import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.bind.JavaScriptMethod;

/**
 * MetaShift recipe's comment detail view action class.
 */
public class MetaShiftRecipeCommentsAction
    extends MetaShiftRecipeActionChild {

  static final String STORE_KEY_COMMENTLIST = "CommentList";

  /**
   * constructor.
   *
   * @param parent     parent action
   * @param listener   logger
   * @param criteria   criteria
   * @param dataSource datasource
   * @param recipe     recipe
   * @param metadata   metadata
   */
  public MetaShiftRecipeCommentsAction(
      MetaShiftRecipeAction parent, TaskListener listener,
      Criteria criteria, DataSource dataSource, Recipe recipe, JSONObject metadata) {
    super(parent);

    List<CommentRateData> commentList = recipe.objects(CommentData.class)
        .map(o -> new CommentRateData(o)).collect(Collectors.toList());

    try {
      dataSource.put(commentList, this.getParentAction().getName(), STORE_KEY_COMMENTLIST);
    } catch (IOException e) {
      listener.getLogger().println(e.getMessage());
      e.printStackTrace(listener.getLogger());
    }
  }

  @Override
  public String getIconFileName() {
    return "document.png";
  }

  @Override
  public String getDisplayName() {
    return "Comments";
  }

  @Override
  public String getUrlName() {
    return "comments";
  }

  /**
   * add comment rate to commentdata.
   */
  public static class CommentRateData implements Serializable {
    private final String file;
    private final long lines;
    private final long commentLines;
    private final double commentRate;

    /**
     * constructor.
     */
    public CommentRateData(CommentData data) {
      this.file = data.getFile();
      this.lines = data.getLines();
      this.commentLines = data.getCommentLines();
      this.commentRate = (double) this.commentLines / (double) this.lines;
    }

    public String getFile() {
      return this.file;
    }

    public long getLines() {
      return this.lines;
    }

    public long getCommentLines() {
      return this.commentLines;
    }

    public double getCommentRate() {
      return this.commentRate;
    }
  }

  /**
   * return paginated comment list.
   *
   * @param pageIndex page index
   * @param pageSize  page size
   * @return comment list
   */
  @JavaScriptMethod
  public JSONObject getRecipeFiles(int pageIndex, int pageSize, TableSortInfo [] sortInfos) {
    List<CommentRateData> commentDataList = this.getDataSource().get(
        this.getParentAction().getName(), STORE_KEY_COMMENTLIST);

    if (sortInfos.length > 0) {
      Comparator<CommentRateData> comparator = this.getComparator(sortInfos[0]);

      for (int i = 1; i < sortInfos.length; i++) {
        comparator = comparator.thenComparing(this.getComparator(sortInfos[i]));
      }

      commentDataList.sort(comparator);
    }

    return getPagedDataList(pageIndex, pageSize, commentDataList);
  }

  private Comparator<CommentRateData> getComparator(TableSortInfo sortInfo) {
    Comparator<CommentRateData> comparator;

    switch (sortInfo.getField()) {
      case "file":
        comparator = Comparator.<CommentRateData, String>comparing(
            a -> a.getFile());
        break;
      case "lines":
        comparator = Comparator.<CommentRateData, Long>comparing(
            a -> a.getLines());
        break;
      case "commentLines":
        comparator = Comparator.<CommentRateData, Long>comparing(
            a -> a.getCommentLines());
        break;
      case "commentRate":
        comparator = Comparator.<CommentRateData, Double>comparing(
            a -> a.getCommentRate());
        break;
      default:
        throw new IllegalArgumentException(
            String.format("unknown field for comments table : %s", sortInfo.getField()));
    }

    if (sortInfo.getDir().equals("desc")) {
      comparator = comparator.reversed();
    }

    return comparator;
  }
}
