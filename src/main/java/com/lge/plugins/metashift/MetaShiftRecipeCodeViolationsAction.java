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
import com.lge.plugins.metashift.models.CodeViolationData;
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
 * MetaShift recipe's code violation detail view action class.
 */
public class MetaShiftRecipeCodeViolationsAction
    extends MetaShiftRecipeActionChild {

  static final String STORE_KEY_CODEVIOLATIONLIST = "CodeViolationList";
  static final String STORE_KEY_FILECODEVIOLATIONSTAT = "FileCodeViolationStat";

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
  public MetaShiftRecipeCodeViolationsAction(
      MetaShiftRecipeAction parent, TaskListener listener,
      Criteria criteria, DataSource dataSource, Recipe recipe, JSONObject metadata)
      throws IOException {
    super(parent);

    List<CodeViolationData> codeViolationDataList =
        recipe.objects(CodeViolationData.class).collect(Collectors.toList());

    HashMap<String, List<CodeViolationData>> fileCodeViloationList = new HashMap<>();

    for (CodeViolationData codeViolationData : codeViolationDataList) {
      String file = codeViolationData.getFile();

      if (!fileCodeViloationList.containsKey(file)) {
        fileCodeViloationList.put(file, new ArrayList<>());
      }

      fileCodeViloationList.get(file).add(codeViolationData);
    }

    List<FileCodeViolationStats> fileCodeViolationStats = new ArrayList<>();

    fileCodeViloationList.forEach((file, violationList) -> {
      fileCodeViolationStats.add(new FileCodeViolationStats(file,
          violationList.stream().filter(o -> o.getLevel().equals("MAJOR")).count(),
          violationList.stream().filter(o -> o.getLevel().equals("MINOR")).count(),
          violationList.stream().filter(o -> o.getLevel().equals("INFO")).count()
      ));
      try {
        this.saveFileContents(metadata, file);
        dataSource.put(violationList,
            this.getParentAction().getName(), file, STORE_KEY_CODEVIOLATIONLIST);
      } catch (IOException e) {
        listener.getLogger().println(e.getMessage());
        e.printStackTrace(listener.getLogger());
      }
    });

    try {
      dataSource.put(fileCodeViolationStats,
          this.getParentAction().getName(), STORE_KEY_FILECODEVIOLATIONSTAT);
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
    return "Code Violations";
  }

  @Override
  public String getUrlName() {
    return "code_violations";
  }

  /**
   * code violation for each file.
   */
  public static class FileCodeViolationStats implements Serializable {

    private final String file;
    private long major;
    private long minor;
    private long info;

    /**
     * constructor.
     */
    public FileCodeViolationStats(String file, long major, long minor, long info) {
      this.file = file;
      this.major = major;
      this.minor = minor;
      this.info = info;
    }

    public String getFile() {
      return this.file;
    }

    public long getMajor() {
      return this.major;
    }

    public long getMinor() {
      return this.minor;
    }

    public long getInfo() {
      return this.info;
    }
  }

  /**
   * return paginated code violation list.
   *
   * @param pageIndex page index
   * @param pageSize  page size
   * @return code violation list
   * @throws IOException invalid recipe uri
   */
  @JavaScriptMethod
  public JSONObject getRecipeFiles(int pageIndex, int pageSize, TableSortInfo [] sortInfos)
      throws IOException {
    List<FileCodeViolationStats> dataList = this.getDataSource().get(
        this.getParentAction().getName(), STORE_KEY_FILECODEVIOLATIONSTAT);

    if (sortInfos.length > 0) {
      Comparator<FileCodeViolationStats> comparator = this.getComparator(sortInfos[0]);

      for (int i = 1; i < sortInfos.length; i++) {
        comparator = comparator.thenComparing(this.getComparator(sortInfos[i]));
      }

      dataList.sort(comparator);
    }
    return getPagedDataList(pageIndex, pageSize, dataList);
  }

  private Comparator<FileCodeViolationStats> getComparator(TableSortInfo sortInfo) {
    Comparator<FileCodeViolationStats> comparator;

    switch (sortInfo.getField()) {
      case "file":
        comparator = Comparator.<FileCodeViolationStats, String>comparing(
            a -> a.getFile());
        break;
      case "major":
        comparator = Comparator.<FileCodeViolationStats, Long>comparing(
            a -> a.getMajor());
        break;
      case "minor":
        comparator = Comparator.<FileCodeViolationStats, Long>comparing(
            a -> a.getMinor());
        break;
      case "info":
        comparator = Comparator.<FileCodeViolationStats, Long>comparing(
            a -> a.getInfo());
        break;
      default:
        throw new IllegalArgumentException(
            String.format("unknown field for code violations table : %s", sortInfo.getField()));
    }

    if (sortInfo.getDir().equals("desc")) {
      comparator = comparator.reversed();
    }

    return comparator;
  }

  /**
   * return file code violation detail.
   */
  @JavaScriptMethod
  public JSONObject getFileCodeViolationDetail(String codePath) {
    JSONObject result = new JSONObject();

    List<CodeViolationData> codeViolationDataList = this.getDataSource().get(
        this.getParentAction().getName(), codePath, STORE_KEY_CODEVIOLATIONLIST);

    result.put("dataList", codeViolationDataList);
    result.put("content", this.readFileContents(codePath));

    return result;
  }
}
