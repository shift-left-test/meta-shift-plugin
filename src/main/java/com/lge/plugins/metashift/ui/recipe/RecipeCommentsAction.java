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

package com.lge.plugins.metashift.ui.recipe;

import com.lge.plugins.metashift.metrics.Evaluator;
import com.lge.plugins.metashift.models.CommentData;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.persistence.DataSource;
import com.lge.plugins.metashift.ui.models.FileCommentTableItem;
import com.lge.plugins.metashift.ui.models.StatisticsItem;
import com.lge.plugins.metashift.ui.models.TableSortInfo;
import com.lge.plugins.metashift.ui.models.TabulatorUtils;
import hudson.model.TaskListener;
import hudson.remoting.VirtualChannel;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.bind.JavaScriptMethod;

/**
 * MetaShift recipe's comment detail view action class.
 */
public class RecipeCommentsAction
    extends RecipeActionChild {

  static final String STORE_KEY_COMMENTLIST = "CommentList";

  /**
   * constructor.
   *
   * @param parent     parent action
   * @param listener   logger
   * @param dataSource datasource
   * @param recipe     recipe
   * @param metadata   metadata
   */
  public RecipeCommentsAction(
      RecipeAction parent, TaskListener listener, VirtualChannel channel,
      DataSource dataSource, Recipe recipe, JSONObject metadata) {
    super(parent);

    List<FileCommentTableItem> commentList = recipe.objects(CommentData.class)
        .map(FileCommentTableItem::new).collect(Collectors.toList());

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

  @Override
  public int getScale() {
    return (int) (this.getParentAction().getMetrics().getComments().getRatio() * 100);
  }

  @Override
  public JSONArray getStatistics() {
    Evaluator<?> evaluator = this.getParentAction().getMetrics().getComments();

    StatisticsItem[] result = new StatisticsItem[]{
        new StatisticsItem(
            "Comments",
            (int) (evaluator.getRatio() * 100),
            (int) evaluator.getNumerator(),
            "valid-good"
        ),
        new StatisticsItem(
            "Code",
            (int) ((1 - evaluator.getRatio()) * 100),
            (int) (evaluator.getDenominator() - evaluator.getNumerator()),
            "invalid"
        )
    };

    return JSONArray.fromObject(result);
  }

  /**
   * return paginated comment list.
   *
   * @param pageIndex page index
   * @param pageSize  page size
   * @return comment list
   */
  @JavaScriptMethod
  public JSONObject getRecipeFiles(int pageIndex, int pageSize, TableSortInfo[] sortInfos) {
    List<FileCommentTableItem> commentDataList = this.getDataSource().get(
        this.getParentAction().getName(), STORE_KEY_COMMENTLIST);

    if (sortInfos.length > 0) {
      commentDataList.sort(FileCommentTableItem.createComparator(sortInfos));
    }

    return TabulatorUtils.getPage(pageIndex, pageSize, commentDataList);
  }


}
