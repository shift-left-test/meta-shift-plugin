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
import com.lge.plugins.metashift.utils.TableSortInfo;
import hudson.model.TaskListener;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.bind.JavaScriptMethod;

/**
 * MetaShift recipe's complexity detail view action class.
 */
public class MetaShiftRecipeComplexityAction
    extends MetaShiftRecipeActionChild {

  static final String STORE_KEY_COMPLEXITYLIST = "ComplexityList";
  static final String STORE_KEY_FILECOMPLEXITYSTAT = "FileComplexityStat";

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
    super(parent);

    List<ComplexityData> complexityDataList =
        recipe.objects(ComplexityData.class).collect(Collectors.toList());

    HashMap<String, List<ComplexityData>> fileComplexityList = new HashMap<>();

    for (ComplexityData complexityData : complexityDataList) {
      String file = complexityData.getFile();

      if (!fileComplexityList.containsKey(file)) {
        fileComplexityList.put(file, new ArrayList<>());
      }

      fileComplexityList.get(file).add(complexityData);
    }

    List<FileComplexityStats> fileComplexityStats = new ArrayList<>();
    long complexityThreashold =
        this.getParentAction().getParentAction().getCriteria().getComplexityLevel();

    fileComplexityList.forEach((file, complexityList) -> {
      fileComplexityStats.add(new FileComplexityStats(file,
          complexityList.size(),
          complexityList.stream().filter(o -> o.getValue() > complexityThreashold).count()
      ));

      try {
        this.saveFileContents(metadata, file);
        dataSource.put(complexityList,
            this.getParentAction().getName(), file, STORE_KEY_COMPLEXITYLIST);
      } catch (IOException e) {
        listener.getLogger().println(e.getMessage());
        e.printStackTrace(listener.getLogger());
      }
    });

    try {
      dataSource.put(fileComplexityStats,
          this.getParentAction().getName(), STORE_KEY_FILECOMPLEXITYSTAT);
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
  public static class FileComplexityStats implements Serializable {

    String file;
    long functions;
    long complexFunctions;

    /**
     * constructor.
     */
    public FileComplexityStats(String file, long functions, long complexFunctions) {
      this.file = file;
      this.functions = functions;
      this.complexFunctions = complexFunctions;
    }

    public String getFile() {
      return this.file;
    }

    public long getFunctions() {
      return this.functions;
    }

    public long getComplexFunctions() {
      return this.complexFunctions;
    }
  }

  /**
   * return paginated complexity list.
   *
   * @param pageIndex page index
   * @param pageSize  page size
   * @return complexity list
   */
  @JavaScriptMethod
  public JSONObject getRecipeFiles(int pageIndex, int pageSize, TableSortInfo [] sortInfos) {
    List<FileComplexityStats> dataList = this.getDataSource().get(
        this.getParentAction().getName(), STORE_KEY_FILECOMPLEXITYSTAT);

    if (sortInfos.length > 0) {
      Comparator<FileComplexityStats> comparator = this.getComparator(sortInfos[0]);

      for (int i = 1; i < sortInfos.length; i++) {
        comparator = comparator.thenComparing(this.getComparator(sortInfos[i]));
      }

      dataList.sort(comparator);
    }
    return getPagedDataList(pageIndex, pageSize, dataList);
  }

  private Comparator<FileComplexityStats> getComparator(TableSortInfo sortInfo) {
    Comparator<FileComplexityStats> comparator;

    switch (sortInfo.getField()) {
      case "file":
        comparator = Comparator.<FileComplexityStats, String>comparing(
            a -> a.getFile());
        break;
      case "functions":
        comparator = Comparator.<FileComplexityStats, Long>comparing(
            a -> a.getFunctions());
        break;
      case "complexFunctions":
        comparator = Comparator.<FileComplexityStats, Long>comparing(
            a -> a.getComplexFunctions());
        break;
      default:
        throw new IllegalArgumentException(
            String.format("unknown field for complexity table : %s", sortInfo.getField()));
    }

    if (sortInfo.getDir().equals("desc")) {
      comparator = comparator.reversed();
    }

    return comparator;
  }


  /**
   * return file complexity detail.
   */
  @JavaScriptMethod
  public JSONObject getFileComplexityDetail(String codePath) {
    JSONObject result = new JSONObject();

    List<ComplexityData> dataList = this.getDataSource().get(
        this.getParentAction().getName(), codePath, STORE_KEY_COMPLEXITYLIST);

    result.put("dataList", dataList);
    result.put("content", this.readFileContents(codePath));

    return result;
  }
}
