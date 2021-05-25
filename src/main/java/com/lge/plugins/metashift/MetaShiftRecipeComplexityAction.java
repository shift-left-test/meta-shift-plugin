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
import com.lge.plugins.metashift.models.ComplexityData;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.persistence.DataSource;
import hudson.model.TaskListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.bind.JavaScriptMethod;

/**
 * MetaShift recipe's complexity detail view action class.
 */
public class MetaShiftRecipeComplexityAction
    extends MetaShiftRecipeActionChild {

  static final String STORE_KEY_COMPLEXITYLIST = "ComplexityList";

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
  public MetaShiftRecipeComplexityAction(
      MetaShiftRecipeAction parent, TaskListener listener,
      Criteria criteria, DataSource dataSource, Recipe recipe, JSONObject metadata) {
    super(parent, listener, criteria, dataSource, recipe, metadata);

    List<ComplexityData> complexityList =
        recipe.objects(ComplexityData.class).collect(Collectors.toList());

    try {
      dataSource.put(complexityList, this.getParentAction().getName(), STORE_KEY_COMPLEXITYLIST);
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
    return "Complexity";
  }

  @Override
  public String getUrlName() {
    return "complexity";
  }

  /**
   * complexity for each file.
   */
  public static class FileComplexityStats {

    String file;
    int functions = 0;
    int complexFunctions = 0;

    public FileComplexityStats(String file) {
      this.file = file;
    }

    public String getFile() {
      return this.file;
    }

    public int getFunctions() {
      return this.functions;
    }

    public int getComplexFunctions() {
      return this.complexFunctions;
    }

    /**
     * add function.
     */
    public void addFunction(boolean isComplex) {
      this.functions++;
      if (isComplex) {
        this.complexFunctions++;
      }
    }
  }

  /**
   * return paginated complexity list.
   *
   * @param pageIndex page index
   * @param pageSize  page size
   * @return complexity list
   * @throws IOException invalid recipe uri
   */
  @JavaScriptMethod
  public JSONObject getRecipeFiles(int pageIndex, int pageSize) throws IOException {
    if (getParentAction().getMetrics().getComplexity().isAvailable()) {
      List<ComplexityData> complexityDataList = this.getDataSource().get(
          this.getParentAction().getName(), STORE_KEY_COMPLEXITYLIST);

      Map<String, FileComplexityStats> fileInfo = new HashMap<>();

      for (ComplexityData complexityData : complexityDataList) {
        String file = complexityData.getFile();

        if (!fileInfo.containsKey(file)) {
          fileInfo.put(file, new FileComplexityStats(file));
        }

        // TODO: compare with criteria
        fileInfo.get(file).addFunction(complexityData.getValue()
            > this.getParentAction().getParentAction().getCriteria().getComplexityLevel());
      }

      return getPagedDataList(pageIndex, pageSize, new ArrayList<>(fileInfo.values()));
    } else {
      return null;
    }
  }

  /**
   * return file complexity detail.
   */
  @JavaScriptMethod
  public JSONObject getFileComplexityDetail(String recipePath) {
    JSONObject result = new JSONObject();

    List<ComplexityData> complexityDataList = this.getDataSource().get(
        this.getParentAction().getName(), STORE_KEY_COMPLEXITYLIST);

    List<ComplexityData> dataList =
        complexityDataList.stream().filter(o -> o.getFile().equals(recipePath))
            .collect(Collectors.toList());

    result.put("dataList", dataList);
    result.put("content", this.readFileContents(recipePath));

    return result;
  }
}
