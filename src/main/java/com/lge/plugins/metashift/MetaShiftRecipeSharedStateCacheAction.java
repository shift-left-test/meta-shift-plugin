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
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.SharedStateCacheData;
import com.lge.plugins.metashift.persistence.DataSource;
import hudson.model.TaskListener;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.bind.JavaScriptMethod;

/**
 * MetaShift recipe's cache availability detail view action class.
 */
public class MetaShiftRecipeSharedStateCacheAction
    extends MetaShiftRecipeActionChild {

  static final String STORE_KEY_CACHELIST = "SharedStateCacheList";

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
  public MetaShiftRecipeSharedStateCacheAction(
      MetaShiftRecipeAction parent, TaskListener listener,
      Criteria criteria, DataSource dataSource, Recipe recipe, JSONObject metadata) {
    super(parent);

    List<SharedStateCacheData> cacheList =
        recipe.objects(SharedStateCacheData.class).collect(Collectors.toList());

    try {
      dataSource.put(cacheList, this.getParentAction().getName(), STORE_KEY_CACHELIST);
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
    return "Shared State Cache";
  }

  @Override
  public String getUrlName() {
    return "sharedstate_cache";
  }

  /**
   * return paginated cache availability list.
   *
   * @param pageIndex page index
   * @param pageSize  page size
   * @return cache availability list
   */
  @JavaScriptMethod
  public JSONObject getRecipeCaches(int pageIndex, int pageSize) {
    List<SharedStateCacheData> cacheList = this.getDataSource().get(
        this.getParentAction().getName(), STORE_KEY_CACHELIST);
    return getPagedDataList(pageIndex, pageSize, cacheList);
  }
}