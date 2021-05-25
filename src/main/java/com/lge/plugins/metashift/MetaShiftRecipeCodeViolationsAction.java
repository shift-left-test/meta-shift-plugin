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
import hudson.model.TaskListener;
import java.io.IOException;
import java.util.ArrayList;
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

  static final String STORE_KEY_CODEVIOLATIONLIST = "CodeViloationList";

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
      Criteria criteria, DataSource dataSource, Recipe recipe, JSONObject metadata) {
    super(parent, listener, criteria, dataSource, recipe, metadata);

    List<CodeViolationData> codeViolationDataList =
        recipe.objects(CodeViolationData.class).collect(Collectors.toList());

    try {
      dataSource.put(codeViolationDataList,
          this.getParentAction().getName(), STORE_KEY_CODEVIOLATIONLIST);
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
  public static class FileCodeViolations {

    private final String file;
    private int major;
    private int minor;
    private int info;

    /**
     * constructor.
     */
    public FileCodeViolations(String file) {
      this.file = file;
      this.major = 0;
      this.minor = 0;
      this.info = 0;
    }

    public String getFile() {
      return this.file;
    }

    public int getMajor() {
      return this.major;
    }

    public int getMinor() {
      return this.minor;
    }

    public int getInfo() {
      return this.info;
    }

    public void addMajor() {
      this.major++;
    }

    public void addMinor() {
      this.minor++;
    }

    public void addInfo() {
      this.info++;
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
  public JSONObject getRecipeFiles(int pageIndex, int pageSize) throws IOException {
    List<CodeViolationData> codeViolationDataList = this.getDataSource().get(
        this.getParentAction().getName(), STORE_KEY_CODEVIOLATIONLIST);

    HashMap<String, FileCodeViolations> fileInfo = new HashMap<>();

    for (CodeViolationData codeViolationData : codeViolationDataList) {
      String file = codeViolationData.getFile();
      if (!fileInfo.containsKey(file)) {
        fileInfo.put(file, new FileCodeViolations(file));
      }
      switch (codeViolationData.getLevel()) {
        case "MAJOR":
          fileInfo.get(file).addMajor();
          break;
        case "MINOR":
          fileInfo.get(file).addMinor();
          break;
        case "INFO":
          fileInfo.get(file).addInfo();
          break;
        default:
          // TODO: temporary check code
          throw new IOException("invalid level name");
      }
    }

    return getPagedDataList(pageIndex, pageSize, new ArrayList<>(fileInfo.values()));
  }

  /**
   * return file code violation detail.
   */
  @JavaScriptMethod
  public JSONObject getFileCodeViolationDetail(String codePath) {
    JSONObject result = new JSONObject();

    List<CodeViolationData> codeViolationDataList = this.getDataSource().get(
        this.getParentAction().getName(), STORE_KEY_CODEVIOLATIONLIST);

    List<CodeViolationData> violationDataList =
        codeViolationDataList.stream().filter(o -> o.getFile().equals(codePath))
            .collect(Collectors.toList());

    result.put("dataList", violationDataList);
    result.put("content", this.readFileContents(codePath));

    return result;
  }
}
